/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.packbsp.conf.Profile;
import com.technofovea.packbsp.devkits.DetectedGame;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 *
 * @author Darien Hager
 */
public class GamePhaseUpdater extends AbstractPackbspComponent implements ApplicationContextAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(GamePhaseUpdater.class);
    protected ApplicationContext parentContext = null;
    
    protected SteamState sourceState;
    protected GameState gameState;
    protected ProfileState profileState;
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        parentContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(sourceState);
        Assert.notNull(gameState);
        Assert.notNull(profileState);
        Assert.notNull(parentContext);
    }

    public void updatePhase(DetectedGame chosenGame, Profile chosenProfile) throws PhaseFailedException {
        try {
            if (chosenProfile == null) {
                throw new PhaseFailedException("No profile selected").addLocalization("error.input.no_profile");
            }
            if (chosenGame == null) {
                throw new PhaseFailedException("No SDK game-choice selected").addLocalization("error.input.no_game");
            }
            if (!chosenGame.getParentKit().getId().equalsIgnoreCase(chosenProfile.getDevkit())) {
                throw new PhaseFailedException("Selected profile is not valid for this game").addLocalization("error.input.mismatched_profile");
            }
            
            
            /*
             * GameState must be updated before loading child context or beans
             * in the child context won't have important environment information.
             */            
            gameState.setActiveGame(chosenGame);
            gameState.setActiveProfile(chosenProfile);
            

            ApplicationContext child = initChildContext(chosenProfile);

            // Pull out expected beans
            profileState.setGameContext(child);
            profileState.setExplorer(null); //TODO
            //Modify target state
        }
        catch (PhaseFailedException ex) {
            throw localizer.localize(ex);
        }
    }

    protected ApplicationContext initChildContext(Profile chosenProfile) throws PhaseFailedException {
        logger.info("Creating profile-context for {}", chosenProfile.getName());
        List<String> beanPaths = chosenProfile.getBeanFiles();
        List<String> propPaths = chosenProfile.getPropertyFiles();
        beanPaths.add(0, "profiles/base.xml");
        propPaths.add(0, "profiles/base.properties");
        try {
            return GameContextBuilder.createGameContext(beanPaths, propPaths, parentContext);
        }
        catch (BeanDefinitionStoreException e) {
            logger.error("Error instantiating profile", e);
            throw new PhaseFailedException("Error instantiating profile", e).addLocalization("error.profile_loading",chosenProfile.getName());
        }
    }
    public SteamState getSourceState() {
        return sourceState;
    }

    public void setSourceState(SteamState sourceState) {
        this.sourceState = sourceState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public ProfileState getProfileState() {
        return profileState;
    }

    public void setProfileState(ProfileState profileState) {
        this.profileState = profileState;
    }


    
}
