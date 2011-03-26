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

import com.technofovea.packbsp.DependencyItem;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class PackingTableModel extends AbstractTableModel implements TableModel {

    private static final Logger logger = LoggerFactory.getLogger(PackingTableModel.class);
    private static Map<String, String> extensionNames = new HashMap<String, String>();

    static {
        extensionNames.put(".ain", "AI Navgraph");
        extensionNames.put(".vmt", "Material");
        extensionNames.put(".mdl", "Model");
        extensionNames.put(".vtf", "Texture");
        extensionNames.put(".txt", "Misc text");
        extensionNames.put(".ani", "Model animations");
        extensionNames.put(".phy", "Model physics");
        extensionNames.put(".vvd", "Model collision data");
        extensionNames.put(".vtx", "Model vertex strip");

        //TODO more types
    }

    protected enum Column {

        PATH("Relative Path", String.class, true) {

            @Override
            public Object output(DependencyItem di) {
                return di.getPath();
            }
        },
        TYPE("Type", String.class, false) {

            @Override
            public Object output(DependencyItem di) {
                String path = di.getPath().toLowerCase();

                if (path.matches("materials/maps/[^/]+/c(-?\\d+)_(-?\\d+)_(-?\\d+).vtf")) {
                    return "Cubemap";
                }
                for (String ext : extensionNames.keySet()) {
                    if (path.endsWith(ext)) {
                        return extensionNames.get(ext);
                    }
                }
                return "";
            }
        },
        SOURCE("File Source", File.class, true) {

            @Override
            public Object output(DependencyItem di) {
                return di.getDataSource();
            }
        },
        REQ("Required", DependencyItem.Necessity.class, false) {

            @Override
            public Object output(DependencyItem di) {
                return di.getNecessity();
            }
        },
        STATUS("Status", DependencyItem.Status.class, false) {

            @Override
            public Object output(DependencyItem di) {
                return di.getStatus();
            }
        };
        String displayName;
        Class<?> classType;
        boolean editable;

        Column(String displayName, Class<?> classType, boolean editable) {
            this.displayName = displayName;
            this.classType = classType;
            this.editable = editable;
        }

        public boolean isEditable() {
            return editable;
        }

        public abstract Object output(DependencyItem di);
    }
    List<DependencyItem> data = new ArrayList<DependencyItem>();

    public PackingTableModel() {
    }

    public void setData(List<DependencyItem> data) {
        if (this.data.size() > 0) {
            fireTableRowsDeleted(0, this.data.size() - 1);
        }

        this.data = new ArrayList<DependencyItem>(data);

        if (this.data.size() > 0) {
            fireTableRowsInserted(0, this.data.size() - 1);
        }

    }

    public void setData(Map<String, DependencyItem> map) {

        List<String> keys = new ArrayList<String>(map.keySet());
        Collections.sort(keys);
        List<DependencyItem> newList = new ArrayList<DependencyItem>(map.size());
        for (String k : keys) {
            DependencyItem di = map.get(k);
            if (DependencyItem.Status.STOCK.equals(di.getStatus())) {
                logger.debug("Screening out 'stock' item: {}", di.getPath());
                continue;
            }
            if (DependencyItem.Status.PREPACKED.equals(di.getStatus())) {
                logger.debug("Screening out 'prepacked' item: {}", di.getPath());
                continue;
            }
            if (di.getPath().matches("materials/maps/[^/]+/c(-?\\d+)_(-?\\d+)_(-?\\d+).vtf")) {
                logger.debug("Screening out probable cubemap: {}", di.getPath());
                continue;
            }

            newList.add(di);
        }

        setData(newList);
    }

    public void clear() {
        setData(new ArrayList<DependencyItem>());
    }

    public boolean addItem(String relPath, File source) {
        DependencyItem di = new DependencyItem(relPath);
        di.setDataSource(source);
        di.setNecessity(DependencyItem.Necessity.UNKNOWN);
        di.setStatus(DependencyItem.Status.UNKNOWN);

        if (data.contains(di)) {
            return false;
        }
        int oldLen = data.size();
        boolean success = data.add(di);
        if (success) {
            int newLen = data.size();
            assert (oldLen + 1 == newLen);
            fireTableRowsInserted(newLen - 1, newLen - 1);
        }
        return success;
    }

    public void removeItems(int[] selectedRows) {
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            int rowIndex = selectedRows[i];
            removeItem(rowIndex);
        }
    }

    public boolean removeItem(int rowIndex) {
        assert (rowIndex >= 0);
        assert (rowIndex < data.size());
        DependencyItem item = data.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
        return (item != null);
    }

    public List<DependencyItem> getData() {
        return data;
    }

    /**
     * Tests packable pairs to see if any input is malformed or if any
     * source files do not exist. 
     * 
     * @param pairs Target-path to source-file mappings.
     * @return List of error strings
     */
    public static List<String> validatePackablePairs(Map<String, File> pairs) {
        List<String> errors = new ArrayList<String>();
        for (String path : pairs.keySet()) {
            File f = pairs.get(path);
            if (!f.exists()) {
                errors.add("Source file does not exist: " + f.getAbsolutePath());
                continue;
            }
            if (f.isDirectory()) {
                errors.add("Source file is actually a directory: " + f.getAbsolutePath());
                continue;
            }
            if (path.replace("/", "").trim().length() == 0) {
                errors.add("Target path is empty, with corresponding source file: " + f.getAbsolutePath());
                continue;
            }
        }
        return errors;

    }

    public Map<String, File> getPackablePairs() {
        Map<String, File> ret = new HashMap<String, File>();
        for (DependencyItem di : data) {
            String p = di.getPath();
            File f = di.getDataSource();
            if (f == null) {
                continue;
            }
            if (p == null) {
                p = "";
            }
            assert (!DependencyItem.Status.STOCK.equals(di.getStatus()));
            ret.put(p, f);
        }
        return ret;
    }

    public int getColumnCount() {
        return Column.values().length;
    }

    public int getRowCount() {
        return data.size();
    }

    @Override
    public String getColumnName(int index) {
        if (index < 0 || index >= Column.values().length) {
            return "Error";
        }
        return Column.values()[index].displayName;
    }

    @Override
    public Class<?> getColumnClass(int index) {
        if (index < 0 || index >= Column.values().length) {
            return Void.class;
        }
        return Column.values()[index].classType;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        assert (rowIndex >= 0);
        assert (rowIndex < getRowCount());
        assert (columnIndex >= 0);
        assert (columnIndex <= Column.values().length);

        //TODO Certain item settings should be invalidated on edit
        //DependencyItem di = data.get(rowIndex);
        Column c = Column.values()[columnIndex];
        return c.isEditable();

    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        assert (rowIndex >= 0);
        assert (rowIndex < getRowCount());
        assert (columnIndex >= 0);
        assert (columnIndex <= Column.values().length);

        DependencyItem di = data.get(rowIndex);
        Column c = Column.values()[columnIndex];

        return c.output(di);


    }

    @Override
    public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
        assert (rowIndex >= 0);
        assert (rowIndex < getRowCount());
        assert (columnIndex >= 0);
        assert (columnIndex <= Column.values().length);

        //TODO Certain item settings should be invalidated on edit
        DependencyItem di = data.get(rowIndex);
        Column c = Column.values()[columnIndex];
        assert (newValue != null);
        assert (c.classType.equals(newValue.getClass()));

        switch (c) {
            case PATH:
                String newPath = (String) newValue;
                String oldPath = di.getPath();
                if (oldPath != null && oldPath.equals(newPath)) {
                    return;
                }
                if (newPath == null || newPath.trim().replace("/", "").length() == 0) {
                    return;
                }
                di.setPath(newPath);
                di.setNecessity(DependencyItem.Necessity.UNKNOWN);
                di.setStatus(DependencyItem.Status.UNKNOWN);

                break;
            case SOURCE:
                File newSource = (File) newValue;
                File oldSource = di.getDataSource();
                if (oldSource != null && oldSource.equals(newSource)) {
                    return;
                }
                di.setDataSource(newSource);
                di.setNecessity(DependencyItem.Necessity.UNKNOWN);
                di.setStatus(DependencyItem.Status.UNKNOWN);
                break;

        }
        fireTableRowsUpdated(rowIndex, rowIndex);

    }
}
