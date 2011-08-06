/*
 * 
 */
package com.technofovea.packbsp;

import com.technofovea.packbsp.conf.Profile;
import com.technofovea.packbsp.devkits.DetectedGame;
import com.technofovea.packbsp.spring.ExceptionLocalizer;
import com.technofovea.packbsp.spring.GamePhaseUpdater;
import com.technofovea.packbsp.spring.KitLoader;
import com.technofovea.packbsp.spring.GameContextBuilder;
import com.technofovea.packbsp.spring.GameState;
import com.technofovea.packbsp.spring.GraphStarterChoice;
import com.technofovea.packbsp.spring.PhaseFailedException;
import com.technofovea.packbsp.spring.ProfileLoader;
import com.technofovea.packbsp.spring.ProfileState;
import com.technofovea.packbsp.spring.SteamPhaseUpdater;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
        protected GameState gameState;
        protected ProfileLoader profileLoader;
        protected ProfileState profileState;

        public void afterPropertiesSet() throws Exception {
            Assert.notNull(localizer);
            Assert.notNull(steamUpdater);
            Assert.notNull(kitLoader);
            Assert.notNull(gameUpdater);
            Assert.notNull(gameState);
            Assert.notNull(profileLoader);
            Assert.notNull(profileState);
        }

        public void go() throws PhaseFailedException {
            try {
                steamUpdater.updatePhase(new File("c:/program files/steam/"));
                //f1.createPhase();
                Set<DetectedGame> games = kitLoader.loadGames();
                System.out.println(games);
                List<Profile> profiles = profileLoader.loadProfiles();

                Profile p = profiles.get(0);
                for (DetectedGame g : games) {
                    if (g.getName().contains("Fortress")) {
                        gameUpdater.updatePhase(g, p);
                        break;
                    }
                }

                //AssetLocator temp = profileState.getGameContext().getBean("default-asset-locator", AssetLocator.class);
                //System.out.println(temp.locate("particles/bigboom.pcf"));
                
                final GraphStarterChoice graphStarter = profileState.getGraphStarter();
                Set<File> maps = new HashSet<File>();
                File[] foundMaps =  gameState.getActiveGame().getBspDir().listFiles(graphStarter.getFileFilter());
                maps.addAll(Arrays.asList(foundMaps));
                profileState.getGraphStarter().setChoices(maps);
                

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

        public ProfileState getProfileState() {
            return profileState;
        }

        public void setProfileState(ProfileState profileState) {
            this.profileState = profileState;
        }

        public GameState getGameState() {
            return gameState;
        }

        public void setGameState(GameState gameState) {
            this.gameState = gameState;
        }
        
    }

    public static void main(String[] args) throws Exception {
        List<String> beanPaths = new ArrayList<String>();
        beanPaths.add("core.xml");
        ApplicationContext ctx = GameContextBuilder.createRoot(beanPaths, new File("conf"));
        ctx.getBean("launcher", LauncherTest.class).go();

    }
}
