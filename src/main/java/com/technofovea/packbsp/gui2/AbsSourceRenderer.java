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
package com.technofovea.packbsp.gui2;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Darien Hager
 */
public class AbsSourceRenderer extends JLabel implements TableCellRenderer {

    public AbsSourceRenderer() {
        //setHorizontalAlignment(RIGHT);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if ((value != null) && !(value instanceof File)) {
            return null;
        }
        File f = (File) value;
        if (f == null) {
            setText("<none>");
            setOpaque(true);
            setBackground(Color.LIGHT_GRAY);
            return this;
        } else if (!f.isFile()) {
            // Nonexistent or non-file
            setText(f.getAbsolutePath());
            setBackground(Color.RED);
            setOpaque(true);
        } else {
            // Is a file
            // TODO check extension vs. relative path
            setText(f.getAbsolutePath());
            setOpaque(false);

        }
        return this;
    }
}
