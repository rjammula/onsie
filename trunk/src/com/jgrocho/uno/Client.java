package com.jgrocho.uno;

import java.io.*;
import java.net.*;

public class Client {

    private String host;
    private int port;
    private boolean connected;

    private Socket socket;

    /*
    private PrintWriter socketOut;
    private BufferedReader socketIn;
    */
    private ObjectOutputStream socketObjOut;
    private ObjectInputStream socketObjIn;

    public Client() {
	this("localhost");
    }

    public Client(String host) {
	this(host, Protocol.PORT);
    }

    public Client(String host, int port) {
	this.host = host;
	this.port = port;

	/*
	socketOut = null;
	socketIn = null;
	*/
	socketObjOut = null;
	socketObjIn = null;
    }

    public void connect() {
	try {
	    socket = new Socket(host, port);

	    /*
	    socketOut = new PrintWriter(socket.getOutputStream());
	    socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    */
	    socketObjOut = new ObjectOutputStream(socket.getOutputStream());
	    socketObjIn = new ObjectInputStream(socket.getInputStream());

	    connected = true;
	    System.out.println("Connection opened to " +
			       socket.getInetAddress());

	} catch (IOException eStream) {
	    System.err.println("Error creating IO streams.");
	    eStream.printStackTrace();
	    try {
		socket.close();
	    } catch (IOException eSocket) {
		System.out.println("Error closing socket.");
		eSocket.printStackTrace();
	    } finally {
		connected = false;
	    }
	}
    }

    public void send(String protocol) {
	try {
	    socketObjOut.writeObject(protocol);
	    socketObjOut.flush();
	} catch (IOException e) {
	    System.out.println("Error sending protocol: " + protocol);
	    e.printStackTrace();
	    connected = false;
	}
	/*
	socketOut.println(protocol);
	socketOut.flush();
	if (socketOut.checkError()) {
	    System.out.println("Error sending protocol: " + protocol);
	    connected = false;
	}
	*/
    }

    public void sendObject(Object object) {
	try {
	    socketObjOut.writeObject(object);
	    socketObjOut.flush();
	} catch (IOException e) {
	    System.out.println("Error sending object: " + object);
	    e.printStackTrace();
	    connected = false;
	}
    }

    public String receive(String protocol) {
	String line = "";

	while (! line.equals(protocol))
	    line = readSocketInput();

	return line;
    }

    public Object receiveObject() {
	Object object = readSocketObjInput();

	return object;
    }

    private String readSocketInput() {
	String line = null;

	try {
	    //line = socketIn.readLine();
	    line = (String) socketObjIn.readObject();
        } catch (SocketException e) {
	    System.err.println("Socket Error.");
	    e.printStackTrace();
	    connected = false;
	} catch (IOException e) {
	    System.out.println("Error reading input.");
	    e.printStackTrace();
	    connected = false;
	} catch (ClassNotFoundException e) {
	    System.err.println("Cannot find class for STRING");
	    System.err.println("Are you running Java?");
	    e.printStackTrace();
	    connected = false;
	}

	if (line == null)
	    connected = false;

	return line;
    }

    private Object readSocketObjInput() {
	Object object = null;

	try {
	    object = socketObjIn.readObject();
	} catch (SocketException e) {
	    System.err.println("Error reading object from socket");
	    e.printStackTrace();
	    connected = false;
	} catch (IOException e) {
	    System.err.println("Error reading object");
	    e.printStackTrace();
	    connected = false;
	} catch (ClassNotFoundException e) {
	    System.err.println("Cannot find class for object");
	    e.printStackTrace();
	    connected = false;
	}

	return object;
    }

    public boolean receiveOptions(String trueProtocol, String falseProtocol) {
	while (true) {
	    String protocol = readSocketInput();
	    if (protocol.equals(trueProtocol))
		return true;
	    else if (protocol.equals(falseProtocol))
		return false;
	}
    }

    public boolean receivePlaying() {
	return receiveOptions(Protocol.Playing, Protocol.End);
	/*
	boolean playing = null;

	while (playing == null) {
	    String protocol = readSocketInput();
	    if (protocol.equals(Protocol.Playing))
		playing = true;
	    else if (protocol.equals(Protocol.End))
		playing = false;
	}

	return playing;
	*/
    }

    public boolean receiveTurn() {
	return receiveOptions(Protocol.Turn, Protocol.OtherTurn);
	/*
	boolean turn = null;

	while (turn == null) {
	    String protocol = readSocketInput();
	    if (protocol.equals(Protocol.Turn))
		turn = true;
	    else if (protocol.equals(Protocol.OtherTurn))
		turn = false;
	}

	return turn;
	*/
    }

    public boolean receiveDraw() {
	return receiveOptions(Protocol.Draw, Protocol.NoDraw);
    }

    public boolean receiveCardPlayed() {
	return receiveOptions(Protocol.Success, Protocol.RequestCard);
    }

    public boolean receiveWinner() {
	return receiveOptions(Protocol.Winner, Protocol.Loser);
    }

    public boolean receivePlayer() {
	return receiveOptions(Protocol.Player, Protocol.NoPlayer);
    }

}