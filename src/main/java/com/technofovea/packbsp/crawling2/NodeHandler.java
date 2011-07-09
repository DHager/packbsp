package com.technofovea.packbsp.crawling2;

import edu.uci.ics.jung.graph.util.Pair;
import java.util.Collection;

/**
 * Handlers are responsible for reacting when a new edge is drawn to a node. This
 * generally means checking the type of the node (to see if it knows how to
 * handle it) and then creating new child nodes or edges to existing nodes.
 * 
 * @author Darien Hager
 */
public interface NodeHandler {

    /**
     * Makes changes to the graph based on the node being entered by the given 
     * edge. The node the edge points to may or may not have been encountered
     * by this handler before.
     * 
     * If the handler is unable to do anything, it may simply return false.
     * 
     * @param graph The graph to be inspected and modified
     * @param incomign The edge being checked.
     * @return True if other handlers should be offered the opportunity to process this node.
     */
    public HandlingResult handle(MapDepGraph graph, Edge incoming);

    /**
     * Potentially called after {@link #handle(com.technofovea.packbsp.crawling2.MapDepGraph, com.technofovea.packbsp.crawling2.Edge)},
     * this method informs handlers that the graph has de-duplicated certain
     * node instances. This is useful for any handler which is maintaining its
     * own indirect information about nodes, particularly those that rely on 
     * weak references.
     * 
     * @param swaps Pairs of nodes, first the instance that was given, then the
     * instance which is actually stored in the graph.
     */
    public void replacedNodes(MapDepGraph graph, Collection<Pair<Node>> swaps);
}
