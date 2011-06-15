/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.packbsp.spring.SteamPhaseFactory.SteamPhase;

/**
 *
 * @author Darien Hager
 */
public class GamePhaseFactory {
    public static interface GamePhase {
    }

    protected static class GamePhaseImpl implements GamePhase {

    }
    NestedScope scope = null;
    SteamPhase steamPhase; 
    
    
    
    
    
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
    
    
}
