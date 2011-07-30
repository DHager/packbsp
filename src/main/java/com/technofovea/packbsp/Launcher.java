/*
 * 
 */
package com.technofovea.packbsp;

import com.technofovea.packbsp.spring.PackbspApplicationContext;
import com.technofovea.packbsp.spring.PhaseFailedException;
import com.technofovea.packbsp.spring.SteamPhaseFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Darien Hager
 */
public class Launcher {

    private static Logger logger = LoggerFactory.getLogger(Launcher.class);
    
    public static class LauncherTest{
        
        protected SteamPhaseFactory f1;
        
        public void go() throws PhaseFailedException{
            f1.setSteamDir(new File("c:/program files/steam/"));
            f1.createPhase();
        }

        public SteamPhaseFactory getF1() {
            return f1;
        }

        public void setF1(SteamPhaseFactory f1) {
            this.f1 = f1;
        }

        
        
        
    
    }

    public static void main(String[] args) throws Exception{
        List<String> beanPaths = new ArrayList<String>();
        List<String> propPaths = new ArrayList<String>();
        beanPaths.add("core.xml");
        ApplicationContext ctx = PackbspApplicationContext.create(beanPaths, propPaths, new File("conf"));
        ctx.getBean("launcher",LauncherTest.class).go();
    }
}
