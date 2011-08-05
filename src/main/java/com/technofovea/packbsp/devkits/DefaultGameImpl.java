/*
 * 
 */
package com.technofovea.packbsp.devkits;

import com.technofovea.hl2parse.vdf.GameConfigReader;
import com.technofovea.hl2parse.vdf.GameInfoReader;
import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class DefaultGameImpl implements DetectedGame {

    private static final Logger logger = LoggerFactory.getLogger(DefaultGameImpl.class);
    File engineBinDir;
    Devkit parent;
    GameConfigReader.Game gcData;
    File infoFile;
    GameInfoReader giReader;
   
    protected DefaultGameImpl(){
        
    }
    
    public Devkit getParentKit() {
        return parent;
    }

    public File getBspDir() {
        return gcData.getBspDir();
    }

    public File getVmfDir() {
        return gcData.getVmfDir();
    }

    public File getGameDir() {
        return gcData.getGameDir();
    }

    public List<File> getFgdFiles() {
        return gcData.getFgds();
    }

    public String getName() {
        return gcData.getName();
    }

    public File getKitBinDir() {
        return engineBinDir;
    }
    
    
    public int getAppId() {
        return giReader.getSteamAppId();
    }

    public List<Integer> getExtraIds() {
        return giReader.getAdditionalIds();
    }

    public List<String> getSearchPaths() {
        return giReader.getSearchPaths();
    }

    public File getSource() {
        return giReader.getSourceFile();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultGameImpl other = (DefaultGameImpl) obj;
        if (this.engineBinDir != other.engineBinDir && ( this.engineBinDir == null || !this.engineBinDir.equals(other.engineBinDir) )) {
            return false;
        }
        if (this.parent != other.parent && ( this.parent == null || !this.parent.equals(other.parent) )) {
            return false;
        }
        if (this.gcData != other.gcData && ( this.gcData == null || !this.gcData.equals(other.gcData) )) {
            return false;
        }
        if (this.infoFile != other.infoFile && ( this.infoFile == null || !this.infoFile.equals(other.infoFile) )) {
            return false;
        }
        if (this.giReader != other.giReader && ( this.giReader == null || !this.giReader.equals(other.giReader) )) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + ( this.engineBinDir != null ? this.engineBinDir.hashCode() : 0 );
        hash = 59 * hash + ( this.parent != null ? this.parent.hashCode() : 0 );
        hash = 59 * hash + ( this.gcData != null ? this.gcData.hashCode() : 0 );
        hash = 59 * hash + ( this.infoFile != null ? this.infoFile.hashCode() : 0 );
        hash = 59 * hash + ( this.giReader != null ? this.giReader.hashCode() : 0 );
        return hash;
    }


    @Override
    public String toString() {
        return "{" + parent.getId() + ":" + getName() + "}";
    }
}
