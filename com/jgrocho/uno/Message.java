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