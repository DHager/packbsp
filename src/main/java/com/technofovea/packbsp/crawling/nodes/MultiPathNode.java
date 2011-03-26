/**
 * Copyright (C) 2011 Darien Hager
 *
 * This code is part of the "PackBSP" project, and is licensed under
 * a Creative Commons Attribution-ShareAlike 3.0 Unported License. For
 * either a summary of conditions or the full legal text, please visit:
 *
 * http://creativecommons.org/licenses/by-sa/3.0/
 *
 * Permissions beyond the scope of this license may be available
 * at http://technofovea.com/ .
 */
package com.technofovea.packbsp.crawling.nodes;

import com.technofovea.packbsp.crawling.Node;
import com.technofovea.packbsp.crawling.NodeImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Darien Hager
 */
public class MultiPathNode extends NodeImpl implements Node {
    protected String searchName;
    protected List<String> searchPaths;


    public MultiPathNode( String searchName, List<String> searchPaths) {
        if(searchPaths.size() == 0){
            throw new IllegalArgumentException("Cannot have a zero-sized set of search paths");
        }
        this.searchName = searchName;
        this.searchPaths = new ArrayList<String>();
        for(String path : searchPaths){
            // Strip trailing slashes
            this.searchPaths.add(path.replaceAll("/+$",""));
        }
    }

    public List<String> getPossibilities(){
        List<String> ret = new ArrayList<String>();
        for(String path: searchPaths){
            ret.add(path+"/"+searchName);
        }
        return ret;
    }
   

    @Override
    public String toString() {
        return stringify(this, "name",searchName,"paths",searchPaths);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MultiPathNode other = (MultiPathNode) obj;
        if ((this.searchName == null) ? (other.searchName != null) : !this.searchName.equalsIgnoreCase(other.searchName)) {
            return false;
        }
        if (this.searchPaths != other.searchPaths && (this.searchPaths == null || !this.searchPaths.equals(other.searchPaths))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.searchName != null ? this.searchName.hashCode() : 0);
        hash = 89 * hash + (this.searchPaths != null ? this.searchPaths.hashCode() : 0);
        return hash;
    }

    


   
   

    


}
