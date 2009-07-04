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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;

public class Deck {

    private ArrayList<Card> deck;

    public Deck() {
	deck = new ArrayList<Card>(108);

	for (Card.Color color :
		 EnumSet.range(Card.Color.RED, Card.Color.YELLOW)) {
	    deck.add(new Card(color, Card.Number.ZERO));
	    for (Card.Number number :
		     EnumSet.range(Card.Number.ONE, Card.Number.DRAW_TWO)) {
		deck.add(new Card(color, number));
		deck.add(new Card(color, number));
	    }
	}

	for (int i = 0; i < 4; ++i) {
	    deck.add(new Card(Card.Color.NONE, Card.Number.WILD));
	    deck.add(new Card(Card.Color.NONE, Card.Number.FOUR_WILD));
	}
    }

    public void shuffle() {
	Collections.shuffle(deck);
    }

    public int size() {
	return deck.size();
    }

    public Card deal() {
	return deal(1)[0];
    }

    public Card[] deal(int num) {
	if (num > deck.size())
	    throw new DeckTooSmallException(num, deck.size());

	Card[] deal = new Card[num];

	Iterator<Card> iter = deck.iterator();
	int count = 0;
	while (count < num) {
	    deal[count++] = iter.next();
	    iter.remove();
	}

	return deal;
    }

    public void reload(Card card) {
	deck.add(card);
    }

    public void reload(Card[] cards) {
	for (Card card : cards)
	    deck.add(card);
    }

    public String toString() {
	return deck.toString();
    }

    public static void main(String[] args) {
	Deck deck = new Deck();

	System.out.println(deck);
    }

}