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
package com.technofovea.packbsp.crawling;

import com.technofovea.packbsp.assets.AssetHit;
import com.technofovea.packbsp.assets.AssetSource.Type;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.IOUtils;

/**
 * A decorator to wrap {@link AssetHit} objects and cache file/hash data.
 *
 * @author Darien Hager
 */
public class AssetAlternative {

    public static enum CollisionType{
        /**
         * There is no important collision.
         */
        NONE,
        /**
         * A collision occured, but both files are identical in content.
         */
        DUPLICATE,
        /**
         * A collision occured, and files are not equivalent in content.
         */
        CONFLICTING,
    }

    static List<AssetAlternative> moveBspToEnd(List<AssetAlternative> alts) {
        // There are no expected cases where there will be more than one BSP
        // source in this list, but just to be sure...
        List<AssetAlternative> bspValues = new ArrayList<AssetAlternative>();
        List<AssetAlternative> ret = new ArrayList<AssetAlternative>();
        for (AssetAlternative alt : alts) {
            if (Type.BSP.equals(alt.getType())) {
                bspValues.add(alt);
            } else {
                ret.add(alt);
            }
        }
        ret.addAll(bspValues);
        return ret;
    }

    
    AssetHit hit;
    File file = null;

    /**
     * Returns a copy of the list which only contains the "top-most" entry for
     * each {@link AssetSource.Type}, and wraps hits within the {@link AssetAlternative} to
     * make future data comparisons easier.
     * @param hits
     * @return
     */
    public static List<AssetAlternative> screenTypes(List<AssetHit> hits) {
        Set<Type> seen = new HashSet<Type>();
        List<AssetAlternative> ret = new ArrayList<AssetAlternative>();

        for (AssetHit h : hits) {
            if (seen.contains(h.getSource().getType())) {
                continue;
            }
            seen.add(h.getSource().getType());
            ret.add(new AssetAlternative(h));
        }
        return ret;
    }

    /**
     * Taking an array of alternative items, tests if there is a
     * @param items
     * @return
     * @throws IOException
     */
    public static CollisionType hasCustomCollision(List<AssetAlternative> items) throws IOException {
        //TODO same-hash in BSP+Archive is a bad idea since it means the map is not forward-compatible. Fix in future
        if (items.size() < 2) {
            // Nothing to even check against
            return CollisionType.NONE;
        }

        // Test if our first-level item collides with the first ARCHIVE-type things below it

        AssetAlternative topLevelItem = items.get(0);
        AssetAlternative archivedItem = null;
        for (int i = 1; i < items.size(); i++) {
            AssetAlternative next = items.get(i);
            if (next.getType().equals(Type.ARCHIVE)) {
                archivedItem = next;
                break;
            }
        }
        if (archivedItem == null) {
            // No secondary item was found that was an archive-type
            // Nothing relevant to collide with
            return CollisionType.NONE;
        }

        // Basic size check
        File diskFile = topLevelItem.getFile();
        File archiveFile = archivedItem.getFile();
        
        boolean sameSize = (diskFile.length() == archiveFile.length());
        boolean sameData = false;
        if(sameSize){
            // Tougher check involving actual contents
            FileInputStream diskStream = new FileInputStream(diskFile);
            FileInputStream archiveStream = new FileInputStream(archiveFile);
            sameData = IOUtils.contentEquals(diskStream, archiveStream);
            diskStream.close();
            archiveStream.close();
        }
        

        if(sameData){
            return CollisionType.DUPLICATE;
        }else{
            return CollisionType.CONFLICTING;
        }
        
    }

    public AssetAlternative(AssetHit hit) {
        this.hit = hit;
        if (!hit.getSource().hasAsset(hit.getPath())) {
            throw new IllegalArgumentException("Hit is invalid, source says asset not found.");
        }

    }

    public Type getType() {
        return hit.getSource().getType();
    }

    public String getRelativePath() {
        return hit.getPath();
    }

    public File getFile() {
        // Lazy-load this file, since it may require the backend to
        // make a temporary file on disk.
        synchronized (this) {
            if (file == null) {
                file = hit.getSource().getData(hit.getPath());
            }
        }
        return file;
    }

}
