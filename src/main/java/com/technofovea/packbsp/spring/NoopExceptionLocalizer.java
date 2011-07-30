/*
 * 
 */
package com.technofovea.packbsp.spring;

/**
 * This implementation doesn't actually do localization, and may be used as a
 * fall-back when a real implementation is for some reason unavailable.
 * 
 * @author Darien Hager
 */
public class NoopExceptionLocalizer implements ExceptionLocalizer {

    private static final NoopExceptionLocalizer instance = new NoopExceptionLocalizer();
    
    private NoopExceptionLocalizer() {
    }

    public static NoopExceptionLocalizer getInstance() {
        return instance;
    }

    public <T extends IntlException> T localize(T ex) {
        return ex;
    }
    
}
