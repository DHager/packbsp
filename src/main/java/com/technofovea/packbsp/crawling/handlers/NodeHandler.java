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

package com.technofovea.packbsp.crawling.handlers;

import com.technofovea.packbsp.crawling.Edge;
import com.technofovea.packbsp.crawling.Node;
import com.technofovea.packbsp.crawling.OutgoingPair;
import java.io.File;
import java.util.List;

/**
 *
 * @author Darien Hager
 */
public interface NodeHandler<E extends Edge, T extends Node> {
    public List<OutgoingPair> handle(E edge, T node, File dataSource) throws HandlingException;
}
