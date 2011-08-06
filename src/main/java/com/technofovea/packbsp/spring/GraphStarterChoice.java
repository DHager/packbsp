/*
 * 
 */
package com.technofovea.packbsp.spring;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;

/**
 *
 * @author Darien Hager
 */
public interface GraphStarterChoice{

    public String getDescription();

    public FileFilter getFileFilter();
    
    public boolean isMultipleAllowed(); // Grammatically problematic, but we must all make sacrifices for science.
    
    public Set<File> getChoices();
    
    public void setChoices(Set<File> items) throws IllegalArgumentException;
}
