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
package com.technofovea.packbsp.gui2;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Darien Hager
 */
public class AbsSourceEditor extends AbstractCellEditor implements TableCellEditor {

    JButton button = new JButton("Browse");
    FilePicker picker = new FilePicker();
    String currentRelPath = "";
    File gameDir;

    public AbsSourceEditor() {
        this.gameDir = new File("");
        /*
        ActionListener clickListener = new ActionListener() {

        public void actionPerformed(ActionEvent actionEvent) {
        int choice = chooser.showOpenDialog(button);
        switch (choice) {
        case JFileChooser.APPROVE_OPTION:
        currentFile = chooser.getSelectedFile();
        break;
        case JFileChooser.ERROR_OPTION:
        //TODO log
        //Fallthrough
        case JFileChooser.CANCEL_OPTION:
        currentFile = null;
        break;
        }
        }
        };
        button.addActionListener(clickListener);
         *
         */
    }

    public File getGameDir() {
        return gameDir;
    }

    public void setGameDir(File gameDir) {
        this.gameDir = gameDir;
    }

    

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if ((value != null) && value instanceof File) {
            picker.setSelectedFile((File) value);
            return picker;
        }
        ;


        //TODO initialize from the relative path in the table
        String path = (String)table.getValueAt(row, column-1);
        picker.setSelectedFile(new File(gameDir,path));

        return picker;
    }

    public Object getCellEditorValue() {
        File target = picker.getSelectedFile();
        if (target != null && target.isFile()) {
            return target;
        } else {
            return null;
        }
    }
}
