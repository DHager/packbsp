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
package com.technofovea.packbsp.jung;

import com.technofovea.packbsp.crawling.Edge;
import com.technofovea.packbsp.crawling.Node;
import com.technofovea.packbsp.crawling.nodes.MapNode;
import com.technofovea.packbsp.crawling.nodes.ModelNode;
import com.technofovea.packbsp.crawling.nodes.MultiPathNode;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import org.apache.commons.collections15.Transformer;
import org.omg.CORBA.UNKNOWN;

/**
 *
 * @author Darien Hager
 */
public class NodeVisualizer extends BasicVisualizationServer<Node, Edge> {

    final Color COLOR_MAP = Color.WHITE;
    final Color COLOR_MODEL = Color.RED;
    final Color COLOR_MATERIAL = Color.BLUE;
    final Color COLOR_TEX = Color.cyan;
    final Color COLOR_SKINSEARCH = trans(COLOR_MATERIAL);

    static Color trans(Color c) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 128);

    }

    class VertexFill implements Transformer<Node, Paint> {

        public Paint transform(Node node) {
            Class<? extends Node> nodeClass = node.getClass();
            if (nodeClass.equals(MapNode.class)) {
                return COLOR_MAP;
            } else if (nodeClass.equals(ModelNode.class)) {
                return COLOR_MODEL;
            } else if (nodeClass.equals(MultiPathNode.class)) {
               return COLOR_SKINSEARCH;
            } else {
                return Color.gray;
            }
        }
    }

    class VertexShape implements Transformer<Node, Shape> {

        public Shape transform(Node node) {
            return new RoundRectangle2D.Float(-50, -25, 100, 50, 20, 20);
        }
    }
    private final VertexShape shaper;
    private final VertexFill filler;

    public NodeVisualizer(Layout<Node, Edge> layout, Dimension preferredSize) {
        super(layout, preferredSize);

        filler = new VertexFill();
        shaper = new VertexShape();
        this.renderContext.setVertexFillPaintTransformer(filler);
        this.renderContext.setVertexShapeTransformer(shaper);

    }
}
