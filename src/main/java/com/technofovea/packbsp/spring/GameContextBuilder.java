/*
 * 
 */
package com.technofovea.packbsp.spring;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author Darien Hager
 */
public class GameContextBuilder {
    
    
    protected static Resource[] pathsToResources(ResourceLoader loader, List<String> paths) {
        List<Resource> ret = new ArrayList<Resource>(paths.size());
        for (String p : paths) {
            ret.add(loader.getResource(p));
        }
        return ret.toArray(new Resource[ret.size()]);
    }

    public static ApplicationContext createRoot(List<String> beanPaths, File confdir) {
        HybridResourceLoader loader = new HybridResourceLoader();
        loader.setRelativeFile(confdir);

        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.setResourceLoader(loader);

        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
        for (String path : beanPaths) {
            xmlReader.loadBeanDefinitions(path);
        }
/*
        PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
        pc.setLocations(pathsToResources(loader, propertyPaths));
        pc.setIgnoreUnresolvablePlaceholders(false);
        pc.postProcessBeanFactory(ctx.getBeanFactory());
  */      
        // Validating configurer
//        PropertyPlaceholderConfigurer vpc = new PropertyPlaceholderConfigurer();
 //       vpc.setOrder(10000);
  //      vpc.postProcessBeanFactory(ctx.getBeanFactory());
        
        
        
        ctx.refresh();
        return ctx;

    }

    public static ApplicationContext createGameContext(List<String> beanPaths, List<String> propertyPaths, ApplicationContext parent) {
        GenericApplicationContext ctx = new GenericApplicationContext(parent);
        ctx.setResourceLoader(parent);
        
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
        for (String path : beanPaths) {
            xmlReader.loadBeanDefinitions(path);
        }
        PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
        pc.setLocations(pathsToResources(parent, propertyPaths));
        pc.postProcessBeanFactory(ctx.getBeanFactory());
        
        ctx.refresh();
        return ctx;
    }
}
