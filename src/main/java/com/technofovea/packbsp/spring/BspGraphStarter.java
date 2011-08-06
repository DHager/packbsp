/*
 * 
 */
package com.technofovea.packbsp.spring;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 *
 * @author Darien Hager
 */
public class BspGraphStarter implements GraphStarterChoice, MessageSourceAware {

    protected static class BspFilter implements FileFilter {

        public boolean accept(File pathname) {
            return pathname.getName().toLowerCase().endsWith(".bsp");
        }
    }
    
    protected FileFilter filter = new BspFilter();
    protected Set<File> choices = new HashSet<File>();
    protected MessageSource messageSource = null;
    protected String messageCode = "starter.bsp_files";
    protected String defaultDescription = "BSP files to analyze";

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public Set<File> getChoices() {
        return new HashSet<File>(choices);
    }

    public void setChoices(Set<File> choice) {
        this.choices.clear();
        for(File f : choice){
            if(! filter.accept(f)){
                throw new IllegalArgumentException("Invalid choice given: "+f);
            }
        }
        this.choices.addAll(choice);
    }

    public String getDescription() {
        final String code = "starter.pick_bsp";
        if(messageSource == null){
            return "Unresolved message: "+code;
        }
        return messageSource.getMessage(code, new Object[]{}, null);

    }

    public FileFilter getFileFilter() {
        return filter;
    }

    public boolean isMultipleAllowed() {
        return true;
    }
}
