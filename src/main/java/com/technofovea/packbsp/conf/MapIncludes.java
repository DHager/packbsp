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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Darien Hager
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class MapIncludes {

    @XmlElement(required = true, name = "include")
    protected List<IncludeItem> items = new ArrayList<IncludeItem>();
    @XmlElement(required = true, name = "set")
    protected List<IncludeSet> sets = new ArrayList<IncludeSet>();

    public static MapIncludes fromXml(File src) throws JAXBException, FileNotFoundException {
        JAXBContext jc = JAXBContext.newInstance(MapIncludes.class, IncludeItem.class, IncludeSet.class);
        Unmarshaller um = jc.createUnmarshaller();
        //Marshaller m = jc.createMarshaller();
        //m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);


        MapIncludes conf = (MapIncludes) um.unmarshal(new FileReader(src));

        return conf;
    }

    public IncludeSet getByName(String name) {
        for (IncludeSet set : sets) {
            if (set.getName().equalsIgnoreCase(name)) {
                return set;
            }
        }
        return null;
    }

    public IncludeSet getByGameName(String name) {
        name = "game:" + name;
        return getByName(name);
    }

    public IncludeSet getByEngineName(String name) {
        name = "engine:" + name;
        return getByName(name);
    }

    public Set<IncludeItem> getItems(IncludeSet set) {
        Set<IncludeItem> ret = new HashSet<IncludeItem>();
        for (IncludeItem item : items) {
            if (set.getIncludes().contains(item.getId())) {
                ret.add(item);
            }
        }
        return ret;
    }

    public Set<IncludeItem> getItems(String gameName, String engineName) {
        Map<IncludeSet, Set<IncludeItem>> data = new HashMap<IncludeSet, Set<IncludeItem>>();
        if (getByName("game:" + gameName) != null) {
            recursiveFind("game:" + gameName, data);
        }
        recursiveFind("engine:" + engineName, data);

        Set<IncludeItem> ret = new HashSet<IncludeItem>();
        for (IncludeSet key : data.keySet()) {
            ret.addAll(data.get(key));
        }
        return ret;
    }

    protected void recursiveFind(String name, Map<IncludeSet, Set<IncludeItem>> data) {
        IncludeSet set = getByName(name);
        if (set == null) {
            //TODO log
            return;
        }
        if (data.containsKey(set)) {
            //TODO log cyclic relationship
            return;
        }
        Set<IncludeItem> items = getItems(set);
        data.put(set, items);
        if (set.hasParent()) {
            recursiveFind(set.getParent(), data);
        }
    }
}
