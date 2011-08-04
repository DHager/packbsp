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
    protected GraphExplorer explorer;

    public boolean isComplete() {
        return ((gameContext != null )
                && ( gameContext != null )
                && ( explorer != null ));
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
