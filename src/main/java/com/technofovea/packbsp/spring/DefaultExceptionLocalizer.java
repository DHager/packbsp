/*
 * 
 */
package com.technofovea.packbsp.spring;

import java.util.Arrays;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.Assert;

/**
 *  
 * @author Darien Hager
 */
public class DefaultExceptionLocalizer implements MessageSourceAware, InitializingBean, ExceptionLocalizer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultExceptionLocalizer.class);
    protected MessageSource messageSource = null;
    protected Locale locale = null;

    @Override
    public <T extends IntlException> T localize(T ex) {
        String localized;
        try {
            String code = ex.getLocalizationCode();
            logger.debug("Localizing code {}",code);
            localized = messageSource.getMessage(code, ex.getLocalizationArgs(), locale);
            ex.setLocalizedMessage(localized);
        }
        catch (NoSuchMessageException e) {
            logger.error("Localization failure",e);
        }

        if (ex instanceof Throwable) {
            stripOwnTraceElement((Throwable) ex);
        }

        return ex;
    }

    /**
     * Modifies the stack-trace in the given exception to hide its passage through
     * this class, for better readability.
     * @param ex Exception to alter.
     */
    protected void stripOwnTraceElement(Throwable ex) {
        int items = 0;
        final String exClass = ex.getClass().getCanonicalName();
        final String utilClass = this.getClass().getCanonicalName();
        final StackTraceElement[] trace = ex.getStackTrace();

        for (StackTraceElement el : trace) {
            if (exClass.equals(el.getClassName())) {
                items++;
            } else if (utilClass.equals(el.getClassName())) {
                items++;
            } else {
                break;
            }
        }

        ex.setStackTrace(Arrays.copyOfRange(trace, items, trace.length - 1));
    }
    

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(messageSource);
    }
}
