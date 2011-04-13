package com.technofovea.packbsp.devkits;

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

    public String getTitle();

    /**
     * Gets a list of games using this engine.
     * @return A list of games.
     * @throws UnsupportedOperationException If this engine is not present or if
     * there is a problem getting game information.
     */
    public List<Game> getGames() throws UnsupportedOperationException;

}
