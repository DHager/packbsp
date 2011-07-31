/*
 * 
 */
package com.technofovea.packbsp.spring;

/**
 *
 * @author Darien Hager
 */
public interface BasicPhase {

    public boolean isComplete();
    
    public NestedScope getLinkedScope();

    public void setLinkedScope(NestedScope linkedScope);
    
}
