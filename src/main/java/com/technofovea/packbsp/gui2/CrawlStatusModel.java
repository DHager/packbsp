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

import com.technofovea.packbsp.crawling.EmptyCrawlListener;
import com.technofovea.packbsp.crawling.CrawlListener;
import com.technofovea.packbsp.crawling.Edge;
import com.technofovea.packbsp.crawling.Node;
import com.technofovea.packbsp.crawling.handlers.HandlingException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;

/**
 *
 * @author Darien Hager
 */
public class CrawlStatusModel extends EmptyCrawlListener implements CrawlListener {

    protected static class ErrorListModel extends AbstractListModel {

        List<String> items = new ArrayList<String>();

        public Object getElementAt(int index) {
            if (index < 0 || index >= getSize()) {
                return null;
            }
            return items.get(index);
        }

        public int getSize() {
            return items.size();
        }

        public void append(String s) {
            items.add(s);
            int newindex = items.size() - 1;
            assert (newindex >= 0);
            fireIntervalAdded(this, newindex, newindex);
        }

        public void clear() {
            int oldSize = getSize();
            items.clear();
            if (oldSize > 0) {
                fireIntervalRemoved(this, 0, oldSize - 1);
            }
        }
    }
    public static final String PROP_CURRENTNODE = "currentNode";
    public static final String PROP_MISSINGCOUNT = "missingCount";
    public static final String PROP_FOUNDCOUNT = "foundCount";
    public static final String PROP_SKIPPEDCOUNT = "skippedCount";
    public static final String PROP_ERRORCOUNT = "errorCount";
    public static final String PROP_CURRENTNODETEXT = "currentNodeText";
    PropertyChangeSupport pchange;
    protected Node currentNode = null;
    protected int missingCount = 0;
    protected int foundCount = 0;
    protected int skippedCount = 0;
    protected int errorCount;
    protected ErrorListModel errorList = new ErrorListModel();
    protected String currentNodeText = "";

    public CrawlStatusModel() {
        pchange = new PropertyChangeSupport(this);
    }

    void updateNodeText() {
        String s;
        if (currentNode == null) {
            s = "";
        } else {
            s = currentNode.toString();
        }
        setCurrentNodeText(s);
    }

    void reset() {
        setCurrentNode(null);
        setMissingCount(0);
        setFoundCount(0);
        setSkippedCount(0);
        int oldSize = errorList.getSize();
        errorList.clear();
        pchange.firePropertyChange(PROP_ERRORCOUNT, oldSize, 0);

    }

    void addError(String str) {
        errorList.append(str);
        int newsize = errorList.getSize();
        pchange.firePropertyChange(PROP_ERRORCOUNT, newsize - 1, newsize);

    }

    @Override
    public void nodeContentMissing(Edge edge, Node node, String filePath) {
        setMissingCount(getMissingCount() + 1);
    }

    @Override
    public void nodePackingCollision(Edge edge, Node node, String filePath, File dataSource) {
        addError("Path collision. This file attempts to override a built-in asset and will be ignored: " + dataSource.getAbsolutePath());
    }

    @Override
    public void nodeContentSkipped(Edge edge, Node node, String filePath) {
        setSkippedCount(getSkippedCount() + 1);
    }

    @Override
    public void nodeError(Edge edge, Node node, String filePath, File dataSource, HandlingException ex) {
        addError(filePath + " : " + ex.getMessage());
    }

    @Override
    public void nodeHandleStart(Edge edge, Node node) {
        setCurrentNode(node);
    }

    @Override
    public void nodeHandleEnd(Edge edge, Node node) {
        if (currentNode == node) {
            setCurrentNode(null);
        }
    }

    @Override
    public void nodePackingFound(Edge edge, Node node, String filePath, File dataSource) {
        setFoundCount(getFoundCount() + 1);
    }

    /**
     * Get the value of foundCount
     *
     * @return the value of foundCount
     */
    public int getFoundCount() {
        return foundCount;
    }

    /**
     * Set the value of foundCount
     *
     * @param foundCount new value of foundCount
     */
    public void setFoundCount(int foundCount) {
        int oldFoundCount = this.foundCount;
        this.foundCount = foundCount;
        pchange.firePropertyChange(PROP_FOUNDCOUNT, oldFoundCount, foundCount);
    }

    /**
     * Get the value of missingCount
     *
     * @return the value of missingCount
     */
    public int getMissingCount() {
        return missingCount;
    }

    /**
     * Set the value of missingCount
     *
     * @param missingCount new value of missingCount
     */
    public void setMissingCount(int missingCount) {
        int oldMissingCount = this.missingCount;
        this.missingCount = missingCount;
        pchange.firePropertyChange(PROP_MISSINGCOUNT, oldMissingCount, missingCount);
    }

    /**
     * Get the value of skippedCount
     *
     * @return the value of skippedCount
     */
    public int getSkippedCount() {
        return skippedCount;
    }

    /**
     * Set the value of skippedCount
     *
     * @param skippedCount new value of skippedCount
     */
    public void setSkippedCount(int skippedCount) {
        int oldSkippedCount = this.skippedCount;
        this.skippedCount = skippedCount;
        pchange.firePropertyChange(PROP_SKIPPEDCOUNT, oldSkippedCount, skippedCount);
    }

    /**
     * Get the value of currentNode
     *
     * @return the value of currentNode
     */
    public Node getCurrentNode() {
        return currentNode;
    }

    /**
     * Set the value of currentNode
     *
     * @param currentNode new value of currentNode
     */
    public void setCurrentNode(Node currentNode) {
        Node oldcurrentNode = this.currentNode;
        this.currentNode = currentNode;
        pchange.firePropertyChange(PROP_CURRENTNODE, oldcurrentNode, currentNode);
        updateNodeText();
    }

    public ListModel getErrorList() {
        return errorList;
    }

    /**
     * Get the value of errorCount
     *
     * @return the value of errorCount
     */
    public int getErrorCount() {
        return errorCount;
    }

    /**
     * Get the value of currentNodeText
     *
     * @return the value of currentNodeText
     */
    public String getCurrentNodeText() {
        return currentNodeText;
    }

    /**
     * Set the value of currentNodeText
     *
     * @param newText new value of currentNodeText
     */
    public void setCurrentNodeText(String newText) {
        if (this.currentNodeText.equals(newText)) {
            return;
        }
        String oldCurrentNodeText = this.currentNodeText;
        this.currentNodeText = newText;
        pchange.firePropertyChange(PROP_CURRENTNODETEXT, oldCurrentNodeText, newText);
    }

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pchange.removePropertyChangeListener(propertyName, listener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        pchange.removePropertyChangeListener(listener);
    }

    public synchronized boolean hasListeners(String propertyName) {
        return pchange.hasListeners(propertyName);
    }

    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pchange.addPropertyChangeListener(propertyName, listener);
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        pchange.addPropertyChangeListener(listener);
    }
}

