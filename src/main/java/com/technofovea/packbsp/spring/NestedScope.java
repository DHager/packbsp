/*
 * 
 */
package com.technofovea.packbsp.spring;

import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.Scope;

/**
 *
 * @author Darien Hager
 */
public abstract class NestedScope implements Scope, BeanNameAware, NestedScopeListener {

    private static final Logger logger = LoggerFactory.getLogger(NestedScope.class);
    protected final List<NestedScopeListener> listeners = new LinkedList<NestedScopeListener>();
    private String beanName = null;

    protected abstract void cleanObjects();

    public void invalidateScope() {
        logger.debug("Invalidating items for scope-bean {}", beanName);
        cleanObjects();
        synchronized (listeners) {
            for (NestedScopeListener child : listeners) {
                child.notifyInvalid(this);
            }
        }
    }

    public boolean addListener(NestedScopeListener l) {
        synchronized (listeners) {
            if (listeners.contains(l)) {
                return false;
            }
            listeners.add(l);
            return true;
        }
    }

    public boolean removeListener(NestedScopeListener l) {
        synchronized (listeners) {
            return listeners.remove(l);
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

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void notifyInvalid(NestedScope source) {
        // Propogate the invalidation
        this.invalidateScope();
    }
}
