/*
 * 
 */
package com.technofovea.packbsp.spring;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.ObjectFactory;

/**
 *
 * @author Darien Hager
 */
public class DefaultNestedScope extends NestedScope{
    
    protected Map<String,Object> beans = new HashMap<String, Object>();

    @Override
    protected synchronized void cleanObjects() {
        beans.clear();
    }

    public synchronized Object get(String name, ObjectFactory<?> objectFactory) {
        Object o = beans.get(name);
        if(o==null){
            o = objectFactory.getObject();
            beans.put(name, o);
        }
        return o;
    }

    public synchronized Object remove(String name) {
        return beans.remove(name);
    }
    
    
    
    
}
