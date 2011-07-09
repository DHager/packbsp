/*
 * 
 */
package com.technofovea.packbsp.crawling2;

import edu.uci.ics.jung.graph.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for managing incremental interaction between a {@link MapDepGraph}
 * and a series of {@link NodeHandler} instances. 
 * @author Darien Hager
 */
public class GraphExplorer {

    private static final Logger logger = LoggerFactory.getLogger(GraphExplorer.class);
    protected List<NodeHandler> handlers = new ArrayList<NodeHandler>();
    protected MapDepGraph graph;

    public List<NodeHandler> getHandlers() {
        return Collections.unmodifiableList(handlers);
    }

    public void setHandlers(List<NodeHandler> handlers) {
        this.handlers.clear();
        this.handlers.addAll(handlers);
    }

    public MapDepGraph getGraph() {
        return graph;
    }

    public void setGraph(MapDepGraph graph) {
        this.graph = graph;
    }

    /**
     * Attempt to handle a new edge, if any.
     * @return True if some work was done, false if there were no new edges left to process.
     */
    public boolean doStep() {
        Edge edge = graph.getUnhandledEdge();
        if (edge == null) {
            return false;
        }

        for (NodeHandler handler : handlers) {
            final HandlingResult res;
            try {
                res = handler.handle(graph, edge);
            }
            catch (HandlerException ex) {
                logger.error("Unexpected error from handler", ex);
                continue; // Next handler
            }
            final Collection<Pair<Node>> swaps = new ArrayList<Pair<Node>>();
            final Set<GraphAddition> newEdges = res.getAdditions();
            for (GraphAddition link : newEdges) {
                final Node parentVertex = graph.getDest(edge);
                final Node newVertex = link.getChild();
                final Node usedVertex = graph.addUniqueVertex(newVertex);
                assert ( newVertex.equals(usedVertex) );
                graph.addEdge(link.getConnection(), parentVertex, usedVertex);
                if (newVertex != usedVertex) {
                    swaps.add(new Pair<Node>(newVertex, usedVertex));
                }
            }
            handler.replacedNodes(graph, swaps);
        }
        return true;
    }
}
