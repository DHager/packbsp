package com.technofovea.packbsp.devkits;

import java.io.File;
import java.util.List;

/**
 *
 * @author Darien Hager
 */
public interface DetectedGame {


    /**
     * Retrieve the Devkit this DetectedGame is able to be managed through.
     * @return The parent Devkit
     */
    public Devkit getParentKit();
    
    /**
     * Returns the name of the game. In the case of GameConfig.txt vs GameInfo.txt,
     * the latter is returned.
     * @return 
     */
    public String getName();

    public File getVmfDir();

    public File getBspDir();

    public File getGameDir();

    public List<File> getFgdFiles();
    
        
    /**
     * Retrieve the location where the development binaries are stored.
     * @return 
     */
    public File getKitBinDir();

    //TODO appid
    
    //TODO search paths
}
