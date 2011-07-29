package com.technofovea.packbsp.devkits;

import java.io.File;
import java.util.Collection;

/**
 * Represents a development kit. Examples include the Source SDK or the
 * Left 4 Dead Authoring Tools. Useful for grouping a series of {@link DetectedGame}
 * items together.
 *
 * @author Darien Hager
 */
public interface Devkit {

    /**
     * Returns a friendly name of this development kit for the user.
     * @return A friendly name, such as "Source Development Kit".
     */
    public String getName();

    /**
     * Returns a string ID (possibly specific to PackBSP) designed to disambiguate
     * this kit from others. Lowercase alphanumerics and underscores only.
     * @return A string ID
     */
    public String getId();

}
