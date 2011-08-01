/*
 * 
 */
package com.technofovea.packbsp.spring;

/**
 *
 * @author Darien Hager
 */
public abstract class AbstractState implements BasicState{
    protected NestedScope linkedScope;
    
    protected void markDirty(){
        if(linkedScope!=null){
            linkedScope.invalidateScope();
        }
    }

    @Override
    public NestedScope getLinkedScope() {
        return linkedScope;
    }

    @Override
    public void setLinkedScope(NestedScope linkedScope) {
        this.linkedScope = linkedScope;
    }
    
}
