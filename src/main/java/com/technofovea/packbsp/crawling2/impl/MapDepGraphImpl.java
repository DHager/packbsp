package com.technofovea.packbsp.crawling2.impl;

import com.technofovea.packbsp.crawling2.Edge;
import com.technofovea.packbsp.crawling2.Layer;
import com.technofovea.packbsp.crawling2.MapDepGraph;
import com.technofovea.packbsp.crawling2.Node;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Darien Hager
 */
public class MapDepGraphImpl extends DirectedSparseMultigraph<Node, Edge> implements MapDepGraph {

    protected class RootNode implements Node {
    }
    private static final Logger logger = LoggerFactory.getLogger(MapDepGraph.class);
    protected final Node root = new RootNode();
    protected final Collection<MapLayer> mapLayers = new ArrayList<MapLayer>();
    protected final LinkedHashSet<Edge> newEdges = new LinkedHashSet<Edge>();

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public MapDepGraphImpl(Collection<Node> maps) {
        this.addVertex(this.root);
        for (Node map : maps) {

            MapLayer layer = new MapLayer(map);
            mapLayers.add(layer);



            boolean added = false;

            added = this.addVertex(map);
            assert ( added );
            added = this.addEdge(new BasicEdge(layer, "Rooted Map"), this.root, map);
            assert ( added );
        }
    }

    protected Map<Layer, Collection<Edge>> groupEdges(Collection<Edge> outs) {
        final Map<Layer, Collection<Edge>> ret = new HashMap<Layer, Collection<Edge>>();
        for (Edge e : outs) {
            final Layer l = e.getLayer();
            if (ret.containsKey(l)) {
                ret.get(l).add(e);
            } else {
                final HashSet<Edge> set = new HashSet<Edge>();
                set.add(e);
                ret.put(l, set);
            }
        }
        return ret;
    }

    public Map<Layer, Collection<Edge>> getOutEdgesByLayer(Node vertex) {
        Collection<Edge> outs = super.getOutEdges(vertex);
        if (outs == null) {
            throw new IllegalArgumentException("Vertex is not contained in graph");
        }
        return groupEdges(outs);
    }

    public Map<Layer, Collection<Edge>> getInEdgesByLayer(Node vertex) {
        Collection<Edge> ins = super.getOutEdges(vertex);
        if (ins == null) {
            throw new IllegalArgumentException("Vertex is not contained in graph");
        }
        return groupEdges(ins);
    }

    public <V extends Node> V addUniqueVertex(V vertex) {
        boolean added = super.addVertex(vertex);
        if (added) {
            return vertex;
        } else {
            return getVertexIfContained(vertex);
        }
    }

    public <V extends Node> V getVertexIfContained(V vertex) {
        for (Node stored : vertices.keySet()) {
            if (stored == vertex) {
                return vertex;
            }
            if (stored.equals(vertex)) {
                assert ( vertex.getClass().isAssignableFrom(stored.getClass()) );
                return (V) stored;
            }
        }
        return null;
    }

    public Edge getUnhandledEdge() {
        Iterator<Edge> i = newEdges.iterator();
        if (i.hasNext()) {
            return i.next();
        } else {
            return null;
        }

    }

    public boolean markEdgeHandled(Edge e) {
        return newEdges.remove(e);
    }

    @Override
    public boolean addEdge(Edge edge, Pair<? extends Node> endpoints, EdgeType edgeType) {
        final boolean added = super.addEdge(edge, endpoints, edgeType);
        if (added) {
            newEdges.add(edge);
        }
        return added;
    }

    @Override
    public boolean addEdge(Edge edge, Collection<? extends Node> vertices) {
        final boolean added = super.addEdge(edge, vertices);
        if (added) {
            newEdges.add(edge);
        }
        return added;
    }

    @Override
    public boolean addEdge(Edge edge, Collection<? extends Node> vertices, EdgeType edgeType) {
        final boolean added = super.addEdge(edge, vertices, edgeType);
        if (added) {
            newEdges.add(edge);
        }
        return added;
    }

    @Override
    public boolean addEdge(Edge e, Node v1, Node v2) {
        final boolean added = super.addEdge(e, v1, v2);
        if (added) {
            newEdges.add(e);
        }
        return added;
    }

    @Override
    public boolean addEdge(Edge e, Node v1, Node v2, EdgeType edge_type) {
        final boolean added = super.addEdge(e, v1, v2, edge_type);
        if (added) {
            newEdges.add(e);
        }
        return added;
    }

    @Override
    public boolean addEdge(Edge edge, Pair<? extends Node> endpoints) {
        final boolean added = super.addEdge(edge, endpoints);
        if (added) {
            newEdges.add(edge);
        }
        return added;
    }
}
