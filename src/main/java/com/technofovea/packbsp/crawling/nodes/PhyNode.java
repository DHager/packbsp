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
package com.technofovea.packbsp.crawling.nodes;

import com.technofovea.packbsp.crawling.NodeImpl;

/**
 * 
 * @author Darien Hager
 */
public class PhyNode extends NodeImpl{

    String path;
    boolean movable;

    public PhyNode(String path, boolean movable) {
        this.path = path;
        this.movable = movable;
    }


    public String getPath() {
        return path;
    }

    public boolean isMovable() {
        return movable;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PhyNode other = (PhyNode) obj;
        if ((this.path == null) ? (other.path != null) : !this.path.equals(other.path)) {
            return false;
        }
        if (this.movable != other.movable) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.path != null ? this.path.hashCode() : 0);
        hash = 97 * hash + (this.movable ? 1 : 0);
        return hash;
    }

    

    @Override
    public String toString() {
        return stringify(this, "path",path,"physics",movable);
    }

    

















}
