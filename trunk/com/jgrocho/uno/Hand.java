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