/*
 * 
 */
package com.technofovea.packbsp.spring;

/**
 *
 * @author Darien Hager
 */
public class PhaseFailedException extends RuntimeException implements IntlException {

    public PhaseFailedException(Throwable cause) {
        super(cause);
    }

    public PhaseFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhaseFailedException(String message) {
        super(message);
    }

    public PhaseFailedException() {
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
