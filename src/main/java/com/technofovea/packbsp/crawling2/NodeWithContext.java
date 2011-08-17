
package com.technofovea.packbsp.crawling2;

/**
 * Simply combines a {@link MapDepGraph} and a {@link Node} to create a key
 * for collections.
 * 
 * @author Darien Hager
 */
class NodeWithContext {
    final MapDepGraph g;
    private final Node n;

    public NodeWithContext(MapDepGraph g, Node n) {
        this.g = g;
        this.n = n;
    }

    public MapDepGraph getG() {
        return g;
    }

    public Node getN() {
        return n;
    }
    
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NodeWithContext other = (NodeWithContext) obj;
        if (this.g != other.g && ( this.g == null || !this.g.equals(other.g) )) {
            return false;
        }
        if (this.n != other.n && ( this.n == null || !this.n.equals(other.n) )) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + ( this.g != null ? this.g.hashCode() : 0 );
        hash = 29 * hash + ( this.n != null ? this.n.hashCode() : 0 );
        return hash;
    }
    
}
