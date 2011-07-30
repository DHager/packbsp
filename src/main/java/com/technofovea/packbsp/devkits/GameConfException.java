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

    protected String localizationCode = null;
    protected Object[] localizationArgs= new Object[]{};
    protected String localizedMessage = null;

    
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
    
    public GameConfException addLocalization(String code, Object... arguments) {
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
