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

import java.io.File;

/**
 * Provides access to assets which may be in a directory or within some form
 * of compressed file.
 *
 * @author Darien Hager
 */
public interface AssetSource {

    /**
     * The technical type of the source.
     */
    public enum Type {
        /**
         * A directory which exists in a normal filesystem
         */
        DIR,
        /**
         * A PAK file (or similar non-Steam archive) which exists in the
         * normal filesystem
         */
        DIR_PAK,
        /**
         * A Steam archive, generally GCF files
         */
        ARCHIVE,
        /**
         * A map file, typically BSP.
         */
        BSP
    }

    /**
     * Returns the type of the current source object
     * @return A type
     */
    public Type getType();

    /**
     * Checks whether an asset exists at the given relative path. Note that
     * each implementation may internally choose to interpret or prefix this path
     * differently.
     *
     * @param relativePath The relative path to check.
     * @return True if an asset was found, false otherwise.
     */
    public boolean hasAsset(String relativePath);

    /**
     * Optional operation.
     *
     * Retrieves a reference to the file designated by the given relative path.
     * Note that in some cases this may be intensive, involving extracting and
     * creating a temporary file.
     *
     * @param relativePath Relative path to asset
     * @return A File (temporary or otherwise) containing the relevant data
     * @throws UnsupportedOperationException If this source does not support retrieval.
     */
    public File getData(String relativePath) throws UnsupportedOperationException;
}
