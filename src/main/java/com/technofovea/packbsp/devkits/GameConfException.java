/*
 * 
 */
package com.technofovea.packbsp.devkits;

import com.technofovea.packbsp.spring.IntlException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 *
 * @author Darien Hager
 */
public class GameConfException extends Exception implements IntlException{

    MessageSourceResolvable intlMessage;

    private GameConfException(Throwable cause) {
        super(cause);
    }

    private GameConfException(String message, Throwable cause) {
        super(message, cause);
    }

    private GameConfException(String message) {
        super(message);
    }

    
    
    public static GameConfException create(String defaultMessage, String code, Object... arguments){
        return create(defaultMessage, null, code, arguments);
    }
    public static GameConfException create(String defaultMessage, Throwable cause, String code, Object... arguments){
        
        GameConfException ex;
        if(cause != null){
            ex= new GameConfException(defaultMessage,cause);
        }else{
            ex = new GameConfException(defaultMessage);
        }
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
