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
 * FilePicker.java
 *
 * Created on Apr 25, 2010, 9:08:31 PM
 */
package com.technofovea.packbsp.gui2;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Darien Hager
 */
public class FilePicker extends javax.swing.JPanel {

    public static final int DIRECTORIES_ONLY = JFileChooser.DIRECTORIES_ONLY;
    public static final int FILES_AND_DIRECTORIES = JFileChooser.FILES_AND_DIRECTORIES;
    public static final int FILES_ONLY = JFileChooser.FILES_ONLY;
    public static final int OPEN_DIALOG = JFileChooser.OPEN_DIALOG;
    public static final int SAVE_DIALOG = JFileChooser.SAVE_DIALOG;
    public static final int CUSTOM_DIALOG = JFileChooser.CUSTOM_DIALOG;
    final PropertyChangeSupport pchange;
    final Component owner;
    String chooseButtonText = "Choose";
    boolean setToNull = false;

    public FilePicker() {
        this(null);
    }

    /** Creates new form FilePicker */
    public FilePicker(Component parent) {
        super();
        pchange = new PropertyChangeSupport(this);
        this.owner = parent;
        initComponents();
        chooserDialog.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("SelectedFileChangedProperty".equals(evt.getPropertyName())) {
                        Object newFile = evt.getNewValue();
                    setToNull = (newFile == null);
                    if (newFile != null) {
                        assert (newFile instanceof File);
                        pathTextField.setText(((File) newFile).getAbsolutePath());
                        pchange.firePropertyChange("SelectedFile", evt.getOldValue(), newFile);
                    } else {
                        pathTextField.setText("");
                        pchange.firePropertyChange("SelectedFile", evt.getOldValue(), newFile);
                    }
                }
            }
        });
    }

    @Override
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (pchange != null) {
            pchange.removePropertyChangeListener(listener);
        }
    }

    @Override
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        if (pchange != null) {
            return pchange.getPropertyChangeListeners();
        } else {
            return new PropertyChangeListener[0];
        }
    }

    @Override
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        // If we don't check for null, something REALLY bizarre happens during instantiation
        if (pchange != null) {
            pchange.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        pchange.addPropertyChangeListener(listener);
    }

    public String getChooseButtonText() {
        return chooseButtonText;
    }

    public void setChooseButtonText(String chooseButtonText) {
        this.chooseButtonText = chooseButtonText;
    }

    public void setSelectedFile(File f) {

        chooserDialog.setSelectedFile(f);
        if (f != null && f.exists()) {
            if (f.isDirectory()) {
                chooserDialog.setCurrentDirectory(f);
            } else {
                chooserDialog.setCurrentDirectory(f.getParentFile());
            }
        }else{
            chooserDialog.setSelectedFile(null);
        }
    }

    public File getSelectedFile() {
        if (setToNull) {
            return null;
        }
        return new File(this.pathTextField.getText());
    }

    public List<FileFilter> getFilters() {
        return Arrays.asList(chooserDialog.getChoosableFileFilters());
    }

    public void setFilters(FileFilter filter) {
        setFilters(Arrays.asList(new FileFilter[]{filter}));
    }

    public void setFilters(List<FileFilter> filters) {
        // Remove old filters
        for (FileFilter f : chooserDialog.getChoosableFileFilters()) {
            chooserDialog.removeChoosableFileFilter(f);
        }

        // Add new filters
        for (FileFilter f : filters) {
            chooserDialog.addChoosableFileFilter(f);
        }
    }

    public void setFileSelectionMode(int mode) {
        chooserDialog.setFileSelectionMode(mode);
    }

    public int getFileSelectionMode() {
        return chooserDialog.getFileSelectionMode();
    }

    public void setDialogType(int dialogType) {
        chooserDialog.setDialogType(dialogType);
    }

    public void setDialogTitle(String dialogTitle) {
        chooserDialog.setDialogTitle(dialogTitle);
    }

    public void setCurrentDirectory(File dir) {
        chooserDialog.setCurrentDirectory(dir);
    }

    public int getDialogType() {
        return chooserDialog.getDialogType();
    }

    public String getDialogTitle() {
        return chooserDialog.getDialogTitle();
    }

    public File getCurrentDirectory() {
        return chooserDialog.getCurrentDirectory();
    }

    public void showDialog() {
        browseButtonActionPerformed(null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chooserDialog = new javax.swing.JFileChooser();
        pathTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();

        chooserDialog.setDialogType(javax.swing.JFileChooser.CUSTOM_DIALOG);
        chooserDialog.setFileHidingEnabled(true);
        chooserDialog.setName("chooserDialog"); // NOI18N

        pathTextField.setName("pathTextField"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(FilePicker.class);
        browseButton.setIcon(resourceMap.getIcon("icons.browse")); // NOI18N
        browseButton.setText("Browse...");
        browseButton.setName("browseButton"); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(pathTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(browseButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(pathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(browseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        File target = new File(pathTextField.getText());
        File prevTarget = null;
        // If a "real" file or directory is not defined, backtrack and attempt
        // to get as near of a directory path as possible
        while ((target != null) && (!target.exists()) && (target != prevTarget)) {
            target = target.getParentFile();
        }
        if (target != null) {
            if (target.isDirectory()) {
                chooserDialog.setCurrentDirectory(target);
            } else {
                chooserDialog.setCurrentDirectory(target.getParentFile());
            }
        }

        // Pop up the dialog
        int result = chooserDialog.showDialog(this.owner, chooseButtonText);
        switch (result) {
            case JFileChooser.APPROVE_OPTION:
                break;
            case JFileChooser.CANCEL_OPTION:
                break;
            case JFileChooser.ERROR_OPTION:
                //TODO log
                break;

        }
    }//GEN-LAST:event_browseButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JFileChooser chooserDialog;
    private javax.swing.JTextField pathTextField;
    // End of variables declaration//GEN-END:variables
}
