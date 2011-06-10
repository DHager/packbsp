/*
 * 
 */
package com.technofovea.packbsp.devkits;

import java.io.File;
import java.util.List;

/**
 *
 * @author Darien Hager
 */
public class L4D2Game implements Game {
    static final String GAME_ID = "l4d2";
    final L4D2Engine parent;
    final File gameExe;

    public L4D2Game(L4D2Engine parent) {
        this.parent = parent;
        gameExe = new File(parent.parent.baseDir, "left4dead2.exe");
    }

    public boolean isPresent() {
        return gameExe.isFile();
    }

    public GameEngine getParent() {
        return parent;
    }

    public String getId() {
        return GAME_ID;
    }

    public File getVmfDir() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public File getBspDir() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public File getGameDir() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<File> getFgdFiles() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
