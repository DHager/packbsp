/*
 * 
 */
package com.technofovea.packbsp.devkits;

import com.technofovea.hl2parse.vdf.GameConfigReader;
import com.technofovea.hl2parse.vdf.GameInfoReader;
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
class DefaultGameImpl implements Game {

    private static final Logger logger = LoggerFactory.getLogger(DefaultGameImpl.class);
    final Devkit parent;
    final GameConfigReader.Game gcData;
    final File infoFile;
    final GameInfoReader giReader;

    public DefaultGameImpl(Devkit parent, GameConfigReader.Game gcData) throws GameConfException{
        this.gcData = gcData;
        this.parent = parent;
        infoFile = new File(gcData.getGameDir(), GameInfoReader.DEFAULT_FILENAME);
        
        try {
            logger.debug("Loading gameinfo: {}",infoFile);
            // Parse each gameconfig.txt file
            ANTLRFileStream afs = new ANTLRFileStream(infoFile.getAbsolutePath());
            ValveTokenLexer lexer = new ValveTokenLexer(afs);
            SloppyParser parser = new SloppyParser(new CommonTokenStream(lexer));
            VdfRoot root = parser.main();
            giReader = new GameInfoReader(root,infoFile);    
        }
        catch (IOException ex) {
            throw GameConfException.create("Unable to read game-info file", ex, "error.sourcesdk.cant_read_gameinfo", gcData.getName(),infoFile);
        }
        catch (RecognitionException ex) {
            throw GameConfException.create("Unable to read game-info file", ex, "error.sourcesdk.cant_read_gameinfo", gcData.getName(),infoFile);
        }
        catch (JXPathException ex) {
            throw GameConfException.create("Unable to read game-info file", ex, "error.sourcesdk.cant_read_gameinfo", gcData.getName(), infoFile);
        }
        
        
    }

    public Devkit getParent() {
        return parent;
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public File getBspDir() {
        return gcData.getBspDir();
    }

    public File getVmfDir() {
        return gcData.getVmfDir();
    }

    public File getGameDir() {
        return gcData.getGameDir();
    }

    public List<File> getFgdFiles() {
        return gcData.getFgds();
    }

    public String getId() {
        //TODO double-check appropriate value
        return gcData.getName();
    }


}
