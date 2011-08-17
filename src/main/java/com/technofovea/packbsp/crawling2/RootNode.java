/*
 * 
 */
package com.technofovea.packbsp.crawling2;

/**
 *
 * @author Darien Hager
 */
public class RootNode implements Node{
    
    protected final MapDepGraph owner;

    public RootNode(MapDepGraph owner) {
        this.owner = owner;
    }

    public MapDepGraph getOwner() {
        return owner;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RootNode other = (RootNode) obj;
        if (this.owner != other.owner && ( this.owner == null || !this.owner.equals(other.owner) )) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + ( this.owner != null ? this.owner.hashCode() : 0 );
        return hash;
    }
    
}
