package com.technofovea.packbsp.devkits;

import java.io.File;
import java.util.List;

/**
 *
 * @author Darien Hager
 */
public interface Game {


    /**
     * Retrieve the Devkit this Game is able to be managed through.
     * @return The parent Devkit
     */
    public Devkit getParent();

    /**
     * Returns a string ID (possibly specific to PackBSP) designed to disambiguate
     * this kit from others. Lowercase alphanumerics and underscores only.
     * @return A string ID
     */
    public String getId();
    
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

    //TODO appid
    
    //TODO search paths
}
