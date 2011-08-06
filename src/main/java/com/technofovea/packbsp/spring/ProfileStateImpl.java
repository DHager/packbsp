/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.packbsp.crawling2.GraphExplorer;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Darien Hager
 */
public class ProfileStateImpl extends AbstractState implements ProfileState {

    protected ApplicationContext gameContext;
    protected GraphStarterChoice graphStarter;

    public boolean isComplete() {
        return ( ( gameContext != null )
                && ( gameContext != null )
                && ( graphStarter != null ) );
    }

    public ApplicationContext getGameContext() {
        return gameContext;
    }

    public void setGameContext(ApplicationContext gameContext) {
        this.gameContext = gameContext;
    }

    public GraphStarterChoice getGraphStarter() {
        return graphStarter;
    }

    public void setGraphStarter(GraphStarterChoice graphStarter) {
        this.graphStarter = graphStarter;
    }
}
