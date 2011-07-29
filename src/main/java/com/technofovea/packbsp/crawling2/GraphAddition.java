/*
 * 
 */
package com.technofovea.packbsp.crawling2;

/**
 * A simple data structure to represent adding a single layer of outgoing edges
 * to an unspecified parent node. Child nodes may either be new or they may 
 * already be present in the Graph.
 * 
 * @author Darien Hager
 */
public interface GraphAddition {
    public Edge getConnection();
    public Node getChild();
}
