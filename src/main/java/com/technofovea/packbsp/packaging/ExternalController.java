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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.technofovea.packbsp.packaging;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public abstract class ExternalController {

    private static final Logger logger = LoggerFactory.getLogger(ExternalController.class);


    protected static int WATCHDOG_TIME = 60000;
    protected static int SUCCESSFUL_EXIT = 0;
    protected Map<String,String> environment;
    protected DefaultExecutor executor = new DefaultExecutor();
    protected ExecuteWatchdog watchdog;

    public ExternalController() {
        executor.setExitValue(SUCCESSFUL_EXIT);
        watchdog = new ExecuteWatchdog(WATCHDOG_TIME);
        executor.setWatchdog(watchdog);
        environment = new HashMap<String, String>(System.getenv());
    }




    protected int exec(CommandLine cl, OutputStream out) throws ExecuteException {
        PumpStreamHandler handler = new PumpStreamHandler(out); // Auto started/stopped
        executor.setStreamHandler(handler);
        try {
            logger.debug("Executing: "+cl.toString());
            int exitval = executor.execute(cl,environment);
            logger.debug("Exit val: "+exitval);
            return exitval;
        } catch (IOException ex) {
            throw new ExecuteException("IO error with output", -1, ex);
        }
    }
}
