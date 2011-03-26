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
package com.technofovea.packbsp;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Darien Hager
 */
public class DependencyManager {

    HashMap<String, DependencyItem> items = new HashMap<String, DependencyItem>();

    protected DependencyItem getOrCreate(String path) {
        DependencyItem ret = items.get(path);
        if (ret == null) {
            ret = new DependencyItem(path);
            items.put(path, ret);
        }
        return ret;
    }

    public void setRequired(String path, boolean required) {
        DependencyItem target = getOrCreate(path);
        if (required) {
            target.setNecessity(DependencyItem.Necessity.REQUIRED);
        } else {
            target.setNecessity(DependencyItem.Necessity.OPTIONAL);
        }
    }

    public void setMissing(String path) {
        getOrCreate(path).setStatus(DependencyItem.Status.MISSING);
    }

    public void setStock(String path) {
        getOrCreate(path).setStatus(DependencyItem.Status.STOCK);
    }

    public void setPrePacked(String path) {
        getOrCreate(path).setStatus(DependencyItem.Status.PREPACKED);

    }

    public void setFound(String path, File source) {
        DependencyItem target = getOrCreate(path);
        target.setStatus(DependencyItem.Status.FOUND);
        target.setDataSource(source);
    }

    public void setSource(String path, File source) {
        getOrCreate(path).setDataSource(source);
    }

    public List<String> getPaths() {
        List<String> sortedKeys = new ArrayList<String>(items.keySet());
        Collections.sort(sortedKeys);
        return sortedKeys;
    }
}
