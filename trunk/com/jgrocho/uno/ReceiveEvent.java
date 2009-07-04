package com.jgrocho.uno;

class ReceiveEvent {

    Message message;

    public ReceiveEvent(Message message) {
	this.message = message;
    }

    public Message getMessage() {
	return message;
    }

}