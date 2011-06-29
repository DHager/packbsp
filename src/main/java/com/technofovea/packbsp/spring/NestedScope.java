/*
 * 
 */
package com.technofovea.packbsp.spring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.Scope;

/**
 *
 * @author Darien Hager
 */
public abstract class NestedScope implements Scope, BeanNameAware {
    
    
    private static final Logger logger = LoggerFactory.getLogger(NestedScope.class);
    protected List<NestedScope> childScopes = new ArrayList<NestedScope>();
    private String beanName = null;

    protected abstract void cleanObjects();

    public void invalidateScope() {
        logger.debug("Invalidating items for scope-bean {}",beanName);
        cleanObjects();
        for (NestedScope child : childScopes) {
            child.invalidateScope();
        }
    }

    public void registerDestructionCallback(String name, Runnable callback) {
    }
    
    

    public Object resolveContextualObject(String key) {
        return null;
    }

    public String getConversationId() {
        return null;
    }

    public List<NestedScope> getChildScopes() {
        return Collections.unmodifiableList(childScopes);
    }

    public void setChildScopes(List<NestedScope> childScopes) {
        this.childScopes.clear();
        this.childScopes.addAll(childScopes);
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }
    
    
    
}
