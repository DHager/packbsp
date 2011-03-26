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
package com.technofovea.packbsp.crawling.nodes;

import com.technofovea.packbsp.crawling.NodeImpl;
import com.technofovea.hl2parse.fgd.FgdSpec;
import com.technofovea.packbsp.conf.IncludeItem;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Darien Hager
 */
public class MapNode extends NodeImpl {

    String mapName;
    File dataSource;
    FgdSpec fgdSpecification;
    Set<IncludeItem> includes;

    public MapNode(File map, String mapName, FgdSpec fgdSpec, Set<IncludeItem> includes) {
        this.mapName = mapName;
        this.dataSource = map;
        this.fgdSpecification = fgdSpec;
        this.includes = includes;
    }

    public MapNode(File dataSource, FgdSpec fgdSpecification,  Set<IncludeItem> includes) {
        this(dataSource,dataSource.getName(),fgdSpecification,includes);
    }

    public String getMapName() {
        return mapName;
    }

    


    public File getDataSource() {
        return dataSource;
    }

    public FgdSpec getFgdSpecification() {
        return fgdSpecification;
    }

    public Set<IncludeItem> getIncludes() {
        return new HashSet<IncludeItem>(includes);
    }



    @Override
    public String toString() {
        return stringify(this, "map",dataSource.getPath(),"spec",fgdSpecification);
    }


    
}
