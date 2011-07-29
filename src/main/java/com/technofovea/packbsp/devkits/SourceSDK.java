/*
 * 
 */
package com.technofovea.packbsp.devkits;

import com.technofovea.hl2parse.vdf.GameConfigReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Source SDK is a bit special: It is actually three different parallel 
 * SDKs bundled together grouped by "engines". 
 * @author Darien Hager
 */
public class SourceSDK implements Devkit {

    private static final Logger logger = LoggerFactory.getLogger(SourceSDK.class);
    protected String title = "Source SDK";
    protected String id = "source_sdk";
    protected File sdkDir;
    protected File binDir;
    protected List<GameConfigReader.Game> gameConfigSections = new ArrayList<GameConfigReader.Game>();

    protected SourceSDK() {
    }

    public File getBinDir() {
        return binDir;
    }

    public String getName() {
        return title;
    }

    public String getId() {
        return id;
    }
}
