/*
 * 
 */
package com.technofovea.packbsp.spring;

import com.technofovea.packbsp.devkits.GameConfException;

/**
 *
 * @author Darien Hager
 */
public interface GameErrorListener {
    public void devkitInitError(Object source, GameConfException ex);
    public void gameInitError(Object source, GameConfException ex);
}
