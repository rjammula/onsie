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

import java.io.File;
import javax.swing.ImageIcon;

public class CardImageCache {

    public static final int IMAGE_WIDTH = 81;
    public static final int IMAGE_HEIGHT = 131;

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
	String separator = "/";
	String basePath = "images" + separator + 
	    "mini" + separator + 
	    card.getColor().name() + separator + 
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