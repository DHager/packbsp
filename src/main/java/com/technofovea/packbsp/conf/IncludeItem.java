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

import com.technofovea.packbsp.LanguageCode;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a dependency for packing which has either a fixed path or else a
 * path that uses wildcards to indicate that it is constructed form the map-name
 * or a localization code.
 *
 * @see LanguageCode
 * @author Darien Hager
 */
@XmlAccessorType(XmlAccessType.NONE)
public class IncludeItem {

    public static final String VAR_MAPNAME = "%m";
    public static final String VAR_LANGUAGE = "%l";

    @XmlType
    @XmlEnum(value = String.class)
    public static enum IncludeType {

        @XmlEnumValue("plain")
        PLAIN,
        @XmlEnumValue("material")
        MATERIAL,
        @XmlEnumValue("soundscape")
        SOUNDSCAPE,
        @XmlEnumValue("particles")
        PARTICLE_MANIFEST,
    }
    @XmlAttribute(required = true, name = "id")
    @XmlID
    protected String id = null;
    @XmlAttribute(required = true, name = "path")
    protected String path = null;
    @XmlAttribute(required = true)
    protected IncludeType type = IncludeType.PLAIN;

    public IncludeItem() {
        // Required for JAXB
    }

    public IncludeItem(String id, String path, IncludeType type) {
        this.id = id;
        this.path = path;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public IncludeType getType() {
        return type;
    }

    public void setType(IncludeType type) {
        this.type = type;
    }

    public Set<String> dereference(String mapname) {
        Set<String> ret = new HashSet<String>();
        String intermediate = path.replace(VAR_MAPNAME, mapname);
        if(path.contains(VAR_LANGUAGE)){
            for(LanguageCode l: LanguageCode.values()){
                ret.add(intermediate.replace(VAR_LANGUAGE, l.toString()));
            }
        }else{
            ret.add(intermediate);
        }
        return ret;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IncludeItem other = (IncludeItem) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.path == null) ? (other.path != null) : !this.path.equals(other.path)) {
            return false;
        }
        if (this.type != other.type && (this.type == null || !this.type.equals(other.type))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.path != null ? this.path.hashCode() : 0);
        hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
}
