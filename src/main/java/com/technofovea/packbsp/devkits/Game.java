package com.technofovea.packbsp.devkits;

/**
 *
 * @author Darien Hager
 */
public interface Game {
    public boolean isPresent();
    public String getTitle();
    public GameEngine getParent();
}
