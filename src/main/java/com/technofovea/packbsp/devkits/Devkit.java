package com.technofovea.packbsp.devkits;

import java.io.File;
import java.util.List;

/**
 * Represents a development kit. Examples include the Source SDK or the
 * Left 4 Dead Authoring Tools.
 *
 * @author Darien Hager
 */
public interface Devkit {


    /**
     * Returns a Game object for the given key, which may be either instantiated
     * or re-used from a past invocation.
     * 
     * @return The Game associated with the key, or null if the key was invalid.
     * @throws GameConfigurationException if there was a problem creating the Game object
     */
    public Game getGame(String gameKey) throws GameConfException;
    
    /**
     * Returns a unique list of keys which are used to refer to specific games this kit 
     * provides access to. These keys are *not* intended to be used for long-term
     * identification and may be inconsistent across program executions or platforms.
     * @return A unique list of keys to identify available games
     */
    public List<String> getGameKeys();
    /**
     * Returns a friendly name of this development kit for the user.
     * @return A friendly name, such as "Source Development Kit".
     */
    @Override
    public String toString();

    /**
     * Returns a string ID (possibly specific to PackBSP) designed to disambiguate
     * this kit from others. Lowercase alphanumerics and underscores only.
     * @return A string ID
     */
    public String getId();
    
    /**
     * Retrieve the location where the various executables for this kit are stored.
     * @return 
     */
    public File getBinDir();

}
