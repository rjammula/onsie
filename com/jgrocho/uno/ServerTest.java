package com.jgrocho.uno;

public class ServerTest {

    public static void main(String[] args) throws InterruptedException {
	int players = 4;
	if (args.length == 1)
	    players = Integer.parseInt(args[0]);

	Server server = new Server(players);

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