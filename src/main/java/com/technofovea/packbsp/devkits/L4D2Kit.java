/*
 * s
 */
package com.technofovea.packbsp.devkits;

import com.technofovea.packbsp.devkits.SourceSDK.PredefinedEngine;
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
public class L4D2Kit implements Devkit {

    private static final Logger logger = LoggerFactory.getLogger(L4D2Kit.class);
    static final String DEVKIT_ID = "l4d2_authoring_tools";
    static final String TITLE = "Left 4 Dead 2 Authoring Tools";
    final File baseDir;
    final File binDir;
    L4D2Engine soleEngine;

    public static L4D2Kit createKit(File steamDir) {
        // Ex:         //C:\Program Files\Steam\steamapps\common\left 4 dead 2\bin
        File deadDir = new File(steamDir, "steamapps/common/left 4 dead 2/");
        L4D2Kit ret = new L4D2Kit(deadDir);
        return ret;
    }

    private L4D2Kit(File deadDir) {
        baseDir = deadDir;
        binDir = new File(deadDir,"bin");
    }

    public List<GameEngine> getGameEngines() throws GameConfigurationException {
        if (!isPresent()) {
            throw new GameConfigurationException("SDK is not present");
        }
        synchronized (this) {
            if (soleEngine == null) {
                soleEngine = new L4D2Engine(this);
            }
        }
        List<GameEngine> ret = new ArrayList<GameEngine>();
        ret.add(soleEngine);
        return ret;
    }

    @Override
    public String toString() {
        return TITLE;
    }

    public String getId() {
        return DEVKIT_ID;
    }

    public boolean isPresent() {
        return binDir.isDirectory();
    }
}
