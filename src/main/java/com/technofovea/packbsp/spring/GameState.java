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
public interface GameState extends BasicState {
    
    public DetectedGame getActiveGame();

    public Profile getActiveProfile();

    public GraphExplorer getExplorer();

    public ApplicationContext getGameContext();

    void setActiveGame(DetectedGame activeGame);

    void setActiveProfile(Profile activeProfile);

    void setExplorer(GraphExplorer explorer);

    void setGameContext(ApplicationContext gameContext);
    
}
