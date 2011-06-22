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
    public void errorOccurred(Object source, GameConfException ex);
}
