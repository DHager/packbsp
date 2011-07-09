/*
 * 
 */
package com.technofovea.packbsp.crawling2.impl;

import com.technofovea.packbsp.assets.AssetHit;
import com.technofovea.packbsp.crawling2.Edge;
import com.technofovea.packbsp.crawling2.GraphAddition;
import com.technofovea.packbsp.crawling2.HandlingResult;
import com.technofovea.packbsp.crawling2.Layer;
import com.technofovea.packbsp.crawling2.LocatedAssetNode;
import com.technofovea.packbsp.crawling2.MapDepGraph;
import com.technofovea.packbsp.crawling2.Node;
import com.technofovea.packbsp.crawling2.NodeHandler;
import com.technofovea.packbsp.crawling2.RelativeAssetNode;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

/**
 *
 * @author Darien Hager
 */
public class AssetResolvingHandler implements NodeHandler {

    

    protected NodeMetaStorage nodeMap;
    
    public HandlingResult handle(MapDepGraph graph, Edge incoming) {
        final Node n = graph.getDest(incoming);
        if (!( n instanceof RelativeAssetNode )) {
            return HandlingResultImpl.createSkip();
        }
        Set<GraphAddition> newItems = new HashSet<GraphAddition>();
        final RelativeAssetNode target = (RelativeAssetNode) n;
        final String path = target.getPath();
        final Layer layer = incoming.getLayer();

        //TODO use layer and path to get assets
        final Collection<AssetHit> layerHits = null;

        for (AssetHit hit : layerHits) {
            final Edge e = new BasicEdge(layer);
            LocatedAssetNode c = createAbs(graph, target, hit);

            target.createLocatedNode(hit);
            LocatedAssetNode c2 = graph.getVertexIfContained(c);
            if (c2 != null) {
                c = c2;
            }
            newItems.add(new GraphAdditionImpl(e, c));
        }

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
