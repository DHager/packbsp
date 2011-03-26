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

/**
 *
 * @author Darien Hager
 */
public enum SdkEngine {

    ORANGEBOX("Source Engine 2009", "orangebox"),
    EP2("Source Engine 2007", "source2007"),
    EP1("Source Engine 2006", "ep1"),;
    String displayName;
    String dirName;

    private SdkEngine(String displayName, String dirName) {
        this.displayName = displayName;
        this.dirName = dirName;
    }

    public String getDirName() {
        return dirName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName + " (" + dirName + ")";
    }
}
