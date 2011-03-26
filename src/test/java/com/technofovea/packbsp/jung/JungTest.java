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
package com.technofovea.packbsp.jung;

import com.technofovea.packbsp.crawling.DependencyGraph;
import com.technofovea.packbsp.crawling.Edge;
import com.technofovea.packbsp.crawling.EdgeImpl;
import com.technofovea.packbsp.crawling.Node;
import com.technofovea.packbsp.crawling.nodes.MaterialNode;
import com.technofovea.packbsp.crawling.nodes.MiscFileNode;
import com.technofovea.packbsp.crawling.nodes.ModelNode;
import com.technofovea.packbsp.crawling.nodes.MultiPathNode;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import java.awt.Dimension;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Darien Hager
 */
public class JungTest {

    @Ignore
    @Test
    public void go() throws Exception {
        DependencyGraph g = new DependencyGraph();


        Node n1 = new ModelNode("mdl");
        Node n2 = new MultiPathNode("myMaterial", Arrays.asList(new String[]{"foo", "bar"}));
        Node n3 = new MaterialNode("vmt");
        Node n4 = new MiscFileNode("vtf");


        g.addVertex(n1);
        g.addEdge(new EdgeImpl(), n1, n2);

        g.addEdge(new EdgeImpl(), n2, n3);
        g.addEdge(new EdgeImpl(), n3, n4);



        // The Layout<V, E> is parameterized by the vertex and edge types
        Layout<Node, Edge> layout = new ISOMLayout<Node, Edge>(g);
        layout.setSize(new Dimension(300, 300)); // sets the initial size of the layout space
        // The BasicVisualizationServer<V,E> is parameterized by the vertex and edge types
        final BasicVisualizationServer<Node, Edge> nv = new BasicVisualizationServer<Node, Edge>(layout, new Dimension(350, 350));

        final JFrame frame = new JFrame("Simple Graph View");
        SwingUtilities.invokeAndWait(new Runnable() {

            public void run() {
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(nv);
                frame.pack();
                frame.setVisible(true);
            }
        });


        while (frame.isVisible()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }

    }
}
