/*
 * 
 */
package com.technofovea.packbsp.devkits;

/**
 *
 * @author Darien Hager
 */
public interface GameErrorListener {
    public void devkitInitError(KitFactory source, GameConfException ex);
    public void gameInitError(KitFactory source, Devkit cause, GameConfException ex);
}
