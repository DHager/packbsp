/*
 * 
 */
package com.technofovea.packbsp.spring;

import java.io.File;
import java.util.List;

/**
 *
 * @author Darien Hager
 */
public interface GameInfoData {
    public String getName();
    public int getAppId();
    public List<Integer> getExtraIds();
    public List<String> getSearchPaths();
    public File getSource();
}
