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
package com.technofovea.packbsp.crawling;

import com.technofovea.hl2parse.fgd.DefaultLoader;
import com.technofovea.hl2parse.fgd.FgdSpec;
import com.technofovea.hllib.HlLib;
import com.technofovea.hllib.methods.ManagedLibrary;
import com.technofovea.packbsp.assets.SimpleTestLocator;
import com.technofovea.packbsp.conf.IncludeItem;
import com.technofovea.packbsp.crawling.handlers.HandlingException;
import com.technofovea.packbsp.crawling.nodes.MapNode;
import com.technofovea.packbsp.crawling.nodes.MiscFileNode;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Level;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Darien Hager
 */
public class CrawlTest {

    Mockery context = new JUnit4Mockery();

    public static class TestListener implements CrawlListener {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        Set<Node> missing = new HashSet<Node>();
        public TestListener() {
        }

        public Set<Node> getMissing() {
            return new HashSet<Node>(missing);
        }

        public void nodeContentMissing(Edge path, Node node, String filePath) {
            //out.println("L-Missing:\t"+node);
            missing.add(node);
        }

        public void nodeContentSkipped(Edge path, Node node, String filePath) {
            out.println("Skipped:\t"+node);
        }

        public void nodeError(Edge path, Node node, String filePath, File dataSource, HandlingException ex) {
            out.println("Error Handling:\t"+node+"\t"+ex.toString());
        }

        public void nodeHandleEnd(Edge path, Node node) {
        }

        public void nodeHandleStart(Edge path, Node node) {
        }

        public void nodePackingFound(Edge path, Node node, String relativePath, File dataSource) {
            out.println("To Pack:\t"+relativePath);
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
    public void temp2() throws Exception {
        org.apache.log4j.LogManager.getRootLogger().setLevel(Level.DEBUG);
        //File map = new File(getClass().getResource("../utest.bsp").toURI());
        File map = new File(getClass().getResource("../swamp_vignette_1.bsp").toURI());
        File srcFile = new File(getClass().getResource("../tf.fgd").toURI());

        final long startTime = System.currentTimeMillis();

        FgdSpec spec = new FgdSpec();
        DefaultLoader.fillSpec(srcFile,spec);

        DependencyGraph graph = new DependencyGraph();
        MapNode start = new MapNode(map, spec, new HashSet<IncludeItem>());
        //MaterialNode start = new MaterialNode("materials/models/props_swamp/cattails.vmt");
        graph.addVertex(start);

        final File gameDir = new File("C:/Program Files/Steam/steamapps/STEAM-LOGON-HERE/team fortress 2/tf");
        final File steamApps = new File("C:/Program Files/Steam/steamapps/");

        List<File> gcfs = new ArrayList<File>();
        gcfs.addAll(Arrays.asList(steamApps.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                if (name.endsWith(".gcf") && name.startsWith("team fortress 2")) {
                    return true;
                } else {
                    return false;
                }
            }
        })));
        gcfs.addAll(Arrays.asList(steamApps.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                if (name.endsWith(".gcf") && name.startsWith("source 2007 shared")) {
                    return true;
                } else {
                    return false;
                }
            }
        })));



        final ManagedLibrary lib = HlLib.getLibrary();
        Assert.assertNotNull(lib);
        final SimpleTestLocator al = new SimpleTestLocator(lib);
        al.addBsp(map);
        al.addDirectory(gameDir);
        for (File g : gcfs) {
            al.addGcf(g, "tf");
            al.addGcf(g, "hl2");
        }

        TestListener listener = new TestListener();
        DependencyExpander expander = new DependencyExpander(graph, start, al);
        expander.addListener(listener);

        while (!expander.isDone()) {
            expander.step();
        }
        final long endTime = System.currentTimeMillis();
        float secs = (endTime - startTime)/ (float)1000;

        System.out.write(listener.baos.toByteArray());
        for(Node n : listener.getMissing()){
            if(expander.wasRequired(n)){
                System.out.println("Missing (required):\t"+n);
            }
        }
        for(Node n : listener.getMissing()){
            if(!expander.wasRequired(n)){
                System.out.println("Missing (optional):\t"+n);
            }
        }

        System.out.println("Dependency crawling complete in "+secs+" seconds.");


        /*
        Set<Node> optional = new HashSet<Node>(expander.ctx.visited);
        optional.removeAll(expander.ctx.requiredNodes);
        System.out.println("Optional items:");
        for(Node n : optional){
            System.out.println("\t"+n);
        }
        Set<Node> missing = new HashSet<Node>();
        for (Node node : expander.ctx.visited) {
            NodeDecorator dec = expander.getDecorator(node);
            for (PackingPair pp : dec.packable) {
                if (pp.isMissing()) {
                    missing.add(node);
                    listener.nodeRequiredContentMissing(node, pp.relativePath);
                }
            }
        }
        System.out.println("Missing items:");
        for(Node n : missing){
            System.out.println("\t"+n);
        }

        System.out.println("Required+Missing items:");
        Set<Node> oops = new HashSet<Node>(expander.ctx.visited);

        oops.removeAll(optional);
        oops.retainAll(missing);
        for(Node n : oops){
            System.out.println("\t"+n);
        }
         */
        // TODO add listener/event support for crawler and work-phase





    }
}
