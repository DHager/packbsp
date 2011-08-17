/*
 * 
 */
package com.technofovea.packbsp.crawling2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Darien Hager
 */
public abstract class AbstractHandler<N extends Node, V> implements NodeHandler {
    protected Map<NodeWithContext, V> storage = new HashMap<NodeWithContext, V>();

    public void edgeAdded(MapDepGraph graph, Edge e) {
    }

    public void edgeRemoved(MapDepGraph graph, Edge e) {
    }

    public void graphAdded(MapDepGraph g) {
    }

    public void graphRemoved(MapDepGraph g) {
        Iterator<NodeWithContext> iter = storage.keySet().iterator();
        while (iter.hasNext()) {
            NodeWithContext sk = iter.next();
            if (sk.g.equals(g)) {
                iter.remove();

            }
        }
    }

    public void nodeAdded(MapDepGraph graph, Node n) {
    }

    public void nodeRemoved(MapDepGraph graph, Node n) {
        final NodeWithContext sk = new NodeWithContext(graph, n);
        storage.remove(sk);
    }

    public boolean handle(MapDepGraph graph, Node target) throws HandlerException {
        final Class<N> targetClass = getTargetClass();
        if (targetClass.isAssignableFrom(target.getClass())) {
            N casted = (N) target;
            return handleInternal(graph, casted);
        } else {
            return true;
        }
    }

    public abstract Class<N> getTargetClass();

    public abstract boolean handleInternal(MapDepGraph graph, N target) throws HandlerException;
}