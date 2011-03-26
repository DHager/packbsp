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
package com.technofovea.packbsp;

import com.technofovea.packbsp.packaging.ExternalController;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class PackbspUtil {

    private static final Logger logger = LoggerFactory.getLogger(PackbspUtil.class);
    private final static String steamRegistryPath = "HKCU\\Software\\Valve\\Steam\\SteamPath";
    private final static String steamDefaultGuess = "c:\\program files\\valve\\steam\\";

    public static final File createTempCopy(File source) throws IOException {
        String ext = FilenameUtils.getExtension(source.getName());
        File dest = File.createTempFile("packbsp_temp_", "." + ext);
        dest.deleteOnExit();
        FileInputStream fis = new FileInputStream(source);
        FileOutputStream fos = new FileOutputStream(dest);
        IOUtils.copy(fis, fos);
        fis.close();
        fos.close();
        return dest;
    }

    public static final File guessSteamDir() {

        /* 
         * Not efficient to create this over and over, but in this context we 
         * do not expect many repeat invocations, if at all.
         */
        WindowsRegistryChecker wrc = new WindowsRegistryChecker();

        String guess = null;
        try {
            guess = wrc.getKey(steamRegistryPath);
        } catch (IOException ex) {
            logger.error("Had an exception querying registry for Steam folder: {}",ex);
        }
        if (guess != null) {
            File f = new File(guess);
            if(f.exists()){
                return(f);
            }else{
                logger.warn("Found Steam directory from registry, but file location does not exist: {}",guess);
            }
        }else{
            logger.warn("Registry querying for Steam directory did not succeed.");
        }
        return new File(steamDefaultGuess);
        

    }
}