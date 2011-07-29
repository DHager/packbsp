/*
 * 
 */
package com.technofovea.packbsp.conf;

import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Darien Hager
 */
public class ProfileTest {

    public ProfileTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testLoad() throws JAXBException {
        InputStream src = this.getClass().getResourceAsStream("test_profiles.xml");
        Assert.assertNotNull(src);
        JAXBContext jc = JAXBContext.newInstance(ProfileList.class, Profile.class);
        Unmarshaller um = jc.createUnmarshaller();
        //Marshaller m = jc.createMarshaller();
        //m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);


        ProfileList conf = (ProfileList) um.unmarshal(src);

        Assert.assertEquals(2, conf.size());
        Assert.assertEquals("atest", conf.get(0).getDescription());
        Assert.assertEquals("btest", conf.get(1).getDescription());

        Assert.assertEquals("a.xml", conf.get(0).getBeanFiles().get(0));
        Assert.assertEquals("a.properties", conf.get(0).getPropertyFiles().get(0));


    }
}
