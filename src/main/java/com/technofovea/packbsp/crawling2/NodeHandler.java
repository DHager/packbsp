package com.technofovea.packbsp.crawling2;

/**
 * Handlers are responsible for reacting when a new edge is drawn to a node. This
 * generally means checking the type of the node (to see if it knows how to
 * handle it) and then creating new child nodes or edges to existing nodes.
 * 
 * @author Darien Hager
 */
public interface NodeHandler extends DependencyProjectListener {

    /**
     * Inspects a given edge in the context of a particular graph, potentially
     * changing the graph in the process
     * 
     * @param graph The graph to be inspected and modified
     * @param target The node being visited
     * @return True if other handlers should be offered the opportunity to process this node.
     */
    public boolean handle(MapDepGraph graph, Node target) throws HandlerException;

}
