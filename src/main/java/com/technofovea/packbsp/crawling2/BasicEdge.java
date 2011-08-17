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
    protected String description;

    public BasicEdge(){
        this(false,"");
    }
    
    public BasicEdge(boolean implicit, String description) {
        this.implicit = implicit;
        this.description = description;
    }

    public boolean isImplicit() {
        return implicit;
    }

    public boolean setImplicit(boolean implicit) {
        final boolean prev = this.implicit;
        this.implicit = implicit;
        return prev;
    }

    public String getDescription() {
        return description;
    }
    
}
