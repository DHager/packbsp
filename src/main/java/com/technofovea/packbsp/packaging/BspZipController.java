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
package com.technofovea.packbsp.packaging;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;

/**
 *
 * @author Darien Hager
 */
public class BspZipController extends ExternalController {

    static final String KEY_SRC_BSP = "srcbsp";
    static final String KEY_DST_BSP = "dstbsp";
    static final String KEY_FILELIST = "filelist";
    /**
     * Must be capitalized to override
     */
    static final String ENV_VPROJECT = "VPROJECT";
    CommandLine cmd;

    public BspZipController(File exe) {
        super();
        executor.setExitValues(new int[]{0,1});
        // Quotes are necessary or it complains when it hits spaces
        cmd = CommandLine.parse("\""+exe.getAbsolutePath()+"\"");

        // Does -game even work with bspzip? Try VProject env variable instead.
        /*
        cmd.addArgument("-game");
        cmd.addArgument("${" + KEY_GAME_DIR +"}");
         */
        cmd.addArgument("-addlist");
        cmd.addArgument("${" + KEY_SRC_BSP + "}");
        cmd.addArgument("${" + KEY_FILELIST + "}");
        cmd.addArgument("${" + KEY_DST_BSP + "}");

    }

    public static void writePackingList(Map<String, File> packItems, Writer dest) throws IOException {
        final String newline = "\r\n";
        List<String> keys = new ArrayList<String>();
        keys.addAll(packItems.keySet());
        Collections.sort(keys);
        for (String inner : keys) {
            File outer = packItems.get(inner);

            dest.write(inner);
            dest.write(newline);
            dest.write(outer.getAbsolutePath());
            dest.write(newline);

        }
    }

    public int packAssets(File filelist, File source, File dest, File gameDir, OutputStream out) throws ExecuteException {

        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_FILELIST, filelist.getAbsolutePath());
        params.put(KEY_SRC_BSP, source.getAbsolutePath());
        params.put(KEY_DST_BSP, dest.getAbsolutePath());
        //params.put(KEY_GAME_DIR,gameDir.getAbsolutePath());
        cmd.setSubstitutionMap(params);
        /*
        Iterator<String> iter = environment.keySet().iterator();
        while(iter.hasNext()){
            String key = iter.next();
            if(ENV_VPROJECT.equalsIgnoreCase(key)){
                iter.remove();
            }
        }
         */
        environment.put(ENV_VPROJECT, gameDir.getAbsolutePath());
        int exitval = exec(cmd, out);
        return exitval;
    }
}



