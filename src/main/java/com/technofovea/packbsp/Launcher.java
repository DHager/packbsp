/*
 * 
 */
package com.technofovea.packbsp;

import com.technofovea.packbsp.devkits.Devkit;
import com.technofovea.packbsp.devkits.Game;
import com.technofovea.packbsp.spring.GamePhaseFactory;
import com.technofovea.packbsp.spring.GamePhaseFactory.GamePhase;
import com.technofovea.packbsp.spring.PhaseFailedException;
import com.technofovea.packbsp.spring.QuickMessages;
import com.technofovea.packbsp.spring.SteamPhaseFactory;
import com.technofovea.packbsp.spring.SteamPhaseFactory.SteamPhase;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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


        //SteamPhaseFactory f1 = ctx.getBean("factory.steam", SteamPhaseFactory.class);
        GamePhaseFactory f2 = ctx.getBean("factory.game", GamePhaseFactory.class);
        //SteamPhase p = ctx.getBean("phase.steam",SteamPhase.class);
        //System.out.println(p.getSteamDir());
        //ctx.getBean("scope.steam",NestedScope.class).invalidateScope();
        SteamPhase sp = ctx.getBean("phase.steam", SteamPhaseFactory.SteamPhase.class);
        Map<Devkit, List<Game>> games = sp.getGames();
        //f2.setMaps(new HashSet<File>());
        Game cgame = null;
        for (Devkit k : games.keySet()) {
            List<Game> gl = games.get(k);
            for (Game g : gl) {
                cgame = g;
                break;
            }
            if (cgame != null) {
                break;
            }
        }
        f2.setChosenGame(cgame);
        GamePhase gp = ctx.getBean("phase.game", GamePhaseFactory.GamePhase.class);
        System.out.println(ctx.getBean("regtemp"));

        //PropertiesConfiguration conf = ctx.getBean("config",PropertiesConfiguration.class); 


        /*
        final String msgname = "gozer";
        String message = qm.getMessage(msgname, "a", "b", "c");
        System.out.println(message);
        message = ctx.getMessage(msgname, new Object[]{"a", "b", "c"}, null);
        System.out.println(message);
         */

    }
}
