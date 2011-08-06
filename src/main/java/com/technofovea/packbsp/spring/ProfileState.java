/*
 * 
 */
package com.technofovea.packbsp.spring;

import org.springframework.context.ApplicationContext;

/**
 *
 * @author Darien Hager
 */
public interface ProfileState extends BasicState {
    

    public ApplicationContext getGameContext();

    public void setGameContext(ApplicationContext gameContext);
    
    public GraphStarterChoice getGraphStarter();

    public void setGraphStarter(GraphStarterChoice stater);
    
}
