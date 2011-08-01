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
public interface SteamState extends BasicState {

    public String getCurrentUser();

    public ClientRegistry getRegistry();

    public File getSteamDir();

    public void setCurrentUser(String currentUser);

    public void setRegistry(ClientRegistry registry);

    public void setSteamDir(File steamDir);
}
