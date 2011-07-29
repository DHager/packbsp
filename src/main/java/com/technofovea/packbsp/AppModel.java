/**
 * Copyright (C) 2011 Darien Hager
 *
 * This code is part of the "PackBSP" project, and is licensed under
 * a Creative Commons Attribution-ShareAlike 3.0 Unported License. For
 * either a summary of conditions or the full legal text, please visit:
 *
 * http://creativecommons.org/licenses/by-sa/3.0/
 *
 * Permissions beyond the scope of this license may be available
 * at http://technofovea.com/ .
 */
package com.technofovea.packbsp;

import com.technofovea.hl2parse.vdf.SteamMetaReader;
import com.technofovea.hl2parse.ParseUtil;
import com.technofovea.hl2parse.fgd.DefaultLoader;
import com.technofovea.hl2parse.fgd.FgdSpec;
import com.technofovea.hl2parse.registry.BlobFolder;
import com.technofovea.hl2parse.registry.BlobParseFailure;
import com.technofovea.hl2parse.registry.CdrParser;
import com.technofovea.hl2parse.registry.ClientRegistry;
import com.technofovea.hl2parse.registry.RegParser;
import com.technofovea.hl2parse.vdf.GameInfoReader;
import com.technofovea.hl2parse.vdf.SloppyParser;
import com.technofovea.hl2parse.vdf.ValveTokenLexer;
import com.technofovea.hl2parse.vdf.VdfRoot;
import com.technofovea.hllib.HlLib;
import com.technofovea.hllib.methods.ManagedLibrary;
import com.technofovea.packbsp.assets.ArchiveIOException;
import com.technofovea.packbsp.assets.AssetHit;
import com.technofovea.packbsp.assets.AssetLocator;
import com.technofovea.packbsp.assets.BasicLocator;
import com.technofovea.packbsp.assets.AssetSource.Type;
import com.technofovea.packbsp.conf.IncludeItem;
import com.technofovea.packbsp.conf.MapIncludes;
import com.technofovea.packbsp.assets.CachingLocator;
import com.technofovea.packbsp.assets.MapFirstLocator;
import com.technofovea.packbsp.crawling.CrawlListener;
import com.technofovea.packbsp.crawling.DependencyExpander;
import com.technofovea.packbsp.crawling.DependencyGraph;
import com.technofovea.packbsp.crawling.Edge;
import com.technofovea.packbsp.crawling.EmptyCrawlListener;
import com.technofovea.packbsp.crawling.Node;
import com.technofovea.packbsp.crawling.TraversalException;
import com.technofovea.packbsp.crawling.nodes.MapNode;
import com.technofovea.packbsp.devkits.Devkit;
import com.technofovea.packbsp.devkits.DetectedGame;
import com.technofovea.packbsp.packaging.BspZipController;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is really just a wrapper around some reusable procedural code.
 * It is expected that the GUI and controller will be set up in such a way that
 * the various phases are called in order, or else an IllegalStateException--
 * which represents programmer error--may be thrown by any method.
 * 
 * @author Darien Hager
 */
public class AppModel {

    final static String CR_BLOB = "clientregistry.blob";
    final static int SDK_APPID = 211;
    final static String STEAM_APPS_FOLDER = "steamapps";
    static final String ENGINE_BIN = "bin";
    static final String GAMEDATA_PATH = "bin/gameconfig.txt";
    static final String BSPZIP_FILENAME = "bspzip.exe";
    static final String STEAM_APP_DATA = "config/SteamAppData.vdf";

    public enum Phase {

        /**
         * Done: Nothing.
         * Require: Steam directory path
         */
        STEAM,
        /**
         * Done: Parsed steam settings, found current user and SDK dir, parsed gameconfig
         * Require: Engine and DetectedGame choice
         */
        GAME,
        /**
         * Done: Loaded FGD data and game-specific paths
         * Require: Source BSP
         */
        SOURCE,
        /**
         * Done: Set up for pre-crawling phase
         * Require: Callback for crawling effort
         */
        CRAWL,
        /**
         * Done: Done: Crawling and dependency detection, packing listt finalized
         * Require: Destination BSP or list output
         */
        PACK,
        /**
         * Done: Packed new BSP or exported list
         * Require: Exit or return to either GAME or SOURCE step
         */
        FINAL,;

        private boolean isAfter(Phase phase) {
            //TODO make this not dependent on declaration order
            return (this.ordinal() > phase.ordinal());
        }
    }
    private static final Logger logger = LoggerFactory.getLogger(AppModel.class);
    Phase currentPhase = Phase.STEAM;
    File _steamDirectory;
    CdrParser _cdr;
    MapIncludes _includeConf;
    Set<IncludeItem> _includes;
    ClientRegistry _reg;
    File _sourceBsp;
    File _sourceCopy;
    List<Devkit> _kits;
    DetectedGame _chosenGame;
    GameInfoReader _gameInfoData;
    DependencyGraph _graph;
    Map<String, DependencyItem> _deps;

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    protected void assertPhase(Phase val) {
        if (currentPhase != val) {
            throw new IllegalStateException("Model was in phase " + currentPhase + " but the function called is only valid for " + val);
        }
    }

    protected void assertPhaseAfter(Phase val) {
        if (!currentPhase.isAfter(val)) {
            throw new IllegalStateException("Model was in phase " + currentPhase + " but the function called is only valid for states following " + val);
        }
    }

    protected void assertPhaseSameOrAfter(Phase val) {
        if ((currentPhase != val) && (!currentPhase.isAfter(val))) {
            throw new IllegalStateException("Model was in phase " + currentPhase + " but the function called is only valid for states including or following " + val);
        }
    }

    static File createTempCopy(File source) throws IOException {
        String ext = FilenameUtils.getExtension(source.getName());
        File dest = File.createTempFile("packbsp_temp_", "." + ext);
        dest.deleteOnExit();
        FileInputStream fis = new FileInputStream(source);
        FileOutputStream fos = new FileOutputStream(dest);
        IOUtils.copy(fis, fos);
        fis.close();
        fos.close();
        return dest;
    }
    
    /**
     *
     * @param dir
     * @throws IllegalArgumentException If arguments are incorrect
     * @throws IllegalStateException If called during the wrong phase
     * @throws PackbspException If another error occurs. May or may no be related to bad input.
     */
    public void acceptSteamDirectory(final File steamDir) throws IllegalArgumentException, IllegalStateException, PackbspException {
        assertPhaseSameOrAfter(Phase.STEAM);
        if (!steamDir.isDirectory()) {
            throw new IllegalArgumentException("The given directory does not exist");
        }
        final File originalBlob = new File(steamDir, CR_BLOB);
        if (!originalBlob.isFile()) {
            throw new IllegalArgumentException("Required file " + CR_BLOB + " could not be found.");
        }

        logger.info("Creating temporary copy of registry blob to avoid read/write conflicts.");
        final File blobfile;
        try {
            blobfile = createTempCopy(originalBlob);
        } catch (IOException ex) {
            throw new PackbspException("Could not create temporary copy of client registry blob. Pause any game-downloads or wait for Steam to finish updating and try again.", ex);
        }

        logger.info("Attempting to parse client registry blob at: {}", blobfile);
        final ClientRegistry reg;
        try {
            BlobFolder bf = RegParser.parseClientRegistry(ParseUtil.mapFile(blobfile));
            reg = new ClientRegistry(bf);
        } catch (IOException ex) {
            throw new PackbspException("Could not access client registry blob (" + ex.getMessage() + ")", ex);
        } catch (BlobParseFailure ex) {
            throw new PackbspException("Could not parse the data within client registry blob", ex);
        }

        final CdrParser cdr = reg.getContentDescriptionRecord();
        File steamAppData = new File(steamDir, STEAM_APP_DATA);

        final String currentUser;
        try {
            CharStream ais = new ANTLRFileStream(steamAppData.getAbsolutePath());
            ValveTokenLexer lexer = new ValveTokenLexer(ais);
            SloppyParser parser = new SloppyParser(new CommonTokenStream(lexer));
            VdfRoot metaData = parser.main();
            SteamMetaReader smr = new SteamMetaReader(metaData);
            currentUser = smr.getAutoLogon();
            if ("".equals(currentUser)) {
                throw new PackbspException("Is Steam running? Could not read current Steam username from: " + steamAppData.getAbsolutePath());
            }
            logger.info("Username detected as: {}", currentUser);
        } catch (IOException ex) {
            throw new PackbspException("Is Steam running? Could not read current Steam username from: " + steamAppData.getAbsolutePath(), ex);
        } catch (RecognitionException ex) {
            throw new PackbspException("Is Steam running? Could not determine current Steam username from: " + steamAppData.getAbsolutePath(), ex);
        }


        /**
         * Init supported development kits
         */
        List<Devkit> kits = new ArrayList<Devkit>();
         /*
        for (Engine e : SourceSDK.Engine.values()) {
            try {
                kits.add(SourceSDK.createKit(e, steamDir, reg, currentUser));
            }
            catch (GameConfException ex) {
                logger.error("Problem instantiating SDK",ex);
            }
        }
          * 
          */


        currentPhase = Phase.GAME;
        // On success, save everything
        _kits = kits;
        _reg = reg;
        _cdr = cdr;
        _steamDirectory = steamDir;
    }

    public List<Devkit> getKits() {
        List<Devkit> copy = new ArrayList<Devkit>();
        copy.addAll(_kits);
        return copy;
    }

    public void acceptGame(final DetectedGame chosen) throws PackbspException, IllegalArgumentException {
        assertPhaseSameOrAfter(Phase.GAME);

        if (chosen == null) {
            throw new IllegalArgumentException("No game was selected");
        }


        // Find and load gameinfo file
        final File gameInfoPath = new File(chosen.getGameDir(), GameInfoReader.DEFAULT_FILENAME);
        logger.info("Gameinfo file at: {}", gameInfoPath.getAbsolutePath());

        if (!gameInfoPath.isFile()) {
            throw new PackbspException("Game info not found at: " + gameInfoPath.getAbsolutePath());
        }

        final GameInfoReader gameInfoData;
        try {
            ANTLRInputStream ais = new ANTLRInputStream(new FileInputStream(gameInfoPath));
            ValveTokenLexer lexer = new ValveTokenLexer(ais);
            SloppyParser parser = new SloppyParser(new CommonTokenStream(lexer));
            VdfRoot n = parser.main();
            gameInfoData = new GameInfoReader(n, gameInfoPath);
        } catch (RecognitionException ex) {
            throw new PackbspException("Could not parse Gameinfo file", ex);
        } catch (FileNotFoundException ex) {
            throw new PackbspException("Could not read Gameinfo file", ex);
        } catch (IOException ex) {
            throw new PackbspException("Could not read Gameinfo file", ex);
        }

        _includeConf = null;
        _includes = new HashSet<IncludeItem>();
        try {
            _includeConf = MapIncludes.fromXml(new File("conf/map_includes.xml"));
            _includes = _includeConf.getItems(chosen);
        } catch (JAXBException ex) {
            throw new PackbspException("Could not read map-includes configuration file", ex);
        } catch (FileNotFoundException ex) {
            throw new PackbspException("Could not read map-includes configuration file", ex);
        }

        currentPhase = Phase.SOURCE;
        _chosenGame = chosen;
        _gameInfoData = gameInfoData;
    }

    public File getSourceDir() {
        assertPhaseAfter(Phase.GAME);
        return _chosenGame.getVmfDir();
    }

    public File getBspDir() {
        assertPhaseAfter(Phase.GAME);
        return _chosenGame.getBspDir();
    }

    public File getGameDir() {
        assertPhaseAfter(Phase.GAME);
        return _chosenGame.getGameDir();

    }

    public void acceptSourceFile(final File source) throws PackbspException {
        assertPhaseSameOrAfter(Phase.SOURCE);
        if (!source.isFile()) {
            throw new IllegalArgumentException("Specified source file does not exist");
        }
        logger.info("Source BSP specified as: {}", source.getAbsolutePath());


        File tempCopy;
        try {
            tempCopy = createTempCopy(source);
        } catch (IOException ex) {
            throw new PackbspException("Unable to create a temporary copy of the source file for secure reading", ex);
        }

        _sourceBsp = source;
        _sourceCopy = tempCopy;
        currentPhase = Phase.CRAWL;
    }

    public File getSourceFile() {
        assertPhaseAfter(Phase.SOURCE);
        return _sourceBsp;
    }

    public void acceptCrawling(final CrawlListener crawlingListener, final GraphListener graphListener) throws PackbspException {
        assertPhaseSameOrAfter(Phase.CRAWL);

        final ManagedLibrary hllib;
        final AssetLocator locator;
        final DependencyGraph graph;
        final FgdSpec spec;
        final MapNode startMapNode;
        final DependencyExpander expander;
        final DependencyManager depManager = new DependencyManager();
        // We keep this mapping because we need to transfer "required or optional"
        // status from the GraphContext--which has no concept of paths--to the
        // Dependencies, which have no concept of nodes.
        final Map<String, Node> nodeMapping = new HashMap<String, Node>();


        // Load HlLib DLL
        try {
            hllib = HlLib.getLibrary();
        } catch (UnsatisfiedLinkError ule) {
            throw new PackbspException("Unable to load HlLib.dll", ule);
        }

        // Make locator
        
        BasicLocator baseLocator = new BasicLocator(_gameInfoData, new File(_steamDirectory, "steamapps"), _reg, hllib);
        MapFirstLocator realLocator;
        try {
            realLocator = new MapFirstLocator(baseLocator, hllib, _sourceCopy);
        }catch (ArchiveIOException ex) {
            throw new PackbspException("Could not open source map file to see packed contentts",ex);
        }
        locator = new CachingLocator(realLocator);

        // Make FGD spec
        //TODO determine if gamedata0 overrides gamedata1 or vice-versa in gameconfig. Assuming later entries override former

        spec = new FgdSpec();
        try {
            for (File fgd : _chosenGame.getFgdFiles()) {
                logger.info("Parsing FGD at: {}", fgd.getAbsolutePath());
                DefaultLoader.fillSpec(fgd, spec);
            }
        } catch (IOException ex) {
            throw new PackbspException("Could not access FGD data", ex);
        } catch (RecognitionException ex) {
            throw new PackbspException("Could not interpret FGD data", ex);
        }

        // This object will listen to crawling activity and handle the storage
        // of which items are optional/required/present/missing/skipped
        final EmptyCrawlListener mainListener = new EmptyCrawlListener() {

            @Override
            public void nodeContentMissing(Edge edge, Node node, String filePath) {
                depManager.setMissing(filePath);
                nodeMapping.put(filePath, node);
            }

            @Override
            public void nodeContentSkipped(Edge edge, Node node, String filePath) {
                //Query locator and figure out if it's in the BSP or just stock
                boolean inBsp = false;
                List<AssetHit> hits = locator.locate(filePath);
                for (AssetHit h : hits) {
                    if (Type.BSP.equals(h.getSource().getType())) {
                        inBsp = true;
                        break;
                    }
                }
                if (inBsp) {
                    depManager.setPrePacked(filePath);
                } else {
                    depManager.setStock(filePath);
                }
                nodeMapping.put(filePath, node);

            }

            @Override
            public void nodePackingFound(Edge edge, Node node, String filePath, File dataSource) {
                depManager.setFound(filePath, dataSource);
                nodeMapping.put(filePath, node);
            }
        };

        graph = new DependencyGraph();
        startMapNode = new MapNode(_sourceCopy, _sourceBsp.getName(), spec, _includes);
        graph.addVertex(startMapNode);

        if (graphListener != null) {
            graphListener.graphCreated(graph);
        }
        expander = new DependencyExpander(graph, startMapNode, locator);
        expander.addListener(mainListener);
        if (crawlingListener != null) {
            expander.addListener(crawlingListener);
        }
        logger.info("Beginning dependency graph traversal");
        while (!expander.isDone()) {
            try {
                expander.step();
            } catch (TraversalException ex) {
                if (ex.isRecoverable()) {
                    logger.warn("Recoverable traversal error: " + ex.getMessage(), ex);
                } else {
                    throw new PackbspException("An error occurred while exploring the dependency graph", ex);
                }
            }
        }

        logger.info("Dependency graph traversal complete");

        // Determine required-vs-optional items and set status appropriately
        assert (expander.isDone());
        Collection<String> paths = depManager.getPaths();
        for (String path : paths) {
            Node n = nodeMapping.get(path);
            if ((n != null) && expander.wasRequired(n)) {
                depManager.setRequired(path, true);
            } else {
                depManager.setRequired(path, false);
            }
        }


        currentPhase = Phase.PACK;
        // On success, save everything
        _graph = graph;
        _deps = depManager.items;
    }

    public DependencyGraph getGraph() {
        assertPhaseAfter(Phase.CRAWL);
        return _graph;
    }

    public Map<String, DependencyItem> getDependencies() {
        assertPhaseAfter(Phase.CRAWL);
        return _deps;
    }

    public static void savePackList(final Map<String, File> items, final File packListFile) throws IOException {

        logger.info("Packing list will be written to: {}", packListFile.getAbsolutePath());
        FileWriter fw = new FileWriter(packListFile, false);
        BspZipController.writePackingList(items, fw);
        fw.close();

    }

    public void acceptPacking(final File packListFile, final File destination) throws PackbspException {
        assertPhaseSameOrAfter(Phase.PACK);
        logger.info("Packing list file at: {}", packListFile.getAbsolutePath());
        logger.info("Final destination path: {}", destination.getAbsolutePath());
        boolean alreadyExists;

        if (destination.isDirectory()) {
            throw new IllegalArgumentException("Destination file is a directory");
        }
        try {
            alreadyExists = !destination.createNewFile();
        } catch (IOException ex) {
            throw new PackbspException("Unable to create destination BSP file", ex);
        }
        logger.info("Does destination file already exist: {}", alreadyExists);


        // If it already exists, move it to a name with a backup
        if (alreadyExists) {

            // If foo.bak exists, try foo.bak1, if foo.bak1 exists, try foo.bak2, etc.
            int n = 1;
            String baseBakName = destination.getAbsolutePath();
            File bakCandidate = new File(baseBakName + ".bak");
            while (bakCandidate.exists()) {
                n += 1;
                bakCandidate = new File(baseBakName + ".bak" + n);
                assert (n < 1000000); // Should never happen in normal use, but quickly hit on logic error
            }


            logger.info("Renaming existing destination file to a backup: {}", bakCandidate.getAbsolutePath());
            boolean success = destination.renameTo(bakCandidate);
            if (!success) {
                throw new PackbspException("Could not rename target file to a backup.");
            }
        }

        final File sdkToolsDir = _chosenGame.getKitBinDir();
        final File bspzipExe = new File(sdkToolsDir, "bin/" + BSPZIP_FILENAME);
        final BspZipController bspzip = new BspZipController(bspzipExe);
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        try {
            logger.info("Running bspzip at:", bspzipExe.getAbsolutePath());
            int exitval = bspzip.packAssets(packListFile, _sourceCopy, destination, getGameDir(), resultStream);

            String text = resultStream.toString();
            text = "\t" + text.replace("\n", "\n\t");
            logger.debug("BSPZIP output:\n{}", text);
            if (!text.contains("Writing new bsp file")) {
                if (text.toLowerCase().contains("local steam service is not running")) {
                    throw new PackbspException("BspZip cannot execute: Steam is not running.");
                }
                throw new PackbspException("BspZip does not have expected success text:\n" + text);
            }
        } catch (ExecuteException ex) {
            logger.error("BSPZIP failed with exception.", ex);
            String text = resultStream.toString();
            text = "\t" + text.replace("\n", "\n\t");
            if (text.trim().length() > 0) {
                logger.error("BSPZIP failed with text:\n{}", text);
            }
            throw new PackbspException("An error occured while creating a copy of the map with packed content. Please see logs for more details.", ex);
        }
        logger.info("Packing completed");

        currentPhase = Phase.FINAL;

    }
}
