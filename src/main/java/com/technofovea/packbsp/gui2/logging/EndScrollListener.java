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

import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author Darien Hager
 */
public class EndScrollListener implements TableModelListener {

    final EndScrollListener self = this;
    JTable logTable;
    JFrame logFrame;
    boolean pending = false;

    Runnable scrollLater = (new Runnable() {
        public void run() {
            self.scrollToEnd();
        }
    });

    public EndScrollListener(JTable logTable, JFrame logFrame) {
        this.logTable = logTable;
        this.logFrame = logFrame;
    }


    

    public void scrollToEnd() {
        final Rectangle rect = logTable.getCellRect(logTable.getRowCount() - 1, 0, true);
        logTable.scrollRectToVisible(rect);
        pending = false;
    }

    public void tableChanged(TableModelEvent e) {
        if (!logFrame.isVisible()) {
            return; // Don't bother scrolling if not seen


        }
        if(pending){
            return; // Already scheduled
        }
        pending = true;
        SwingUtilities.invokeLater(scrollLater);


    }
}
