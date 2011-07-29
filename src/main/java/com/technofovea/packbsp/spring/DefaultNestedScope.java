/*
 * 
 */
package com.technofovea.packbsp.spring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.ObjectFactory;

/**
 *
 * @author Darien Hager
 */
public class DefaultNestedScope extends NestedScope {
    
    protected Map<String, Object> beans = new HashMap<String, Object>();
    protected Set<NestedScope> parentScopes = new HashSet<NestedScope>();
    
    @Override
    protected synchronized void cleanObjects() {
        beans.clear();
    }
    
    public synchronized Object get(String name, ObjectFactory<?> objectFactory) {
        Object o = beans.get(name);
        if (o == null) {
            o = objectFactory.getObject();
            beans.put(name, o);
        }
        return o;
    }
    
    public synchronized Object remove(String name) {
        return beans.remove(name);
    }
    
    public Set<NestedScope> getParentScopes() {
        return new HashSet<NestedScope>(parentScopes);
    }
    
    public synchronized void setParentScopes(Set<NestedScope> parentScopes) {
        for (NestedScope p : parentScopes) {
            p.removeListener(this);
        }
        this.parentScopes.clear();
        this.parentScopes.addAll(parentScopes);
        for (NestedScope p : parentScopes) {
            p.addListener(this);
        }
    }
}
