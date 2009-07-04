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