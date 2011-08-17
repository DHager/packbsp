package com.technofovea.packbsp.crawling2;

import com.technofovea.packbsp.assets.AssetHit;

/**
 * Nodes which implement this interface represent an asset on a relative path, 
 * where the actual target may differ depending on the context where it is 
 * interpreted.
 * 
 * @author Darien Hager
 */
public abstract class RelativeAssetNode<N extends Node> implements Node{

    protected String path;

    public RelativeAssetNode(String path) {
        this.path = path;
    }
    
    /**
     * Returns the path, ex. "materials/foo/bar.vmt". 
     * 
     * Forward-slashes are used to separate directories, and the path may be
     * case-sensitive and contain capitalization.
     * 
     * @return The path associated with this node
     */
    public String getPath(){
        return path;
    }
    
    /**
     * A factory method to instantiate a non-relative version of this node when given
     * the resolved location.
     * @param hit The discovered asset
     * @return The new located node
     */
    public abstract N createLocatedNode(AssetHit hit);
}
