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

import com.technofovea.packbsp.SdkEngine;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 *
 * @author Darien Hager
 */
public class GameComboModel extends AbstractListModel implements ComboBoxModel, ItemListener {

    Map<SdkEngine, List<String>> games;
    List<String> items;
    SdkEngine currentEngine;
    int selected;

    public GameComboModel() {
        this(new HashMap<SdkEngine, List<String>>());
    }

    public GameComboModel(Map<SdkEngine, List<String>> games) {
        super();
        this.games = games;
        this.items = new ArrayList<String>();
        currentEngine = null;
        selected = 0;

    }

    public SdkEngine getCurrentEngine() {
        return currentEngine;
    }

    public void itemStateChanged(ItemEvent e) {
        if(e.getItem() instanceof SdkEngine){
            SdkEngine eng = (SdkEngine)e.getItem();
            if(games.containsKey(eng)){
                setCurrentEngine(eng);
            }
        }
    }



    public void setCurrentEngine(SdkEngine newEngine) {
        if (currentEngine == newEngine) {
            return;
        }
        int origSize = this.items.size();
        if (this.games.containsKey(newEngine)) {
            this.items = games.get(newEngine);
        } else {
            this.items = new ArrayList<String>();
        }
        this.currentEngine = newEngine;

        if (origSize > 0) {
            fireIntervalRemoved(this, 0, origSize - 1);
        }
        if (this.items.size() > 0) {
            fireIntervalAdded(this, 0, this.items.size() - 1);
        }
    }

    public void setItems(Map<SdkEngine, List<String>> games) {
        this.games = games;
        int origSize = this.items.size();
        if (games.containsKey(currentEngine)) {
            this.items = games.get(currentEngine);
        } else {
            this.items = new ArrayList<String>();
        }
        selected = 0;

        if (origSize > 0) {
            fireIntervalRemoved(this, 0, origSize - 1);
        }
        if (this.items.size() > 0) {
            fireIntervalAdded(this, 0, this.items.size() - 1);
        }
    }

    public Object getElementAt(int index) {
        if (index > items.size() - 1) {
            return null;
        }
        return items.get(index);
    }

    public int getSize() {
        return items.size();
    }

    public Object getSelectedItem() {
        return getElementAt(selected);
    }

    public void setSelectedItem(Object anItem) {
        selected = items.indexOf(anItem);
    }
}
