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

import java.io.File;

/**
 *
 * @author Darien Hager
 */
public class PackingPair {

    String relativePath;
    File sourceFile;
    boolean skipped = false; // Used only if sourceFile is null

    public static PackingPair create(String relativePath, File sourceFile){
        PackingPair ret = new PackingPair(relativePath, sourceFile);
        return ret;
    }

    public static PackingPair createSkip(String relativePath){
        PackingPair ret = new PackingPair(relativePath, null);
        ret.skipped = true;
        return ret;
    }
     public static PackingPair createMissing(String relativePath){
        PackingPair ret = new PackingPair(relativePath, null);
        ret.skipped = false;
        return ret;
    }

    protected PackingPair(String relativePath, File sourceFile) {
        this.relativePath = relativePath;
        this.sourceFile = sourceFile;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public boolean isSkipped() {
        return (sourceFile == null) && skipped;
    }
    public boolean isMissing() {
        return (sourceFile == null) && (!skipped);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PackingPair other = (PackingPair) obj;
        if ((this.relativePath == null) ? (other.relativePath != null) : !this.relativePath.equalsIgnoreCase(other.relativePath)) {
            return false;
        }
        if (this.sourceFile != other.sourceFile && (this.sourceFile == null || !this.sourceFile.equals(other.sourceFile))) {
            return false;
        }
        if (this.skipped != other.skipped) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.relativePath != null ? this.relativePath.hashCode() : 0);
        hash = 37 * hash + (this.sourceFile != null ? this.sourceFile.hashCode() : 0);
        hash = 37 * hash + (this.skipped ? 1 : 0);
        return hash;
    }

    

    

}
