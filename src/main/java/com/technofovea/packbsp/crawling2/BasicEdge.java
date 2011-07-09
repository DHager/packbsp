/*
 * 
 */
package com.technofovea.packbsp.crawling2;

/**
 *
 * @author Darien Hager
 */
public class BasicEdge implements Edge {

    protected boolean implicit;
    protected Layer layer;
    protected String description;

    public BasicEdge(Layer layer){
        this(layer,false,"");
    }
    
    public BasicEdge(Layer layer, boolean implicit, String description) {
        this.implicit = implicit;
        this.layer = layer;
        this.description = description;
    }

    public BasicEdge(Layer layer, String description) {
        this(layer,false,description);
    }
    
    
    

    public boolean isImplicit() {
        return implicit;
    }

    public boolean setImplicit(boolean implicit) {
        final boolean prev = this.implicit;
        this.implicit = implicit;
        return prev;
    }

    public Layer getLayer() {
        return layer;
    }

    public String getDescription() {
        return description;
    }
    
}
