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

import com.technofovea.packbsp.assets.AssetHit;
import com.technofovea.packbsp.assets.AssetLocator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Darien Hager
 */
public class CachingLocatorWrap implements AssetLocator {

    AssetLocator delegate;
    Map<String, List<AssetHit>> cache = new HashMap<String, List<AssetHit>>();

    public CachingLocatorWrap(AssetLocator delegate) {
        this.delegate = delegate;
    }

    public AssetLocator getDelegate() {
        return delegate;
    }

    public List<AssetHit> locate(String path) {
        List<AssetHit> cached = cache.get(path);
        if(cached != null){
            return cached;
        }
        List<AssetHit> result = delegate.locate(path);
        cache.put(path, result);
        return result;
    }

    public void clearCache(){
        cache.clear();
    }
}
