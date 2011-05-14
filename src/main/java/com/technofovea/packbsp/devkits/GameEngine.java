package com.technofovea.packbsp.devkits;

import java.io.File;
import java.util.List;

/**
 * Represents a game engine supported by a particular development kit. Some kits
 * may support multiple engines (like the Source SDK) while others may only
 * support a single implicit engine (like the Left 4 Dead Authoring Tools.)
 *
 * @author Darien Hager
 */
public interface GameEngine {
    /**
     * Tests if the given engine is present and supported on the system.
     * @return True if present, false otherwise.
     */
    public boolean isPresent();

    /**
     * Returns the development kit which provides tools for this engine.
     * @return
     */
    public Devkit getParent();

    /**
     * Returns a friendly name of this development kit for the user.
     * @return A friendly name, such as "Source 2007".
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
     * Gets a list of games using this engine.
     * @return A list of games.
     * @throws GameConfigurationException If this engine is not present or if
     * there is a problem getting game information.
     */
    public List<Game> getGames() throws GameConfigurationException;

    public File getBinDir();

}
