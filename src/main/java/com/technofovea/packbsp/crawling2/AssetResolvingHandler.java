/*
 * 
 */
package com.technofovea.packbsp.crawling2;

import com.technofovea.packbsp.assets.AssetHit;
import com.technofovea.packbsp.assets.AssetLocator;
import edu.uci.ics.jung.graph.util.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visits nodes that represent a relative path and adds children, potentially one 
 * child for every layer.
 * @author Darien Hager
 */
public class AssetResolvingHandler extends AbstractHandler implements NodeHandler {

    protected class HandlingRecord {

        protected NodeMetaStorage<Set<Layer>> store = new NodeMetaStorage<Set<Layer>>();

        public synchronized void add(Node n, Layer l) {
            Set<Layer> s = store.get(n);
            if (s == null) {
                s = new HashSet<Layer>();
                store.put(n, s);
            }
            s.add(l);
        }

        public synchronized boolean contains(Node n, Layer l) {
            Set<Layer> s = store.get(n);
            if (s == null) {
                return false;
            }
            return s.contains(l);
        }
    }
    private final Logger logger = LoggerFactory.getLogger(AssetResolvingHandler.class);
    protected Map<Layer, AssetLocator> locators = new HashMap<Layer, AssetLocator>();
    protected HandlingRecord record = new HandlingRecord();

    public AssetResolvingHandler(Map<Layer, AssetLocator> locators) {
        this.locators.putAll(locators);
    }

    public HandlingResult handle(MapDepGraph graph, Edge incoming) {
        final Node n = graph.getDest(incoming);
        // Test nodetype
        if (!( n instanceof RelativeAssetNode )) {
            return HandlingResultImpl.createSkip();
        }
        final Layer layer = incoming.getLayer();
        // Test if already handled
        if (record.contains(n, layer)) {
            logger.debug("Node {} already handled for layer {}", n, layer);
            return HandlingResultImpl.createSkip();
        }

        // Test if layer is recognized
        final AssetLocator locator = locators.get(layer);
        if (locator == null) {
            logger.warn("Unable to handle layer {}", layer);
            return HandlingResultImpl.createSkip();
        }


        final Set<GraphAddition> newItems = new HashSet<GraphAddition>();
        final RelativeAssetNode target = (RelativeAssetNode) n;
        final String path = target.getPath();

        final List<AssetHit> layerHits = locator.locate(path);
        if (layerHits.isEmpty()) {
            logger.warn("Could not resolve relative path {} for layer {}", path, layer);
            return HandlingResultImpl.createSkip();
        }

        AssetHit hit = layerHits.get(0);
        final Edge e = new BasicEdge(layer);
        LocatedAssetNode c = createAbs(graph, target, hit);

        target.createLocatedNode(hit);
        LocatedAssetNode c2 = graph.getVertexIfContained(c);
        if (c2 != null) {
            c = c2;
        }
        newItems.add(new GraphAdditionImpl(e, c));
        return new HandlingResultImpl(newItems, false);
    }

    public void replacedNodes(MapDepGraph graph, Collection<Pair<Node>> swaps) {
        /**
         * No implementation because we do not track data about created nodes
         */
        return;
    }

    protected LocatedAssetNode createAbs(MapDepGraph graph, RelativeAssetNode target, AssetHit hit) {
        final LocatedAssetNode c = target.createLocatedNode(hit);
        final LocatedAssetNode c2 = graph.getVertexIfContained(c);
        if (c2 == null) {
            return c;
        } else {
            return c2;
        }
    }
}
