/*
 * 
 */
package com.technofovea.packbsp.spring;

/**
 * Exceptions of this type contain I18N message-codes. It is the responsibility
 * of catching code (at an indefinite layer) to convert these codes into a
 * final representation.
 * 
 * @author Darien Hager
 */
public interface IntlException {
    
    public String getLocalizationCode();
    public Object[] getLocalizationArgs();
    public <T extends IntlException> T addLocalization(String code, Object... arguments);
    public void setLocalizedMessage(String msg);
    
}
