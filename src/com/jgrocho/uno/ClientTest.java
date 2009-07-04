package com.jgrocho.uno;

import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.InputMismatchException;

public class ClientTest {

    public static void main(String[] args) {
	String host = "localhost";
	if (args.length == 1)
	    host = args[0];

	Scanner sc = new Scanner(System.in);

	Client client = new Client(host);
	client.connect();

	client.send(Protocol.Ready);

	client.receive(Protocol.Hand);
	Hand hand = (Hand) client.receiveObject();

	client.receive(Protocol.Turn);
	int turn = ((Integer) client.receiveObject()).intValue();

	Card discard;
	Card.Color wildColor;

	System.out.println("Player " + turn);
	System.out.println("Starting Hand:");
	System.out.println(hand);

	client.receive(Protocol.Start);
	System.out.println("Game starting");

	while (client.receivePlaying()) {
	    if (client.receiveTurn()) {
		Card cardPlayed = null;
		
		System.out.println("Player " + turn);
		System.out.println("Your Turn");
		
		while (client.receiveDraw()) {
		    System.out.print("Drawing card...");
		    System.out.flush();
		    Card card = (Card) client.receiveObject();
		    System.out.println(card);
		}
		
		client.receive(Protocol.Hand);
		hand = (Hand) client.receiveObject();

		client.receive(Protocol.Discard);
		discard = (Card) client.receiveObject();
		System.out.print("DISCARD: " + discard);
		if (discard.getColor() == Card.Color.NONE) {
		    wildColor = (Card.Color) client.receiveObject();
		    System.out.println(" (play " + wildColor + ")");
		} else {
		    wildColor = Card.Color.NONE;
		    System.out.println();
		}
		
		for (int i = 0; i < hand.size(); ++i)
		    System.out.println(i + ": " + hand.get(i));

		client.receive(Protocol.RequestCard);

		while (cardPlayed == null) {
		    int command;
		    try {
			command = sc.nextInt();
		    } catch (InputMismatchException e) {
			sc.next();
			System.out.println("Try again");
			command = -1;
		    }
		    
		    if (command > -1 && command < hand.size()) {
			cardPlayed = hand.get(command);
			if (canPlay(cardPlayed, discard, wildColor)) {
			    client.send(Protocol.PlayCard);
			    client.sendObject((Integer) command);
			    
			    if (! client.receiveCardPlayed())
				cardPlayed = null;
			} else
			    cardPlayed = null;
		    }
		}

		if (cardPlayed.getColor() == Card.Color.NONE) {
		    client.receive(Protocol.SetWild);

		    boolean picked = false;
		    char color = 'n';

		    System.out.print("What color for your wild [BGRY]: ");
		    System.out.flush();
		    while (!picked) {
			try {
			    color = sc.next("[bgryBGRY]").charAt(0);
			    picked = true;
			} catch (NoSuchElementException e) {
			    sc.next();
			    System.out.print("Try again [BGRY]: ");
			    System.out.flush();
			}
		    }

		    client.send(Protocol.SetWild);

		    switch (color) {
		    case 'b':
		    case 'B':
			client.sendObject(Card.Color.BLUE);
			break;
		    case 'g':
		    case 'G':
			client.sendObject(Card.Color.GREEN);
			break;
		    case 'r':
		    case 'R':
			client.sendObject(Card.Color.RED);
			break;
		    case 'y':
		    case 'Y':
			client.sendObject(Card.Color.YELLOW);
			break;
		    }

		    client.receive(Protocol.Success);
		}
		
	    } else {
		System.out.println("Not your Turn");

		/*
		client.receive(Protocol.Hand);
		hand = (Hand) client.receiveObject();
		*/
		while (client.receivePlayer()) {
		    int otherPlayer =
			((Integer) client.receiveObject()).intValue();
		    client.receive(Protocol.CardCount);
		    int cardCount = 
			((Integer) client.receiveObject()).intValue();
		    System.out.print("Player " + otherPlayer);
		    System.out.println(" has " + cardCount + " cards");
		}

		client.receive(Protocol.Discard);
		discard = (Card) client.receiveObject();
		System.out.println("DISCARD: " + discard);
	    }
	}

	System.out.println("Game over");

	if (client.receiveWinner())
	    System.out.println("Winner");
	else
	    System.out.println("Loser");
    }
    
    private static  boolean canPlay(Card card, Card discard,
				    Card.Color wildColor) {
	if (card != null &&
	    ((discard.getColor() == Card.Color.NONE && 
	      card.getColor() == wildColor) ||
	     card.getColor() == Card.Color.NONE ||
	     card.getColor() == discard.getColor() ||
	     card.getNumber() == discard.getNumber())) {

	    return true;
	}

	return false;
    }
    
}