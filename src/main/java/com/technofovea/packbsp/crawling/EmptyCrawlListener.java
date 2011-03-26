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
public class EmptyCrawlListener implements CrawlListener{

    public void nodeContentMissing(Edge edge, Node node, String filePath) {
    }

    public void nodeContentSkipped(Edge edge, Node node, String filePath) {
    }

    public void nodeEnter(Edge edge, Node node) {
    }

    public void nodeError(Edge edge, Node node, String filePath, File dataSource, HandlingException ex) {
    }

    public void nodeExit(Edge edge, Node node) {
    }

    public void nodeHandleEnd(Edge edge, Node node) {
    }

    public void nodeHandleStart(Edge edge, Node node) {
    }

    public void nodePackingFound(Edge edge, Node node, String filePath, File dataSource) {
    }

    public void nodePackingCollision(Edge edge, Node node, String filePath, File dataSource) {
    }

    


}
