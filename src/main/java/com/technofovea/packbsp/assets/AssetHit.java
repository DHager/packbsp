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
 * Represents an asset which was found at a relative path inside an {@link AssetSource}.
 * @author Darien Hager
 */
public class AssetHit {

    /**
     * Creates a new hit object
     * @param source The source the asset was found in
     * @param path The relative path used to address the asset
     */
    public AssetHit(AssetSource source, String path) {
        this.source = source;
        this.path = path;
    }

    AssetSource source;
    String path;

    /**
     * Get the relative path.
     * @return A string path using forward-slashes
     */
    public String getPath() {
        return path;
    }

    /**
     * Retrieves the source where the asset was found.
     * @return The source
     */
    public AssetSource getSource() {
        return source;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AssetHit other = (AssetHit) obj;
        if (this.source != other.source && (this.source == null || !this.source.equals(other.source))) {
            return false;
        }
        if ((this.path == null) ? (other.path != null) : !this.path.equalsIgnoreCase(other.path)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 79 * hash + (this.path != null ? this.path.hashCode() : 0);
        return hash;
    }
}

