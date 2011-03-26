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

import com.technofovea.hl2parse.vdf.ParticleManifestReader;
import com.technofovea.hl2parse.vdf.VdfRoot;
import com.technofovea.packbsp.crawling.Edge;
import com.technofovea.packbsp.crawling.EdgeImpl;
import com.technofovea.packbsp.crawling.OutgoingPair;
import com.technofovea.packbsp.crawling.nodes.MaterialNode;
import com.technofovea.packbsp.crawling.nodes.MiscFileNode;
import com.technofovea.packbsp.crawling.nodes.ParticleManifestNode;
import java.util.ArrayList;




import java.util.List;

import org.apache.commons.jxpath.JXPathException;

/**
 *
 * @author Darien Hager
 */
public class ParticleHandler extends VdfBaseParser<Edge, ParticleManifestNode> {

    @Override
    protected List<OutgoingPair> innerParse(ParticleManifestNode node, VdfRoot root) throws HandlingException {
        List<OutgoingPair> ret = new ArrayList<OutgoingPair>();
        try {
            ParticleManifestReader pm = new ParticleManifestReader(root);

            for (String path : pm.getPcfs()) {
                ret.add(new OutgoingPair(new EdgeImpl("particle set"),new MiscFileNode(path)));
            }
        } catch (JXPathException e) {
            throw new HandlingException("Structure invalid", e);
        }
        return ret;
    }
}
