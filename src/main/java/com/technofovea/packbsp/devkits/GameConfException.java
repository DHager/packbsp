/*
 * 
 */
package com.technofovea.packbsp.devkits;

import com.technofovea.packbsp.spring.IntlException;

/**
 *
 * @author Darien Hager
 */
public class GameConfException extends Exception implements IntlException {

    public GameConfException(Throwable cause) {
        super(cause);
    }

    public GameConfException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameConfException(String message) {
        super(message);
    }

    public GameConfException() {
    }
    String localizedMessage = null;

    public void setLocalizedMessage(String localizedMessage) {
        this.localizedMessage = localizedMessage;
    }

    @Override
    public String getLocalizedMessage() {
        if (localizedMessage != null) {
            return localizedMessage;
        } else {
            return super.getLocalizedMessage();
        }
    }
}
