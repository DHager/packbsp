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
package com.technofovea.packbsp;

import java.io.File;

/**
 * A dependency which PackBSP has discovered.
 * @author Darien Hager
 */
public class DependencyItem implements Comparable<DependencyItem> {

    /**
     * How important it is for this asset to be included. Some assets are
     * "optional" to signify that they are nowhere explicitly required and are
     * only implicitly pulled in, often based on the map-name.
     */
    public enum Necessity {
        UNKNOWN,
        REQUIRED,
        OPTIONAL;
    }

    /**
     * The status of this asset in the packing process.
     */
    public enum Status {

        UNKNOWN,
        /**
         * Exists as part of the default installation for this game, no action required.
         */
        STOCK,
        /**
         * Already present inside the map file, no action required.
         */
        PREPACKED,
        /**
         * Not available.
         */
        MISSING,
        /**
         * Available.
         */
        FOUND;
    }
    Necessity necessity = Necessity.UNKNOWN;
    Status status = Status.UNKNOWN;
    String path;
    File dataSource = null;

    public DependencyItem(String path) {
        this.path = path;
    }

    public void setDataSource(File dataSource) {
        this.dataSource = dataSource;
    }

    public void setNecessity(Necessity necessity) {
        this.necessity = necessity;
    }

    public void setPath(String path) {
        path = path.trim();
        while(path.endsWith("/") && path.length()>1){
            path = path.substring(0,path.length()-1);
        }
        if(!path.startsWith("/")){
            path = "/"+path;
        }
        this.path = path;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int compareTo(DependencyItem o) {
        return getPath().compareTo(o.getPath());
    }

    public File getDataSource() {
        return dataSource;
    }

    public Necessity getNecessity() {
        return necessity;
    }

    public String getPath() {
        return path;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("path:").append(path);
        sb.append(",status:").append(status);
        sb.append(",need:").append(necessity);
        sb.append(",file:").append(dataSource);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DependencyItem other = (DependencyItem) obj;
        if ((this.path == null) ? (other.path != null) : !this.path.equalsIgnoreCase(other.path)) {
            return false;
        }
        if (this.dataSource != other.dataSource && (this.dataSource == null || !this.dataSource.equals(other.dataSource))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.path != null ? this.path.hashCode() : 0);
        hash = 79 * hash + (this.dataSource != null ? this.dataSource.hashCode() : 0);
        return hash;
    }

    
}
