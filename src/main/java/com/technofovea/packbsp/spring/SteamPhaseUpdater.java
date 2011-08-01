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
public class SteamPhaseUpdater extends AbstractPackbspComponent {

    private static final Logger logger = LoggerFactory.getLogger(SteamPhaseUpdater.class);

    protected SteamUserReader userDetector;
    protected RegistryFactory registryFactory;
    protected SteamState holder;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(userDetector);
        Assert.notNull(registryFactory);
        Assert.notNull(holder);

    }

    public SteamState getHolder() {
        return holder;
    }

    public void setHolder(SteamState holder) {
        this.holder = holder;
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

    public void updatePhase(File steamDir) throws PhaseFailedException {
        try {
            if (steamDir == null || ( !steamDir.isDirectory() )) {
                throw new PhaseFailedException("Invalid Steam directory").addLocalization("error.input.invalid_steam_dir", steamDir);
            }

            logger.info("Opening clientregistry from Steam dir {}", steamDir);
            final ClientRegistry reg = registryFactory.create(steamDir);
            logger.info("Attempting to detect current user");
            final String currentUser = userDetector.detectCurrentUser(steamDir);
            
            // If creation successful, notify scope to invalidate old item
            holder.setSteamDir(steamDir);
            holder.setRegistry(reg);
            holder.setCurrentUser(currentUser);
        }
        catch (PhaseFailedException e) {
            throw localizer.localize(e);
        }
    }
}
