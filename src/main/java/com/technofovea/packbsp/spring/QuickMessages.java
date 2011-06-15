/*
 * 
 */
package com.technofovea.packbsp.spring;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.Assert;

/**
 *
 * @author Darien Hager
 */
public class QuickMessages implements ApplicationContextAware, InitializingBean, MessageSource {

    private static Logger logger = LoggerFactory.getLogger(QuickMessages.class);
    private ApplicationContext ctx;
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(ctx);
    }

    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return ctx.getMessage(resolvable, locale);
    }

    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return ctx.getMessage(code, args, locale);
    }

    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return ctx.getMessage(code, args, defaultMessage, locale);
    }
    

    
    public String getMessage(String code, Object... args) {
        try {
            return ctx.getMessage(code, args, null);
        } catch (NoSuchMessageException e) {
            logger.error("Missing message definition", e);
            return code.toUpperCase(Locale.ENGLISH);
        }
    }
    public String getMessage(MessageSourceResolvable o){
        try{
            return ctx.getMessage(o,null);
        }catch(NoSuchMessageException e){
            logger.error("Missing message definition",e);
            String[] codes = o.getCodes();
            if(codes.length>0){
                return codes[codes.length-1].toUpperCase(Locale.ENGLISH);
            }else{
                return e.toString();
            }
        }
    }
}
