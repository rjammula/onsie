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

public class ServerTest {

    public static void main(String[] args) throws InterruptedException {
	int players = 4;
	if (args.length >= 1)
	    players = Integer.parseInt(args[0]);

	int port = Protocol.PORT;
	if (args.length >= 2)
	    port = Integer.parseInt(args[1]);

	Server server = new Server(port, players);

	System.out.println("Server waiting for connections");
	server.listen();
	while (! server.checkConnections())
	    server.listen();
	System.out.println("Server connected to all clients");

	while (! server.areClientsReady()) {
	    System.out.println("waiting");
	    Thread.sleep(100);
	}

	System.out.println("Starting game");
	server.start();

	while (server.isPlaying()) {
	    server.updateClients();
	    System.out.println("Clients updated");

	    while (! server.isPlayerDone())
		Thread.sleep(100);

	    if (server.checkWinner())
		server.gameOver();
	    else
		server.next();
	}

    }

}