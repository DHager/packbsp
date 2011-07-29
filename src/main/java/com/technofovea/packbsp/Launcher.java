/*
 * 
 */
package com.technofovea.packbsp;

import com.technofovea.hl2parse.registry.ClientRegistry;
import com.technofovea.packbsp.conf.ProfileList;
import com.technofovea.packbsp.spring.GamePhaseFactory;
import com.technofovea.packbsp.spring.PackbspApplicationContext;
import com.technofovea.packbsp.spring.ProfileLoader;
import com.technofovea.packbsp.spring.SteamDirectoryFinder;
import com.technofovea.packbsp.spring.SteamPhase;
import com.technofovea.packbsp.spring.SteamPhaseFactory;
import com.technofovea.packbsp.spring.SteamUserReader;
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

    public static void main(String[] args) {
        List<String> beanPaths = new ArrayList<String>();
        List<String> propPaths = new ArrayList<String>();
        beanPaths.add("templaunch.xml");
        ApplicationContext ctx = PackbspApplicationContext.create(beanPaths, propPaths, new File("conf"));

        //ctx.getBean("user-detector", SteamUserReader.class).detectCurrentUser(new File("c:/program files/steam"));
        //ctx.getBean("steamdir-detector", SteamDirectoryFinder.class).guessSteamDir();
        //ctx.getBean("profile-loader", ProfileLoader.class);

        ctx.getBean("steam-phase-factory",SteamPhaseFactory.class).setSteamDir(new File("c:/program files/steam"));
        
        System.out.println(ctx.getBean("steam-directory"));
        //ClientRegistry cr = ctx.getBean("client-registry", ClientRegistry.class); 


//        SteamPhaseFactory f1 = ctx.getBean("factory-steam", SteamPhaseFactory.class);
//        GamePhaseFactory f2 = ctx.getBean("factory-game", GamePhaseFactory.class);
//        SteamPhase p1 = ctx.getBean("phase-steam",SteamPhase.class);
//        System.out.println(p1.getCurrentUser());
//        System.out.println(p1.getGames());


//        f2.setChosenGame(p1.getGames().iterator().next());
        // f2.setChosenProfile(null);

        //SteamPhase p = ctx.getBean("phase.steam",SteamPhase.class);
        //System.out.println(p.getSteamDir());
        //ctx.getBean("scope.steam",NestedScope.class).invalidateScope();

    }
}
