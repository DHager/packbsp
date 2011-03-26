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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 *
 * @author Darien Hager
 */
public class EngineComboModel extends AbstractListModel implements ComboBoxModel{
    List<SdkEngine> items;
    int selected = 0;

     public EngineComboModel() {
        this(new ArrayList<SdkEngine>());
    }
    public EngineComboModel(Collection<SdkEngine> items) {
        super();
        this.items = new ArrayList<SdkEngine>(items);
        Collections.sort(this.items);
    }

    public void setItems(Collection<SdkEngine> items){
        int origSize = this.items.size();
        this.items = new ArrayList<SdkEngine>(items);
        Collections.sort(this.items);
        selected = 0;

        if(origSize>0){
            fireIntervalRemoved(this, 0, origSize-1);
        }
        if(this.items.size()>0){
            fireIntervalAdded(this,0, this.items.size()-1);
        }
    }


    public Object getElementAt(int index) {
        if(index > items.size()-1){
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
