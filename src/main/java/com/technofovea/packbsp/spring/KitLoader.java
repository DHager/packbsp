/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.packbsp.devkits.GameErrorListener;
import com.technofovea.hl2parse.registry.ClientRegistry;
import com.technofovea.packbsp.devkits.DetectedGame;
import com.technofovea.packbsp.devkits.Devkit;
import com.technofovea.packbsp.devkits.GameConfException;
import com.technofovea.packbsp.devkits.KitFactory;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 *
 * @author Darien Hager
 */
public class KitLoader implements InitializingBean {

    private static class NoopListener implements GameErrorListener {

        public void devkitInitError(KitFactory source, GameConfException ex) {
        }

        public void gameInitError(KitFactory source, Devkit cause, GameConfException ex) {
        }

        
    }
    
    private static final Logger logger = LoggerFactory.getLogger(KitLoader.class);
    protected GameErrorListener gameErrorListener = new NoopListener();
    protected List<KitFactory> kitFactories;

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(gameErrorListener);
        Assert.notNull(kitFactories);

    }

    public List<KitFactory> getKitFactories() {
        return kitFactories;
    }

    public void setKitFactories(List<KitFactory> kitFactories) {
        this.kitFactories = kitFactories;
    }

    
    
    public Set<DetectedGame> loadGames() {
        Set<DetectedGame> games = new HashSet<DetectedGame>();

        for(KitFactory f : kitFactories){
            Set<Devkit> kits = f.createKits(gameErrorListener);
            for(Devkit k : kits){
                games.addAll(f.createDetectedGames(k, gameErrorListener));
            }
            
        }
        return games;
    }


    public GameErrorListener getGameErrorListener() {
        return gameErrorListener;
    }

    public void setGameErrorListener(GameErrorListener gameErrorListener) {
        this.gameErrorListener = gameErrorListener;
    }
}
