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
public interface ProfileState extends BasicState {
    

    public GraphExplorer getExplorer();

    public ApplicationContext getGameContext();

    void setExplorer(GraphExplorer explorer);

    void setGameContext(ApplicationContext gameContext);
    
}
