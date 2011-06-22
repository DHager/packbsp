/*
 * 
 */
package com.technofovea.packbsp.devkits;

import com.technofovea.hl2parse.registry.BlobParseFailure;
import com.technofovea.hl2parse.registry.CdrParser;
import com.technofovea.hl2parse.registry.ClientRegistry;
import com.technofovea.hl2parse.vdf.GameConfigReader;
import com.technofovea.hl2parse.vdf.SloppyParser;
import com.technofovea.hl2parse.vdf.ValveTokenLexer;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.jxpath.JXPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Source SDK is a bit special: It is actually three different parallel 
 * SDKs bundled together grouped by "engines". Because of that, these are
 * instantiated as a group through a factory method.
 * @author Darien Hager
 */
public class SourceSDK implements Devkit {

    private static final Logger logger = LoggerFactory.getLogger(SourceSDK.class);
    static final int APPID = 211;
    static final String STEAM_APPS_FOLDER = "steamapps";
    static final String ROOT_BIN = "bin";
    static final String GAMECONFIG_NAME = "gameconfig.txt";
    static final String ENGINE_BIN = "bin";

    public enum Engines {

        ORANGEBOX("Source Engine 2009", "orangebox"),
        EP2("Source Engine 2007", "source2007"),
        EP1("Source Engine 2006", "ep1"),;
        final String displayName;
        final String dirName;

        private Engines(String displayName, String dirName) {
            this.displayName = displayName;
            this.dirName = dirName;
        }
    }

    protected static File getAppFolder(File steamDir, CdrParser cdr, String currentUser, int appid) throws BlobParseFailure {
        String appName = cdr.getAppFolderName(appid);
        File appDir = new File(steamDir,
                STEAM_APPS_FOLDER + File.separator
                + currentUser + File.separator
                + appName);

        return appDir;
    }

    public static SourceSDK createKit(Engines target, File steamDir, ClientRegistry reg, String currentUser) throws GameConfException {
        logger.debug("Instantiating SDK for sub-engine {}",target.displayName);
        logger.debug("Trying to retrieve SDK directory name from app-id");
        File appDir;
        try {
            appDir = getAppFolder(steamDir, reg.getContentDescriptionRecord(), currentUser, APPID);
        }
        catch (BlobParseFailure ex) {
            throw GameConfException.create("Unable to parse blob", ex, "error.cant_parse_registry");
        }
        File engineDir = new File(appDir, ROOT_BIN + File.separator + target.dirName);
        File engineBinDir = new File(engineDir, ENGINE_BIN);
        logger.debug("Source SDK dir detected as {}", appDir);
        logger.debug("Engine Dir: {}", engineDir);
        logger.debug("Engine 'bin' Dir: {}", engineBinDir);

        return new SourceSDK(target, engineDir, engineBinDir);

    }
    final File sdkDir;
    final File binDir;
    List<Game> games = null;
    String title = "Source SDK";
    String id = "source_sdk";
    File gameDataPath;
    final Map<String, GameConfigReader.Game> listedGames = new HashMap<String, GameConfigReader.Game>();
    final List<String> listKeys = new ArrayList<String>();
    final Map<String, Game> loadedGames = new HashMap<String, Game>();

    protected SourceSDK(Engines e, File engineDir, File engineBinDir) throws GameConfException {
        title += ": " + e.displayName;
        id += "_" + e.dirName;
        sdkDir = engineDir;
        binDir = engineBinDir;
        gameDataPath = new File(binDir, GAMECONFIG_NAME);


        if (!binDir.isDirectory()) {
            throw GameConfException.create("Bin dir missing", "error.sourcesdk.bad_bin_dir", e.displayName, binDir);
        } else if (!gameDataPath.isFile()) {
            throw GameConfException.create("Gameconfig file missing", "error.sourcesdk.bad_gameconfig", e.displayName, binDir);
        }
        logger.debug("Checking for games defined in in {}", gameDataPath);
        try {
            // Parse each gameconfig.txt file
            ANTLRFileStream afs = new ANTLRFileStream(gameDataPath.getAbsolutePath());
            ValveTokenLexer lexer = new ValveTokenLexer(afs);
            SloppyParser parser = new SloppyParser(new CommonTokenStream(lexer));
            VdfRoot root = parser.main();
            GameConfigReader greader = new GameConfigReader(root);
            listedGames.putAll(greader.getGames());
            listKeys.clear();
            listKeys.addAll(listedGames.keySet());
        }
        catch (IOException ex) {
            throw GameConfException.create("Unable to read SDK's game-config file", ex, "error.sourcesdk.cant_read_gameconfig", e.displayName, gameDataPath);
        }
        catch (RecognitionException ex) {
            throw GameConfException.create("Unable to read SDK's game-config file", ex, "error.sourcesdk.cant_read_gameconfig", e.displayName, gameDataPath);
        }
        catch (JXPathException ex) {
            throw GameConfException.create("Unable to read SDK's game-config file", ex, "error.sourcesdk.cant_read_gameconfig", e.displayName, gameDataPath);
        }


    }

    public File getBinDir() {
        return binDir;
    }

    @Override
    public String toString() {
        return title;
    }

    public Game getGame(String gameKey) throws GameConfException {
        synchronized (loadedGames) {
            Game cached = loadedGames.get(gameKey);
            if (cached != null) {
                return cached;
            }

            GameConfigReader.Game v = listedGames.get(gameKey);
            if (v == null) {
                return null;
            }
            DefaultGameImpl gameObj = new DefaultGameImpl(this, v);
            loadedGames.put(gameKey, gameObj);
            return gameObj;
        }
    }

    public List<String> getGameKeys() {
        return Collections.unmodifiableList(listKeys);
    }

    public String getId() {
        return id;
    }
}
