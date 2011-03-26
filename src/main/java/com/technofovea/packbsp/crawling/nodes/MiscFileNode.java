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
public class MiscFileNode extends NodeImpl{

    String path;

    public MiscFileNode(String path) {
        this.path = path;
    }



    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MiscFileNode other = (MiscFileNode) obj;
        if ((this.path == null) ? (other.path != null) : !this.path.equalsIgnoreCase(other.path)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.path != null ? this.path.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return stringify(this, "path",path);
    }

    
}
