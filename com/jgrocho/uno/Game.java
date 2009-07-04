package com.jgrocho.uno;

public class Game {

    private static final int HAND_COUNT = 4;
    private static final int CARD_COUNT = 7;

    private boolean playing;
    private Deck deck;
    private Discard discard;
    private Hand[] hands;
    private int players;
    private int currentTurn;
    private int direction;
    private Card.Color wildColor;

    public Game() {
	this(HAND_COUNT, CARD_COUNT);
    }

    public Game(int players) {
	this(players, CARD_COUNT);
    }      

    public Game(int players, int cards) {
	deck = new Deck();
	deck.shuffle();

	this.players = players;
	hands = new Hand[players];
	for (int i = 0; i < players; ++i) {
	    Hand hand = new Hand();
	    hand.addAll(deck.deal(cards));
	    hands[i] = hand;
	}

	discard = new Discard();
	Card discardCard = deck.deal();
	Card.Number discardNumber = discardCard.getNumber();
	while (discardNumber.ordinal() > Card.Number.NINE.ordinal()) {
	    deck.reload(discardCard);
	    deck.shuffle();
	    discardCard = deck.deal();
	    discardNumber = discardCard.getNumber();
	}
	discard.add(discardCard);

	currentTurn = 0;
	direction = 1;
	playing = false;
    }

    public void start() {
	playing = true;
    }

    public void stop() {
	playing = false;
    }

    public boolean isPlaying() {
	return playing;
    }

    public void next() {
	if (discard.getTop().getNumber() == Card.Number.REVERSE) {
	    if (players == 2)
		currentTurn += direction;
	    direction *= -1;
	} else if (discard.getTop().getNumber() == Card.Number.SKIP)
	    currentTurn += direction;

	currentTurn += direction;
	
	if (currentTurn >= players)
	    while (currentTurn >= players)
		currentTurn -= players;
	else if (currentTurn < 0)
	    while (currentTurn < 0)
		currentTurn += players;
    }
    
    public Hand getCurrentHand() {
	return hands[currentTurn];
    }

    public Hand getHand(int index) {
	return hands[index];
    }

    public int getCurrentPlayerNumber() {
	return currentTurn;
    }

    public int getDirection() {
	return direction;
    }

    public int getPlayers() {
	return players;
    }

    public Card topDiscard() {
	return discard.getTop();
    }

    public boolean canPlay() {
	Hand hand = getCurrentHand();
	Card discard = topDiscard();

	for (Card card : hand)
	    if (card.getColor() == discard.getColor() ||
		card.getNumber() == discard.getNumber() ||
		card.getColor() == Card.Color.NONE ||
		(discard.getColor() == Card.Color.NONE && 
		 card.getColor() == wildColor))
		return true;
	
	return false;

    }

    public boolean playCard(int index) {
	Hand hand = getCurrentHand();
	Card card = hand.get(index);

	if (card != null &&
	    ((topDiscard().getColor() == Card.Color.NONE && 
	      card.getColor() == wildColor) ||
	     card.getColor() == Card.Color.NONE ||
	     card.getColor() == topDiscard().getColor() ||
	     card.getNumber() == topDiscard().getNumber())) {

	    discard.add(hand.remove(index));
	    return true;
	}

	return false;
    }

    public Card drawCard() {
	if (deck.size() == 0) {
	    deck.reload(discard.empty());
	    deck.shuffle();
	}

	Hand hand = getCurrentHand();
	Card card = deck.deal();
	hand.add(card);

	return card;
    }

    public void setWildColor(Card.Color color) {
	if (topDiscard().getColor() != Card.Color.NONE)
	    return;

	wildColor = color;
    }

    public Card.Color getWildColor() {
	return wildColor;
    }

}