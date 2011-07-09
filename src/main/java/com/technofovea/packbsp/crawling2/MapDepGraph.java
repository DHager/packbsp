/*
 * 
 */
package com.technofovea.packbsp.crawling2;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.MultiGraph;
import java.util.Collection;
import java.util.Map;

/**
 * A dependency graph is directed and acyclic. It is also a multigraph, since 
 * it allows parallel edges between pairs of nodes.
 * 
 * Edges are categorized by their {@link Layer}. Thus, different layers describe
 * different potential graph structures while still sharing access to the same
 * collection of nodes.
 * 
 * The graph is also responsible for tracking the creation of newly-created edges and
 * marking them as "handled". 
 * 
 * @author Darien Hager
 */
public interface MapDepGraph extends DirectedGraph<Node, Edge>, MultiGraph<Node, Edge> {

    /**
     * This method is extremely similar to {@link Graph#addVertex(java.lang.Object)}, except 
     * that its return values are a bit more useful. It returns the vertex which was
     * either just added or else the equivalent vertex that was already inside
     * the graph.
     * that object. 
     * @param <V> The type of vertex the user is adding.
     * @param vertex The vertex to add.
     * @return The vertex now being used, or null on error.
     */
    public <V extends Node> V addUniqueVertex(V vertex);

    /**
     * If the given vertex--or a vertex that is equal to it--already exists within the 
     * graph, return the stored version. This allows more control over the 
     * de-duplication of nodes.
     * @param <V> The type of vertex the user is testing.
     * @param newVertex The vertex being checked.
     * @return The vertex currently stored, or null if not yet in the graph
     */
    public <V extends Node> V getVertexIfContained(V vertex);
    
    /**
     * Retrieve the an "unhandled" edge from the known set. This method is not
     * guaranteed to return the same item each time, nor to return successive
     * items in any particular order.
     * 
     * @return An "unhandled" edge, or null if none exist. 
     */
    public Edge getUnhandledEdge();
    
    /**
     * Used to mark an edge as "handled" after it has been considered and handled.
     * @param e The completed edge
     * @return True on success, false if the edge does not exist or was already
     * marked handled.
     */
    public boolean markEdgeHandled(Edge e);

    /**
     * Similar to {@link #getOutEdges(java.lang.Object)}, but groups the edges
     * by the layer they belong to.
     * 
     * @throws IllegalArgumentException If the vertex does not exist in the graph
     * @param vertex The vertex to examine
     * @return The edges leaving the vertex, grouped by Layer
     */
    public Map<Layer, Collection<Edge>> getOutEdgesByLayer(Node vertex);

    /**
     * Similar to {@link #getInEdges(java.lang.Object)}, but groups the edges
     * by the layer they belong to.
     * 
     * @throws IllegalArgumentException If the vertex does not exist in the graph
     * @param vertex The vertex to examine
     * @return The edges entering the vertex, grouped by Layer
     */
    public Map<Layer, Collection<Edge>> getInEdgesByLayer(Node vertex);
}
