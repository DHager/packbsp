/**
 * Copyright (C) 2011 Darien Hager
 *
 * This code is part of the "PackBSP" project, and is licensed under
 * a Creative Commons Attribution-ShareAlike 3.0 Unported License. For
 * either a summary of conditions or the full legal text, please visit:
 *
 * http://creativecommons.org/licenses/by-sa/3.0/
 *
 * Permissions beyond the scope of this license may be available
 * at http://technofovea.com/ .
 */
/*
 * 
 */
package com.technofovea.packbsp.crawling;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class GraphContext {

    private static final Logger logger = LoggerFactory.getLogger(GraphContext.class);

    protected static class CycleDetectedException extends Exception {

        public CycleDetectedException(String message, Throwable cause) {
            super(message, cause);
        }

        public CycleDetectedException(String message) {
            super(message);
        }
    }

    protected static class Segment {

        public Segment(Edge incomingEdge, Node target) {
            this.incomingEdge = incomingEdge;
            this.target = target;
        }
        Edge incomingEdge;
        Node target;
        Set<Edge> testedOutEdges = new HashSet<Edge>();
    }
    LinkedList<Segment> segments = new LinkedList<Segment>();
    Set<Node> segmentNodes = new HashSet<Node>();
    Set<Node> visited = new HashSet<Node>();
    DependencyGraph graph;
    Node startNode;
    Set<Node> requiredNodes = new HashSet<Node>();

    public GraphContext(DependencyGraph graph, Node start) {
        this.startNode = start;
        this.graph = graph;
        segments.add(new Segment(null, this.startNode));
        segmentNodes.add(this.startNode);
        requiredNodes.add(this.startNode);

    }

    public Node getStartNode() {
        return startNode;
    }

    public String printSegments() {
        StringBuffer sb = new StringBuffer();
        if (isEmpty()) {
            return "{empty}";
        }
        sb.append(segments.get(0).target);
        for (int i = 1; i < segments.size(); i++) {
            Segment s = segments.get(i);
            for (int j = 0; j < i; j++) {
                sb.append("    ");
            }
            sb.append("--");
            sb.append(s.incomingEdge.getName());
            sb.append("-->");
            sb.append(s.target);
            sb.append("\n");
        }
        return sb.toString();
    }

    public void dropLevel() {
        if (isEmpty()) {
            return;
        }

        Segment formerTop = segments.removeLast();
        visited.add(formerTop.target);
        segmentNodes.remove(formerTop.target);
        if (!isEmpty()) {
            segments.peekLast().testedOutEdges.add(formerTop.incomingEdge);
        }
    }

    public void addLevel(Edge e) {
        assert (graph.getOutEdges(getCurrentNode()).contains(e));
        Segment s = new Segment(e, graph.getDest(e));
        segments.add(s);
        segmentNodes.add(s.target);

        if (currentPathIsRequired()) {
            requiredNodes.add(s.target);
        }
    }

    public boolean currentPathIsRequired() {
        for (Segment s : segments) {
            if (s.incomingEdge == null) {
                continue;
            }
            if (s.incomingEdge.isOptional()) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmpty() {
        return segments.size() < 1;
    }

    protected Segment getCurrentSegment() {
        if (isEmpty()) {
            return null;
        }
        return segments.peekLast();

    }

    public Edge getCurrentIncomingEdge() {
        if (isEmpty()) {
            return null;
        }
        return getCurrentSegment().incomingEdge;
    }

    public Node getCurrentNode() {
        if (isEmpty()) {
            return null;
        }
        return getCurrentSegment().target;
    }

    /**
     * Returns one possible outgoing edge (no particular order) from the current
     * node leading to an unvisited node.
     * 
     * @return The next edge to visit.
     * @throws CycleDetectedException If a cycle was encountered. Re-calling this
     * method will attempt to recover from the problem, although additional exceptions
     * may be thrown if other cyclic links are encountered.
     */
    public Edge getNextEdge() throws CycleDetectedException {
        Set<Edge> edges = getNextTargets();
        if (edges.size() == 0) {
            return null;
        }
        Iterator<Edge> i = edges.iterator();
        assert (i.hasNext());
        return i.next();
    }

    /**
     * Returns a set of edges that can be visited next.
     * @return A set of outgoing edges from the current node whose targets have not
     * been visited.
     * @throws CycleDetectedException If a cycle was found. Re-calling this method
     * should no longer throw an exception (at least, not for the same cycle) thus
     * allowing recovery.
     */
    protected Set<Edge> getNextTargets() throws CycleDetectedException {
        if (isEmpty()) {
            return new HashSet<Edge>();
        }
        final Node currentNode = getCurrentNode();
        // Get all possible targets
        Set<Edge> outEdges = new HashSet<Edge>(graph.getOutEdges(currentNode));

        // Don't re-check edges we've explicitly marked as tested
        outEdges.removeAll(getCurrentSegment().testedOutEdges);

        // Now check potential targets against visited nodes
        Set<Edge> nodeScreenedEdges = new HashSet<Edge>();
        final boolean currentPathRequired = currentPathIsRequired();
        for (Edge e : outEdges) {
            Node n = graph.getDest(e);

            if (currentPathRequired && (!e.isOptional())) {
                requiredNodes.add(n);
            }
            if (visited.contains(n)) {
                nodeScreenedEdges.add(e);
            }
            if (segmentNodes.contains(n)) {
                // Damn! We've got a cycle in our "acyclic" graph!

                // First, make sure subsequent invocations of this method won't
                // be blocked by the problem, by marking the path as visited
                getCurrentSegment().testedOutEdges.add(e);

                // Now throw the exception
                throw new CycleDetectedException("Detected a cycle from node "+getCurrentNode()+" through edge " + e
                        + " to node " + n);
            }

        }

        // We don't want to do these dupe-nodes
        outEdges.removeAll(nodeScreenedEdges);

        // For speed, let's add these guys to the first check for future calls
        getCurrentSegment().testedOutEdges.addAll(nodeScreenedEdges);
        return outEdges;


    }
}
