/*
 * 
 */
package com.technofovea.packbsp.crawling2;

import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * Binds one or more {@link MapDepGraph} instances together, allowing them to share
 * nodes.
 * 
 * @author Darien Hager
 */
public class DependencyProject implements GraphEventListener<Node, Edge> {

    protected Map<RootNode, MapDepGraph> graphs = new HashMap<RootNode, MapDepGraph>();
    protected final List<DependencyProjectListener> listeners = new CopyOnWriteArrayList<DependencyProjectListener>();
    protected GraphEventAdapter eventAdapter = new GraphEventAdapter(listeners);
    protected Map<MapDepGraph, List<Node>> unvisitedNodes = new HashMap<MapDepGraph, List<Node>>();
    protected List<NodeHandler> handlers = new ArrayList<NodeHandler>();

    public List<NodeHandler> getHandlers() {
        return new ArrayList<NodeHandler>(handlers);
    }

    public void setHandlers(List<NodeHandler> handlers) {
        this.handlers.clear();
        this.handlers.addAll(handlers);
    }

    
    
    
    public void handleGraphEvent(GraphEvent<Node, Edge> evt) {
        eventAdapter.handleGraphEvent(evt);
    }

    protected boolean markNodeVisited(MapDepGraph context, Node n) {
        List<Node> unvisitedList = unvisitedNodes.get(context);
        if (unvisitedList == null) {
            return false;
        }
        return unvisitedList.remove(n);
    }
}
