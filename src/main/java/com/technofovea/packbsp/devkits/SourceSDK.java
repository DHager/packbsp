/*
 * 
 */
package com.technofovea.packbsp.devkits;

import com.technofovea.hl2parse.registry.BlobParseFailure;
import com.technofovea.hl2parse.registry.CdrParser;
import com.technofovea.hl2parse.registry.ClientRegistry;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    static final String ID = "source_sdk";

    enum PredefinedEngine {

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
    final File sdkDir;
    List<GameEngine> engines = null;

    public static File getAppFolder(File steamDir, CdrParser cdr, String currentUser, int appid) throws BlobParseFailure {
        String appName = cdr.getAppFolderName(appid);
        File appDir = new File(steamDir,
                STEAM_APPS_FOLDER + File.separator
                + currentUser + File.separator
                + appName);

        return appDir;
    }

    public static SourceSDK createKit(File steamDir, ClientRegistry reg, String currentUser) throws BlobParseFailure {
        logger.debug("Trying to retrieve SDK directory name from app-id");
        File appDir = getAppFolder(steamDir, reg.getContentDescriptionRecord(), currentUser, APPID);
        logger.debug("Source SDK dir detected as {}", appDir);
        SourceSDK ret = new SourceSDK(appDir);
        return ret;
    }

    protected SourceSDK(File dir){
        sdkDir = dir;
    }

    public boolean isPresent() {
        return sdkDir.isDirectory();
    }

    @Override
    public String toString() {
        return TITLE;
    }

    public List<GameEngine> getGameEngines() throws GameConfigurationException {
        if (!isPresent()) {
            throw new GameConfigurationException("SDK is not present");
        }
        synchronized (this) {
            if (engines == null) {
                engines = new ArrayList<GameEngine>();
                for (PredefinedEngine e : PredefinedEngine.values()) {
                    engines.add(new SourceSDKEngine(this, e.dirName, e.displayName));
                }
            }
        }
        return Collections.unmodifiableList(engines);
    }

    public String getId() {
        return ID;
    }
}
