/*
 * 
 */
package com.technofovea.packbsp.spring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 *
 * @author Darien Hager
 */
public abstract class AbstractPackbspComponent implements InitializingBean {

    protected ExceptionLocalizer localizer = NoopExceptionLocalizer.getInstance();

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(localizer);
    }

    public ExceptionLocalizer getLocalizer() {
        return localizer;
    }

    public void setLocalizer(ExceptionLocalizer localizer) {
        this.localizer = localizer;
    }
     
}
