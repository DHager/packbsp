/*
 * 
 */
package com.technofovea.packbsp.crawling2;

/**
 *
 * @author Darien Hager
 */
public interface DependencyProjectListener {

    public void graphAdded(MapDepGraph g);

    public void graphRemoved(MapDepGraph g);

    public void nodeAdded(MapDepGraph graph, Node n);

    public void nodeRemoved(MapDepGraph graph, Node n);

    public void edgeAdded(MapDepGraph graph, Edge e);

    public void edgeRemoved(MapDepGraph graph, Edge e);
}
