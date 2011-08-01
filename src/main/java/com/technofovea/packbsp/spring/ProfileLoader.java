/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.packbsp.conf.Profile;
import com.technofovea.packbsp.conf.ProfileList;
import java.io.File;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 *
 * @author Darien Hager
 */
public class ProfileLoader implements InitializingBean {
    
    protected File source;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(source);
        Assert.isTrue(source.isFile());
    }
    
    public File getSource() {
        return source;
    }
    
    public void setSource(File source) {
        this.source = source;
    }
    
    public List<Profile> loadProfiles() throws PhaseFailedException {
        try {
            return ProfileList.load(source);
        }
        catch (JAXBException ex) {
            throw new PhaseFailedException("Unable to load profile list", ex).addLocalization("error.bad_profile_listing", source);
        }
    }
}
