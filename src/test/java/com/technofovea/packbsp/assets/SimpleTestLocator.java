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
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class SimpleTestLocator implements AssetLocator {

    private static final Logger logger = LoggerFactory.getLogger(SimpleTestLocator.class);
    List<AssetSource> locs = new ArrayList<AssetSource>();
    ManagedLibrary lib;

    public SimpleTestLocator(ManagedLibrary lib) {
        this.lib = lib;
    }

    public void addDirectory(File dir) {
        locs.add(new DirectoryAssetSource(dir));
    }

    public void addBsp(File bsp) throws ArchiveIOException {
        logger.info("Adding BSP file to sources: {}", bsp.getAbsolutePath());
        locs.add(new MapAssetSource(lib, bsp));
    }

    public void addGcf(File gcf, String path) throws ArchiveIOException {
        try {
            logger.info("Adding archive file to sources (with prefix '{}'): {}", path, gcf.getAbsolutePath());
            locs.add(new ArchiveAssetSource(lib, gcf, path));
        } catch (ArchivePathException ex) {
            logger.debug("Skipping archive, does not possess subpath");
            return;
        }
    }

    public List<AssetHit> locate(String path) {
        List<AssetHit> ret = new ArrayList<AssetHit>();
        for (AssetSource loc : locs) {
            if (loc.hasAsset(path)) {
                ret.add(new AssetHit(loc, path));
            }
        }
        return ret;
    }
}
