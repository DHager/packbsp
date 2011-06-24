/*
 * 
 */
package com.technofovea.packbsp.gui2;

import com.technofovea.packbsp.devkits.Devkit;
import com.technofovea.packbsp.devkits.Game;
import com.technofovea.packbsp.devkits.GameConfException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class GameTreeModel implements TreeModel {

    protected static class KitRoot extends ArrayList<Devkit> {

        @Override
        public String toString() {
            return "Development Kits";
        }
    }
    
    private static final Logger logger = LoggerFactory.getLogger(GameTreeModel.class);
    protected final KitRoot root = new KitRoot();
    protected final CopyOnWriteArrayList<TreeModelListener> listeners = new CopyOnWriteArrayList<TreeModelListener>();

    public GameTreeModel() {
    }

    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    public boolean isLeaf(Object node) {
        return (node instanceof Game);
    }

    public Object getRoot() {
        return root;
    }

    public void setKits(List<Devkit> kits) {
        root.clear();
        root.addAll(kits);

        TreeModelEvent e = new TreeModelEvent(this, new Object[]{root});
        for (TreeModelListener l : listeners) {
            l.treeStructureChanged(e);
        }
    }

    protected List getChildList(Object parent) {


        try {
            if (parent == root) {
                // Root itself is a list
                return root;
            } else if (parent instanceof Devkit) {
                final Devkit kit = (Devkit) parent;
                Collection<?> keys =kit.getGameKeys();
                List<Game> ret = new ArrayList<Game>();
                for(Object k:keys){
                    ret.add(kit.getGame(k));
                }
                return ret;
            } else {
                return null;
            }
        } catch (GameConfException gex) {
            logger.warn("Problem expanding tree node "+parent.toString(),gex);
            return null;
        }
    }

    public int getChildCount(Object parent) {
        List l = getChildList(parent);
        if(l==null){
            return 0;
        }
        return l.size();
    }

    public int getIndexOfChild(Object parent, Object child) {
        List l = getChildList(parent);
        if(l==null){
            return 0;
        }
        return l.indexOf(child);
    }

    public Object getChild(Object parent, int index) {
        List l = getChildList(parent);
        if(l==null){
            return null;
        }
        return l.get(index);
    }
    

    public void valueForPathChanged(TreePath path, Object newValue) {
        // Deliberately non-functional, this is a read-only model
    }
}
