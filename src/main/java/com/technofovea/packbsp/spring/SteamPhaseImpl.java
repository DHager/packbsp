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
class SteamPhaseImpl extends AbstractPhase implements SteamPhase {
    
    File steamDir;
    ClientRegistry registry;
    String currentUser;

    public boolean isComplete(){
        if(steamDir==null){
            return false;
        }
        if(registry==null){
            return false;
        }
        if(currentUser==null || currentUser.length()<1){
            return false;
        }
        return true;
    }

    @Override
    public String getCurrentUser() {
        return currentUser;
    }

    @Override
    public void setCurrentUser(String currentUser) {
        if(!currentUser.equals(this.currentUser)){
            markDirty();
        }
        this.currentUser = currentUser;
    }

    @Override
    public ClientRegistry getRegistry() {
        return registry;
    }

    @Override
    public void setRegistry(ClientRegistry registry) {
        if(!registry.equals(this.registry)){
            markDirty();
        }
        this.registry = registry;
    }

    @Override
    public File getSteamDir() {
        return steamDir;
    }

    @Override
    public void setSteamDir(File steamDir) {
        if(!steamDir.equals(this.steamDir)) {
            markDirty();
        }
        this.steamDir = steamDir;
    }

    
}
