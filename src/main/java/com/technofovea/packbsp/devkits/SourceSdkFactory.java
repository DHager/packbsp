/*
 * 
 */
package com.technofovea.packbsp.devkits;

import com.technofovea.hl2parse.registry.BlobParseFailure;
import com.technofovea.hl2parse.registry.CdrParser;
import com.technofovea.hl2parse.registry.ClientRegistry;
import com.technofovea.hl2parse.vdf.GameConfigReader;
import com.technofovea.hl2parse.vdf.GameInfoReader;
import com.technofovea.hl2parse.vdf.SloppyParser;
import com.technofovea.hl2parse.vdf.ValveTokenLexer;
import com.technofovea.hl2parse.vdf.VdfRoot;
import com.technofovea.packbsp.spring.AbstractPackbspComponent;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.jxpath.JXPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 *
 * @author Darien Hager
 */
public class SourceSdkFactory extends AbstractPackbspComponent implements KitFactory<SourceSDK, DefaultGameImpl> {

    private static final Logger logger = LoggerFactory.getLogger(SourceSdkFactory.class);
    static final int APPID = 211;
    static final String STEAM_APPS_FOLDER = "steamapps";
    static final String ROOT_BIN = "bin";
    static final String GAMECONFIG_NAME = "gameconfig.txt";
    static final String ENGINE_BIN = "bin";

    protected enum Engine {

        ORANGEBOX("Source Engine 2009", "orangebox"),
        EP2("Source Engine 2007", "source2007"),
        EP1("Source Engine 2006", "ep1"),;
        final String displayName;
        final String dirName;

        private Engine(String displayName, String dirName) {
            this.displayName = displayName;
            this.dirName = dirName;
        }
    }
    protected File steamDir;
    protected ClientRegistry registry;
    protected String currentUser;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(steamDir);
        Assert.notNull(registry);
        Assert.notNull(currentUser);
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public ClientRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(ClientRegistry registry) {
        this.registry = registry;
    }

    

    public File getSteamDir() {
        return steamDir;
    }

    public void setSteamDir(File steamDir) {
        this.steamDir = steamDir;
    }
    
    

    public Set<SourceSDK> createKits(GameErrorListener listener) {
        CdrParser cdr = registry.getContentDescriptionRecord();
        Set<SourceSDK> kits = new HashSet<SourceSDK>();
        for (Engine subEngine : Engine.values()) {
            logger.debug("Instantiating SDK for sub-engine {}", subEngine.displayName);
            logger.debug("Trying to retrieve SDK directory name from app-id");
            final File appDir;
            try {
                String appName = cdr.getAppFolderName(APPID);
                appDir = new File(steamDir,
                        STEAM_APPS_FOLDER + File.separator
                        + currentUser + File.separator
                        + appName);

            }
            catch (BlobParseFailure ex) {
                GameConfException gcex = localizer.localize(new GameConfException("Unable to parse blob", ex), "error.cant_parse_registry");
                listener.devkitInitError(this, gcex);
                continue;
            }

            File engineDir = new File(appDir, ROOT_BIN + File.separator + subEngine.dirName);
            File engineBinDir = new File(engineDir, ENGINE_BIN);
            logger.debug("Source SDK dir detected as {}", appDir);
            logger.debug("Engine Dir: {}", engineDir);
            logger.debug("Engine 'bin' Dir: {}", engineBinDir);


            try {
                SourceSDK k = createKit(subEngine, engineDir, engineBinDir);
                kits.add(k);
            }
            catch (GameConfException e) {
                listener.devkitInitError(this, e);
            }
        }
        return kits;
    }

    protected SourceSDK createKit(Engine subEngine, File engineDir, File engineBinDir) throws GameConfException {
        File gameDataPath = new File(engineBinDir, GAMECONFIG_NAME);
        SourceSDK k = new SourceSDK();
        k.title += ": " + subEngine.displayName;
        k.id += "_" + subEngine.dirName;
        k.sdkDir = engineDir;
        k.binDir = engineBinDir;


        if (!engineBinDir.isDirectory()) {
            throw localizer.localize(new GameConfException("Bin dir missing"), "error.sourcesdk.bad_bin_dir", subEngine.displayName, engineBinDir);
        } else if (!gameDataPath.isFile()) {
            throw localizer.localize(new GameConfException("Gameconfig file missing"), "error.sourcesdk.bad_gameconfig", subEngine.displayName, gameDataPath);
        }

        try {

            // Parse each gameconfig.txt file
            logger.debug("Checking for games defined in in {}", gameDataPath);
            ANTLRFileStream afs = new ANTLRFileStream(gameDataPath.getAbsolutePath());
            ValveTokenLexer lexer = new ValveTokenLexer(afs);
            SloppyParser parser = new SloppyParser(new CommonTokenStream(lexer));
            VdfRoot root = parser.main();
            GameConfigReader greader = new GameConfigReader(root);
            for (GameConfigReader.Game g : greader.getGames()) {
                k.gameConfigSections.add(g);
            }
        }
        catch (IOException ex) {
            throw localizer.localize(new GameConfException("Unable to read SDK's game-config file", ex), "error.sourcesdk.cant_read_gameconfig", subEngine.displayName, gameDataPath);
        }
        catch (RecognitionException ex) {
            throw localizer.localize(new GameConfException("Unable to read SDK's game-config file", ex), "error.sourcesdk.cant_read_gameconfig", subEngine.displayName, gameDataPath);
        }
        catch (JXPathException ex) {
            throw localizer.localize(new GameConfException("Unable to read SDK's game-config file", ex), "error.sourcesdk.cant_read_gameconfig", subEngine.displayName, gameDataPath);
        }
        return k;
    }

    public Set<DefaultGameImpl> createDetectedGames(SourceSDK kit, GameErrorListener listener) {
        Set<DefaultGameImpl> games = new HashSet<DefaultGameImpl>();

        for (GameConfigReader.Game section : kit.gameConfigSections) {
            try {
                DefaultGameImpl g = createDetectedGame(kit, section);
                games.add(g);
            }
            catch (GameConfException ex) {
                listener.gameInitError(this, kit, ex);
            }
        }
        return games;
    }

    protected DefaultGameImpl createDetectedGame(SourceSDK kit, GameConfigReader.Game section) throws GameConfException {
        DefaultGameImpl g = new DefaultGameImpl();
        g.parent = kit;
        g.gcData = section;
        g.infoFile = new File(section.getGameDir(), GameInfoReader.DEFAULT_FILENAME);
        g.engineBinDir = kit.binDir;
        
        try {
            logger.debug("Loading gameinfo: {}", g.infoFile);
            // Parse each gameinfo.txt file
            ANTLRFileStream afs = new ANTLRFileStream(g.infoFile.getAbsolutePath());
            ValveTokenLexer lexer = new ValveTokenLexer(afs);
            SloppyParser parser = new SloppyParser(new CommonTokenStream(lexer));
            VdfRoot root = parser.main();
            g.giReader = new GameInfoReader(root, g.infoFile);
        }
        catch (IOException ex) {
            throw localizer.localize(new GameConfException("Unable to read game-info file", ex), "error.sourcesdk.cant_read_gameinfo", g.getName(), g.infoFile);
        }
        catch (RecognitionException ex) {
            throw localizer.localize(new GameConfException("Unable to read game-info file", ex), "error.sourcesdk.cant_read_gameinfo", g.getName(), g.infoFile);
        }
        catch (JXPathException ex) {
            throw localizer.localize(new GameConfException("Unable to read game-info file", ex), "error.sourcesdk.cant_read_gameinfo", g.getName(), g.infoFile);
        }
        return g;
    }
}
