/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.packbsp.devkits.Game;
import com.technofovea.packbsp.spring.SteamPhaseFactory.SteamPhase;
import java.io.File;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class GamePhaseFactory {

    public static interface GamePhase {
    }

    protected static class GamePhaseImpl implements GamePhase {
    }
    
    private static final Logger logger = LoggerFactory.getLogger(GamePhaseFactory.class);
    protected NestedScope scope = null;
    protected SteamPhase steamPhase;
    protected Game chosenGame;
    
    public NestedScope getScope() {
        return scope;
    }

    public void setScope(NestedScope scope) {
        this.scope = scope;
    }

    public SteamPhase getSteamPhase() {
        return steamPhase;
    }

    public void setSteamPhase(SteamPhase steamPhase) {
        this.steamPhase = steamPhase;
    }

    public Game getChosenGame() {
        return chosenGame;
    }

    public void setChosenGame(Game chosenGame) {
        if (steamPhase == null || (!steamPhase.containsGame(chosenGame))) {
            throw new IllegalArgumentException("Chosen game is not part of acceptable list");
        }
        this.chosenGame = chosenGame;
    }


    public GamePhase proceed() throws PhaseFailedException {
        //Check inputs
        //Configuration of map includes, materials
        //Create temporary copies of map files
        //Create graphs, initially populate with map nodes
        
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
