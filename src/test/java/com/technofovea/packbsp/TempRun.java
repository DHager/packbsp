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

import com.technofovea.packbsp.crawling.CrawlListener;
import com.technofovea.packbsp.crawling.Edge;
import com.technofovea.packbsp.crawling.Node;
import com.technofovea.packbsp.crawling.handlers.HandlingException;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Darien Hager
 */
public class TempRun {

    static class TestListener implements CrawlListener {

        Set<Node> missing = new HashSet<Node>();
        PrintStream out;

        public TestListener(OutputStream dest) {
            out = new PrintStream(dest);
        }

        public Set<Node> getMissing() {
            return new HashSet<Node>(missing);
        }

        public void nodeContentMissing(Edge path, Node node, String filePath) {
            //out.println("L-Missing:\t"+node);
            missing.add(node);
        }

        public void nodeContentSkipped(Edge path, Node node, String filePath) {
            out.println("Skipped:\t" + node);
        }

        public void nodeError(Edge path, Node node, String filePath, File dataSource, HandlingException ex) {
            out.println("Error Handling:\t" + node + "\t" + ex.toString());
            throw (new RuntimeException(ex));
        }

        public void nodeHandleEnd(Edge path, Node node) {
        }

        public void nodeHandleStart(Edge path, Node node) {
        }

        public void nodePackingFound(Edge path, Node node, String relativePath, File dataSource) {
            out.println("To Pack:\t" + relativePath);
        }

        public void nodePackingCollision(Edge edge, Node node, String filePath, File dataSource) {
            out.println("Collided Pack:\t" + filePath);
        }

        

        public void nodeEnter(Edge edge, Node node) {
        }

        public void nodeExit(Edge edge, Node node) {
        }
    }
    @Ignore
    @Test
    public void go() throws Exception {
        final long startTime = System.currentTimeMillis();
        try {
            AppModel am = new AppModel();

            am.acceptSteamDirectory(new File("c:/program files/steam"));
            System.out.println(am.getGameOptions());
            am.acceptGame(SdkEngine.ORANGEBOX, "Team Fortress 2");
            System.out.println(am.getBspDir());
            am.acceptSourceFile(new File(
                    "C:/Program Files/Steam/steamapps/STEAM-LOGON-HERE/team fortress 2/tf/maps/swamp_vignette_1.bsp"));

            TestListener listener = new TestListener(System.out);
            am.acceptCrawling(listener,null);

            Map<String,DependencyItem> deps = am.getDependencies();
            List<String> sortedKeys = new ArrayList<String>(deps.keySet());
            Collections.sort(sortedKeys);
            for(String path : sortedKeys){
                DependencyItem di = deps.get(path);
                System.out.println(di);
            }
            

        } finally {
            final long endTime = System.currentTimeMillis();
            float elapsed = (endTime-startTime) / ((float)1000);
            System.out.println("Test run completed in "+elapsed+" seconds");
        }

    }
}
