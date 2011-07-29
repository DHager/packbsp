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
 * Signifies an error occurred when trying to open or access data inside of
 * an archive.
 * 
 * @author Darien Hager
 */
public class ArchiveIOException extends Exception {

    public ArchiveIOException(Throwable cause) {
        super(cause);
    }

    public ArchiveIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArchiveIOException(String message) {
        super(message);
    }

    public ArchiveIOException() {
    }
}
