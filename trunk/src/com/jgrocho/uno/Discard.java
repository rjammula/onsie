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