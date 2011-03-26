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
package com.technofovea.packbsp.assets;

import java.util.List;

/**
 * This interface defines a general way to ask about what assets exist, what
 * order of precedence they have, and where they were found. The reason for 
 * providing hits beyond the first is to  allow code to warn and query the user 
 * on certain special conditions
 *
 * An AssetLocator is generally made once, and then never altered except to swap
 * out the map/bsp-file being used.
 *
 * For example, you may want to think twice before packing a file into a BSP
 * which would override content already on the end-user's machine. A user should
 * probably be warned if a file inside a BSP conflicts with a file on the disk:
 * Which version did they want to use?
 *
 * @author Darien Hager
 */
public interface AssetLocator {

    /**
     * Given a relative path, return a list of hits for assets which were found,
     * in the order of precedence.
     *
     * @param path A relative path to an asset
     * @return A list of hits, which may be empty.
     */
    List<AssetHit> locate(String path);

}
