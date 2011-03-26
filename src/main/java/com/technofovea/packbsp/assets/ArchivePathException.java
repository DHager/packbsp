/**
 * Copyright (C) 2011 Darien Hager
 *
 * This code is part of the "PackBSP" project, and is licensed under
 * a Creative Commons Attribution-ShareAlike 3.0 Unported License. For
 * either a summary of conditions or the full legal text, please visit:
 *
 * http://creativecommons.org/licenses/by-sa/3.0/
 *
 * Permissions beyond the scope of this license may be available
 * at http://technofovea.com/ .
 */
package com.technofovea.packbsp.assets;

/**
 * This exception specifically conveys that an archive could not be opened
 * and used because the requested prefix does not exist within it.
 *
 * For example, a Counter-Strike GCF archive would probably not have the "ROOT\tf\" folder
 * used for Team Fortress 2.
 *
 * Signaling this explicitly allows calling code to intelligently recover.
 * @author Darien Hager
 */
public class ArchivePathException extends ArchiveIOException {

    public ArchivePathException() {
    }

    public ArchivePathException(String message) {
        super(message);
    }

    public ArchivePathException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArchivePathException(Throwable cause) {
        super(cause);
    }
}
