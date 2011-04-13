/*
 * 
 */
package com.technofovea.packbsp.devkits;

import com.technofovea.hl2parse.registry.BlobParseFailure;
import com.technofovea.hl2parse.registry.CdrParser;
import com.technofovea.hl2parse.vdf.GameConfigReader;
import com.technofovea.hl2parse.vdf.SloppyParser;
import com.technofovea.hl2parse.vdf.ValveTokenLexer;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
public class SourceSDK implements Devkit {

    private static final Logger logger = LoggerFactory.getLogger(SourceSDK.class);
    static final int APPID = 211;
    static final String STEAM_APPS_FOLDER = "steamapps";
    static final String TITLE = "Source SDK";


    protected enum PredefinedEngine {

        ORANGEBOX("Source Engine 2009", "orangebox"),
        EP2("Source Engine 2007", "source2007"),
        EP1("Source Engine 2006", "ep1"),;
        final String displayName;
        final String dirName;

        private PredefinedEngine(String displayName, String dirName) {
            this.displayName = displayName;
            this.dirName = dirName;
        }
    }
    final String sdkDirName;
    final File sdkDir;
    final List<GameEngine> engines;

    public SourceSDK(File steamDir, CdrParser cdr, String currentUser) throws BlobParseFailure {
        logger.debug("Trying to retrieve SDK directory name from app-id");
        sdkDirName = cdr.getAppFolderName(APPID);
        sdkDir = new File(steamDir,
                STEAM_APPS_FOLDER + File.pathSeparator
                + currentUser + File.pathSeparator
                + sdkDirName);
        logger.debug("Source SDK dir detected as {}",sdkDir);

        engines = new ArrayList<GameEngine>();
        for (PredefinedEngine e : PredefinedEngine.values()) {
            engines.add(new SourceSDKEngine(this,e.dirName,e.displayName));
        }
    }

    public boolean isPresent() {
        return sdkDir.isDirectory();
    }

    public String getTitle() {
        return TITLE;
    }

    public List<GameEngine> getGameEngines() throws UnsupportedOperationException {
        return Collections.unmodifiableList(engines);
    }
}
