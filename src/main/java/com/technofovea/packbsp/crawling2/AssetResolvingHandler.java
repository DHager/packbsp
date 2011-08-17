/*
 * 
 */
package com.technofovea.packbsp.crawling2;

import com.technofovea.packbsp.assets.AssetHit;
import com.technofovea.packbsp.assets.AssetLocator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visits nodes that represent a relative path and adds children, potentially one 
 * child for every layer.
 * @author Darien Hager
 */
public class AssetResolvingHandler extends AbstractHandler<RelativeAssetNode, Boolean> implements NodeHandler {

    private final Logger logger = LoggerFactory.getLogger(AssetResolvingHandler.class);

    @Override
    public Class<RelativeAssetNode> getTargetClass() {
        return RelativeAssetNode.class;
    }

    @Override
    public boolean handleInternal(MapDepGraph graph, RelativeAssetNode target) throws HandlerException {

        final NodeWithContext sk = new NodeWithContext(graph, target);
        // Test if already handled
        if (Boolean.TRUE.equals(storage.get(sk))) {
            logger.debug("Node {} already handled");
            return true;
        }

        final AssetLocator locator = graph.getLocator();
        final String path = target.getPath();

        final List<AssetHit> hits = locator.locate(path);
        if (hits.isEmpty()) {
            logger.warn("Could not resolve relative path {}", path);
            //TODO create placeholder node
            return false;
        }

        AssetHit firstHit = hits.get(0);
        Node absNode = target.createLocatedNode(firstHit);

        storage.put(sk, Boolean.TRUE);
        graph.addVertex(absNode);
        final Edge e = new BasicEdge();
        graph.addEdge(e, target, absNode);

        return false;
    }
}
