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

import com.technofovea.packbsp.crawling.handlers.HandlingException;
import java.io.File;

/**
 *
 * @author Darien Hager
 */
public interface CrawlListener {

    /**
     * Crawling has moved to an new node along the path
     * @param edge Edge taken (may be null)
     * @param node Node entered
     */
    public void nodeEnter(Edge edge, Node node);

    /**
     * Crawling has begun to process the details of a node
     * @param edge Edge taken (may be null)
     * @param node Node entered
     */
    public void nodeHandleStart(Edge edge, Node node);

    /**
     * An error occurred handling details of a node
     * @param edge Edge taken to reach node
     * @param node The node the error occurredon
     * @param filePath The relative file path under consideration (may be null)
     * @param dataSource The data-source being interpreted (may be null or refer to a temporary file)
     * @param ex The error which occurred
     */
    public void nodeError(Edge edge, Node node, String filePath, File dataSource, HandlingException ex);

    /**
     * During handling of a node, a missing resource was discovered
     * @param edge The edge taken to reach the node
     * @param node The node being processed
     * @param filePath The relative file path for which a data source was not found
     */
    public void nodeContentMissing(Edge edge, Node node, String filePath);

    /**
     * During handling of a node, a resource was "skipped" since it was determined
     * to be a "stock" resource always provided externally from the map.
     * @param edge The edge taken to reach the node
     * @param node The node being processed
     * @param filePath The relative file path for which a "stock" data source was found
     */
    public void nodeContentSkipped(Edge edge, Node node, String filePath);

    /**
     * During handling of a node, a resource was found that should be packed into
     * the map.
     *
     * @param edge The edge taken to reach the node
     * @param node The node being processed
     * @param filePath The relative file path for which a data source was found
     */
    public void nodePackingFound(Edge edge, Node node, String filePath, File dataSource);

    /**
     * Crawling has finished processing a node
     * @param edge Edge taken (may be null)
     * @param node Node entered
     */
    public void nodeHandleEnd(Edge edge, Node node);

    /**
     * Crawling has withdrawn back through a node along the now-shorter path
     * @param edge Edge retreated along (may be null)
     * @param node Node left behind
     */
    public void nodeExit(Edge edge, Node node);

    /**
     * An asset was found that attempts to wrongfuly "replace" an asset in the
     * stock material for a game.
     *
     * @param edge The edge taken to reach the node
     * @param node The node being processed
     * @param filePath The relative file path for which a collision occurred
     * @param dataSource The offending asset location (may be null)
     */
    public void nodePackingCollision(Edge edge, Node node, String filePath, File dataSource);

}
