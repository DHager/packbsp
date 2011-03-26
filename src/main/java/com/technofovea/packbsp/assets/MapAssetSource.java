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

import com.technofovea.hllib.methods.ManagedLibrary;
import java.io.File;

/**
 * Provides access to data embedded inside map files
 * 
 * @author Darien Hager
 */
public class MapAssetSource extends ArchiveAssetSource {

    /**
     * Creates a new asset-source for files that have been packaged into a map
     * @param lib The JhlLib object for I/O
     * @param bspFile The map to be accessed
     * @throws ArchiveIOException If there was a problem opening the map.
     */
    public MapAssetSource(ManagedLibrary lib, File bspFile) throws ArchiveIOException {
        super(lib, bspFile, "");
        //TODO throw AssetArchiveOpenException if not a bsp/vbsp file
    }

    @Override
    public Type getType() {
        return Type.BSP;
    }

}
