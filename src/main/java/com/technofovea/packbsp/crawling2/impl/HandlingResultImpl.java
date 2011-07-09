/*
 * 
 */
package com.technofovea.packbsp.crawling2.impl;

import com.technofovea.packbsp.crawling2.GraphAddition;
import com.technofovea.packbsp.crawling2.HandlingResult;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Darien Hager
 */
public class HandlingResultImpl implements HandlingResult{
    private Set<GraphAddition> additions;
    private boolean lastHandler;

    public static HandlingResult createSkip(){
        return new HandlingResultImpl(null, false);
    }
    
    public HandlingResultImpl(Set<GraphAddition> additions, boolean lastHandler) {
        this.additions = additions;
        this.lastHandler = lastHandler;
        
        if(this.additions==null){
            this.additions = Collections.EMPTY_SET;
        }
    }
    

    public Set<GraphAddition> getAdditions() {
        return Collections.unmodifiableSet(additions);
    }

    public boolean isLastHandler() {
        return lastHandler;
    }
    
}
