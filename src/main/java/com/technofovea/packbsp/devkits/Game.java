package com.technofovea.packbsp.devkits;

import java.io.File;
import java.util.List;

/**
 *
 * @author Darien Hager
 */
public interface Game {

    /**
     * Returns a friendly name of this development kit for the user.
     * @return A friendly name, such as "Half Life 2".
     */
    @Override
    public String toString();

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

    public File getVmfDir();

    public File getBspDir();

    public File getGameDir();

    public List<File> getFgdFiles();
    
    //TODO search paths
}
