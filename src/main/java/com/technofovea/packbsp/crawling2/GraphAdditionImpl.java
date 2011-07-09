/*
 * 
 */
package com.technofovea.packbsp.crawling2;

/**
 *
 * @author Darien Hager
 */
public class GraphAdditionImpl implements GraphAddition{
    protected Edge connection;
    protected Node child;

    public GraphAdditionImpl(Edge connection, Node child) {
        this.connection = connection;
        this.child = child;
    }
    

    public Node getChild() {
        return child;
    }

    public Edge getConnection() {
        return connection;
    }
    
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GraphAdditionImpl other = (GraphAdditionImpl) obj;
        if (this.connection != other.connection && ( this.connection == null || !this.connection.equals(other.connection) )) {
            return false;
        }
        if (this.child != other.child && ( this.child == null || !this.child.equals(other.child) )) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + ( this.connection != null ? this.connection.hashCode() : 0 );
        hash = 53 * hash + ( this.child != null ? this.child.hashCode() : 0 );
        return hash;
    }
}
