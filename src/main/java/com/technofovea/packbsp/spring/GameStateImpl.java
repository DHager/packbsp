/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.packbsp.conf.Profile;
import com.technofovea.packbsp.crawling2.GraphExplorer;
import com.technofovea.packbsp.devkits.DetectedGame;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Darien Hager
 */
public class GameStateImpl extends AbstractState implements GameState {

    protected Profile activeProfile;
    protected DetectedGame activeGame;
    protected ApplicationContext gameContext;
    protected GraphExplorer explorer;

    public boolean isComplete() {
        return ( activeProfile != null )
                && ( activeGame != null )
                && ( gameContext != null )
                && ( gameContext != null )
                && ( explorer != null );
    }

    @Override
    public DetectedGame getActiveGame() {
        return activeGame;
    }

    @Override
    public void setActiveGame(DetectedGame activeGame) {
        this.activeGame = activeGame;
    }

    @Override
    public Profile getActiveProfile() {
        return activeProfile;
    }

    @Override
    public void setActiveProfile(Profile activeProfile) {
        this.activeProfile = activeProfile;
    }

    @Override
    public GraphExplorer getExplorer() {
        return explorer;
    }

    @Override
    public void setExplorer(GraphExplorer explorer) {
        this.explorer = explorer;
    }

    @Override
    public ApplicationContext getGameContext() {
        return gameContext;
    }

    @Override
    public void setGameContext(ApplicationContext gameContext) {
        this.gameContext = gameContext;
    }
    
    
}
