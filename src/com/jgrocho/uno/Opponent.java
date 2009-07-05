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

public class Opponent implements Comparable<Opponent> {

    private final String name;
    private final int position;
    private int cards;

    public Opponent(String name, int position, int cards) {
	this.name = name;
	this.position = position;
	this.cards = cards;
    }

    public String getName() {
	return name;
    }

    public int getPosition() {
	return position;
    }

    public void setCards(int cards) {
	this.cards = cards;
    }

    public int getCards() {
	return cards;
    }

    public int compareTo(Opponent other) {
	return this.getPosition() - other.getPosition();
    }

}