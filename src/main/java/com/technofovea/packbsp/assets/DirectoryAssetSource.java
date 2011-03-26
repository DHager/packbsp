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
 * Provides access to files on-disk, typically inside the game-directory for
 * a mod.
 *
 * @author Darien Hager
 */
public class DirectoryAssetSource implements AssetSource {

    File target;

    /**
     * Creates a new director-based asset source
     * @param baseDir The directory to use as a root location
     */
    public DirectoryAssetSource(File baseDir) {
        target = baseDir;
    }

     public Type getType() {
        return Type.DIR;
    }

    

    public boolean hasAsset(String relativePath) {
       
        File temp = getFile(relativePath);
        return temp.isFile();
    }



    public File getData(String relativePath) {
       
        File temp = getFile(relativePath);
        if(!temp.isFile()){
            return null;
        }else{
            return temp;
        }
    }

    /**
     * Given a relative path constructs a File object to the destination.
     * @param relativePath The relative path
     * @return A File object which may or may not exist or be a directory.
     */
    File getFile(String relativePath){
        return new File(target,relativePath);
    }



}
