/*
 * 
 */
package com.technofovea.packbsp.crawling2;

import com.technofovea.packbsp.assets.AssetHit;

/**
 *
 * @author Darien Hager
 */
public interface LocatedAssetNode extends Node{
    
    public AssetHit getLocation();
    
}
