/*
 * 
 */
package com.technofovea.packbsp.spring;

import java.util.Arrays;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 *
 * @author Darien Hager
 */
public class IntlExceptionUtil {
    
    protected static void stripTrace(Throwable ex) {
        int items = 0;
        final String exClass = ex.getClass().getCanonicalName();
        final String utilClass = IntlExceptionUtil.class.getCanonicalName();
        final StackTraceElement[] trace = ex.getStackTrace();
        for(StackTraceElement el : trace){
            if(exClass.equals(el.getClassName())){
                items++;
            }else if(utilClass.equals(el.getClassName())){
                items++;
            }else{
                break;
            }
        }
        ex.setStackTrace(Arrays.copyOfRange(trace, items, trace.length-1));
    }

    public static void combine(IntlException ex, String code, Object[] arguments) {
        if(arguments == null){
            arguments = new Object[0];
        }
        ex.setIntlMessage(new DefaultMessageSourceResolvable(new String[]{code}, arguments));
        if(ex instanceof Throwable){
            IntlExceptionUtil.stripTrace((Throwable)ex);
        }
    }
    
}
