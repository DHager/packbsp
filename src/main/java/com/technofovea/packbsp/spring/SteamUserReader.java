/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.hl2parse.vdf.SloppyParser;
import com.technofovea.hl2parse.vdf.SteamMetaReader;
import com.technofovea.hl2parse.vdf.ValveTokenLexer;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.io.File;
import java.io.IOException;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
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
public class SteamUserReader implements InitializingBean {
    
    private static final Logger logger = LoggerFactory.getLogger(SteamUserReader.class);
    protected String appDataPath = "config/SteamAppData.vdf";
    
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(appDataPath);
    }
    
    public String detectCurrentUser(File steamDir) throws PhaseFailedException {
        final File steamAppData = new File(steamDir, appDataPath);
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
                throw new PhaseFailedException("Could not read username from: " + steamAppData.getAbsolutePath()).addLocalization("error.steam_not_running", steamAppData.getAbsolutePath());
            }
            logger.debug("Username detected as: {}", currentUser);
        }
        catch (IOException ex) {
            throw new PhaseFailedException("Could not read username from: " + steamAppData.getAbsolutePath(), ex).addLocalization("error.steam_not_running", steamAppData.getAbsolutePath());
        }
        catch (RecognitionException ex) {
            throw new PhaseFailedException("Could not read username from: " + steamAppData.getAbsolutePath(), ex).addLocalization("error.steam_not_running", steamAppData.getAbsolutePath());
        }
        return currentUser;
    }
    
    public String getAppDataPath() {
        return appDataPath;
    }
    
    public void setAppDataPath(String appDataPath) {
        this.appDataPath = appDataPath;
    }
}
