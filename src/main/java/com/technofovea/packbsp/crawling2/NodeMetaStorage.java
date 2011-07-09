/*
 * 
 */
package com.technofovea.packbsp.crawling2;

import com.technofovea.packbsp.crawling2.MapDepGraph;
import com.technofovea.packbsp.crawling2.Node;
import java.util.WeakHashMap;

/**
 * A convenience class for handlers which allows them to safely store information
 * about nodes in a {@link MapDepGraph}.
 * 
 * @author Darien Hager
 */
public class NodeMetaStorage<V> extends WeakHashMap<Node, V> {

    public boolean replaceKey(Node oldKey, Node newKey){
        if(oldKey==null || newKey==null){
            throw new IllegalArgumentException("Values cannot be null");
        }
        if(!oldKey.equals(newKey)){
            throw new IllegalArgumentException("Keys are not equal");
        }
        if(oldKey==newKey){
            return true;
        }
        if(this.containsKey(oldKey)){
            return false;
        }
        // This would be easier if getEntry() was protected...
        for(Node n : this.keySet()){
            if(n == oldKey){
                V payload = this.remove(oldKey);
                this.put(newKey, payload);
                return true;
            }
        }
        assert(!this.containsKey(oldKey));
        return false;
    }
    
    
}
