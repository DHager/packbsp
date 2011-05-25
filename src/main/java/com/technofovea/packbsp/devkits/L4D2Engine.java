/*
 * 
 */
package com.technofovea.packbsp.devkits;

import com.technofovea.hl2parse.vdf.GameConfigReader;
import com.technofovea.hl2parse.vdf.GameConfigReader;
import com.technofovea.hl2parse.vdf.SloppyParser;
import com.technofovea.hl2parse.vdf.ValveTokenLexer;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
public class L4D2Engine implements GameEngine {

    private static final Logger logger = LoggerFactory.getLogger(L4D2Engine.class);
    static final String ENGINE_ID = "l4d2";
    final L4D2Kit parent;
    final File binDir;
    final File gameConfig;
    
    GameConfigReader conf;
    L4D2Game soleGame;

    protected L4D2Engine(L4D2Kit parent) {
        this.parent = parent;
        soleGame = new L4D2Game(this);
        binDir = parent.binDir;
        gameConfig = new File(parent.baseDir, "gameconfig.txt");
    }

    public File getBinDir() {
        return binDir;
    }

    public List<Game> getGames() throws GameConfigurationException {
        if (!isPresent()) {
            throw new GameConfigurationException("Game-engine is not present");
        }
        synchronized (this) {
            if (soleGame == null) {
                loadConfig();
                soleGame = new L4D2Game(this);
            }else{
                assert(gameConfig != null);
            }
        }
        List<Game> ret = new ArrayList<Game>();
        ret.add(soleGame);
        return ret;
    }

    public String getId() {
        return ENGINE_ID;
    }

    public Devkit getParent() {
        return parent;
    }

    public boolean isPresent() {
        return gameConfig.isFile();

    }

    protected void loadConfig() throws GameConfigurationException {
        try {
            // Parse each gameconfig.txt file
            ANTLRFileStream afs = new ANTLRFileStream(gameConfig.getAbsolutePath());
            ValveTokenLexer lexer = new ValveTokenLexer(afs);
            SloppyParser parser = new SloppyParser(new CommonTokenStream(lexer));
            VdfRoot root = parser.main();
            conf = new GameConfigReader(root);
        } catch (IOException ex) {
            throw new GameConfigurationException("Unable to read GameConfig",ex);
        } catch (RecognitionException rex) {
            throw new GameConfigurationException("Unable to read GameConfig",rex);
        } catch (JXPathException jex) {
            throw new GameConfigurationException("Unable to read GameConfig",jex);
        }
    }
}