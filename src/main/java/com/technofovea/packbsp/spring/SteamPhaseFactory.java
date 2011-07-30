/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.hl2parse.registry.ClientRegistry;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * 
 * @author Darien Hager
 */
public class SteamPhaseFactory extends AbstractPackbspComponent {

    private static final Logger logger = LoggerFactory.getLogger(SteamPhaseFactory.class);
    protected NestedScope scope = null;
    protected File steamDir = null;
    protected SteamUserReader userDetector;
    protected RegistryFactory registryFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(userDetector);
        Assert.notNull(registryFactory);

    }

    protected void markDirty() {
        if (scope != null) {
            scope.invalidateScope();
        }
    }

    public NestedScope getScope() {
        return scope;
    }

    public void setScope(NestedScope scope) {
        this.scope = scope;
    }

    public File getSteamDir() {
        return steamDir;
    }

    public void setSteamDir(File steamDir) {
        if (( this.steamDir != null ) && this.steamDir.equals(steamDir)) {
            return; // not a real change
        }
        this.steamDir = steamDir;
        markDirty();
    }

    public RegistryFactory getRegistryFactory() {
        return registryFactory;
    }

    public void setRegistryFactory(RegistryFactory registryFactory) {
        this.registryFactory = registryFactory;
    }

    public SteamUserReader getUserDetector() {
        return userDetector;
    }

    public void setUserDetector(SteamUserReader userDetector) {
        this.userDetector = userDetector;
    }

    public SteamPhase createPhase() throws PhaseFailedException {
        try {
            if (steamDir == null || ( !steamDir.isDirectory() )) {
                throw new PhaseFailedException("Invalid Steam directory").addLocalization("error.input.invalid_steam_dir", steamDir);
            }

            logger.info("Opening clientregistry from Steam dir {}", steamDir);
            final ClientRegistry reg = registryFactory.create(steamDir);
            logger.info("Attempting to detect current user");
            final String currentUser = userDetector.detectCurrentUser(steamDir);
            
            // If creation successful, notify scope to invalidate old item
            SteamPhase ret = new SteamPhaseImpl(steamDir, reg, currentUser);
            return ret;
        }
        catch (PhaseFailedException e) {
            throw localizer.localize(e);
        }
    }
}
