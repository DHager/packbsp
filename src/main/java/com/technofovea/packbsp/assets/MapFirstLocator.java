package com.technofovea.packbsp.assets;

import com.technofovea.hllib.methods.ManagedLibrary;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Darien Hager
 */
public class MapFirstLocator implements AssetLocator {

    AssetLocator core;
    MapAssetSource mapSource;

    public MapFirstLocator(AssetLocator core, ManagedLibrary lib, File bspFile) throws ArchiveIOException {
        this.core = core;
        this.mapSource = new MapAssetSource(lib, bspFile);

    }

    public List<AssetHit> locate(String path) {
        List<AssetHit> hits = new ArrayList<AssetHit>();
        if (mapSource != null) {
            if (mapSource.hasAsset(path)) {
                hits.add(new AssetHit(mapSource, path));
            }
        }
        hits.addAll(core.locate(path));
        return hits;
    }
}
