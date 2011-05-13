package com.technofovea.packbsp.devkits;

import com.technofovea.packbsp.PackbspException;
import java.util.List;

/**
 * Represents a development kit. Examples include the Source SDK or the
 * Left 4 Dead Authoring Tools.
 *
 * @author Darien Hager
 */
public interface Devkit {

    /**
     * Tests if the given devkit is present and supported on the system.
     * @return True if present, false otherwise.
     */
    public boolean isPresent();

    /**
     * Returns a list of game engines this kit supports.
     * @return A list of supported GameEngines.
     * @throws UnsupportedOperationException if this Devkit is not present or if 
     * there is not enough information to determine what its engines are.
     */
    public List<GameEngine> getGameEngines() throws GameConfigurationException;
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

}
