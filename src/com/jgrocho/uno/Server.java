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

import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.regex.*;

public class Server implements ServerThreadListener {

    private enum Awaiting { NONE, CARD, WILD };

    private ServerSocket listener;
    private ArrayList<ServerThread> clients;
    private int players;
    private Game game;
    private boolean clientsReady;
    private boolean playerDone;
    private int winner;
    private Awaiting awaiting;

    public Server() {
	this(Protocol.PORT, 4);
    }

    public Server(int players) {
	this(Protocol.PORT, players);
    }

    public Server(int port, int players) {
	this.players = players;

	clients = new ArrayList<ServerThread>(players);
	clientsReady = false;

	try {
	    listener = new ServerSocket(port);
	} catch (IOException e) {
	    System.err.println("Cannot create server on port " + port);
	    e.printStackTrace();
	    System.exit(-1);
	}

	game = new Game(players);
    }

    public void listen() {
	while(clients.size() < players) {
	    try {
		Socket connection = listener.accept();
		final ServerThread client =
		    new ServerThread(connection, clients.size());
		client.addListener(this);
		client.start();
		new Thread() {
		    public void run() {
			client.setup(game);
		    }
		}.start();
		clients.add(client);
	    } catch (IOException e) {
		System.err.println("Error accepting connection");
		e.printStackTrace();
	    }

	    checkConnections();
	}
    }

    public void start() {
	try {
	    listener.close();
	} catch (IOException e) {
	    System.err.println("Error occured closing listener");
	    e.printStackTrace();
	}

	game.start();

	for (ServerThread client : clients)
	    client.send(Protocol.Start);
    }

    public void gameOver() {
	game.stop();

	for (ServerThread client : clients) {
	    client.send(Protocol.End);

	    if (client.getPosition() == getWinner())
		client.send(Protocol.Winner);
	    else
		client.send(Protocol.Loser);

	    client.running = false;
	}
    }

    public void next() {
	game.next();
    }

    public boolean isPlaying() {
	return game.isPlaying();
    }

    public boolean areClientsReady() {
	return clientsReady;
    }

    public boolean isPlayerDone() {
	return playerDone;
    }

    public int getWinner() {
	return winner;
    }

    public boolean checkWinner() {
	if (game.getCurrentHand().empty()) {
	    winner = game.getCurrentPlayerNumber();
	    return true;
	} else {
	    return false;
	}
    }

    public void updateClients() {
	for (ServerThread client : clients) {
	    client.send(Protocol.Playing);

	    if (client.getPosition() == game.getCurrentPlayerNumber())
		updateCurrentPlayer();
	    else {
		client.send(Protocol.OtherTurn);
		/*
		client.send(Protocol.Hand);
		client.sendObject(game.getHand(client.getPosition()).copy());
		*/
		for (ServerThread otherClient : clients) {
		    if (otherClient.getPosition() != client.getPosition()) {
			client.send(Protocol.Player);
			client.sendObject(otherClient.getPosition());

			client.send(Protocol.CardCount);
			client.sendObject(game.getHand(otherClient.getPosition()).size());
		    }
		}
		client.send(Protocol.NoPlayer);

		client.send(Protocol.Discard);
		client.sendObject(game.topDiscard());
	    }
	}
    }

    public void updateCurrentPlayer() {
	ServerThread client = clients.get(game.getCurrentPlayerNumber());

	client.send(Protocol.Turn);

	if (game.topDiscard().getNumber() == Card.Number.DRAW_TWO) {
	    Card card = game.drawCard();
	    client.send(Protocol.Draw);
	    client.sendObject(card);

	    card = game.drawCard();
	    client.send(Protocol.Draw);
	    client.sendObject(card);
	} else if (game.topDiscard().getNumber() == Card.Number.FOUR_WILD) {
	    Card card = game.drawCard();
	    client.send(Protocol.Draw);
	    client.sendObject(card);

	    card = game.drawCard();
	    client.send(Protocol.Draw);
	    client.sendObject(card);

	    card = game.drawCard();
	    client.send(Protocol.Draw);
	    client.sendObject(card);

	    card = game.drawCard();
	    client.send(Protocol.Draw);
	    client.sendObject(card);
	}

	while (! game.canPlay()) {
	    Card card = game.drawCard();
	    client.send(Protocol.Draw);
	    client.sendObject(card);
	}

	client.send(Protocol.NoDraw);

	client.send(Protocol.Hand);
	client.sendObject(game.getCurrentHand().copy());

	Card discard = game.topDiscard();
	client.send(Protocol.Discard);
	client.sendObject(discard);
	if (discard.getColor() == Card.Color.NONE)
	    client.sendObject(game.getWildColor());

	requestCard();
    }

    private void requestCard() {
	playerDone = false;

	final ServerThread client = clients.get(game.getCurrentPlayerNumber());

	client.send(Protocol.RequestCard);
	
	new Thread() {
	    public void run() {
		client.receive(Protocol.PlayCard);
		client.receiveObject();
	    }
	}.start();
    }

    private void cardAccepted() {
	ServerThread client = clients.get(game.getCurrentPlayerNumber());

	client.send(Protocol.Success);

	if (game.topDiscard().getColor() == Card.Color.NONE)
	    requestWild();    
	else
	    playerDone = true;
    }

    private void requestWild() {
	final ServerThread client = clients.get(game.getCurrentPlayerNumber());

	client.send(Protocol.SetWild);

	new Thread() {
	    public void run() {
		client.receive(Protocol.SetWild);
		client.receiveObject();
	    }
	}.start();
    }

    private void wildAccepted() {
	ServerThread client = clients.get(game.getCurrentPlayerNumber());

	client.send(Protocol.Success);

	playerDone = true;
    }

    public boolean checkConnections() {
	if (clients.size() < players)
	    return false;

	boolean allConnected = true;
	for (Iterator<ServerThread> i = clients.iterator(); i.hasNext();) {
	    ServerThread client = i.next();
	    if (!client.isConnected()) {
		i.remove();
		allConnected = false;
	    }
	}

	return allConnected;
    }

    public void messageReceived(ReceiveEvent event) {
	Message message = event.getMessage();

	if (message.getType() == Message.Type.PROTOCOL) {
	    String protocol = message.getProtocol();
	    if (protocol.equals(Protocol.Ready)) {
		if (checkConnections()) {
		    for (ServerThread client : clients)
			if (! client.isReady())
			    return;

		    clientsReady = true;
		    System.out.println(clientsReady);
		}
	    } else if (protocol.equals(Protocol.PlayCard)) {
		awaiting = Awaiting.CARD;
	    } else if (protocol.equals(Protocol.SetWild)) {
		awaiting = Awaiting.WILD;
	    }
	} else if (message.getType() == Message.Type.OBJECT) {
	    if (awaiting == Awaiting.CARD) {
		int card = ((Integer) message.getObject()).intValue();
		if (! game.playCard(card)) {
		    requestCard();
		} else {
		    cardAccepted();
		    awaiting = Awaiting.NONE;
		}
	    } else if (awaiting == Awaiting.WILD) {
		Card.Color wild = (Card.Color) message.getObject();
		game.setWildColor(wild);
		wildAccepted();
		awaiting = Awaiting.NONE;
	    }
	} else {
	    System.err.println("Unsupported message type recieved: " + 
			       message.getType());
	}
    }
}