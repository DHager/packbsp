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

import com.technofovea.packbsp.PackbspException;
import com.technofovea.packbsp.assets.AssetHit;
import com.technofovea.packbsp.assets.AssetLocator;
import com.technofovea.packbsp.crawling.AssetAlternative.CollisionType;
import com.technofovea.packbsp.crawling.handlers.HandlingException;
import com.technofovea.packbsp.crawling.handlers.MapHandler;
import com.technofovea.packbsp.crawling.handlers.ModelHandler;
import com.technofovea.packbsp.crawling.handlers.ParticleHandler;
import com.technofovea.packbsp.crawling.handlers.PhyHandler;
import com.technofovea.packbsp.crawling.handlers.SkyboxHandler;
import com.technofovea.packbsp.crawling.handlers.SoundscapeHandler;
import com.technofovea.packbsp.crawling.handlers.VmtHandler;
import com.technofovea.packbsp.crawling.nodes.MapNode;
import com.technofovea.packbsp.crawling.nodes.MaterialNode;
import com.technofovea.packbsp.crawling.nodes.MiscFileNode;
import com.technofovea.packbsp.crawling.nodes.ModelNode;
import com.technofovea.packbsp.crawling.nodes.MultiPathNode;
import com.technofovea.packbsp.crawling.nodes.ParticleManifestNode;
import com.technofovea.packbsp.crawling.nodes.PhyNode;
import com.technofovea.packbsp.crawling.nodes.SkyboxNode;
import com.technofovea.packbsp.crawling.nodes.SoundscapeNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This subclass layers on node-handling behavior to DepthTraverser
 * @author Darien Hager
 */
public class DependencyExpander extends DepthTraverser {

    protected static class DecisionData {

        Decision decision;
        File fileSource;
        boolean isTempFile;

        public DecisionData(Decision decision) {
            this(decision, null, true);
        }

        public DecisionData(Decision decision, File fileSource) {
            this(decision, fileSource, false);
        }

        public DecisionData(Decision d, File f, boolean isTempFile) {
            this.decision = d;
            this.fileSource = f;
            this.isTempFile = isTempFile;
        }
    }
    private static final Logger logger = LoggerFactory.getLogger(DependencyExpander.class);
    AssetLocator locator;
    VmtHandler materialHandler = new VmtHandler(Collections.EMPTY_SET); //TODO load/inject materials
    ModelHandler modelHandler = new ModelHandler();
    PhyHandler phyHandler = new PhyHandler();
    ParticleHandler particleHandler = new ParticleHandler();
    SoundscapeHandler soundscapeHandler = new SoundscapeHandler();
    // The advantage of CopyOnWrite-list is that you don't need to synchronize iterations
    final CopyOnWriteArrayList<CrawlListener> listeners = new CopyOnWriteArrayList<CrawlListener>();

    public DependencyExpander(DependencyGraph graph, Node start, AssetLocator locator) throws PackbspException {
        super(graph, start);
        this.locator = locator;
        


    }

    protected void handleNode(Edge path, Node current) throws TraversalException {
    }

    @Override
    protected void handleNodeEnter(Edge edge, Node node) throws TraversalException {
        fireNodeEnterEvent(edge, node);

        List<OutgoingPair> successors = handleNodeInternal(edge, node);
        for (OutgoingPair p : successors) {
            Node successor = p.getTarget();
            Edge connection = p.getEdge();

            graph.addVertex(successor);
            graph.addEdge(connection, node, successor);
        }
    }

    @Override
    protected void handleNodeExit(Edge edge, Node node) throws TraversalException {
        fireNodeExitEvent(edge, node);
    }

    public void addListener(CrawlListener l) {
        // We need to synchronize here only so we can reliably avoid dupes
        synchronized (listeners) {
            if (listeners.contains(l)) {
                return;
            }
            listeners.add(l);
        }
    }

    public void removeListener(CrawlListener l) {
        listeners.remove(l);
    }

    protected void fireSkipEvent(Edge edge, Node node, String filePath) {
        for (CrawlListener l : listeners) {
            l.nodeContentSkipped(edge, node, filePath);
        }
    }

    protected void fireMissingEvent(Edge edge, Node node, String relativePath) {
        for (CrawlListener l : listeners) {
            l.nodeContentMissing(edge, node, relativePath);
        }
    }

    protected void firePackingEvent(Edge edge, Node node, String relativePath, File dataSource) {
        for (CrawlListener l : listeners) {
            l.nodePackingFound(edge, node, relativePath, dataSource);
        }
    }

    protected void firePrepackedEvent(Edge edge, Node node, String relativePath) {
        /*
         * Currently not implemented.
         * An example of this case would be patch-wvt files which are
         * automatically embedded into the map BSP file by compilation tools.
         * They are necessary, "pre-packed", need to be parsed, but don't exist
         * anywhere else and require no direct action.
         *
         * 
         */
        fireSkipEvent(edge, node, relativePath); //For now, treat them like items found in the GCFs, 

        /*
        for (CrawlListener l : listeners) {
        l.nodePrePacked(edge, node, relativePath);
        }
         */
    }

    protected void fireCollisionEvent(Edge edge, Node node, String relativePath, File dataSource) {
        for (CrawlListener l : listeners) {
            l.nodePackingCollision(edge, node, relativePath, dataSource);
        }
    }

    /**
     *
     * @param edge The incoming edge to the node, may be null for first node.
     * @param node The node being operated on when this event fired
     * @param relativePath Relative path of resource, may be null.
     * @param dataSource The data source for this node, may be null
     * @param ex The exception encountered
     */
    protected void fireHandlingException(Edge edge, Node node, String relativePath, File dataSource, HandlingException ex) {
        for (CrawlListener l : listeners) {
            l.nodeError(edge, node, relativePath, dataSource, ex);
        }
    }

    protected void fireHandlingStartEvent(Edge edge, Node targetNode) {
        for (CrawlListener l : listeners) {
            l.nodeHandleStart(edge, targetNode);
        }
    }

    protected void fireHandlingEndEvent(Edge edge, Node targetNode) {
        for (CrawlListener l : listeners) {
            l.nodeHandleEnd(edge, targetNode);
        }

    }

    protected void fireNodeEnterEvent(Edge edge, Node node) {
        for (CrawlListener l : listeners) {
            l.nodeEnter(edge, node);
        }

    }

    protected void fireNodeExitEvent(Edge edge, Node node) {

        for (CrawlListener l : listeners) {
            l.nodeExit(edge, node);
        }

    }

    @Override
    protected void probablyComplete() {
        super.probablyComplete();
        // Since we're probably done, may as well reclaim some space.
        //TODO this may be unnecessary if the model data cache moves to soft-references
        modelHandler.clearModelDataCache();
    }

    /**
     * This method determines whether a given node was noted as "required"
     * during traversal. For this reason you should not call this method until
     * {@link #isDone()} is true, since the entire graph needs to be examined to
     * get a definitive answer.
     *
     * @param node The node to check
     * @return True if there is a route from the starter-node to the given node
     * where no edge is marked optional.
     */
    public boolean wasRequired(Node node) {
        return ctx.requiredNodes.contains(node);
    }

    /**
     * Handles processing of a node. Calling code will handle adding new successors and de-duping them.
     *
     * @param path The edge which has led to this node. May be null for the first node.
     * @param target The node being handled.
     * @return A list of new edges and successor nodes to add below the current node.
     */
    protected List<OutgoingPair> handleNodeInternal(Edge pathEdge, Node targetNode) throws TraversalException {
        fireHandlingStartEvent(pathEdge, targetNode);
        try {
            /**
             * We could do a fancy reflective visitor pattern or dynamic-dispatch here,
             * but the added complexity and error-prone edge cases are probably not
             * worth the flexibility in this situation.
             */
            if (targetNode instanceof MapNode) {
                return handleMapNode(pathEdge, (MapNode) targetNode);

            } else if (targetNode instanceof SkyboxNode) {
                return handleSkyboxNode(pathEdge, (SkyboxNode) targetNode);


            } else if (targetNode instanceof ParticleManifestNode) {
                return handleParticleManifestNode(pathEdge, (ParticleManifestNode) targetNode);

            } else if (targetNode instanceof SoundscapeNode) {
                return handleSoundscapeNode(pathEdge, (SoundscapeNode) targetNode);

            } else if (targetNode instanceof MaterialNode) {
                return handleMaterialNode(pathEdge, (MaterialNode) targetNode);

            } else if (targetNode instanceof ModelNode) {
                return handleModelNode(pathEdge, (ModelNode) targetNode);

            } else if (targetNode instanceof PhyNode) {
                return handlePhyNode(pathEdge, (PhyNode) targetNode);

            } else if (targetNode instanceof MiscFileNode) {
                return handleMiscFileNode(pathEdge, (MiscFileNode) targetNode);

            } else if (targetNode instanceof MultiPathNode) {
                return handleMultiPathNode(pathEdge, (MultiPathNode) targetNode);

            }
            return handleDefaultCase(pathEdge, targetNode);
        } finally {
            fireHandlingEndEvent(pathEdge, targetNode);

        }

    }

    protected DecisionData decideProcessing(Node n, String path) throws TraversalException {
        // Find all the hits on the asset search paths
        List<AssetHit> hits = locator.locate(path);
        if (hits.size() == 0) {
            // Missing!
            //TODO curb errors for missing cubemaps? ex: materials/maps/swamp_vignette_1/c-256_192_256.vtf
            return new DecisionData(Decision.MISSING);
        }

        // Convert them into AssetAlternative objects and remove redundancies
        List<AssetAlternative> alts = AssetAlternative.screenTypes(hits);
        /*
         * While the BSP is often the first place checked for a resource, we may
         * very well be replacing that with something on the disk that a user has
         * updated, so we're going to monkey with the usual order and look at the
         * BSP copy only if there's nothing else we can find to replace it with.
         */
        alts = AssetAlternative.moveBspToEnd(alts);
        assert (alts.size() > 0);

        AssetAlternative first = alts.get(0);
        switch (first.getType()) {
            case ARCHIVE:
                // Don't parse or pack
                return new DecisionData(Decision.SKIP);
            case BSP:
                // Already packed, nothing to replace it with, parse only
                return new DecisionData(Decision.PARSE_ONLY, first.getFile());
            case DIR:
                /*
                 * Check if there is also an archived version. If the on-disk copy
                 * is hiding it, we shouldn't allow it to be distributed in a map.
                 */
                try {
                    CollisionType collision = AssetAlternative.hasCustomCollision(alts);
                    switch (collision) {
                        case CONFLICTING:
                            //TODO test handling of collision cases after fixing comparison code
                            return new DecisionData(Decision.COLLISION, first.getFile());
                        case NONE:
                            // Parse and pack
                            return new DecisionData(Decision.NORMAL, first.getFile());
                        case DUPLICATE:
                            return new DecisionData(Decision.SKIP);
                    }
                } catch (IOException ex) {
                    throw new TraversalException("Error comparing resource versions at: " + path, ex);
                }
            default:
                throw new TraversalException("Programmer error: Unexpected sourcetype encountered: " + first.getType());
        }


    }

    protected List<OutgoingPair> handleMapNode(Edge edge, MapNode node) {
        MapHandler handler = new MapHandler(node.getFgdSpecification(), node.getIncludes());
        try {
            return handler.handle(edge, node, node.getDataSource());
        } catch (HandlingException ex) {
            fireHandlingException(edge, node, null, node.getDataSource(), ex);
            return new ArrayList<OutgoingPair>();
        }
    }

    protected List<OutgoingPair> handleSkyboxNode(Edge edge, SkyboxNode node) {
        try {
            SkyboxHandler handler = new SkyboxHandler();
            return handler.handle(edge, node, null);
        } catch (HandlingException ex) {
            fireHandlingException(edge, node, null, null, ex);
            return new ArrayList<OutgoingPair>();
        }
    }

    protected List<OutgoingPair> handleMaterialNode(Edge edge, MaterialNode node) throws TraversalException {
        List<OutgoingPair> ret = new ArrayList<OutgoingPair>();

        final String relPath = node.getPath();
        DecisionData dd = decideProcessing(node, relPath);
        Decision dec = dd.decision;
        File dataSource = dd.fileSource;

        switch (dec) {
            case SKIP:
                fireSkipEvent(edge, node, relPath);
                return ret;
            case MISSING:
                fireMissingEvent(edge, node, relPath);
                return ret;
            case COLLISION:
                fireCollisionEvent(edge, node, relPath, dataSource);
                return ret;
        }


        try {
            ret = materialHandler.handle(edge, node, dataSource);
        } catch (HandlingException ex) {
            fireHandlingException(edge, node, relPath, dataSource, ex);
            return ret;
        }

        switch (dec) {
            case NORMAL:
                firePackingEvent(edge, node, relPath, dataSource);
                break;
            case PARSE_ONLY:
                firePrepackedEvent(edge, node, relPath);
                return ret;
        }
        return ret;

    }

    protected List<OutgoingPair> handleParticleManifestNode(Edge edge, ParticleManifestNode node) throws TraversalException {
        List<OutgoingPair> ret = new ArrayList<OutgoingPair>();
        final String relPath = node.getPath();
        DecisionData dd = decideProcessing(node, relPath);
        Decision dec = dd.decision;
        File dataSource = dd.fileSource;

        switch (dec) {
            case SKIP:
                fireSkipEvent(edge, node, relPath);
                return ret;
            case MISSING:
                fireMissingEvent(edge, node, relPath);
                return ret;
            case COLLISION:
                fireCollisionEvent(edge, node, relPath, dataSource);
                return ret;
        }


        try {
            ret = particleHandler.handle(edge, node, dataSource);
        } catch (HandlingException ex) {
            fireHandlingException(edge, node, relPath, dataSource, ex);
            return ret;
        }

        switch (dec) {
            case NORMAL:
                firePackingEvent(edge, node, relPath, dataSource);
                break;
            case PARSE_ONLY:
                firePrepackedEvent(edge, node, relPath);
                return ret;
        }
        return ret;
    }

    protected List<OutgoingPair> handleSoundscapeNode(Edge edge, SoundscapeNode node) throws TraversalException {
        List<OutgoingPair> ret = new ArrayList<OutgoingPair>();
        final String relPath = node.getPath();
        DecisionData dd = decideProcessing(node, relPath);
        Decision dec = dd.decision;
        File dataSource = dd.fileSource;

        switch (dec) {
            case SKIP:
                fireSkipEvent(edge, node, relPath);
                return ret;
            case MISSING:
                fireMissingEvent(edge, node, relPath);
                return ret;
            case COLLISION:
                fireCollisionEvent(edge, node, relPath, dataSource);
                return ret;
        }


        try {
            ret = soundscapeHandler.handle(edge, node, dataSource);
        } catch (HandlingException ex) {
            fireHandlingException(edge, node, relPath, dataSource, ex);
            return ret;
        }

        switch (dec) {
            case NORMAL:
                firePackingEvent(edge, node, relPath, dataSource);
                break;
            case PARSE_ONLY:
                firePrepackedEvent(edge, node, relPath);
                return ret;
        }
        return ret;
    }

    protected List<OutgoingPair> handleModelNode(Edge edge, ModelNode node) throws TraversalException {
        List<OutgoingPair> ret = new ArrayList<OutgoingPair>();
        final String relPath = node.getPath();
        DecisionData dd = decideProcessing(node, relPath);
        Decision dec = dd.decision;
        File dataSource = dd.fileSource;

        switch (dec) {
            case SKIP:
                fireSkipEvent(edge, node, relPath);
                return ret;
            case MISSING:
                fireMissingEvent(edge, node, relPath);
                return ret;
            case COLLISION:
                fireCollisionEvent(edge, node, relPath, dataSource);
                return ret;
        }


        try {
            ret = modelHandler.handle(edge, node, dataSource);
        } catch (HandlingException ex) {
            fireHandlingException(edge, node, relPath, dataSource, ex);
            return ret;
        }

        switch (dec) {
            case NORMAL:
                firePackingEvent(edge, node, relPath, dataSource);
                break;
            case PARSE_ONLY:
                firePrepackedEvent(edge, node, relPath);
                return ret;
        }
        return ret;


    }

    protected List<OutgoingPair> handlePhyNode(Edge edge, PhyNode node) throws TraversalException {
        List<OutgoingPair> ret = new ArrayList<OutgoingPair>();
        final String relPath = node.getPath();
        DecisionData dd = decideProcessing(node, relPath);
        Decision dec = dd.decision;
        File dataSource = dd.fileSource;

        switch (dec) {
            case SKIP:
                fireSkipEvent(edge, node, relPath);
                return ret;
            case MISSING:
                fireMissingEvent(edge, node, relPath);
                return ret;
            case COLLISION:
                fireCollisionEvent(edge, node, relPath, dataSource);
                return ret;
        }


        try {
            ret = phyHandler.handle(edge, node, dataSource);
        } catch (HandlingException ex) {
            fireHandlingException(edge, node, relPath, dataSource, ex);
            return ret;
        }

        switch (dec) {
            case NORMAL:
                firePackingEvent(edge, node, relPath, dataSource);
                break;
            case PARSE_ONLY:
                firePrepackedEvent(edge, node, relPath);
                return ret;
        }
        return ret;


    }

    protected List<OutgoingPair> handleMiscFileNode(Edge edge, MiscFileNode node) throws TraversalException {
        List<OutgoingPair> ret = new ArrayList<OutgoingPair>();
        final String relPath = node.getPath();
        DecisionData dd = decideProcessing(node, relPath);
        Decision dec = dd.decision;
        File dataSource = dd.fileSource;

        switch (dec) {
            case SKIP:
                fireSkipEvent(edge, node, relPath);
                return ret;
            case MISSING:
                fireMissingEvent(edge, node, relPath);
                return ret;
            case COLLISION:
                fireCollisionEvent(edge, node, relPath, dataSource);
                return ret;
        }

        switch (dec) {
            case NORMAL:
                firePackingEvent(edge, node, relPath, dataSource);
                break;
            case PARSE_ONLY:
                firePrepackedEvent(edge, node, relPath);
                return ret;
        }
        return ret;
    }

    protected List<OutgoingPair> handleMultiPathNode(Edge edge, MultiPathNode node) {
        List<OutgoingPair> ret = new ArrayList<OutgoingPair>();

        for (String path : node.getPossibilities()) {
            List<AssetHit> hits = locator.locate(path);
            if (hits.size() > 0) {
                ret.add(new OutgoingPair(new EdgeImpl(), new MaterialNode(path)));
                return ret;
            }
        }

        // Nothing found? Use the first possibility and rely on the
        // crawler to realize it's missing in a later iteration.
        String path = node.getPossibilities().get(0); // Safe due to contract of MultiPathNode
        ret.add(new OutgoingPair(new EdgeImpl(), new MaterialNode(path)));
        return ret;

    }

    protected List<OutgoingPair> handleDefaultCase(Edge edge, Node target) {
        //TODO find better defaults?
        throw new IllegalArgumentException("Unsupported node type: " + target.getClass());
    }
}
