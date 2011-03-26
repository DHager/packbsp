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
package com.technofovea.packbsp.gui2.logging;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * @author Darien Hager
 */
public class LoggingTable extends AbstractTableModel {

    protected static class EventItem {

        private static final SimpleDateFormat df = new SimpleDateFormat();
        private static final String formatSpec = "yyyy/MM/dd HH:mm:ss";

        {
            df.applyPattern(formatSpec);

        }
        public long timestamp;
        public String timeText;
        public Level level;
        public String message;

        public EventItem(LoggingEvent evt) {
            this.timestamp = evt.timeStamp;
            this.level = evt.getLevel();
            this.message = evt.getRenderedMessage();

            synchronized (df) {
                timeText = df.format(new Date(timestamp));
            }
        }

        @Override
        public String toString() {
            return "{" + this.level + ":" + this.message.subSequence(0, Math.min(100, this.message.length()));
        }
    }

    public static class ListAppender extends AppenderSkeleton {

        final LoggingTable parent;
        final LinkedList<LoggingEvent> buffer = new LinkedList<LoggingEvent>();
        boolean pullPending = false;
        Runnable pullEvents = new Runnable() {

            public void run() {
                synchronized (buffer) {
                    if (buffer.size() == 0) {
                        return;
                    }
                    parent.tableAppend(buffer);
                    buffer.clear();
                    pullPending = false;
                }
            }
        };

        ListAppender(LoggingTable parent) {
            this.parent = parent;

        }

        @Override
        protected void append(LoggingEvent event) {
            synchronized (buffer) {
                buffer.add(event);
                if (!pullPending) {
                    // We can't directly stuff events into the logging table, because
                    // then we end up altering swing from this thread rather than
                    // through the event-dispatching thread.
                    pullPending = true; // Avoids extra runs that would get 0 items
                    SwingUtilities.invokeLater(pullEvents);
                }
            }
        }

        @Override
        public void close() {
            //TODO clear?
        }

        @Override
        public boolean requiresLayout() {
            return false;
        }
    }
    static final int MAX_ENTRIES = 5000;
    public static final int COL_TIME = 0;
    public static final int COL_LEVEL = 1;
    public static final int COL_MESSAGE = 2;
    public static final int NUM_COLS = 3;
    protected String PROP_LAST_ERROR = "lastError";
    protected Level threshold = Level.INFO;
    protected final PropertyChangeSupport pchange;
    protected ListAppender appender;
    protected EventItem lastError = null;
    protected final List<EventItem> events = Collections.synchronizedList(new LinkedList<EventItem>());
    int maxEntries = MAX_ENTRIES;
    boolean hitLimit = false;

    public LoggingTable() {
        super();

        pchange = new PropertyChangeSupport(this);
        appender = new ListAppender(this);
        appender.setThreshold(threshold);


    }

    public Level getThreshold() {
        return threshold;
    }

    public void setThreshold(Level threshold) {
        this.threshold = threshold;
        appender.setThreshold(threshold);
    }

    public ListAppender getAppender() {
        return appender;
    }

    public int getMaxEntries() {
        return maxEntries;
    }

    public void setMaxEntries(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    void pruneEvents() {
        int excess = events.size() - maxEntries;
        if (excess > 0) {
            hitLimit = true;
            if (excess == 1) {
                events.remove(0);
            } else {
                events.subList(0, excess).clear();

            }
            fireTableRowsDeleted(0, excess - 1);
        }

    }

    void tableAppend(final Collection<LoggingEvent> newEvents) {

        EventItem lastBatchError = null;
        int oldLastIndex = events.size() - 1;
        for (LoggingEvent event : newEvents) {
            EventItem item = new EventItem(event);
            events.add(item);
            if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
                lastBatchError = item;
            }
        }
        int newLastIndex = events.size() - 1;

        if (lastBatchError != null) {
            setLastError(lastBatchError);
        }

        fireTableRowsInserted(oldLastIndex, newLastIndex);
        pruneEvents();
    }

    void tableAppend(LoggingEvent event) {

        EventItem item = new EventItem(event);

        // Scrolling doesn't occur if the window is hidden, so if someone wants
        // to listen to our property change and make us visible, we need to
        // tell them before the scrolling occurs due to the table-change event.
        if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
            setLastError(item);
        }


        events.add(item);
        int lastIdx = events.size() - 1;
        fireTableRowsInserted(lastIdx, lastIdx);
        pruneEvents();

    }

    public int getColumnCount() {
        return NUM_COLS;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case COL_TIME:
                return "time";
            case COL_LEVEL:
                return "level";
            case COL_MESSAGE:
                return "message";
            default:
                return "";
        }
    }

    public void clear() {
        synchronized (events) {
            events.clear();
            fireTableDataChanged();
        }

    }

    public int getRowCount() {
        return events.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            EventItem item = events.get(rowIndex);
            switch (columnIndex) {
                case COL_TIME:
                    return item.timeText;
                case COL_LEVEL:
                    return item.level;
                case COL_MESSAGE:
                    return item.message;
                default:
                    return null;
            }

        } catch (IndexOutOfBoundsException ioobe) {
            return null;
        }
    }

    public EventItem getLastError() {
        return lastError;
    }

    public void setLastError(EventItem item) {
        EventItem oldError = this.lastError;
        this.lastError = item;
        pchange.firePropertyChange(PROP_LAST_ERROR, oldError, item);
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
