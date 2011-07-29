/*
 * 
 */
package com.technofovea.packbsp.spring;

import java.io.File;
import java.util.List;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 *
 * @author Darien Hager
 */
public class PackbspApplicationContext extends GenericApplicationContext {

    protected PackbspApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    protected PackbspApplicationContext() {
    }

    public static PackbspApplicationContext create(List<String> beanPaths, List<String> propertyPaths, File confdir) {
        HybridResourceLoader loader = new HybridResourceLoader();
        loader.setRelativeFile(confdir);

        PackbspApplicationContext ctx = new PackbspApplicationContext();
        ctx.setResourceLoader(loader);

        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
        for (String path : beanPaths) {
            xmlReader.loadBeanDefinitions(path);
        }
        PropertiesBeanDefinitionReader propReader = new PropertiesBeanDefinitionReader(ctx);
        propReader.setResourceLoader(loader);
        for (String path : propertyPaths) {
            propReader.loadBeanDefinitions(path);
        }

        ctx.refresh();
        return ctx;

    }

    public static PackbspApplicationContext create(List<String> beanPaths, List<String> propertyPaths, ApplicationContext parent) {
        PackbspApplicationContext ctx = new PackbspApplicationContext(parent);
        ctx.setResourceLoader(parent);
        
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
        for (String path : beanPaths) {
            xmlReader.loadBeanDefinitions(path);
        }
        PropertiesBeanDefinitionReader propReader = new PropertiesBeanDefinitionReader(ctx);
        propReader.setResourceLoader(parent);
        for (String path : propertyPaths) {
            propReader.loadBeanDefinitions(path);
        }

        ctx.refresh();
        return ctx;
    }
}
