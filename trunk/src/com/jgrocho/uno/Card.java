/*
 * Copyright 2009 Jonathan Grochowski
 * 
 * This file is part of onsie.
 * 
 * onsie is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * onsie is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with onsie.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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