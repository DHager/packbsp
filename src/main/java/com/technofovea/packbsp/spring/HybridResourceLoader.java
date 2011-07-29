/*
 * 
 */
package com.technofovea.packbsp.spring;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/**
 * When given a string starting in file: or classpath:, this loader will attempt 
 * to load from the "absolute" location given by the string. If no prefix is present,
 * a relative load will be attempted first from the filesystem and then from the
 * classpath.
 * @author Darien Hager
 */
public class HybridResourceLoader implements ResourceLoader {

    String FILE_URL_PREFIX = ResourceUtils.FILE_URL_PREFIX;

    private static class ClassRelativeContextResource extends ClassPathResource implements ContextResource {

        private final Class clazz;

        public ClassRelativeContextResource(String path, Class clazz) {
            super(path, clazz);
            this.clazz = clazz;
        }

        public String getPathWithinContext() {
            return getPath();
        }

        @Override
        public Resource createRelative(String relativePath) {
            String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
            return new ClassRelativeContextResource(pathToUse, this.clazz);
        }
    }

    private static class FileSystemContextResource extends FileSystemResource implements ContextResource {

        public FileSystemContextResource(File f) {
            super(f);
        }

        public String getPathWithinContext() {
            return getPath();
        }
    }
    protected File relativeFile = new File(".");
    protected Class relativeClazz = null;
    protected ClassLoader classLoader;

    public HybridResourceLoader() {
        classLoader = this.getClass().getClassLoader();
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public Class getRelativeClazz() {
        return relativeClazz;
    }

    public void setRelativeClazz(Class relativeClazz) {
        this.relativeClazz = relativeClazz;
        classLoader = this.relativeClazz.getClassLoader();
    }

    public File getRelativeFile() {
        return relativeFile;
    }

    public void setRelativeFile(File relativeFile) {
        this.relativeFile = relativeFile;
    }

    public Resource getResource(String location) {
        Assert.notNull(location, "Location must not be null");
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            // Explicit classpath
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
        } else if (location.startsWith(FILE_URL_PREFIX)) {
            final String path = location.substring(CLASSPATH_URL_PREFIX.length());
            return new FileSystemContextResource(new File(this.relativeFile, path));

        } else {
            try {
                // Try to parse the location as a URL...
                URL url = new URL(location);
                return new UrlResource(url);
            }
            catch (MalformedURLException ex) {
                // No URL -> resolve as resource path.
                return getResourceByPath(location);
            }
        }
    }

    protected Resource getResourceByPath(String location) {
        Resource fileAttempt = new FileSystemContextResource(new File(this.relativeFile, location));
        if (fileAttempt.exists()) {
            return fileAttempt;
        }

        Resource classAttempt;
        if (relativeClazz != null) {
            classAttempt = new ClassRelativeContextResource(location, this.relativeClazz);
        } else {
            classAttempt = new ClassPathResource(location, getClassLoader());
        }
        if (classAttempt.exists()) {
            return classAttempt;
        }

        // If we're going to return a nonexistent resource, let's send back 
        // the file-version since it is our preferred one.
        return fileAttempt;

    }
}
