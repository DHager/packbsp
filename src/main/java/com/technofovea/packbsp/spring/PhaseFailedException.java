/*
 * 
 */
package com.technofovea.packbsp.spring;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 *
 * @author Darien Hager
 */
public class PhaseFailedException extends Exception implements IntlException{

    MessageSourceResolvable intlMessage;

    private PhaseFailedException(Throwable cause) {
        super(cause);
    }

    private PhaseFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    private PhaseFailedException(String message) {
        super(message);
    }

    
    
    public static PhaseFailedException create(String defaultMessage, String code, Object... arguments){
        return create(defaultMessage, null, code, arguments);
    }
    public static PhaseFailedException create(String defaultMessage, Throwable cause, String code, Object... arguments){
        
        PhaseFailedException ex;
        if(cause != null){
            ex= new PhaseFailedException(defaultMessage,cause);
        }else{
            ex = new PhaseFailedException(defaultMessage);
        }
        IntlExceptionUtil.combine(ex, code, arguments);
        return ex;
    }

    @Override
    public MessageSourceResolvable getIntlMessage() {
        return intlMessage;
    }

    public void setIntlMessage(MessageSourceResolvable intlMessage) {
        this.intlMessage = intlMessage;
    }
    
}
