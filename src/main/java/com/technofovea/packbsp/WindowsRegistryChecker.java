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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class WindowsRegistryChecker {

    private static final Logger logger = LoggerFactory.getLogger(WindowsRegistryChecker.class);
    static final String KEY_PATH = "path";
    static final String KEY_VALUE_NAME = "vname";
    protected static int WATCHDOG_TIME = 5000;
    protected static int SUCCESSFUL_EXIT = 0;
    protected Map<String, String> environment;
    protected DefaultExecutor executor = new DefaultExecutor();
    protected ExecuteWatchdog watchdog;
    CommandLine cmd;

    Pattern resultLine = Pattern.compile("^\\s+(\\w+)\\s+REG_([A-Z_]+)\\s+(.*)$");

    public WindowsRegistryChecker() {
        executor.setExitValue(SUCCESSFUL_EXIT);
        watchdog = new ExecuteWatchdog(WATCHDOG_TIME);
        executor.setWatchdog(watchdog);
        environment = new HashMap<String, String>(System.getenv());
        cmd = CommandLine.parse("reg.exe");
        cmd.addArgument("query");
        cmd.addArgument("${" + KEY_PATH + "}");
        cmd.addArgument("/v");
        cmd.addArgument("${" + KEY_VALUE_NAME + "}");
    }

    /**
     * Attempts to get the string representation of a given registry key.
     *
     * @param path The registry path to the key that needs to be queried
     * @return The string representation of the key, or null if not found.
     * @throws IOException
     */
    public String getKey(String path) throws IOException {

        // Chop off last item in path due to how reg.exe expects it seperately
        final List<String> pieces = Arrays.asList(path.split("\\\\"));
        if(pieces.size() <= 1){
            logger.warn("Invalid input for registry path: {}",path);
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for(int i = 0; i < pieces.size()-1;i++){
            sb.append(pieces.get(i));
            if(i < pieces.size()-2){
                sb.append("\\");
            }
        }
        final String valueName = pieces.get(pieces.size()-1);
        final String initialPath = sb.toString();


        final Map<String, String> params = new HashMap<String, String>();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        params.put(KEY_PATH, initialPath);
        params.put(KEY_VALUE_NAME, valueName);
        cmd.setSubstitutionMap(params);
        int exitval = exec(cmd, out);
        final String result = out.toString();

        for(String line : result.split("\\r?\\n")){
            Matcher m = resultLine.matcher(line);
            if(m.matches()){
                return m.group(3);
            }
        }
        return null;
    }

    protected int exec(CommandLine cl, OutputStream out) throws ExecuteException {
        PumpStreamHandler handler = new PumpStreamHandler(out); // Auto started/stopped
        executor.setStreamHandler(handler);
        try {
            logger.debug("Executing: " + cl.toString());
            int exitval = executor.execute(cl, environment);
            logger.debug("Exit val: " + exitval);
            return exitval;
        } catch (IOException ex) {
            throw new ExecuteException("IO error with output", -1, ex);
        }
    }

    public static void main(String[] args) {
        WindowsRegistryChecker c = new WindowsRegistryChecker();
        String result;
        try {
            result = c.getKey("HKCU\\Software\\Valve\\Steam\\KnownCachesHash");
            System.out.println(result);

        } catch (IOException ex) {
        }
    }
}
