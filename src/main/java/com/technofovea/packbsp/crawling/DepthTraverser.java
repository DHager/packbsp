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
package com.technofovea.packbsp.crawling;

import com.technofovea.packbsp.crawling.GraphContext.CycleDetectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class helps navigate the node graph in a depth-first first-in-last-out manner, designed
 * to process each node, add successor nodes, and move on in an iterative fashion.
 * @author Darien Hager
 */
public abstract class DepthTraverser {

    private static final Logger logger = LoggerFactory.getLogger(DepthTraverser.class);
    DependencyGraph graph;
    boolean didFirst = false;
    GraphContext ctx;

    public DepthTraverser(DependencyGraph graph, Node start) {
        this.graph = graph;
        if (!graph.containsVertex(start)) {
            throw new IllegalArgumentException("Start node is not contained in graph");
        }
        reset(start);

    }

    public void reset(Node start) {
        if (!graph.containsVertex(start)) {
            throw new IllegalArgumentException("Given node must exist within the graph");
        }
        ctx = new GraphContext(graph, start);
        didFirst = false;
    }

    public DependencyGraph getGraph() {
        return graph;
    }

    public Node getStart() {
        return ctx.getStartNode();
    }

    public boolean isDone() {
        return didFirst && ctx.isEmpty();
    }

    public void step() throws TraversalException {
        if (!didFirst) {
            logger.trace("Doing initial node");
            didFirst = true;
            handleNodeEnter(null, getStart());
            return;
        }

        Edge nextEdge;
        try {
            nextEdge = ctx.getNextEdge();
        } catch (CycleDetectedException ex) {
            TraversalException e2 = new TraversalException("A cycle was detected in the graph.", ex);
            e2.setRecoverable(true);
            throw (e2);
            
        }
        // If no targets, keep dropping until we have some
        while (nextEdge == null) {
            logger.trace("No remaining edges");
            if (ctx.isEmpty()) {
                logger.trace("No remaining edges");
                probablyComplete();
                return;
            }
            final Edge lastEdge = ctx.getCurrentIncomingEdge();
            final Node lastNode = ctx.getCurrentNode();
            logger.trace("Retreating through {} from {}", lastEdge, lastNode);
            handleNodeExit(lastEdge, lastNode);
            ctx.dropLevel();
            try {
                logger.trace("Retrieving next edge to work on");
                nextEdge = ctx.getNextEdge();
            } catch (CycleDetectedException ex) {
                TraversalException e2 = new TraversalException("A cycle was detected in the graph.", ex);
                e2.setRecoverable(true);
                throw (e2);
            }
        }

        // Do one target
        final Node target = graph.getDest(nextEdge);
        logger.trace("Advancing through {} to {}", nextEdge, target);
        handleNodeEnter(nextEdge, target);
        ctx.addLevel(nextEdge);
    }

    protected abstract void handleNodeEnter(Edge edge, Node node) throws TraversalException;

    protected abstract void handleNodeExit(Edge edge, Node node) throws TraversalException;

    /**
     * Provided for the conveniene of subclasses, calling this method suggests
     * that the depth-first traversal is complete and any necessary cache
     * cleanup may be a good idea.
     *
     * The caller does *not* guarantee that all traversal is actually complete,
     * and this method may be called multiple times.
     *
     * By default does nothing.
     */
    protected void probablyComplete() {
        logger.trace("Hit probably-complete condition");
        /* This space intentionally left blank. */
    }
}
