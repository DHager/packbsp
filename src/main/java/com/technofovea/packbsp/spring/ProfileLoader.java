/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.packbsp.conf.Profile;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 *
 * @author Darien Hager
 */
public class ProfileLoader implements InitializingBean {

    protected final String DEFAULT_DATA_PATH = "conf/profiles/profiles.xml";
    protected String dataPath = DEFAULT_DATA_PATH;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(dataPath);
    }

    
    
    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }
    
    public List<Profile> loadProfiles(){
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
