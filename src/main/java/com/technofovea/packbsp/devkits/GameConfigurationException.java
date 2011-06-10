package com.technofovea.packbsp.devkits;

/**
 * Thrown to indicate that some Development kit, game-engine, or game
 * information was in an error state or not found.
 * @author Darien Hager
 */
public class GameConfigurationException extends Exception{

    public GameConfigurationException(Throwable cause) {
        super(cause);
    }

    public GameConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameConfigurationException(String message) {
        super(message);
    }

    public GameConfigurationException() {
    }


}
