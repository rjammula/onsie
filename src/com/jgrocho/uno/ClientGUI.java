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

public class ClientGUI extends JFrame {

    private Action connectAction;
    private Action disconnectAction;
    private Action exitAction;

    private CardActionListener cardActionListener;

    private String host;
    private int port;

    public ClientGUI() {
	connectAction = new ConnectAction(this);
	disconnectAction = new DisconnectAction();
	exitAction = new ExitAction();

	cardActionListener = new CardActionListener();

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

	CardPanel handPanel = new CardPanel();
	handPanel.setBackground(bgColor);
	Hand hand = new Game().getCurrentHand();
	handPanel.setCards(hand.getAll());

	JPanel boardPanel = new JPanel();
	boardPanel.setBackground(bgColor);
	boardPanel.add(handPanel);

	add(boardPanel);

	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//setDefaultLookAndFeelDecorated(true);
	//pack();
	setSize(700, 400);
	setLocationRelativeTo(null);
    }

    private class ConnectAction extends AbstractAction {
	private ConnectDialog connectDialog;

	public ConnectAction(JFrame owner) {
	    super("Connect");

	    connectDialog = new ConnectDialog(owner);
	    connectDialog.acceptButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event) {
			host = connectDialog.getServer();
			port = connectDialog.getPort();
		    }
		});
	}

	public void actionPerformed(ActionEvent event) {
	    connectDialog.setVisible(true);
	}

	private class ConnectDialog extends JDialog {

	    private JTextField serverTextField;
	    private SpinnerNumberModel portModel;
	    private JSpinner portSpinner;
	    private JButton acceptButton;

	    public ConnectDialog(JFrame owner) {
		super(owner, "Connect", true);

		serverTextField = new JTextField(10);

		portModel = new SpinnerNumberModel(Protocol.PORT,
						   1024, 65535, 1);
		portSpinner = new JSpinner(portModel);
		portSpinner.setEditor(new JSpinner.NumberEditor(portSpinner, 
								"#"));
		((JSpinner.DefaultEditor) portSpinner.getEditor())
		    .getTextField().setHorizontalAlignment(JTextField.LEFT);

		JLabel serverLabel = new JLabel("Server");
		serverLabel.setLabelFor(serverTextField);

		JLabel portLabel = new JLabel("Port");
		portLabel.setLabelFor(portSpinner);

		acceptButton = new JButton("Connect");
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
						    .addComponent(portLabel))
					  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						    .addComponent(serverTextField)
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
						  .addComponent(portLabel)
						  .addComponent(portSpinner))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						  .addComponent(acceptButton)
						  .addComponent(cancelButton)));

		addWindowListener(new WindowHandler());

		pack();
		setResizable(false);
		setLocationRelativeTo(owner);
	    }

	    public String getServer() {
		return serverTextField.getText();
	    }

	    public int getPort() {
		return ((Integer) portModel.getNumber()).intValue();
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

    private class CardPanel extends JPanel {
	public CardPanel() {
	    setMinimumSize(new Dimension(CardImageCache.IMAGE_WIDTH,
					 CardImageCache.IMAGE_HEIGHT));
	    /*
	    setPreferredSize(new Dimension(CardImageCache.IMAGE_WIDTH * 7,
					   CardImageCache.IMAGE_HEIGHT));
	    */
	}

	public void setCards(Card[] cards) {
	    for (int i = 0; i < cards.length; ++i) {
		Card card = cards[i];
		CardButton cardButton = 
		    new CardButton(CardImageCache.getImageIcon(card));
		cardButton.setActionCommand(i + "");
		cardButton.addActionListener(cardActionListener);
		add(cardButton);
	    }
	}

	private class CardButton extends JButton implements MouseListener {
	    private Image image;
	    private Dimension size;
	    private boolean pressed;

	    public CardButton(ImageIcon icon) {
		super();
		setIcon(icon);
		image = icon.getImage();
		size = new Dimension(icon.getIconWidth(), 
				     icon.getIconHeight() + 10);

		pressed = false;
		addMouseListener(this);
	    }

	    protected void paintComponent(Graphics g) {
		g.setColor(getParent().getBackground());
		g.fillRect(0, 0, size.width, size.height);
		if (pressed)
		    g.drawImage(image, 0, 0, this);
		else
		    g.drawImage(image, 0, 10, this);
	    }

	    protected void paintBorder(Graphics g) { }

	    public Dimension getPreferredSize() {
		return size;
	    }

	    public void mouseClicked(MouseEvent e) { }
	    public void mouseEntered(MouseEvent e) { }
	    public void mouseExited(MouseEvent e) { }
	    public void mousePressed(MouseEvent e) {
		pressed = true;
	    }
	    public void mouseReleased(MouseEvent e) {
		pressed = false;
	    }
	}
    }

    private class CardActionListener implements ActionListener {
	public void actionPerformed(ActionEvent event) {
	    System.out.println(event.getActionCommand());;
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