package com.jgrocho.uno;

import java.io.Serializable;

public class Card implements Serializable {

    public enum Color { RED, BLUE, GREEN, YELLOW, NONE };

    public enum Number { ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT,
	    NINE, REVERSE, SKIP, DRAW_TWO, WILD, FOUR_WILD };

    /*
    public enum Wilds { WILD, FOUR_WILD };

    public enum Extras { REVERSE, SKIP, DRAW_TWO };
    */

    private final Color color;
    private final Number number;

    public Card(Color color, Number number) {
	this.color = color;
	this.number = number;
    }

    public Color getColor() {
	return color;
    }
    
    public Number getNumber() {
	return number;
    }

    public String toString() {
	return color + " " + number;
    }

}