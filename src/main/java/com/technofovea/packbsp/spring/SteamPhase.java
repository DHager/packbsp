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
public interface SteamPhase {

    public File getSteamDir();

    public ClientRegistry getRegistry();

    public String getCurrentUser();
    
}
