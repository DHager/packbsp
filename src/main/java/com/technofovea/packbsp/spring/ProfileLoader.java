/*
 * 
 */
package com.technofovea.packbsp.spring;

import org.springframework.util.Assert;

/**
 *
 * @author Darien Hager
 */
public class ProfileLoader extends AbstractPackbspComponent {

    protected final String DEFAULT_DATA_PATH = "conf/profiles/profiles.xml";
    protected String dataPath = DEFAULT_DATA_PATH;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(dataPath);
    }

    
    
    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }
    
    
}
