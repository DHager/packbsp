/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.packbsp.WindowsRegistryChecker;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 *
 * @author Darien Hager
 */
public class SteamDirectoryFinder implements InitializingBean{

    private static final Logger logger = LoggerFactory.getLogger(SteamDirectoryFinder.class);
    protected final String DEFAULT_REGISTRY_PATH = "HKCU\\Software\\Valve\\Steam\\SteamPath";
    protected String registryKey = DEFAULT_REGISTRY_PATH;
    protected WindowsRegistryChecker wrc;

    public SteamDirectoryFinder() {
        wrc = new WindowsRegistryChecker();
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(registryKey);
    }

    /**
     * Checks the Windows registry to determine the installation directory for Steam.
     * @return Where Steam is installed, or null if there was an error. 
     */
    public File guessSteamDir() {
        String guess = null;
        try {
            final String key = DEFAULT_REGISTRY_PATH;
            logger.debug("Trying to load steam dir from registry at {}", key);
            guess = wrc.getKey(key);
            if (guess == null) {
                logger.warn("Registry key not found.");
            }
        }catch (IOException ex) {
            logger.error("Had an exception querying registry for Steam folder: {}", ex);
        }
        
        if (guess != null) {
            File f = new File(guess);
            if (f.exists()) {
                return ( f );
            } else {
                logger.warn("Found Steam directory from registry, but file location does not exist: {}", guess);
            }
        }
        return null;
    }

    public String getRegistryKey() {
        return registryKey;
    }

    public void setRegistryKey(String registryKey) {
        this.registryKey = registryKey;
    }
}
