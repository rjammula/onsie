package com.jgrocho.uno;

import java.io.File;
import javax.swing.ImageIcon;

public class CardImageCache {

    public static final int IMAGE_WIDTH = 85;
    public static final int IMAGE_HEIGHT = 135;

    private static ImageIcon[] ALL_ICONS = new ImageIcon[54];

    public static ImageIcon getImageIcon(Card card) {
	int index = -1;

	if (card.getColor() == Card.Color.NONE)
	    index = card.getNumber().ordinal() + 39;
	else
	    index = card.getNumber().ordinal() + 
		(card.getColor().ordinal() * 13);

	ImageIcon cardIcon = ALL_ICONS[index];
	if (cardIcon == null) {
	    cardIcon = loadImageIcon(card);
	    ALL_ICONS[index] = cardIcon;
	}

	return ALL_ICONS[index];
    }

    private static ImageIcon loadImageIcon(Card card) {
	String basePath = "images" + File.separator + 
	    "mini" + File.separator + 
	    card.getColor().name() + File.separator + 
	    card.getNumber().name() + ".png";
	java.net.URL imageURL = CardImageCache.class.getResource(basePath);

	if (imageURL != null)
	    return new ImageIcon(imageURL, card.toString());
	else
	    throw new ImageNotFoundException(basePath);
    }

    public static void main(String[] args) {

    }

}