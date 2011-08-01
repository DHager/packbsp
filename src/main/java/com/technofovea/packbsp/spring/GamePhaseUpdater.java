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
    protected GameState targetState;
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        parentContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(sourceState);
        Assert.notNull(targetState);
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

//            ApplicationContext child = initChildContext(chosenProfile);

            //Modify target state
            throw new PhaseFailedException("Not yet implemented");
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
        try {
            return PackbspApplicationContext.create(beanPaths, propPaths, parentContext);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error instantiating profile", e);
            throw new PhaseFailedException("Error instantiating profile", e).addLocalization("error.profile_loading");
        }
        catch (ClassCastException e) {
            throw new PhaseFailedException("Error instantiating profile", e).addLocalization("error.profile_loading");
        }
        catch (NullPointerException e) {
            throw new PhaseFailedException("Error instantiating profile", e).addLocalization("error.profile_loading");
        }
    }

    public SteamState getSourceState() {
        return sourceState;
    }

    public void setSourceState(SteamState sourceState) {
        this.sourceState = sourceState;
    }

    public GameState getTargetState() {
        return targetState;
    }

    public void setTargetState(GameState targetState) {
        this.targetState = targetState;
    }
    
    
}
