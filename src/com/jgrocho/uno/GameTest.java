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

import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.InputMismatchException;

public class GameTest {

    public static void main(String[] args) {
	Scanner sc = new Scanner(System.in);

	Game game = new Game();
	game.start();

	Hand hand;
	boolean cardPlayed;

	while (game.isPlaying()) {
	    cardPlayed = false;

	    hand = game.getCurrentHand();

	    System.out.print("Player " + game.getCurrentPlayerNumber());
	    if (game.getDirection() > 0)
		System.out.println(" ->");
	    else
		System.out.println(" <-");

	    System.out.print("Opponents:");
	    for (int i = 0; i < game.getPlayers(); ++i) {
		if (i == game.getCurrentPlayerNumber())
		    continue;

		System.out.print(" Player " + i + 
				 ": " + game.getHand(i).size());
	    }
	    System.out.println();

	    System.out.print("DISCARD: " + game.topDiscard());
	    if (game.topDiscard().getColor() == Card.Color.NONE)
		System.out.println(" (play " + game.getWildColor() + ")");
	    else
		System.out.println();

	    if (game.topDiscard().getNumber() == Card.Number.DRAW_TWO) {
		System.out.print("Drawing two cards");
		Card card = game.drawCard();
		System.out.print("..." + card);
		card = game.drawCard();
		System.out.print("..." + card);
		System.out.println();
	    } else if (game.topDiscard().getNumber() == Card.Number.FOUR_WILD) {
		System.out.print("Drawing four cards");
		Card card = game.drawCard();
		System.out.print("..." + card);
		card = game.drawCard();
		System.out.print("..." + card);
		card = game.drawCard();
		System.out.print("..." + card);
		card = game.drawCard();
		System.out.print("..." + card);
		System.out.println();
	    }

	    while (!game.canPlay()) {
		System.out.print("You can't play a card. Drawing...");
		Card card = game.drawCard();
		System.out.println(card);
	    }

	    for (int i = 0; i < hand.size(); ++i) {
		System.out.println(i + ": " + hand.get(i));
	    }

	    while (!cardPlayed) {
		int command;
		try {
		    command = sc.nextInt();
		} catch (InputMismatchException e) {
		    sc.next();
		    System.out.println("Try again");
		    command = -2;
		}
		if (command == -1) {
		    game.stop();
		    break;
		} else if (command > -1 && command < hand.size()) {
		    cardPlayed = game.playCard(command);
		}
	    }

	    if (game.topDiscard().getColor() == Card.Color.NONE) {
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

		switch (color) {
		case 'b':
		case 'B':
		    game.setWildColor(Card.Color.BLUE);
		    break;
		case 'g':
		case 'G':
		    game.setWildColor(Card.Color.GREEN);
		    break;
		case 'r':
		case 'R':
		    game.setWildColor(Card.Color.RED);
		    break;
		case 'y':
		case 'Y':
		    game.setWildColor(Card.Color.YELLOW);
		    break;
		}
	    }

	    if (hand.empty()) {
		game.stop();
		System.out.println("Winner!");
		System.out.println("Player " + game.getCurrentPlayerNumber() + 
				   " won");
		break;
	    }

	    game.next();
	}
    }

}