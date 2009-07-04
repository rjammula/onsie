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

import java.util.LinkedList;

public class Discard {

    private LinkedList<Card> discard;

    public Discard() {
	discard = new LinkedList<Card>();
    }

    public Card getTop() {
	return discard.peek();
    }

    public void add(Card card) {
	discard.push(card);
    }

    public Card[] empty() {
	Card[] cards = new Card[discard.size()];
	discard.toArray(cards);
	return cards;
    }

}