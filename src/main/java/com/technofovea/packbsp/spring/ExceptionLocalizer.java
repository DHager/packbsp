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
     * Sets a localized message, using the value of the default {@link Throwable#getMessage()} for the message-code.
     * @param <T> The exception type
     * @param ex The exception
     * @param arguments Arguments to use in message-construction
     * @return The modified exception, or an equivalent copy.
     */
    <T extends Throwable & IntlException> T localizeDefault(T ex, Object... arguments);

    /**
     * Sets a localized message, using the given message-code.
     * @param <T> The exception type
     * @param ex The exception
     * @param code The message-code to map to
     * @param arguments Arguments to use in message-construction
     * @return The modified exception, or an equivalent copy.
     */
    <T extends IntlException> T localize(T ex, String code, Object... arguments);
    
}
