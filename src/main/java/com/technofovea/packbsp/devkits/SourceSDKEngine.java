package com.technofovea.packbsp.devkits;

import com.technofovea.hl2parse.vdf.GameConfigReader;
import com.technofovea.hl2parse.vdf.SloppyParser;
import com.technofovea.hl2parse.vdf.ValveTokenLexer;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.jxpath.JXPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class SourceSDKEngine implements GameEngine {

    private static final Logger logger = LoggerFactory.getLogger(SourceSDKEngine.class);

    static final String ENGINE_BIN = "bin";
    static final String GAMEDATA_PATH = "bin" + File.pathSeparator + "gameconfig.txt";
    final SourceSDK parent;
    final String dirName;
    final String displayName;
    final File engineFolder;
    final File gameConf;
    final File binDir;
    List<Game> gameList = null;
    boolean present = false;

    public SourceSDKEngine(SourceSDK parent, String dirName, String displayName) {
        this.parent = parent;
        this.dirName = dirName;
        this.displayName = displayName;

        this.engineFolder = new File(parent.sdkDir, ENGINE_BIN + File.pathSeparator + dirName);
        this.gameConf = new File(engineFolder, GAMEDATA_PATH);
        this.binDir = new File(engineFolder,ENGINE_BIN);


    }

    protected Map<String, GameConfigReader.Game> readGames() {
        Map<String, GameConfigReader.Game> readGames = Collections.emptyMap();
        if (!engineFolder.isDirectory()) {
            logger.warn("Cannot find folder for engine '" + displayName + "'. Please ensure your Source SDK is up-to-date and working.");
        } else if (!gameConf.isFile()) {
            logger.error("Could not find engine gameconfig at: {}", gameConf);
        } else {
            present = true;
            logger.debug("Checking for games defined in in {}", gameConf);

            try {
                // Parse each gameconfig.txt file
                ANTLRFileStream afs = new ANTLRFileStream(gameConf.getAbsolutePath());
                ValveTokenLexer lexer = new ValveTokenLexer(afs);
                SloppyParser parser = new SloppyParser(new CommonTokenStream(lexer));
                VdfRoot root = parser.main();
                GameConfigReader greader = new GameConfigReader(root);
                readGames = greader.getGames();
            } catch (IOException ex) {
                logger.error("Unable to access SDK's game-config file: " + gameConf, ex);
            } catch (RecognitionException rex) {
                logger.error("Unable to parse SDK's game-config file: " + gameConf, rex);
            } catch (JXPathException jex) {
                logger.error("Unable to parse SDK's game-config file: " + gameConf, jex);
            }
        }
        return readGames;
    }

    public Devkit getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public boolean isPresent() {
        return present;
    }

    public List<Game> getGames() throws UnsupportedOperationException {
        synchronized (this) {
            if (gameList == null) {
                gameList = new ArrayList<Game>();
                for (GameConfigReader.Game data : readGames().values()) {
                    DefaultGameImpl gameObj = new DefaultGameImpl(this, data);
                    gameList.add(gameObj);
                }
            }
        }
        return Collections.unmodifiableList(gameList);
    }

    public File getBinDir() {
        return binDir;
    }

    public String getId() {
        //TODO double-check appropriate value
        return this.dirName;
    }

}
