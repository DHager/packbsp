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

import com.technofovea.packbsp.assets.AssetSource.Type;
import java.io.File;

/**
 * Instances of this class are primarily used during the creation of an 
 * {@link AssetLocator} in order to organize and de-duplicate search paths.
 *
 * these should not be confused with {@link AssetSource} objects.
 * 
 * @author Darien Hager
 */
public class AssetSearchLocation {

    protected Type type;
    // Directory, GCF, or exclude-manifest
    protected File base;
    // Prefix within it (does not include GCF's "root\", uses forward slashes
    protected String prefix;

    public AssetSearchLocation(Type type, File base, String prefix) {
        this.type = type;
        this.base = base;
        this.prefix = prefix;
    }
    
    

    public File getBase() {
        return base;
    }

    public String getPrefix() {
        return prefix;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "{"+type.toString()+":"+base.toString()+":"+prefix+"}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AssetSearchLocation other = (AssetSearchLocation) obj;
        if (this.type != other.type) {
            return false;
        }
        if (this.base != other.base && (this.base == null || !this.base.equals(other.base))) {
            return false;
        }
        if ((this.prefix == null) ? (other.prefix != null) : !this.prefix.equalsIgnoreCase(other.prefix)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + this.type.hashCode();
        hash = 11 * hash + (this.base != null ? this.base.hashCode() : 0);
        hash = 11 * hash + (this.prefix != null ? this.prefix.hashCode() : 0);
        return hash;
    }

    

    






}
