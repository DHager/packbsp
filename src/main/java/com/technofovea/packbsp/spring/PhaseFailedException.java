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
public class PhaseFailedException extends Exception{

    MessageSourceResolvable intlMessage;

    protected PhaseFailedException(Throwable cause) {
        super(cause);
    }

    protected PhaseFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    protected PhaseFailedException(String message) {
        super(message);
    }

    
    
    public static PhaseFailedException create(String defaultMessage, String code, Object... arguments){
        PhaseFailedException ex= new PhaseFailedException(defaultMessage);
        if(arguments == null){
            arguments = new Object[0];
        }
        ex.intlMessage = new DefaultMessageSourceResolvable(new String[]{code}, arguments);
        return ex;
    }
    public static PhaseFailedException create(String defaultMessage, Throwable cause, String code, Object... arguments){
        PhaseFailedException ex= new PhaseFailedException(defaultMessage,cause);
        if(arguments == null){
            arguments = new Object[0];
        }
        ex.intlMessage = new DefaultMessageSourceResolvable(new String[]{code}, arguments);
        return ex;
    }

    public MessageSourceResolvable getIntlMessage() {
        return intlMessage;
    }
    
    
    
   
    
    
    

    
    
    
}
