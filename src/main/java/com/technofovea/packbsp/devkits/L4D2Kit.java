/*
 * s
 */
package com.technofovea.packbsp.devkits;

import com.technofovea.hl2parse.vdf.GameConfigReader;
import com.technofovea.hl2parse.vdf.SloppyParser;
import com.technofovea.hl2parse.vdf.ValveTokenLexer;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.io.File;
import java.io.IOException;
import java.util.List;
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
public class L4D2Kit implements Devkit {

    private static final Logger logger = LoggerFactory.getLogger(L4D2Kit.class);
    static final String DEVKIT_ID = "l4d2_authoring_tools";
    static final String TITLE = "Left 4 Dead 2 Authoring Tools";
    static final String GAMECONFIG_NAME = "gameconfig.txt";
    static final String BIN_DIR = "bin";
    static final String L4D2_SUBDIR = "steamapps/common/left 4 dead 2/";

    public static L4D2Kit createKit(File steamDir) throws GameConfException {
        logger.debug("Instantiating Left 4 Dead 2 Authoring Tools");
        // Ex:         //C:\Program Files\Steam\steamapps\common\left 4 dead 2\bin
        File deadDir = new File(steamDir, L4D2_SUBDIR);
        L4D2Kit ret = new L4D2Kit(deadDir);
        return ret;
    }
    File baseDir;
    File binDir;
    File gameConfig;
    GameConfigReader conf;

    private L4D2Kit(File deadDir) throws GameConfException {
        baseDir = deadDir;
        binDir = new File(deadDir, BIN_DIR);
        gameConfig = new File(baseDir, GAMECONFIG_NAME);
        
        if (!binDir.isDirectory()) {
            throw GameConfException.create("Bin dir missing", "error.l4d2sdk.bad_bin_dir", binDir);
        } else if (!gameConfig.isFile()) {
            throw GameConfException.create("Gameconfig file missing", "error.l4d2sdk.bad_gameconfig", binDir);
        }

        try {
            // Parse each gameconfig.txt file
            ANTLRFileStream afs = new ANTLRFileStream(gameConfig.getAbsolutePath());
            ValveTokenLexer lexer = new ValveTokenLexer(afs);
            SloppyParser parser = new SloppyParser(new CommonTokenStream(lexer));
            VdfRoot root = parser.main();
            conf = new GameConfigReader(root);
        }
        catch (IOException ex) {
            throw GameConfException.create("Unable to read SDK's game-config file", ex, "error.l4d2sdk.cant_read_gameconfig", gameConfig);
        }
        catch (RecognitionException ex) {
            throw GameConfException.create("Unable to read SDK's game-config file", ex, "error.l4d2sdk.cant_read_gameconfig", gameConfig);
        }
        catch (JXPathException ex) {
            throw GameConfException.create("Unable to read SDK's game-config file", ex, "error.l4d2sdk.cant_read_gameconfig", gameConfig);
        }
    }

    @Override
    public String toString() {
        return TITLE;
    }

    public String getId() {
        return DEVKIT_ID;
    }

    public File getBinDir() {
        return binDir;
    }

    public List<String> getGameKeys() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Game getGame(Object gameKey) throws GameConfException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getGameName(Object gameKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}