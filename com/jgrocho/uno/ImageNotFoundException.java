package com.jgrocho.uno;

public class ImageNotFoundException extends RuntimeException {

    public ImageNotFoundException(String image) {
	super("Image: " + image + " not found");
    }

}