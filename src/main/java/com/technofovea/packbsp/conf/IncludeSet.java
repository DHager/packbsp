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
package com.technofovea.packbsp.conf;

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;

/**
 * Represents a dependency which
 * @author Darien Hager
 */
@XmlAccessorType(XmlAccessType.NONE)
public class IncludeSet {

   
    @XmlAttribute(required = true,name="name")
    protected String name = null;

    @XmlAttribute(required = false,name="parent")
    protected String parent = null;

    @XmlElement(required = false,name="includeIds")
    @XmlList
    protected Set<String> includes = new HashSet<String>();

    public IncludeSet() {
        // Needed for JAXB
    }

    public IncludeSet(String name, Set<String> includes){
        this.name = name;
        this.includes = includes;
    }
    public IncludeSet(String name, String[] includes){
        this.name = name;
        this.includes = new HashSet<String>();
        for(String s : includes){this.includes.add(s);}
    }

    public Set<String> getIncludes() {
        return includes;
    }

    public void setIncludes(Set<String> includes) {
        this.includes = includes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public boolean hasParent(){
        return (parent != null && (!"".equals(parent)));
    }

    public void setParent(String parent) {
        if(parent != null && "".equals(parent)){
            parent = null;
        }
        this.parent = parent;
    }

    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IncludeSet other = (IncludeSet) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.parent == null) ? (other.parent != null) : !this.parent.equals(other.parent)) {
            return false;
        }
        if (this.includes != other.includes && (this.includes == null || !this.includes.equals(other.includes))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.parent != null ? this.parent.hashCode() : 0);
        hash = 97 * hash + (this.includes != null ? this.includes.hashCode() : 0);
        return hash;
    }





    
}
