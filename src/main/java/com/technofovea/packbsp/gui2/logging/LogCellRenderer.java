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
package com.technofovea.packbsp.gui2.logging;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Darien Hager
 */
public class LogCellRenderer extends JLabel implements TableCellRenderer {

    Map<org.apache.log4j.Level, Color> l4jColors = new HashMap<org.apache.log4j.Level, Color>();
    Map<java.util.logging.Level, Color> julColors = new HashMap<java.util.logging.Level, Color>();

    public LogCellRenderer() {
        super();

        Color lBlue = new Color(128, 205, 255);
        l4jColors.put(org.apache.log4j.Level.FATAL, Color.RED);
        l4jColors.put(org.apache.log4j.Level.ERROR, Color.RED);
        l4jColors.put(org.apache.log4j.Level.WARN, Color.YELLOW);
        l4jColors.put(org.apache.log4j.Level.INFO, lBlue);
        l4jColors.put(org.apache.log4j.Level.DEBUG, Color.LIGHT_GRAY);
        l4jColors.put(org.apache.log4j.Level.TRACE, Color.LIGHT_GRAY);

        julColors.put(java.util.logging.Level.SEVERE, Color.RED);
        julColors.put(java.util.logging.Level.WARNING, Color.YELLOW);
        julColors.put(java.util.logging.Level.CONFIG, lBlue);
        julColors.put(java.util.logging.Level.INFO, lBlue);
        julColors.put(java.util.logging.Level.FINE, Color.LIGHT_GRAY);
        julColors.put(java.util.logging.Level.FINER, Color.LIGHT_GRAY);
        julColors.put(java.util.logging.Level.FINEST, Color.LIGHT_GRAY);




    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        assert(SwingUtilities.isEventDispatchThread());
        if (value instanceof org.apache.log4j.Level) {
            org.apache.log4j.Level lev = (org.apache.log4j.Level) value;
            setText(lev.toString());
            Color c = l4jColors.get(lev);
            if (c != null) {
                setBackground(c);
                setOpaque(true);
            } else {
                setOpaque(false);
            }
        } else if (value instanceof java.util.logging.Level) {
            java.util.logging.Level lev = (java.util.logging.Level) value;
            setText(lev.toString());
            Color c = julColors.get(lev);
            if (c != null) {
                setBackground(c);
                setOpaque(true);
            } else {
                setOpaque(false);
            }
        } else {
            return null;
        }
        return this;

    }
}
