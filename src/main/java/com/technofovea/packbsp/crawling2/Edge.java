/*
 * 
 */
package com.technofovea.packbsp.crawling2;

/**
 * Unlike {@link Node} objects, the equality and hash-code behavior for edges
 * should be reference-equality. 
 * 
 * Doing otherwise will cause problems when trying to add edges to the
 * {@link MapDepGraph}.
 * 
 * @author Darien Hager
 */
public interface Edge {
    
    public boolean isImplicit();
    public boolean setImplicit(boolean implicit);
    
}
