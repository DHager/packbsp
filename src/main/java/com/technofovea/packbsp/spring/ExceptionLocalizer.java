/*
 * 
 */
package com.technofovea.packbsp.spring;

/**
 * Implementations of this interface make it easy to throw an exception that has
 * content localized for the end-user.
 * @author Darien Hager
 */

public interface ExceptionLocalizer {

    /**
     * Using code/argument information on the given exception, generate a final
     * String message and store it on the exception.
     * @param <T> The exception type
     * @param ex The exception
     * @return The modified exception, or an equivalent copy.
     */
    public <T extends IntlException> T localize(T ex);
    
}
