/*
 * 
 */
package com.technofovea.packbsp.spring;

/**
 *
 * @author Darien Hager
 */
public class PhaseFailedException extends Exception implements IntlException {

    protected String localizationCode = null;
    protected Object[] localizationArgs= new Object[]{};
    protected String localizedMessage = null;

    
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

    public PhaseFailedException addLocalization(String code, Object... arguments) {
        localizationCode = code;
        localizationArgs = arguments;
        localizedMessage = null;
        return this;
    }

    public Object[] getLocalizationArgs() {
        return localizationArgs;
    }

    public String getLocalizationCode() {
        return localizationCode;
    }    
    
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
