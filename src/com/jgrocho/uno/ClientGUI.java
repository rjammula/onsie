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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

public class ClientGUI extends JFrame {

    private JFrame frame;

    private Action connectAction;
    private Action disconnectAction;
    private Action exitAction;
    private Action playAction;

    private CardPanel handPanel;
    private DiscardPanel discardPanel;
    private JLabel statusLabel;

    private ClientEventHandler clientEventHandler;

    private Client client;
    private String host;
    private int port;

    private String username;
    private int playOrder;
    private Opponents opponents;
    private Hand hand;
    private Card discard;
    private Card.Color wildColor;
    private Card cardPlayed;

    private volatile boolean turn;
    private volatile boolean starting;
    private volatile boolean playing;
    private volatile boolean playerIncoming;
    private volatile boolean drawing;

    public ClientGUI() {
	super("onsie");

	turn = false;
	starting = false;
	playing = false;
	playerIncoming = false;
	drawing = false;

	frame = this;
	opponents = new Opponents();

	connectAction = new ConnectAction();
	disconnectAction = new DisconnectAction();
	exitAction = new ExitAction();

	playAction = new PlayAction();
	playAction.setEnabled(false);

	clientEventHandler = new ClientEventHandler();

	JMenuItem connectMenuItem = new JMenuItem(connectAction);
	JMenuItem disconnectMenuItem = new JMenuItem(disconnectAction);
	JMenuItem exitMenuItem = new JMenuItem(exitAction);

	JMenu fileMenu = new JMenu("File");
	fileMenu.add(connectMenuItem);
	fileMenu.add(disconnectMenuItem);
	fileMenu.addSeparator();
	fileMenu.add(exitMenuItem);

	JMenuBar menuBar = new JMenuBar();
	menuBar.add(fileMenu);
	setJMenuBar(menuBar);

	Color bgColor = new Color(68, 137, 56);

	handPanel = new CardPanel();
	handPanel.setBackground(bgColor);
	//Hand hand = new Game().getCurrentHand();
	//handPanel.setCards(hand.getAll());

	JButton playButton = new JButton(playAction);

	discardPanel = new DiscardPanel();
	discardPanel.setBackground(bgColor);
	
	JPanel boardPanel = new JPanel();
	boardPanel.setBackground(bgColor);
	boardPanel.add(handPanel);
	boardPanel.add(playButton);
	boardPanel.add(discardPanel);

	statusLabel = new JLabel("Please Connect");

	Container pane = getContentPane();

	pane.add(boardPanel, BorderLayout.CENTER);
	pane.add(statusLabel, BorderLayout.SOUTH);

	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//setDefaultLookAndFeelDecorated(true);
	//pack();
	setSize(700, 400);
	setLocationRelativeTo(null);
    }

    private void connect() {
	client = new Client(host, port);
	client.addClientEventListener(clientEventHandler);

	if (! client.connect()) {
	    statusLabel.setText("Could not connect");
	    JOptionPane.showMessageDialog(frame, 
					  "Could not connect to " + host,
					  "Connection Error",
					  JOptionPane.ERROR_MESSAGE);
	} else {
	    statusLabel.setText("Connected");
	    new Thread() {
		public void run() {
		    setup();
		}
	    }.start();
	}
    }
    
    private void setup() {
	client.send(Protocol.Ready);

	client.send(Protocol.Username);
	client.send(username);

	client.receive(Protocol.Hand);
	client.receiveObject();

	client.receive(Protocol.PlayOrder);
	client.receiveObject();

	client.receive(Protocol.Start);
    }

    private void startGame() {
	while (starting) {
	    receiveUser();
	}

	client.receivePlaying();
    }

    private void endGame() {
	client.receiveWinner();
    }

    private void winner() {
	System.out.println("You WIN!");
    }

    private void loser() {
	System.out.println("You lose");
    }

    private void receiveUser() {
	client.receiveUser();
	if (starting) {
	    client.receive(Protocol.Username);
	    client.receiveObject();
	    
	    client.receive(Protocol.PlayOrder);
	    client.receiveObject();
	    
	    client.receive(Protocol.CardCount);
	    client.receiveObject();

	    client.receive(Protocol.UserEnd);
	}
    }

    private void receivePlayer() {
	client.receivePlayer();
    }

    private void playRound() {
	client.receiveTurn();

	client.receivePlayer();
	while (playerIncoming) {
	    client.receive(Protocol.PlayOrder);
	    client.receiveObject();

	    client.receive(Protocol.CardCount);
	    client.receiveObject();

	    client.receivePlayer();
	}

	if (turn) {
	    client.receiveDraw();
	    while (drawing) {
		client.receiveObject();
		client.receiveDraw();
	    }

	    client.receive(Protocol.Hand);
	    client.receiveObject();

	    client.receive(Protocol.Discard);
	    client.receiveObject();

	    if (isWildDiscard()) {
		client.receive(Protocol.GetWild);
		client.receiveObject();
	    }

	    client.receive(Protocol.RequestCard);
	    playAction.setEnabled(true);
	} else {
	    client.receive(Protocol.Discard);
	    client.receiveObject();

	    client.receivePlaying();
	}
    }

    private void playCard(int card) {
	if (canPlayCard(card)) {
	    client.send(Protocol.PlayCard);
	    client.sendObject((Integer) card);
	    handPanel.removeCard(card);
	    playAction.setEnabled(false);

	    if (isWildCard(card)) {
		discardPanel.addColorButtons();
		Card.Color color = discardPanel.getWildColorChoice();
		client.send(Protocol.SetWild);
		client.sendObject(color);
		client.receive(Protocol.Success);
		discardPanel.resetWildColorChoice();
	    }

	    client.receive(Protocol.Success);
	    client.receivePlaying();
	} else {
	    System.out.println("can't play that Card: " 
			       + card + "  " + hand.get(card));
	}
	repaint();
    }

    private void setStarting(boolean starting) {
	this.starting = starting;
    }

    private void setPlaying(boolean playing) {
	this.playing = playing;
    }

    private void setEnd(boolean end) {
	this.playing = ! end;
	endGame();
    }

    private void setTurn(boolean turn) {
	this.turn = turn;
	/*
	if (turn) {
	    playAction.setEnabled(true);
	} else {
	    playAction.setEnabled(false);
	}
	*/
    }

    private void setPlayerIncoming(boolean playerIncoming) {
	this.playerIncoming = playerIncoming;
    }

    private void setHand(final Hand hand) {
	this.hand = hand;
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    handPanel.setCards(hand.getAll());
		    repaint();
		}
	    });
    }

    private void addCard(final Card card) {
	/*
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    handPanel.addCard(card);
		    repaint();
		}
	    });
	*/
    }

    private void setDiscard(Card card) {
	discard = card;
	discardPanel.setDiscard(card);
	repaint();
    }

    private void setWildColor(Card.Color color) {
	wildColor = color;
	discardPanel.setWild(color);
	repaint();
    }

    private boolean isWildDiscard() {
	return discard.getColor() == Card.Color.NONE;
    }

    private boolean isWildCardPlayed() {
	return cardPlayed.getColor() == Card.Color.NONE;
    }

    private boolean canPlayCard(int cardIndex) {
	Card card = hand.get(cardIndex);

	if (card != null &&
	    ((discard.getColor() == Card.Color.NONE && 
	      card.getColor() == wildColor) ||
	     card.getColor() == Card.Color.NONE ||
	     card.getColor() == discard.getColor() ||
	     card.getNumber() == discard.getNumber())) {

	    return true;
	}

	return false;
    }

    private boolean isWildCard(int cardIndex) {
	return hand.get(cardIndex).getColor() == Card.Color.NONE;
    }

    private class ConnectAction extends AbstractAction {
	private ConnectDialog connectDialog;

	public ConnectAction() {
	    super("Connect");

	    connectDialog = new ConnectDialog();
	    connectDialog.acceptButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event) {
			host = connectDialog.getServer();
			username = connectDialog.getUsername();
			port = connectDialog.getPort();

			statusLabel.setText("Connecting");
			connect();
		    }
		});
	}

	public void actionPerformed(ActionEvent event) {
	    connectDialog.setVisible(true);
	}

	private class ConnectDialog extends JDialog {

	    private JTextField serverTextField;
	    private JTextField usernameTextField;
	    private SpinnerNumberModel portModel;
	    private JSpinner portSpinner;
	    private JButton acceptButton;

	    public ConnectDialog() {
		super(frame, "Connect", true);
		
		TextFieldDocumentHandler documentHandler = 
		    new TextFieldDocumentHandler();

		serverTextField = new JTextField(10);
		serverTextField.getDocument()
		    .addDocumentListener(documentHandler);

		usernameTextField = new JTextField(10);
		usernameTextField.getDocument()
		    .addDocumentListener(documentHandler);

		portModel = new SpinnerNumberModel(Protocol.PORT,
						   1024, 65535, 1);
		portSpinner = new JSpinner(portModel);
		portSpinner.setEditor(new JSpinner.NumberEditor(portSpinner, 
								"#"));
		((JSpinner.DefaultEditor) portSpinner.getEditor())
		    .getTextField().setHorizontalAlignment(JTextField.LEFT);

		JLabel serverLabel = new JLabel("Server");
		serverLabel.setLabelFor(serverTextField);

		JLabel usernameLabel = new JLabel("Username");
		usernameLabel.setLabelFor(usernameTextField);

		JLabel portLabel = new JLabel("Port");
		portLabel.setLabelFor(portSpinner);

		acceptButton = new JButton("Connect");
		acceptButton.setEnabled(false);
		acceptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			    if (!getServer().equals(""))
				setVisible(false);
			}
		    });

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			    setVisible(false);
			}
		    });

		Container content = getContentPane();

		GroupLayout layout = new GroupLayout(content);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		content.setLayout(layout);

		layout.setHorizontalGroup(
					  layout.createSequentialGroup()
					  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						    .addComponent(serverLabel)
						    .addComponent(usernameLabel)
						    .addComponent(portLabel))
					  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						    .addComponent(serverTextField)
						    .addComponent(usernameTextField)
						    .addComponent(portSpinner)
						    .addGroup(layout.createSequentialGroup()
							      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							      .addComponent(acceptButton)
							      .addComponent(cancelButton))));

		layout.setVerticalGroup(
					layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						  .addComponent(serverLabel)
						  .addComponent(serverTextField))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						  .addComponent(usernameLabel)
						  .addComponent(usernameTextField))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						  .addComponent(portLabel)
						  .addComponent(portSpinner))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						  .addComponent(acceptButton)
						  .addComponent(cancelButton)));

		addWindowListener(new WindowHandler());

		pack();
		setResizable(false);
		setLocationRelativeTo(frame);
	    }

	    public String getServer() {
		return serverTextField.getText();
	    }

	    public String getUsername() {
		return usernameTextField.getText();
	    }

	    public int getPort() {
		return ((Integer) portModel.getNumber()).intValue();
	    }

	    private class TextFieldDocumentHandler implements DocumentListener {
		private boolean serverSet;
		private boolean usernameSet;

		public void insertUpdate(DocumentEvent documentEvent) {
		    if (! getServer().equals(""))
			serverSet = true;
		    else
			serverSet = false;

		    if (! getUsername().equals(""))
			usernameSet = true;
		    else
			usernameSet = false;

		    if (serverSet && usernameSet)
			acceptButton.setEnabled(true);
		    else
			acceptButton.setEnabled(false);
		}

		public void removeUpdate(DocumentEvent documentEvent) {
		    if (! getServer().equals(""))
			serverSet = true;
		    else
			serverSet = false;

		    if (! getUsername().equals(""))
			usernameSet = true;
		    else
			usernameSet = false;

		    if (serverSet && usernameSet)
			acceptButton.setEnabled(true);
		    else
			acceptButton.setEnabled(false);
		}

		public void changedUpdate(DocumentEvent documentEvent) {
		}
	    }
	    
	    private class WindowHandler extends WindowAdapter {
		public void windowClosing(WindowEvent windowEvent) {
		    setVisible(false);
		}
	    }
	}
    }

    private class DisconnectAction extends AbstractAction {
	public DisconnectAction() {
	    super("Disconnect");
	}

	public void actionPerformed(ActionEvent event) {
	    System.out.println("disconnect");
	}
    }

    private class ExitAction extends AbstractAction {
	public ExitAction() {
	    super("Exit");
	}

	public void actionPerformed(ActionEvent event) {
	    dispose();
	}
    }

    private class PlayAction extends AbstractAction {
	public PlayAction() {
	    super("Play");
	}

	public void actionPerformed(ActionEvent event) {
	    final int card = handPanel.getSelected();
	    new Thread() {
		public void run() {
		    playCard(card);
		}
	    }.start();
	}
    }

    private class CardPanel extends JPanel {

	private int nextIndex;
	private ButtonGroup buttonGroup;

	public CardPanel() {
	    super();
	    setMinimumSize(new Dimension(CardImageCache.IMAGE_WIDTH,
					 CardImageCache.IMAGE_HEIGHT));
	    /*
	      setPreferredSize(new Dimension(CardImageCache.IMAGE_WIDTH * 7,
	      CardImageCache.IMAGE_HEIGHT));
	    */
	    buttonGroup = new ButtonGroup();
	}

	public void setCards(Card[] cards) {
	    removeAll();
	    for (int i = 0; i < cards.length; ++i) {
		Card card = cards[i];
		CardButton cardButton = 
		    new CardButton(CardImageCache.getImageIcon(card));
		cardButton.setActionCommand(i + "");
		nextIndex = i + 1;
		buttonGroup.add(cardButton);
		add(cardButton);
	    }
	    revalidate();
	}

	public void addCard(Card card) {
	    CardButton cardButton = 
		new CardButton(CardImageCache.getImageIcon(card));
	    cardButton.setActionCommand(nextIndex++ + "");
	    buttonGroup.add(cardButton);
	    add(cardButton);

	    revalidate();
	}

	public void removeCard(int card) {
	    remove(card);
	    revalidate();
	}

	public int getSelected() {
	    return Integer.parseInt(buttonGroup
				    .getSelection().getActionCommand());
	}

	private class CardButton extends JToggleButton {
	    private Image image;
	    private Dimension size;

	    public CardButton(ImageIcon icon) {
		super();
		setIcon(icon);
		image = icon.getImage();
		size = new Dimension(icon.getIconWidth(), 
				     icon.getIconHeight() + 10);

	    }

	    protected void paintComponent(Graphics g) {
		g.setColor(getParent().getBackground());
		g.fillRect(0, 0, size.width, size.height);
		if (isSelected())
		    g.drawImage(image, 0, 0, this);
		else
		    g.drawImage(image, 0, 10, this);
	    }

	    protected void paintBorder(Graphics g) { }

	    public Dimension getPreferredSize() {
		return size;
	    }
	}
    }

    private class DiscardPanel extends JPanel {
	private JLabel discardLabel;
	private JLabel wildLabel;

	private JButton blueButton;
	private JButton greenButton;
	private JButton redButton;
	private JButton yellowButton;

	private volatile Card.Color discardWildColor = Card.Color.NONE;

	public DiscardPanel() {
	    discardLabel = new JLabel("");
	    add(discardLabel);

	    wildLabel = new JLabel("");
	    wildLabel.setEnabled(false);

	    WildColorButtonHandler wildColorButtonHandler = 
		new WildColorButtonHandler();

	    blueButton = new JButton("Blue");
	    blueButton.setActionCommand("blue");
	    blueButton.addActionListener(wildColorButtonHandler);

	    greenButton = new JButton("Green");
	    greenButton.setActionCommand("green");
	    greenButton.addActionListener(wildColorButtonHandler);

	    redButton = new JButton("Red");
	    redButton.setActionCommand("red");
	    redButton.addActionListener(wildColorButtonHandler);

	    yellowButton = new JButton("Yellow");
	    yellowButton.setActionCommand("yellow");
	    yellowButton.addActionListener(wildColorButtonHandler);
	}

	public void setDiscard(Card card) {
	    removeWild();
	    discardLabel.setIcon(CardImageCache.getImageIcon(card));
	    revalidate();
	}

	public void setWild(Card.Color color) {
	    wildLabel.setText(color.toString());
	    wildLabel.setEnabled(true);
	    add(wildLabel);
	    revalidate();
	}

	public void removeWild() {
	    if (wildLabel.isEnabled()) {
		remove(wildLabel);
		wildLabel.setEnabled(false);
		revalidate();
	    }
	}

	public void addColorButtons() {
	    add(blueButton);
	    add(greenButton);
	    add(redButton);
	    add(yellowButton);
	    revalidate();
	}

	public void removeColorButtons() {
	    remove(blueButton);
	    remove(greenButton);
	    remove(redButton);
	    remove(yellowButton);
	    revalidate();
	}

	public Card.Color getWildColorChoice() {
	    while (discardWildColor == Card.Color.NONE) {}
	    return discardWildColor;
	}

	public void resetWildColorChoice() {
	    discardWildColor = Card.Color.NONE;
	}

	private class WildColorButtonHandler implements ActionListener {
	    public void actionPerformed(ActionEvent event) {
		String color = event.getActionCommand();

		if (color.equals("blue"))
		    discardWildColor = Card.Color.BLUE;
		else if (color.equals("green"))
		    discardWildColor = Card.Color.GREEN;
		else if (color.equals("red"))
		    discardWildColor = Card.Color.RED;
		else if (color.equals("yellow"))
		    discardWildColor = Card.Color.YELLOW;

		removeColorButtons();
	    }
	}
    }

    private class ClientEventHandler implements ClientEventListener {
	private String awaiting;
	
	private boolean inUser;
	private Opponent currentOpponent;
	private int opponentPosition;
	private int opponentCardCount;

	public void receiveCompleted(ClientEvent event) {
	    //System.out.println(event.getContent());
	    String content = (String) event.getContent();
	    awaiting = content;
	    if (starting) {
		if (content.equals(Protocol.User)) {
		    inUser = true;
		    currentOpponent = new Opponent();
		} else if (content.equals(Protocol.UserEnd)) {
		    inUser = false;
		    if (currentOpponent.isCreated())
			opponents.put(currentOpponent.getPosition(), 
				      currentOpponent);
		    else
			System.out.println("incomplete user");
		} else if (content.equals(Protocol.NoUser))
		    setStarting(false);
	    } else {
		if (content.equals(Protocol.Start)) {
		    setStarting(true);
		    new Thread() {
			public void run() {
			    startGame();
			}
		    }.start();
		} else if (content.equals(Protocol.Playing)) {
		    setPlaying(true);
		    new Thread() {
			public void run() {
			    playRound();
			}
		    }.start();
		} else if (content.equals(Protocol.End))
		    setEnd(true);
		else if (content.equals(Protocol.Winner))
		    winner();
		else if (content.equals(Protocol.Loser))
		    loser();
		else if (content.equals(Protocol.Turn))
		    setTurn(true);
		else if (content.equals(Protocol.OtherTurn))
		    setTurn(false);
		else if (content.equals(Protocol.Player)) {
		    inUser = true;
		    setPlayerIncoming(true);
		} else if (content.equals(Protocol.PlayerEnd)) {
		    inUser = false;
		    currentOpponent = opponents.get(opponentPosition);
		    currentOpponent.setCards(opponentCardCount);
		} else if (content.equals(Protocol.NoPlayer))
		    setPlayerIncoming(false);
		else if (content.equals(Protocol.Draw))
		    drawing = true;
		else if (content.equals(Protocol.NoDraw))
		    drawing = false;
	    }
	}
	
	public void receiveObjectCompleted(ClientEvent event) {
	    //System.out.println(event.getContent());
	    if (starting) {
		if (inUser) {
		    if (awaiting.equals(Protocol.Username))
			currentOpponent.setName((String) event.getContent());
		    else if (awaiting.equals(Protocol.PlayOrder))
			currentOpponent
			    .setPosition(((Integer) event.getContent()).intValue());
		    else if (awaiting.equals(Protocol.CardCount))
			currentOpponent
			    .setCards(((Integer) event.getContent()).intValue());
		}
	    } else if (playing) {
		if (awaiting.equals(Protocol.Hand)) {
		    final Hand hand = (Hand) event.getContent();
		    SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
				setHand(hand);
			    }
			});
		} else if (awaiting.equals(Protocol.Discard))
		    setDiscard((Card) event.getContent());
		else if (awaiting.equals(Protocol.GetWild))
		    setWildColor((Card.Color) event.getContent());
		if (inUser) {
		    if (awaiting.equals(Protocol.PlayOrder))
			opponentPosition = ((Integer) event.getContent())
			    .intValue();
		    else if (awaiting.equals(Protocol.CardCount))
			opponentCardCount = ((Integer) event.getContent())
			    .intValue();
		} else if (turn) {
		    if (awaiting.equals(Protocol.Draw))
			addCard((Card) event.getContent());
		}
	    } else {
		if (awaiting.equals(Protocol.Hand)) {
		    final Hand hand = (Hand) event.getContent();
		    SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
				setHand(hand);
			    }
			});
		}
		else if (awaiting.equals(Protocol.PlayOrder))
		    playOrder = ((Integer) event.getContent()).intValue();
	    }
	}
    }
    
    public static void main(String[] args) {
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    ClientGUI frame = new ClientGUI();
		    frame.setVisible(true);
		}
	    });
    }

}