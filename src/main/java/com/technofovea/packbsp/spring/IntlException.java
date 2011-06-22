/*
 * 
 */
package com.technofovea.packbsp.spring;

import org.springframework.context.MessageSourceResolvable;

/**
 *
 * @author Darien Hager
 */
public interface IntlException {

    MessageSourceResolvable getIntlMessage();
    
}
