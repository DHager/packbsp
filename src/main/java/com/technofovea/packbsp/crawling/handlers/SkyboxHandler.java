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
package com.technofovea.packbsp.crawling.handlers;

import com.technofovea.packbsp.crawling.Edge;
import com.technofovea.packbsp.crawling.EdgeImpl;
import com.technofovea.packbsp.crawling.Node;
import com.technofovea.packbsp.crawling.OutgoingPair;
import com.technofovea.packbsp.crawling.nodes.MaterialNode;
import com.technofovea.packbsp.crawling.nodes.SkyboxNode;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Darien Hager
 */
public class SkyboxHandler implements NodeHandler<Edge, SkyboxNode>{

    String[] SKYBOX_SUFFIXES = {"up", "dn", "lf", "rt", "ft", "bk"};

    public List<OutgoingPair> handle(Edge edge, SkyboxNode node, File dataSource) throws HandlingException {
        List<OutgoingPair> ret = new ArrayList<OutgoingPair>();
        String name = node.getName();
        if (name.trim().length() == 0) {
            return ret;
        }
        // Skybox ex: sky_night_02
        // Becomes /materials/skybox/sky_night_02.vmt
        for (String suffix : SKYBOX_SUFFIXES) {
            String path = "materials/skybox/" + name + suffix + ".vmt";
            ret.add(new OutgoingPair(new EdgeImpl("suffix"), new MaterialNode(path)));
        }
        return ret;
    }
}
