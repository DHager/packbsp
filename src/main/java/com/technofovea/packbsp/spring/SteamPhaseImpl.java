/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.hl2parse.registry.ClientRegistry;
import java.io.File;

/**
 *
 * @author Darien Hager
 */
class SteamPhaseImpl implements SteamPhase {
    final File steamDir;
    final ClientRegistry registry;
    final String currentUser;

    public SteamPhaseImpl(File steamDir, ClientRegistry registry, String currentUser) {
        this.steamDir = steamDir;
        this.registry = registry;
        this.currentUser = currentUser;
    }

    public File getSteamDir() {
        return steamDir;
    }

    public ClientRegistry getRegistry() {
        return registry;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SteamPhaseImpl other = (SteamPhaseImpl) obj;
        if (this.steamDir != other.steamDir && ( this.steamDir == null || !this.steamDir.equals(other.steamDir) )) {
            return false;
        }
        if (this.registry != other.registry && ( this.registry == null || !this.registry.equals(other.registry) )) {
            return false;
        }
        if (( this.currentUser == null ) ? ( other.currentUser != null ) : !this.currentUser.equals(other.currentUser)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + ( this.steamDir != null ? this.steamDir.hashCode() : 0 );
        hash = 71 * hash + ( this.registry != null ? this.registry.hashCode() : 0 );
        hash = 71 * hash + ( this.currentUser != null ? this.currentUser.hashCode() : 0 );
        return hash;
    }

    
    
    
    
}
