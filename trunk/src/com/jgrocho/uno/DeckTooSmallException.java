package com.jgrocho.uno;

public class DeckTooSmallException extends RuntimeException {
    
    public DeckTooSmallException(int req, int size) {
	super("Deck does not have enough cards to honor request" +
	      System.getProperty("line.seperator") +
	      req + " cards requested, only " + size + " cards available");
    }

}