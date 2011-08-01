/*
 * 
 */
package com.technofovea.packbsp;

import com.technofovea.packbsp.conf.Profile;
import com.technofovea.packbsp.devkits.DetectedGame;
import com.technofovea.packbsp.spring.ExceptionLocalizer;
import com.technofovea.packbsp.spring.GamePhaseUpdater;
import com.technofovea.packbsp.spring.KitLoader;
import com.technofovea.packbsp.spring.PackbspApplicationContext;
import com.technofovea.packbsp.spring.PhaseFailedException;
import com.technofovea.packbsp.spring.ProfileLoader;
import com.technofovea.packbsp.spring.SteamPhaseUpdater;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 *
 * @author Darien Hager
 */
public class Launcher {

    private static Logger logger = LoggerFactory.getLogger(Launcher.class);

    public static class LauncherTest implements InitializingBean {

        protected ExceptionLocalizer localizer;
        protected SteamPhaseUpdater steamUpdater;
        protected KitLoader kitLoader;
        protected GamePhaseUpdater gameUpdater;
        protected ProfileLoader profileLoader;

        public void afterPropertiesSet() throws Exception {
            Assert.notNull(localizer);
            Assert.notNull(steamUpdater);
            Assert.notNull(kitLoader);
            Assert.notNull(gameUpdater);
            Assert.notNull(profileLoader);
        }

        public void go() throws PhaseFailedException {
            try {
                steamUpdater.updatePhase(new File("c:/program files/steam/"));
                //f1.createPhase();
                Set<DetectedGame> games = kitLoader.loadGames();
                List<Profile> profiles = profileLoader.loadProfiles();
                
                gameUpdater.updatePhase(games.iterator().next(), profiles.get(0));
            }
            catch (PhaseFailedException ex) {
                throw localizer.localize(ex);
            }
        }

        public SteamPhaseUpdater getSteamUpdater() {
            return steamUpdater;
        }

        public void setSteamUpdater(SteamPhaseUpdater steamUpdater) {
            this.steamUpdater = steamUpdater;
        }

        public ExceptionLocalizer getLocalizer() {
            return localizer;
        }

        public void setLocalizer(ExceptionLocalizer localizer) {
            this.localizer = localizer;
        }

        public KitLoader getKitLoader() {
            return kitLoader;
        }

        public void setKitLoader(KitLoader kitLoader) {
            this.kitLoader = kitLoader;
        }

        public GamePhaseUpdater getGameUpdater() {
            return gameUpdater;
        }

        public void setGameUpdater(GamePhaseUpdater gameUpdater) {
            this.gameUpdater = gameUpdater;
        }

        public ProfileLoader getProfileLoader() {
            return profileLoader;
        }

        public void setProfileLoader(ProfileLoader profileLoader) {
            this.profileLoader = profileLoader;
        }
    }

    public static void main(String[] args) throws Exception {
        List<String> beanPaths = new ArrayList<String>();
        List<String> propPaths = new ArrayList<String>();
        beanPaths.add("core.xml");
        ApplicationContext ctx = PackbspApplicationContext.create(beanPaths, propPaths, new File("conf"));
        ctx.getBean("launcher", LauncherTest.class).go();

    }
}
