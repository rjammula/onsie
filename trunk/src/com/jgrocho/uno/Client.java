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

import java.util.List;
import java.util.ArrayList;

public class Client {

    private String host;
    private int port;

    private boolean connected;
    private Socket socket;

    private List<ClientEventListener> listeners;

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

	connected = false;

	socketObjOut = null;
	socketObjIn = null;

	listeners = new ArrayList<ClientEventListener>();
    }

    public boolean connect() {
	try {
	    socket = new Socket(host, port);

	    socketObjOut = new ObjectOutputStream(socket.getOutputStream());
	    socketObjIn = new ObjectInputStream(socket.getInputStream());

	    connected = true;
	    System.out.println("Connection opened to " +
			       socket.getInetAddress());

	} catch (IOException eStream) {
	    //System.err.println("Error creating IO streams.");
	    //eStream.printStackTrace();
	    try {
		socket.close();
	    } catch (IOException eSocket) {
		//System.err.println("Error closing socket.");
		//eSocket.printStackTrace();
	    } finally {
		connected = false;
	    }
	} finally {
	    return connected;
	}
    }

    public void send(String protocol) {
	try {
	    socketObjOut.writeObject(protocol);
	    socketObjOut.flush();
	} catch (IOException e) {
	    System.err.println("Error sending protocol: " + protocol);
	    e.printStackTrace();
	    connected = false;
	}

	System.out.println("SEND:: " + protocol);
    }

    public void sendObject(Object object) {
	try {
	    socketObjOut.writeObject(object);
	    socketObjOut.flush();
	} catch (IOException e) {
	    System.err.println("Error sending object: " + object);
	    e.printStackTrace();
	    connected = false;
	}

	System.out.println("SEND:: " + object);
    }

    private String readSocketInput() {
	String line = null;

	try {
	    line = (String) socketObjIn.readObject();
        } catch (SocketException e) {
	    System.err.println("Socket Error.");
	    e.printStackTrace();
	    connected = false;
	} catch (IOException e) {
	    System.err.println("Error reading input.");
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


	System.out.println("RECEIVE:: " + line);
	return line;
    }

    /*
    private String readSocketInput() {
	String line = (String) readSocketObjInput();
	if (line == null)
	    connected = false;
	return line;
    }
    */

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

	System.out.println("RECEIVE:: " + object);
	return object;
    }

    /*
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
    */

    public void receive(String protocol) {
	String line = "";

	while (! line.equals(protocol))
	    line = readSocketInput();

	fireReceived(line);
    }

    public void receiveObject() {
	Object object = readSocketObjInput();

	fireObjectReceived(object);
    }

    public void receiveOptions(String trueProtocol, String falseProtocol) {
	boolean fired = false;
	while (! fired) {
	    String protocol = readSocketInput();
	    if (protocol.equals(trueProtocol)) {
		fireReceived(trueProtocol);
		fired = true;
	    } else if (protocol.equals(falseProtocol)) {
		fireReceived(falseProtocol);
		fired = true;
	    }
	}
    }

    public void receivePlaying() {
	receiveOptions(Protocol.Playing, Protocol.End);
    }

    public void receiveTurn() {
	receiveOptions(Protocol.Turn, Protocol.OtherTurn);
    }
    
    public void receiveDraw() {
	receiveOptions(Protocol.Draw, Protocol.NoDraw);
    }

    public void receiveCardPlayed() {
	receiveOptions(Protocol.Success, Protocol.RequestCard);
    }

    public void receiveWinner() {
	receiveOptions(Protocol.Winner, Protocol.Loser);
    }

    public void receivePlayer() {
	receiveOptions(Protocol.Player, Protocol.NoPlayer);
    }

    public void receiveUser() {
	receiveOptions(Protocol.User, Protocol.NoUser);
    }

    public void addClientEventListener(ClientEventListener listener) {
	listeners.add(listener);
    }

    public void removeClientEventListener(ClientEventListener listener) {
	listeners.remove(listener);
    }

    private void fireReceived(String protocol) {
	ClientEvent event = new ClientEvent(ClientEvent.PROTOCOL, protocol);
	for (ClientEventListener listener : listeners)
	    listener.receiveCompleted(event);
    }

    private void fireObjectReceived(Object object) {
	ClientEvent event = new ClientEvent(ClientEvent.OBJECT, object);
	for (ClientEventListener listener : listeners)
	    listener.receiveObjectCompleted(event);
    }

}