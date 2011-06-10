package com.technofovea.packbsp.devkits;

import java.io.File;
import java.util.List;

/**
 *
 * @author Darien Hager
 */
public interface Game {
    /**
     * Tests if the given game is present and supported on the system.
     * @return True if present, false otherwise.
     */
    public boolean isPresent();
    /**
     * Returns a friendly name of this development kit for the user.
     * @return A friendly name, such as "Half Life 2".
     */
    @Override
    public String toString();
    /**
     * Retrieve the GameEngine this Game is able to be managed through.
     * @return The parent GameEngine
     */
    public GameEngine getParent();

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
}
