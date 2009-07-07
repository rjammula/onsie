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

public class Protocol {

    public static final int PORT = 1080;

    public static final String Success     = "ok";
    public static final String Failure     = "error";
    public static final String Ready       = "ready";
    public static final String PlayOrder   = "play order";
    public static final String Start       = "start";
    public static final String Playing     = "playing";
    public static final String Hand        = "hand";
    public static final String Discard     = "discard";
    public static final String NoPlay      = "no play";
    public static final String Turn        = "turn";
    public static final String OtherTurn   = "other turn";
    public static final String Draw        = "draw";
    public static final String NoDraw      = "no draw";
    public static final String RequestCard = "request card";
    public static final String PlayCard    = "play card";
    public static final String SetWild     = "set wild";
    public static final String End         = "end";
    public static final String Winner      = "winner";
    public static final String Loser       = "loser";
    public static final String Player      = "player";
    public static final String PlayerEnd   = "player end";
    public static final String NoPlayer    = "no player";
    public static final String User        = "user";
    public static final String UserEnd     = "user end";
    public static final String NoUser      = "no user";
    public static final String Username    = "username";
    public static final String CardCount   = "card count";
}