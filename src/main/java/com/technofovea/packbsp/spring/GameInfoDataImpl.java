/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.hl2parse.vdf.GameInfoReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Darien Hager
 */
public class GameInfoDataImpl implements GameInfoData {

    protected String name;
    protected int appId;
    protected List<Integer> extraIds = new ArrayList<Integer>();
    protected List<String> searchPaths = new ArrayList<String>();
    protected File source;
    
    public static GameInfoDataImpl createFromReader(GameInfoReader reader){
        GameInfoDataImpl ret = new GameInfoDataImpl();
        ret.setName(reader.getGameName());
        ret.setAppId(reader.getSteamAppId());
        ret.setSource(reader.getSourceFile());
        ret.setExtraIds(reader.getAdditionalIds());
        ret.setSearchPaths(reader.getSearchPaths());
        return ret;
        
    }

    public File getSource() {
        return source;
    }

    public void setSource(File source) {
        this.source = source;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public List<Integer> getExtraIds() {
        return new ArrayList<Integer>(extraIds);
    }

    public void setExtraIds(List<Integer> extraIds) {
        this.extraIds.clear();
        this.extraIds.addAll(extraIds);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSearchPaths() {
        return new ArrayList<String>(searchPaths);
    }

    public void setSearchPaths(List<String> searchPaths) {
        this.searchPaths.clear();
        this.searchPaths.addAll(searchPaths);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GameInfoDataImpl other = (GameInfoDataImpl) obj;
        if (( this.name == null ) ? ( other.name != null ) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.appId != other.appId) {
            return false;
        }
        if (this.extraIds != other.extraIds && ( this.extraIds == null || !this.extraIds.equals(other.extraIds) )) {
            return false;
        }
        if (this.searchPaths != other.searchPaths && ( this.searchPaths == null || !this.searchPaths.equals(other.searchPaths) )) {
            return false;
        }
        if (this.source != other.source && ( this.source == null || !this.source.equals(other.source) )) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + ( this.name != null ? this.name.hashCode() : 0 );
        hash = 97 * hash + this.appId;
        hash = 97 * hash + ( this.extraIds != null ? this.extraIds.hashCode() : 0 );
        hash = 97 * hash + ( this.searchPaths != null ? this.searchPaths.hashCode() : 0 );
        hash = 97 * hash + ( this.source != null ? this.source.hashCode() : 0 );
        return hash;
    }
    
    
    
    

}
