/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.hl2parse.vdf.GameInfoReader;
import com.technofovea.hl2parse.vdf.SloppyParser;
import com.technofovea.hl2parse.vdf.ValveTokenLexer;
import com.technofovea.hl2parse.vdf.VdfRoot;
import com.technofovea.packbsp.devkits.DetectedGame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 *
 * @author Darien Hager
 */
public class GameInfoLoader implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(GameInfoReader.class);
    public static final String GAME_INFO_PATH = GameInfoReader.DEFAULT_FILENAME;
    
    protected DetectedGame game;
    protected String filePath = GAME_INFO_PATH;

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(game);
        Assert.notNull(filePath);
        Assert.hasText(filePath);
    }
    
    
    
    public GameInfoData load() throws PhaseFailedException{
        final File src = new File(game.getGameDir(), filePath);
        logger.info("Loading file at: {}", src.getAbsolutePath());

        if (!src.isFile()) {
            throw new PhaseFailedException("Game info not found at: " + src.getAbsolutePath()); //TODO:I18N
        }

        final GameInfoReader loaded;
        try {
            ANTLRInputStream ais = new ANTLRInputStream(new FileInputStream(src));
            ValveTokenLexer lexer = new ValveTokenLexer(ais);
            SloppyParser parser = new SloppyParser(new CommonTokenStream(lexer));
            VdfRoot n = parser.main();
            loaded = new GameInfoReader(n, src);
        } catch (RecognitionException ex) {
            throw new PhaseFailedException("Could not parse Gameinfo file", ex); //TODO:I18N
        } catch (FileNotFoundException ex) {
            throw new PhaseFailedException("Could not read Gameinfo file", ex); //TODO:I18N
        } catch (IOException ex) {
            throw new PhaseFailedException("Could not read Gameinfo file", ex); //TODO:I18N
        }
        
        // Move loaded data into hl2parse-independent storage class
        GameInfoDataImpl ret = GameInfoDataImpl.createFromReader(loaded);
        return ret;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public DetectedGame getGame() {
        return game;
    }

    public void setGame(DetectedGame game) {
        this.game = game;
    }
    
    
    
}
