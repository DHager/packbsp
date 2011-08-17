/*
 * 
 */
package com.technofovea.packbsp.crawling2;

import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import java.util.List;

/**
 * Adapts JUNG's {@link GraphEvent} system to callbacks which are more convenient 
 * for our specialized purposes.
 * 
 * @author Darien Hager
 */
public class GraphEventAdapter implements GraphEventListener<Node, Edge> {

    protected List<DependencyProjectListener> listeners;

    public GraphEventAdapter(List<DependencyProjectListener> listeners) {
        this.listeners = listeners;
    }

    public void handleGraphEvent(GraphEvent<Node, Edge> evt) {
        if (!( evt.getSource() instanceof MapDepGraph )) {
            assert ( false );
            return;
        }
        final MapDepGraph src = (MapDepGraph) evt.getSource();

        boolean adding = false;
        switch (evt.getType()) {
            case VERTEX_ADDED:
                adding = true; // Fallthrough
            case VERTEX_REMOVED:
                assert ( evt instanceof GraphEvent.Vertex );
                final GraphEvent.Vertex<Node, Edge> vertexEvent = (GraphEvent.Vertex) evt;
                final Node targ = vertexEvent.getVertex();
                if (adding) {
                    for (DependencyProjectListener l : listeners) {
                        l.nodeAdded(src, targ);
                    }
                } else {
                    for (DependencyProjectListener l : listeners) {
                        l.nodeRemoved(src, targ);
                    }
                }
                break;
            case EDGE_ADDED:
                adding = true; // Fallthrough
            case EDGE_REMOVED:
                assert ( evt instanceof GraphEvent.Edge );
                final GraphEvent.Edge<Node, Edge> edgeEvent = (GraphEvent.Edge) evt;
                final Edge targetEdge = edgeEvent.getEdge();
                if (adding) {
                    for (DependencyProjectListener l : listeners) {
                        l.edgeAdded(src, targetEdge);
                    }
                } else {
                    for (DependencyProjectListener l : listeners) {
                        l.edgeRemoved(src, targetEdge);
                    }
                }
                break;
        }
    }
}
