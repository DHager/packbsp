/*
 * 
 */
package com.technofovea.packbsp;

import com.technofovea.packbsp.spring.NestedScope;
import com.technofovea.packbsp.spring.PhaseFailedException;
import com.technofovea.packbsp.spring.QuickMessages;
import com.technofovea.packbsp.spring.SteamPhaseFactory;
import com.technofovea.packbsp.spring.SteamPhaseFactory.SteamPhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Darien Hager
 */
public class Launcher {
    
    private static Logger logger = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{
                    "beans.xml"
                });
                QuickMessages qm = ctx.getBean("messages", QuickMessages.class);
        
        
        SteamPhaseFactory bean = ctx.getBean("factory.steam",SteamPhaseFactory.class);
        //bean.setSteamDir(new File("c:\\program files\\steam\\steam.exe"));
        
        
        //SteamPhase p = ctx.getBean("phase.steam",SteamPhase.class);
        //System.out.println(p.getSteamDir());
        //ctx.getBean("scope.steam",NestedScope.class).invalidateScope(); 
        
        try{
            bean.proceed();
        }catch(PhaseFailedException pfe){
            System.out.println(">>>\t"+qm.getMessage(pfe.getIntlMessage()));
            logger.error("error",pfe);
        }
        
        
        
        /*
        final String msgname = "gozer";
        String message = qm.getMessage(msgname, "a", "b", "c");
        System.out.println(message);
        message = ctx.getMessage(msgname, new Object[]{"a", "b", "c"}, null);
        System.out.println(message);
       */
         
    }
    
}
