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
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Darien Hager
 */
public class ModelNode extends NodeImpl{

    String path;
    Set<Integer> skins;
    boolean movable;

    public ModelNode(String path, Set<Integer> skins, boolean movable) {
        this.path = path;
        this.skins = skins;
        this.movable = movable;
    }

    public ModelNode(String path, Set<Integer> skins) {
        this(path,skins,true);
    }

    public ModelNode(String path) {
        this(path,null);
    }




    public String getPath() {
        return path;
    }

    /**
     * Returns the set of skin-IDs this node requires. May be null to indicate all skins.
     * @return Integers of required skins, or null.
     */
    public Set<Integer> getSkins() {
        return skins;
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
        final ModelNode other = (ModelNode) obj;
        if ((this.path == null) ? (other.path != null) : !this.path.equalsIgnoreCase(other.path)) {
            return false;
        }
        if (this.skins != other.skins && (this.skins == null || !this.skins.equals(other.skins))) {
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
        hash = 11 * hash + (this.path != null ? this.path.hashCode() : 0);
        hash = 11 * hash + (this.skins != null ? this.skins.hashCode() : 0);
        hash = 11 * hash + (this.movable ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return stringify(this, "path",path,"skins",skins,"physics",movable);
    }

    

















}
