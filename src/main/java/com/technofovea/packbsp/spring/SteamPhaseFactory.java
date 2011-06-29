/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.hl2parse.ParseUtil;
import com.technofovea.hl2parse.registry.BlobFolder;
import com.technofovea.hl2parse.registry.BlobParseFailure;
import com.technofovea.hl2parse.registry.ClientRegistry;
import com.technofovea.hl2parse.registry.RegParser;
import com.technofovea.hl2parse.vdf.SloppyParser;
import com.technofovea.hl2parse.vdf.SteamMetaReader;
import com.technofovea.hl2parse.vdf.ValveTokenLexer;
import com.technofovea.hl2parse.vdf.VdfRoot;
import com.technofovea.packbsp.WindowsRegistryChecker;
import com.technofovea.packbsp.devkits.Devkit;
import com.technofovea.packbsp.devkits.Game;
import com.technofovea.packbsp.devkits.GameConfException;
import com.technofovea.packbsp.devkits.SourceSDK;
import com.technofovea.packbsp.devkits.SourceSDK.Engines;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * @author Darien Hager
 */
public class SteamPhaseFactory implements InitializingBean {

    public static interface SteamPhase {

        public File getSteamDir();

        public ClientRegistry getRegistry();

        public String getCurrentUser();

        public Map<Devkit, List<Game>> getGames();

        public boolean containsGame(Game chosenGame);
    }

    protected static class SteamPhaseImpl implements SteamPhase {

        final File steamDir;
        final ClientRegistry registry;
        final String currentUser;
        final Map<Devkit, List<Game>> games;

        public SteamPhaseImpl(File steamDir, ClientRegistry registry, String currentUser, Map<Devkit, List<Game>> games) {
            this.steamDir = steamDir;
            this.registry = registry;
            this.currentUser = currentUser;
            // Make deeply immutable copy
            HashMap<Devkit, List<Game>> tempMap = new HashMap<Devkit, List<Game>>();
            for (Devkit k : games.keySet()) {
                tempMap.put(k, Collections.unmodifiableList(games.get(k)));
            }
            this.games = Collections.unmodifiableMap(tempMap);
        }

        public File getSteamDir() {
            return steamDir;
        }

        public ClientRegistry getRegistry() {
            return registry;
        }

        public String getCurrentUser() {
            return currentUser;
        }

        public Map<Devkit, List<Game>> getGames() {
            return games;
        }

        public boolean containsGame(Game chosenGame) {
            for (Devkit k : games.keySet()) {
                if (games.get(k).contains(chosenGame)) {
                    return true;
                }
            }
            return false;
        }
    }
    private static final Logger logger = LoggerFactory.getLogger(SteamPhaseFactory.class);
    NestedScope scope = null;
    File steamDir = null;
    GameErrorListener gameErrorListener = null;

    public SteamPhaseFactory() {
        steamDir = guessSteamDir();
    }

    public NestedScope getScope() {
        return scope;
    }

    public void setScope(NestedScope scope) {
        this.scope = scope;
    }

    public File getSteamDir() {
        return steamDir;
    }

    public void setSteamDir(File steamDir) {
        this.steamDir = steamDir;
    }

    public GameErrorListener getGameErrorListener() {
        return gameErrorListener;
    }

    public void setGameErrorListener(GameErrorListener gameErrorListener) {
        this.gameErrorListener = gameErrorListener;
    }

    public void afterPropertiesSet() throws Exception {
    }

    protected static File createTempCopy(File source) throws IOException {
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

    protected static ClientRegistry parseRegistry(File steamDir) throws PhaseFailedException {
        final String STEAM_BLOB_NAME = "clientregistry.blob";
        final File regFile = new File(steamDir, STEAM_BLOB_NAME);
        if (!regFile.isFile()) {
            throw PhaseFailedException.create("Missing registry blob", "error.input.no_clientregistry_blob", STEAM_BLOB_NAME);
        }

        logger.debug("Copying clientregistry from {}", regFile);
        final File regCopy;
        try {
            regCopy = createTempCopy(regFile);
            logger.debug("Created temporary clientregistry copy at {}", regCopy);

        }
        catch (IOException ex) {
            throw PhaseFailedException.create("Couldn't copy client registry blob", ex, "error.cant_copy_blob");
        }

        logger.debug("Attempting to parse client registry blob", regCopy);
        final ClientRegistry reg;



        try {
            BlobFolder bf = RegParser.parseClientRegistry(ParseUtil.mapFile(regCopy));
            reg = new ClientRegistry(bf);
        }
        catch (IOException ex) {
            throw PhaseFailedException.create("Can't access blob file", ex, "error.cant_read_registry");
        }
        catch (BlobParseFailure ex) {
            throw PhaseFailedException.create("Can't parse blob file", ex, "error.cant_parse_registry");
        }

        return reg;
    }

    protected static String detectCurrentUser(File steamDir) throws PhaseFailedException {
        final File steamAppData = new File(steamDir, "config/SteamAppData.vdf");
        final String currentUser;
        try {
            logger.debug("Reading username from {}", steamAppData);
            CharStream ais = new ANTLRFileStream(steamAppData.getAbsolutePath());
            ValveTokenLexer lexer = new ValveTokenLexer(ais);
            SloppyParser parser = new SloppyParser(new CommonTokenStream(lexer));
            VdfRoot metaData = parser.main();
            SteamMetaReader smr = new SteamMetaReader(metaData);
            currentUser = smr.getAutoLogon();
            if ("".equals(currentUser)) {
                throw PhaseFailedException.create("Could not read username from: " + steamAppData.getAbsolutePath(), "error.steam_not_running", steamAppData.getAbsolutePath());
            }
            logger.debug("Username detected as: {}", currentUser);
        }
        catch (IOException ex) {
            throw PhaseFailedException.create("Could not read username from: " + steamAppData.getAbsolutePath(), ex, "error.steam_not_running", steamAppData.getAbsolutePath());
        }
        catch (RecognitionException ex) {
            throw PhaseFailedException.create("Could not read username from: " + steamAppData.getAbsolutePath(), ex, "error.steam_not_running", steamAppData.getAbsolutePath());
        }
        return currentUser;
    }

    protected static File guessSteamDir() {

        /* 
         * Not efficient to create this over and over, but in this context we 
         * do not expect many repeat invocations, if at all.
         */
        final String STEAM_REGISTRY_PATH = "HKCU\\Software\\Valve\\Steam\\SteamPath";
        final String STEAM_DEFAULT_PATH = "c:/program files/valve/steam/";

        WindowsRegistryChecker wrc = new WindowsRegistryChecker();

        String guess = null;
        try {
            final String key = STEAM_REGISTRY_PATH;
            logger.debug("Trying to load steam dir from registry at {}", key);
            guess = wrc.getKey(key);
        }
        catch (IOException ex) {
            logger.error("Had an exception querying registry for Steam folder: {}", ex);
        }
        if (guess != null) {
            File f = new File(guess);
            if (f.exists()) {
                return ( f );
            } else {
                logger.warn("Found Steam directory from registry, but file location does not exist: {}", guess);
            }
        } else {
            logger.warn("Registry querying for Steam directory did not succeed.");
        }
        return new File(STEAM_DEFAULT_PATH);


    }

    protected List<Devkit> loadKits(final ClientRegistry reg, final String currentUser) {
        final List<Devkit> kits = new ArrayList<Devkit>();
        /*
         * Instantiate Source SDK kits
         */
        for (Engines e : SourceSDK.Engines.values()) {
            try {
                kits.add(SourceSDK.createKit(e, steamDir, reg, currentUser));
            }
            catch (GameConfException ex) {
                logger.warn("Error instantiating Devkit", ex);
                if (gameErrorListener != null) {
                    gameErrorListener.devkitInitError(this, ex);
                }
            }
        }
        /**
         * Instantiate Left 4 Dead 2 kit
         */
        /*
        try {
        kits.add(L4D2Kit.createKit(steamDir));
        }
        catch (GameConfException ex) {
        logger.warn("Error instantiating Devkit", ex);
        if (gameErrorListener != null) {
        gameErrorListener.devkitInitError(this, ex);
        }
        }
         * 
         */
        return kits;
    }

    protected Map<Devkit, List<Game>> loadGames(final List<Devkit> kits) {
        final Map<Devkit, List<Game>> ret = new HashMap<Devkit, List<Game>>();
        for (Devkit kit : kits) {
            List<Game> games = new ArrayList<Game>();
            for (Object gk : kit.getGameKeys()) {
                Game game;
                try {
                    game = kit.getGame(gk);
                    games.add(game);
                }
                catch (GameConfException ex) {
                    gameErrorListener.gameInitError(this, ex);
                }
            }
            ret.put(kit, games);

        }
        return ret;
    }

    public SteamPhase proceed() throws PhaseFailedException {
        SteamPhase ret = null;
        if (steamDir == null || ( !steamDir.isDirectory() )) {
            throw PhaseFailedException.create("Invalid Steam directory", "error.input.invalid_steam_dir", steamDir);
        }

        logger.info("Opening clientregistry from Steam dir {}", steamDir);
        final ClientRegistry reg = parseRegistry(steamDir);
        logger.info("Attempting to detect current user");
        final String currentUser = detectCurrentUser(steamDir);
        logger.info("Loading Devkits");
        final List<Devkit> kits = loadKits(reg, currentUser);
        logger.info("Loading Games from devkits");
        final Map<Devkit, List<Game>> gameTree = loadGames(kits);


        // If creation successful, notify scope to invalidate old item
        ret = new SteamPhaseImpl(steamDir, reg, currentUser, gameTree);
        if (( ret != null ) && ( scope != null )) {
            scope.invalidateScope();
        }
        return ret;

    }
}
