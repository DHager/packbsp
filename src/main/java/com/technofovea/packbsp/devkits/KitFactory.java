/*
 * 
 */
package com.technofovea.packbsp.devkits;

import java.util.Set;

/**
 *
 * @author Darien Hager
 */
public interface KitFactory<T extends Devkit, G extends DetectedGame> {
    public Set<T> createKits(GameErrorListener listener);
    public Set<G> createDetectedGames(T kit, GameErrorListener listener);
}
