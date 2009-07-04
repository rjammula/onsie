package com.jgrocho.uno;

public class Test {

    public static void main(String[] args) {
	Game game = new Game();
	game.start();

	Hand hand = game.getHand(0);
	System.out.println(hand);

	Card card = game.getDeck().deal();
	hand.add(card);
	System.out.println(hand);

	Hand handCopy = game.getHand(0);
	System.out.println(handCopy);

    }

}