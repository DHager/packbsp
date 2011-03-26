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

import com.technofovea.hl2parse.vdf.SoundScapeReader;
import com.technofovea.hl2parse.vdf.VdfRoot;
import com.technofovea.packbsp.crawling.Edge;
import com.technofovea.packbsp.crawling.EdgeImpl;
import com.technofovea.packbsp.crawling.OutgoingPair;
import com.technofovea.packbsp.crawling.nodes.MiscFileNode;
import com.technofovea.packbsp.crawling.nodes.ParticleManifestNode;
import com.technofovea.packbsp.crawling.nodes.SoundscapeNode;
import java.util.ArrayList;




import java.util.List;

import org.apache.commons.jxpath.JXPathException;

/**
 *
 * @author Darien Hager
 */
public class SoundscapeHandler extends VdfBaseParser<Edge, SoundscapeNode> {

    @Override
    protected List<OutgoingPair> innerParse(SoundscapeNode node, VdfRoot root) throws HandlingException {
        List<OutgoingPair> ret = new ArrayList<OutgoingPair>();
        try {
            SoundScapeReader ss = new SoundScapeReader(root);

            for (String path : ss.getSoundFiles()) {
                if(isSoundPath(path)){
                    ret.add(new OutgoingPair(new EdgeImpl("sound"),new MiscFileNode(path)));
                }
            }
        } catch (JXPathException e) {
            throw new HandlingException("Structure invalid", e);
        }
        return ret;
    }

    /**
     * Returns true if the string provided is actually a path to a sound file,
     * rather than a symbolic name like "Ambient.Rainfall".
     *
     * @param sound The sound identifier to check
     * @return True if it appears to refer to a file, false otherwise
     */
    public static boolean isSoundPath(String sound) {
        //TODO get confirmation that this heuristic is good-enough
        sound = sound.toLowerCase();
        if(sound.endsWith(".wav")){
            return true;
        } else if (sound.endsWith(".mp3")){
            return true;
        }
        return false;
    }
}
