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

import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Iterator;

import java.io.Serializable;

public class Hand implements Iterable<Card>, Serializable {

    private List<Card> hand;

    public Hand() {
	hand = new LinkedList<Card>();
    }

    public Hand copy() {
	Hand newHand = new Hand();
	for (Card card : hand)
	    newHand.add(new Card(card.getColor(), card.getNumber()));

	return newHand;
    }

    public void add(Card card) {
	hand.add(card);
    }

    public void addAll(Card[] cards) {
	hand.addAll(Arrays.asList(cards));
    }

    public void addAll(Collection<Card> cards) {
	hand.addAll(cards);
    }

    public int size() {
	return hand.size();
    }

    public boolean empty() {
	return hand.size() == 0;
    }

    public Card get(int index) {
	return hand.get(index);
    }

    public Card[] getAll() {
	return hand.toArray(new Card[0]);
    }

    public Card remove(int index) {
	return hand.remove(index);
    }

    public boolean remove(Card card) {
	return hand.remove(card);
    }

    public Iterator<Card> iterator() {
	return hand.iterator();
    }

    public String toString() {
	return hand.toString();
    }

}