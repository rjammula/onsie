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

public class Message {

    public enum Type { PROTOCOL, OBJECT, COMMAND }

    private Type type;
    private String protocol = null;
    private Object object = null;
    private String command = null;

    public Message(String protocol) {
	type = Type.PROTOCOL;
	this.protocol = protocol;
    }

    public Message(Object object) {
	type = Type.OBJECT;
	this.object = object;
    }

    public Message(Type type, String command) {
	this.type = type;
	if (type == Type.COMMAND)
	    command = "RESET";
	else
	    command = "NONE";
    }

    public Type getType() {
	return type;
    }

    public String getProtocol() {
	return protocol;
    }

    public Object getObject() {
	return object;
    }

    public String getCommand() {
	return command;
    }

    public String toString() {
	if (type == Type.PROTOCOL)
	    return type + " " + protocol;
	else if (type == Type.OBJECT)
	    return type + " " + object.toString();
	else if (type == Type.COMMAND)
	    return type + " " + command;
	else
	    return "UNKONW MESSAGE TYPE";
	    //return type.toString();
    }

}