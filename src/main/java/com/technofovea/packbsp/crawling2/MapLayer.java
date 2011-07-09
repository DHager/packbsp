/*
 * 
 */
package com.technofovea.packbsp.crawling2;

import com.technofovea.packbsp.crawling2.Layer;
import com.technofovea.packbsp.crawling2.Node;

/**
 *
 * @author Darien Hager
 */
public class MapLayer implements Layer{
    protected Node map;

    public MapLayer(Node map) {
        this.map = map;
    }

    public Node getMap() {
        return map;
    }
    
    
}
