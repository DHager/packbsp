/*
 * 
 */
package com.technofovea.packbsp.conf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 *
 * @author Darien Hager
 */
@XmlAccessorType(value = XmlAccessType.NONE)
public class Profile {
    @XmlAttribute(required = true)
    String name;
    @XmlAttribute(required = true)
    String devkit;
    @XmlElement
    String description;
    @XmlElementWrapper(name = "configs")
    @XmlElement(name = "file")
    List<String> beanFiles;
    @XmlElementWrapper(name = "properties")
    @XmlElement(name = "file")
    List<String> propFiles;

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getDevkit() {
        return devkit;
    }

    public List<String> getBeanFiles() {
        return new ArrayList<String>(beanFiles);
    }

    public List<String> getPropertyFiles() {
        return new ArrayList<String>(propFiles);
    }

    @Override
    public String toString() {
        return "Profile{" + "name=" + name + "}";
    }
    
}
