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
/*
 * 
 */
package com.technofovea.packbsp.crawling;

/**
 *
 * @author Darien Hager
 */
public class EdgeImpl implements Edge {

    String name;
    boolean optional;

    public EdgeImpl(String name, boolean optional) {
        this.name = name;
        this.optional = optional;
    }

    public EdgeImpl(String name) {
        this(name,false);
    }

    public EdgeImpl(boolean optional) {
        this("",optional);
    }

    public EdgeImpl() {
        this("",false);
    }

    @Override
    public String toString() {
        return "{edge:optional="+optional+",name="+name+"}";
    }






    public boolean isOptional() {
        return optional;
    }

    public String getName() {
        return name;
    }

}
