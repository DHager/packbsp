/*
 * 
 */
package com.technofovea.packbsp.crawling2;

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
