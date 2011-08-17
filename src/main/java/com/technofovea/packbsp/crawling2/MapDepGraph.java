package com.technofovea.packbsp.crawling2;

import com.technofovea.packbsp.assets.AssetLocator;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapDepGraph implements DirectedGraph<Node, Edge> {

    private static final Logger logger = LoggerFactory.getLogger(MapDepGraph.class);
    protected DirectedSparseGraph<Node, Edge> coreGraph = new DirectedSparseGraph<Node, Edge>();
    protected ObservableGraph<Node, Edge> delegate = new ObservableGraph<Node, Edge>(coreGraph);
    protected Node root;
    protected DependencyProject parent;
    protected AssetLocator locator;

    public MapDepGraph(DependencyProject parent,AssetLocator locator) {
        this.root = new RootNode(this);
        this.locator = locator;
        this.parent = parent;
        delegate.addVertex(root); 
        
        //Add listener only after symbolic root is inserted
        delegate.addGraphEventListener(parent);

    }

    public DependencyProject getProject() {
        return parent;
    }

    public Node getRoot() {
        return root;
    }

    public AssetLocator getLocator() {
        return locator;
    }

    /*
     * 
     * Delegates only below this point
     * 
     * 
     */
    public boolean removeVertex(Node vertex) {
        if(root.equals(vertex)){
            throw new IllegalStateException("Cannot remove symbolic root node from dependency graph");
        }
        return delegate.removeVertex(vertex);
    }

    public boolean removeEdge(Edge edge) {
        return delegate.removeEdge(edge);
    }

    public boolean addVertex(Node vertex) {
        return delegate.addVertex(vertex);
    }

    public boolean addEdge(Edge e, Node v1, Node v2) {
        return delegate.addEdge(e, v1, v2);
    }

    public boolean addEdge(Edge e, Node v1, Node v2, EdgeType edgeType) {
        return delegate.addEdge(e, v1, v2, edgeType);
    }

    public boolean addEdge(Edge edge, Collection<? extends Node> vertices) {
        return delegate.addEdge(edge, vertices);
    }

    public int outDegree(Node vertex) {
        return delegate.outDegree(vertex);
    }

    public boolean isSuccessor(Node v1, Node v2) {
        return delegate.isSuccessor(v1, v2);
    }

    public boolean isSource(Node vertex, Edge edge) {
        return delegate.isSource(vertex, edge);
    }

    public boolean isPredecessor(Node v1, Node v2) {
        return delegate.isPredecessor(v1, v2);
    }

    public boolean isNeighbor(Node v1, Node v2) {
        return delegate.isNeighbor(v1, v2);
    }

    public boolean isIncident(Node vertex, Edge edge) {
        return delegate.isIncident(vertex, edge);
    }

    public boolean isDest(Node vertex, Edge edge) {
        return delegate.isDest(vertex, edge);
    }

    public int inDegree(Node vertex) {
        return delegate.inDegree(vertex);
    }

    public Collection<Node> getVertices() {
        return delegate.getVertices();
    }

    public int getVertexCount() {
        return delegate.getVertexCount();
    }

    public Collection<Node> getSuccessors(Node vertex) {
        return delegate.getSuccessors(vertex);
    }

    public int getSuccessorCount(Node vertex) {
        return delegate.getSuccessorCount(vertex);
    }

    public Node getSource(Edge directed_edge) {
        return delegate.getSource(directed_edge);
    }

    public Collection<Node> getPredecessors(Node vertex) {
        return delegate.getPredecessors(vertex);
    }

    public int getPredecessorCount(Node vertex) {
        return delegate.getPredecessorCount(vertex);
    }

    public Collection<Edge> getOutEdges(Node vertex) {
        return delegate.getOutEdges(vertex);
    }

    public Node getOpposite(Node vertex, Edge edge) {
        return delegate.getOpposite(vertex, edge);
    }

    public Collection<Node> getNeighbors(Node vertex) {
        return delegate.getNeighbors(vertex);
    }

    public int getNeighborCount(Node vertex) {
        return delegate.getNeighborCount(vertex);
    }

    public Collection<Node> getIncidentVertices(Edge edge) {
        return delegate.getIncidentVertices(edge);
    }

    public Collection<Edge> getIncidentEdges(Node vertex) {
        return delegate.getIncidentEdges(vertex);
    }

    public int getIncidentCount(Edge edge) {
        return delegate.getIncidentCount(edge);
    }

    public Collection<Edge> getInEdges(Node vertex) {
        return delegate.getInEdges(vertex);
    }

    public Pair<Node> getEndpoints(Edge edge) {
        return delegate.getEndpoints(edge);
    }

    public Collection<Edge> getEdges(EdgeType edgeType) {
        return delegate.getEdges(edgeType);
    }

    public Collection<Edge> getEdges() {
        return delegate.getEdges();
    }

    public EdgeType getEdgeType(Edge edge) {
        return delegate.getEdgeType(edge);
    }

    public int getEdgeCount(EdgeType edge_type) {
        return delegate.getEdgeCount(edge_type);
    }

    public int getEdgeCount() {
        return delegate.getEdgeCount();
    }

    public Node getDest(Edge directed_edge) {
        return delegate.getDest(directed_edge);
    }

    public EdgeType getDefaultEdgeType() {
        return delegate.getDefaultEdgeType();
    }

    public Collection<Edge> findEdgeSet(Node v1, Node v2) {
        return delegate.findEdgeSet(v1, v2);
    }

    public Edge findEdge(Node v1, Node v2) {
        return delegate.findEdge(v1, v2);
    }

    public int degree(Node vertex) {
        return delegate.degree(vertex);
    }

    public boolean containsVertex(Node vertex) {
        return delegate.containsVertex(vertex);
    }

    public boolean containsEdge(Edge edge) {
        return delegate.containsEdge(edge);
    }

    public boolean addEdge(Edge edge, Collection<? extends Node> vertices, EdgeType edge_type) {
        return delegate.addEdge(edge, vertices, edge_type);
    }
}
