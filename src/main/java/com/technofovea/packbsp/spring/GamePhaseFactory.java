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
public class GamePhaseFactory implements ApplicationContextAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(GamePhaseFactory.class);
    protected NestedScope scope = null;
    protected ExceptionLocalizer localizer = NoopExceptionLocalizer.getInstance();
   
    protected ApplicationContext parentContext = null;
    protected SteamPhase steamPhase;
    protected DetectedGame chosenGame;
    protected Profile chosenProfile;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        parentContext = applicationContext;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(localizer);
        Assert.notNull(parentContext);
    }
    
    

    protected void markDirty() {
        if (scope != null) {
            scope.invalidateScope();
        }
    }

    public NestedScope getScope() {
        return scope;
    }

    public void setScope(NestedScope scope) {
        this.scope = scope;
    }

    public ExceptionLocalizer getLocalizer() {
        return localizer;
    }

    public void setLocalizer(ExceptionLocalizer localizer) {
        this.localizer = localizer;
    }

    
    public SteamPhase getSteamPhase() {
        return steamPhase;
    }

    public void setSteamPhase(SteamPhase steamPhase) {
        this.markDirty();
        this.steamPhase = steamPhase;
    }

    public DetectedGame getChosenGame() {
        return chosenGame;
    }

    public void setChosenGame(DetectedGame chosenGame) {
        this.markDirty();
        this.chosenGame = chosenGame;
    }

    public Profile getChosenProfile() {
        return chosenProfile;
    }

    public void setChosenProfile(Profile chosenProfile) {
        this.markDirty();
        this.chosenProfile = chosenProfile;
    }

    public GamePhase createPhase() throws PhaseFailedException {
        if (chosenProfile == null) {
            throw localizer.localize(new PhaseFailedException("No profile selected"), "error.input.no_profile");
        }
        if (chosenGame == null) {
            throw localizer.localize(new PhaseFailedException("No SDK game-choice selected"), "error.input.no_game");
        }
        if (!chosenGame.getParentKit().getId().equalsIgnoreCase(chosenProfile.getDevkit())) {
            throw localizer.localize(new PhaseFailedException("Selected profile is not valid for this game"), "error.input.mismatched_profile");
        }

        ApplicationContext child = initChildContext();

        GamePhaseImpl ret = new GamePhaseImpl();

        return ret;
    }

    protected ApplicationContext initChildContext() throws PhaseFailedException {
        logger.info("Creating profile-context for {}", chosenProfile.getName());
        List<String> beanPaths = chosenProfile.getBeanFiles();
        List<String> propPaths = chosenProfile.getPropertyFiles();
        beanPaths.add(0, "profiles/base.xml");
        try {
            return PackbspApplicationContext.create(beanPaths, propPaths, parentContext);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error instantiating profile", e);
            throw localizer.localize(new PhaseFailedException("Error instantiating profile", e), "error.profile_loading");
        }
        catch (ClassCastException e) {
            throw localizer.localize(new PhaseFailedException("Error instantiating profile", e), "error.profile_loading");
        }
        catch (NullPointerException e) {
            throw localizer.localize(new PhaseFailedException("Error instantiating profile", e), "error.profile_loading");
        }
    }
}
