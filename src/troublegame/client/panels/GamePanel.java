package troublegame.client.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import troublegame.client.SwingUI;
import troublegame.client.UserInput;
import troublegame.client.model.Board;
import troublegame.client.model.Tile;
import troublegame.communication.CommunicationHandler;

/**
 * 
 * The {@link GamePanel} class handles the display of the game of the client.
 * 
 * @author Jeffrey Ung and Tony Tran
 *
 */
public class GamePanel extends JPanel {
	
	/**
	 * Serial ID for object serialisation 
	 */
	private static final long serialVersionUID = -1640618945771077108L;
	
	/**
	 * Transparent colors.
	 */
	public final static Color TRANSPARENT_RED = new Color(1f, 0f, 0f, 0.4f);
	public final static Color TRANSPARENT_BLUE = new Color(0f, 0f, 1f, 0.4f);
	public final static Color TRANSPARENT_GREEN = new Color(0f, 1f, 0f, 0.4f);
	public final static Color TRANSPARENT_YELLOW = new Color(1f, 1f, 0f, 0.3f);
	
	/**
	 * The map of tokens. <position, color>
	 */
	private Map<Integer, Color> tokenMap;
	
	/**
	 * The map of players. <username, color>
	 */
	private Map<String, String> players;
	
	/**
	 * The swing user interface.
	 */
	private SwingUI swingUI;
	
	/**
	 * The user input.
	 */
	private MouseListener userInput;
	
	/**
	 * The label.
	 */
	private JLabel label;
	
	/**
	 * The game messages <index, message>.
	 */
	private Map<Integer, String> messages;
	
	/**
	 * The die roll button.
	 */
	private JButton rollDie;
	
	/**
	 * The board representation.
	 */
	private Board board;
	
	/**
	 * The chat text area.
	 */
	private JTextArea chatMessages;
	
	/**
	 * The game space.
	 */
	private GameSpace gameSpace;
	
	/**
	 * The constructor for the start panel.
	 * @param swingUI is the swing user interface.
	 */
	public GamePanel(SwingUI swingUI) {
		this.swingUI = swingUI;
		this.tokenMap = new HashMap<Integer, Color>();
		this.players = new HashMap<String, String>();
		this.label = new JLabel("");
		this.messages = new LinkedHashMap<Integer, String>();
		this.rollDie = new JButton("Roll Die");
		this.board = new Board();
		this.gameSpace = new GameSpace();
		this.init();
	}

	/**
	 * Initializes the game panel.
	 */
	public void init() {
		this.setLayout(new BoxLayout(this, 1));
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.swingUI.requestFocusInWindow();
		this.setVisible(true);
		rollDie.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (!swingUI.getUser().isSelectedTile()) {
					//updateMessage("Please select a token in order to roll the die!", 0);
					sendChatMessage("Please select a token in order to roll the die!");
					return;
				}

				if (swingUI.getUser().getSelectedTile().getZone() == Board.SLOT_END) {
					//updateMessage("Cannot roll a token in the end zone", 0);
					sendChatMessage("Cannot roll a token in the end zone");
					return;
				}

				//updateMessage(" ", 0);
				for (int i = 0; i < swingUI.getUser().getTokens().size(); i++) {
					if (swingUI.getUser().getTokens().get(i).equals(swingUI.getUser().getSelectedTile())) {
						swingUI.send(CommunicationHandler.GAME_ROLL + " " + i);
						break;
					}
				}
			}
		});
		
		JPanel chatPanel = new JPanel();
		JTextField newMessage = new JTextField();
		JButton sendButton = new JButton("Send");
		chatMessages = new JTextArea(10, 80);
		JScrollPane scroll = new JScrollPane(chatMessages);

		scroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
			public void adjustmentValueChanged(AdjustmentEvent e) {
				// auto scroll to the bottom
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
			}
		});
		newMessage.setPreferredSize(new Dimension(585, 20));
		chatMessages.setEditable(false);
		chatPanel.add(scroll);
		chatPanel.add(newMessage);
		chatPanel.add(sendButton);
	
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				swingUI.send(CommunicationHandler.GAME_CHAT + " " + newMessage.getText());
				newMessage.setText("");	
			}
		});
		newMessage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!newMessage.getText().equals("")) {
					swingUI.send(CommunicationHandler.GAME_CHAT + " " + newMessage.getText());
					newMessage.setText("");	
				}
			}
		});
		this.add(gameSpace);
		this.add(label);
		this.add(rollDie);
		this.add(chatPanel);
	}

	/**
	 * Handles the chat message by reading the text field
	 * @param message is the string being sent to that chat
	 */
	public void sendChatMessage(String message) {
		if (message.equals(""))
			return;
		chatMessages.append(message+"\n");
	}

	
	/**
	 * @return the swing user interface.
	 */
	public SwingUI getSwingUI() {
		return swingUI;
	}

	/**
	 * Sets the swing user interface.
	 * @param swingUI is the swing user interface.
	 */
	public void setSwingUI(SwingUI swingUI) {
		this.swingUI = swingUI;
	}

	
	
	/**
	 * Gets the position of a token from input coming from the server.
	 * @param color is the color.
	 * @param slotId is the slot Id
	 * @param pos is the position
	 * @return the position.
	 */
	public final int getPosition(Color color, int slotId, int pos) {
		switch(slotId) {
		case 0:
		case 1:
		case 2:
		}
		return -1;
	}

	//TODO: Check if slot is already occupied by your own piece
	public void updateToken(String username, int tokenID, int destZone, int destIndex) {
		String color = players.get(username);
		Tile token = null;
		Color paintColor = null;
		ArrayList<Tile> homeZone = null;
		ArrayList<Tile> endZone = null;
		if (color.equals("red")) {
			token = board.getRedTokens().get(tokenID);
			paintColor = Color.RED;
			homeZone = board.getRedHomeZone();
			endZone = board.getRedEndZone();
		} else if (color.equals("blue")) {
			token = board.getBlueTokens().get(tokenID);
			paintColor = Color.BLUE;
			homeZone = board.getBlueHomeZone();
			endZone = board.getBlueEndZone();
		} else if (color.equals("yellow")) {
			token = board.getYellowTokens().get(tokenID);
			paintColor = Color.YELLOW;
			homeZone = board.getYellowHomeZone();
			endZone = board.getYellowEndZone();
		} else if (color.equals("green")) {
			token = board.getGreenTokens().get(tokenID);
			paintColor = Color.GREEN;
			homeZone = board.getGreenHomeZone();
			endZone = board.getGreenEndZone();
		}
		
		if (token.getZone() == Board.SLOT_HOME) {
			Tile newLoc = board.getMainZone().get(destIndex);
			token.setTile(newLoc.getShape(), paintColor);
			token.setZone(Board.SLOT_MAIN);
			token.setIndex(destIndex);
		} else if (token.getZone() == Board.SLOT_MAIN) {
			if (destZone == Board.SLOT_HOME) {
				Tile newLoc = homeZone.get(destIndex);
				token.setTile(newLoc.getShape(), paintColor);
				token.setZone(Board.SLOT_HOME);
				token.setIndex(destIndex);
			} else if (destZone == Board.SLOT_MAIN) {
				Tile newLoc = board.getMainZone().get(destIndex);
				token.setTile(newLoc.getShape(), paintColor);
				token.setIndex(destIndex);
			} else if (destZone == Board.SLOT_END) {
				Tile newLoc = endZone.get(destIndex);
				token.setTile(newLoc.getShape(), paintColor);
				token.setZone(Board.SLOT_END);
				token.setIndex(destIndex);
			}
		}
		gameSpace.repaint();
	}
	
	public void updateMessage(String msg, int index) {
		this.messages.put(index, msg);
		String text = "<html>";
		for (Entry<Integer, String> s : messages.entrySet()) {
			if (s.getValue().equals(""))
				continue;
			text += s.getValue() + "<br>";
		}
		text += "</html>";
		this.label.setText(text);
	}
	
	/**
	 * @return the players map.
	 */
	public Map<String, String> getPlayers() {
		return players;
	}

	/**
	 * @return the token map.
	 */
	public Map<Integer, Color> getTokenMap() {
		return tokenMap;
	}
	
	public void setupPanel() {
		String color = players.get(swingUI.getUser().getUsername());
		
		switch(color) {
			case "red":
				swingUI.getUser().setColor(Color.RED);
				swingUI.getUser().setTokens(board.getRedTokens());
				break;
			case "blue":
				swingUI.getUser().setColor(Color.BLUE);
				swingUI.getUser().setTokens(board.getBlueTokens());
				break;
			case "yellow":
				swingUI.getUser().setColor(Color.YELLOW);
				swingUI.getUser().setTokens(board.getYellowTokens());
				break;
			case "green":
				swingUI.getUser().setColor(Color.GREEN);
				swingUI.getUser().setTokens(board.getGreenTokens());
				break;
			default:
		}
		
		this.userInput = new UserInput(swingUI);
		this.addMouseListener(this.userInput);
	}
	
	@SuppressWarnings("serial")
	private class GameSpace extends JPanel {
		
		public GameSpace() {
			// picked a random dimension
			this.setPreferredSize(swingUI.getSize());
			this.createTiles();
			this.createTokens();
		}
		
		/**
		 * Creates a list of tiles.
		 */
		public void createTiles() {	        
	        int width = getWidth() / 2;
			int height = getHeight() / 2;
			int min = Math.min(width, height);
	        int diameter = 4 * min / 5;
	        int radius = Math.abs(min - diameter) / 3;
	        int tileSize = 25;
	        for (int i = 0; i < 28; i++) {
	            double t = 2 * Math.PI * i / 28;
	            int x = (int) Math.round(width + diameter * Math.cos(t));
	            int y = (int) Math.round(height + diameter * Math.sin(t));
	            Rectangle rectangle = new Rectangle(x - radius, y - radius, tileSize - 2, tileSize - 2);
	            board.getMainZone().get(i).setTile(rectangle, Color.LIGHT_GRAY);
	        }

			double t;
			int x, y, baseX, baseY;
			Rectangle rectangle;
			
			// Red Home Tiles
			// Home 1
			baseX = (int) Math.round(width + diameter * Math.cos(0)) + tileSize - radius;
			baseY = (int) Math.round(height + diameter * Math.sin(0)) - radius;
			rectangle = new Rectangle(baseX, baseY, tileSize - 2, tileSize - 2);
			board.getRedHomeZone().get(0).setTile(rectangle, Color.RED);

			// Home 2
			x = baseX + (tileSize - 2);
			rectangle = new Rectangle(x, baseY, tileSize - 2, tileSize - 2);
			board.getRedHomeZone().get(1).setTile(rectangle, Color.RED);

			// Home 3
			y = baseY - (tileSize - 2);
			rectangle = new Rectangle(baseX, y, tileSize - 2, tileSize - 2);
			board.getRedHomeZone().get(2).setTile(rectangle, Color.RED);

			// Home 4
			y = baseY + (tileSize - 2);
			rectangle = new Rectangle(baseX, y, tileSize - 2, tileSize - 2);
			board.getRedHomeZone().get(3).setTile(rectangle, Color.RED);


			// Blue Home Tiles
			// Home 1
			t = 2 * Math.PI * 7 / 28;
			baseX = (int) Math.round(width + diameter * Math.cos(t)) - radius;
			baseY = (int) Math.round(height + diameter * Math.sin(t)) + tileSize - radius;
			rectangle = new Rectangle(baseX, baseY, tileSize - 2, tileSize - 2);
			board.getBlueHomeZone().get(0).setTile(rectangle, Color.BLUE);

			// Home 2
			y = baseY + (tileSize - 2);
			rectangle = new Rectangle(baseX, y, tileSize - 2, tileSize - 2);
			board.getBlueHomeZone().get(1).setTile(rectangle, Color.BLUE);

			// Home 3
			x = baseX + (tileSize - 2);
			rectangle = new Rectangle(x, baseY, tileSize - 2, tileSize - 2);
			board.getBlueHomeZone().get(2).setTile(rectangle, Color.BLUE);

			// Home 4
			x = baseX - (tileSize - 2);
			rectangle = new Rectangle(x, baseY, tileSize - 2, tileSize - 2);
			board.getBlueHomeZone().get(3).setTile(rectangle, Color.BLUE);

			// Yellow Home Tiles
			// Home 1
			t = 2 * Math.PI * 14 / 28;
			baseX = (int) Math.round(width + diameter * Math.cos(t)) - tileSize - radius;
			baseY = (int) Math.round(height + diameter * Math.sin(t)) - radius;
			rectangle = new Rectangle(baseX, baseY, tileSize - 2, tileSize - 2);
			board.getYellowHomeZone().get(0).setTile(rectangle, Color.YELLOW);

			// Home 2
			x = baseX - (tileSize - 2);
			rectangle = new Rectangle(x, baseY, tileSize - 2, tileSize - 2);
			board.getYellowHomeZone().get(1).setTile(rectangle, Color.YELLOW);

			// Home 3
			y = baseY + (tileSize - 2);
			rectangle = new Rectangle(baseX, y, tileSize - 2, tileSize - 2);
			board.getYellowHomeZone().get(2).setTile(rectangle, Color.YELLOW);

			// Home 4
			y = baseY - (tileSize - 2);
			rectangle = new Rectangle(baseX, y, tileSize - 2, tileSize - 2);
			board.getYellowHomeZone().get(3).setTile(rectangle, Color.YELLOW);

			// Green Home Tiles
			// Home 1
			t = 2 * Math.PI * 21 / 28;
			baseX = (int) Math.round(width + diameter * Math.cos(t)) - radius;
			baseY = (int) Math.round(height + diameter * Math.sin(t)) - tileSize - radius;
			rectangle = new Rectangle(baseX, baseY, tileSize - 2, tileSize - 2);
			board.getGreenHomeZone().get(0).setTile(rectangle, Color.GREEN);

			// Home 2
			y = baseY - (tileSize - 2);
			rectangle = new Rectangle(baseX, y, tileSize - 2, tileSize - 2);
			board.getGreenHomeZone().get(1).setTile(rectangle, Color.GREEN);

			// Home 3
			x = baseX - (tileSize - 2);
			rectangle = new Rectangle(x, baseY, tileSize - 2, tileSize - 2);
			board.getGreenHomeZone().get(2).setTile(rectangle, Color.GREEN);

			// Home 4
			x = baseX + (tileSize - 2);
			rectangle = new Rectangle(x, baseY, tileSize - 2, tileSize - 2);
			board.getGreenHomeZone().get(3).setTile(rectangle, Color.GREEN);


			// Red End Tiles
			baseX = (int) Math.round(width + diameter * Math.cos(0)) - (tileSize + 2) - radius;
			baseY = (int) Math.round(height + diameter * Math.sin(0)) - radius;

			for (int i = 0; i < 4; i++) {
				rectangle = new Rectangle(baseX - i * (tileSize - 2), baseY, tileSize - 2, tileSize - 2);
				board.getRedEndZone().get(i).setTile(rectangle, Color.RED);
			}

			// Blue End Tiles
			t = 2 * Math.PI * 7 / 28;
			baseX = (int) Math.round(width + diameter * Math.cos(t)) - radius;
			baseY = (int) Math.round(height + diameter * Math.sin(t)) - (tileSize + 2) - radius;

			for (int i = 0; i < 4; i++) {
				rectangle = new Rectangle(baseX, baseY - i * (tileSize - 2), tileSize - 2, tileSize - 2);
				board.getBlueEndZone().get(i).setTile(rectangle, Color.BLUE);
			}

			// Yellow End Tiles
			t = 2 * Math.PI * 14 / 28;
			baseX = (int) Math.round(width + diameter * Math.cos(t)) + (tileSize + 2) - radius;
			baseY = (int) Math.round(height + diameter * Math.sin(t)) - radius;

			for (int i = 0; i < 4; i++) {
				rectangle = new Rectangle(baseX + i * (tileSize - 2), baseY, tileSize - 2, tileSize - 2);
				board.getYellowEndZone().get(i).setTile(rectangle, Color.YELLOW);
			}

			// Green End Tiles
			t = 2 * Math.PI * 21 / 28;
			baseX = (int) Math.round(width + diameter * Math.cos(t)) - radius;
			baseY = (int) Math.round(height + diameter * Math.sin(t)) + (tileSize + 2) - radius;

			for (int i = 0; i < 4; i++) {
				rectangle = new Rectangle(baseX, baseY + i * (tileSize - 2), tileSize - 2, tileSize - 2);
				board.getGreenEndZone().get(i).setTile(rectangle, Color.GREEN);
			}

			/*
			for (int i = 0; i < 28; i++) {
				// sets the location for the normal tiles && home tiles
				int tileSize = 25;
				Rectangle rectangle = new Rectangle(i * tileSize, tileSize, tileSize - 2, tileSize - 2);
				if (i == 0) {
					int count = 0;
					board.getMainZone().get(i).setTile(rectangle, Color.RED);
					for (int j = i; j < i + 4; j++) {
						Rectangle homeRectangle = new Rectangle(j * tileSize, tileSize - tileSize, tileSize - 2, tileSize - 2);
						board.getRedHomeZone().get(count).setTile(homeRectangle, Color.RED);
						count++;
					}
				} else if (i == 7) {
					int count = 0;
					board.getMainZone().get(i).setTile(rectangle, Color.BLUE);
					for (int j = i; j < i + 4; j++) {
						Rectangle homeRectangle = new Rectangle(j * tileSize, tileSize - tileSize, tileSize - 2, tileSize - 2);
						board.getBlueHomeZone().get(count).setTile(homeRectangle, Color.BLUE);
						count++;
					}
				} else if (i == 14) {
					int count = 0;
					board.getMainZone().get(i).setTile(rectangle, Color.YELLOW);
					for (int j = i; j < i + 4; j++) {
						Rectangle homeRectangle = new Rectangle(j * tileSize, tileSize - tileSize, tileSize - 2, tileSize - 2);
						board.getYellowHomeZone().get(count).setTile(homeRectangle, Color.YELLOW);
						count++;
					}
				} else if (i == 21) {
					int count = 0;
					board.getMainZone().get(i).setTile(rectangle, Color.GREEN);
					for (int j = i; j < i + 4; j++) {
						Rectangle homeRectangle = new Rectangle(j * tileSize, tileSize - tileSize, tileSize - 2, tileSize - 2);
						board.getGreenHomeZone().get(count).setTile(homeRectangle, Color.GREEN);
						count++;
					}
				} else {
					board.getMainZone().get(i).setTile(rectangle, Color.LIGHT_GRAY);
				}
				
				if (i == 6) {
					int count = 0;
					for (int j = 2; j < 6; j++) {
						Rectangle endRectangle = new Rectangle(i * tileSize, j * tileSize, tileSize - 2, tileSize - 2);
						board.getBlueEndZone().get(count).setTile(endRectangle, Color.BLUE);
						count++;
					}
				} else if (i == 13) {
					int count = 0;
					for (int j = 2; j < 6; j++) {
						Rectangle endRectangle = new Rectangle(i * tileSize, j * tileSize, tileSize - 2, tileSize - 2);
						board.getYellowEndZone().get(count).setTile(endRectangle, Color.YELLOW);
						count++;
					}
				} else if (i == 20) {
					int count = 0;
					for (int j = 2; j < 6; j++) {
						Rectangle endRectangle = new Rectangle(i * tileSize, j * tileSize, tileSize - 2, tileSize - 2);
						board.getGreenEndZone().get(count).setTile(endRectangle, Color.GREEN);
						count++;
					}
				} else if (i == 27) {
					int count = 0;
					for (int j = 2; j < 6; j++) {
						Rectangle endRectangle = new Rectangle(i * tileSize, j * tileSize, tileSize - 2, tileSize - 2);
						board.getRedEndZone().get(count).setTile(endRectangle, Color.RED);
						count++;
					}
				}
			}*/
		}
		
		/**
		 * Create a list of the tokens
		 */
		public void createTokens() {
			for (Tile token : board.getRedTokens()) {
				Tile tile;
				switch (token.getZone()) {
					case Board.SLOT_HOME:
						tile = board.getRedHomeZone().get(token.getIndex());
						token.setTile(tile.getShape(), Color.RED);
						break;
					case Board.SLOT_MAIN:
						tile = board.getMainZone().get(token.getIndex());
						token.setTile(tile.getShape(), Color.RED);
						break;
					case Board.SLOT_END:
						tile = board.getRedEndZone().get(token.getIndex());
						token.setTile(tile.getShape(), Color.RED);
						break;
				}
			}
			
			for (Tile token : board.getBlueTokens()) {
				Tile tile;
				switch (token.getZone()) {
					case Board.SLOT_HOME:
						tile = board.getBlueHomeZone().get(token.getIndex());
						token.setTile(tile.getShape(), Color.BLUE);
						break;
					case Board.SLOT_MAIN:
						tile = board.getMainZone().get(token.getIndex());
						token.setTile(tile.getShape(), Color.BLUE);
						break;
					case Board.SLOT_END:
						tile = board.getBlueEndZone().get(token.getIndex());
						token.setTile(tile.getShape(), Color.BLUE);
						break;
				}
			}
			
			for (Tile token : board.getYellowTokens()) {
				Tile tile;
				switch (token.getZone()) {
					case Board.SLOT_HOME:
						tile = board.getYellowHomeZone().get(token.getIndex());
						token.setTile(tile.getShape(), Color.YELLOW);
						break;
					case Board.SLOT_MAIN:
						tile = board.getMainZone().get(token.getIndex());
						token.setTile(tile.getShape(), Color.YELLOW);
						break;
					case Board.SLOT_END:
						tile = board.getYellowEndZone().get(token.getIndex());
						token.setTile(tile.getShape(), Color.YELLOW);
						break;
				}
			}
			
			for (Tile token : board.getGreenTokens()) {
				Tile tile;
				switch (token.getZone()) {
					case Board.SLOT_HOME:
						tile = board.getGreenHomeZone().get(token.getIndex());
						token.setTile(tile.getShape(), Color.GREEN);
						break;
					case Board.SLOT_MAIN:
						tile = board.getMainZone().get(token.getIndex());
						token.setTile(tile.getShape(), Color.GREEN);
						break;
					case Board.SLOT_END:
						tile = board.getGreenEndZone().get(token.getIndex());
						token.setTile(tile.getShape(), Color.GREEN);
						break;
				}
			}
			/*
			// Circular Board Tokens
			int width = getWidth() / 2;
			int height = getHeight() / 2;
			int min = Math.min(width, height);
	        int diameter = 4 * min / 5;
	        int radius = Math.abs(min - diameter) / 3;
	        int tileSize = 25;

			double t;
			int x, y, baseX, baseY;
			Rectangle rectangle;

			// Red Home Tiles
			// Home 1
			baseX = (int) Math.round(width + diameter * Math.cos(0)) + tileSize - radius;
			baseY = (int) Math.round(height + diameter * Math.sin(0)) - radius;
			rectangle = new Rectangle(baseX, baseY, tileSize - 2, tileSize - 2);
			board.getRedTokens().get(0).setTile(rectangle, Color.RED);

			// Home 2
			x = baseX + (tileSize - 2);
			rectangle = new Rectangle(x, baseY, tileSize - 2, tileSize - 2);
			board.getRedTokens().get(1).setTile(rectangle, Color.RED);

			// Home 3
			y = baseY - (tileSize - 2);
			rectangle = new Rectangle(baseX, y, tileSize - 2, tileSize - 2);
			board.getRedTokens().get(2).setTile(rectangle, Color.RED);

			// Home 4
			y = baseY + (tileSize - 2);
			rectangle = new Rectangle(baseX, y, tileSize - 2, tileSize - 2);
			board.getRedTokens().get(3).setTile(rectangle, Color.RED);


			// Blue Home Tiles
			// Home 1
			t = 2 * Math.PI * 7 / 28;
			baseX = (int) Math.round(width + diameter * Math.cos(t)) - radius;
			baseY = (int) Math.round(height + diameter * Math.sin(t)) + tileSize - radius;
			rectangle = new Rectangle(baseX, baseY, tileSize - 2, tileSize - 2);
			board.getBlueTokens().get(0).setTile(rectangle, Color.BLUE);

			// Home 2
			y = baseY + (tileSize - 2);
			rectangle = new Rectangle(baseX, y, tileSize - 2, tileSize - 2);
			board.getBlueTokens().get(1).setTile(rectangle, Color.BLUE);

			// Home 3
			x = baseX + (tileSize - 2);
			rectangle = new Rectangle(x, baseY, tileSize - 2, tileSize - 2);
			board.getBlueTokens().get(2).setTile(rectangle, Color.BLUE);

			// Home 4
			x = baseX - (tileSize - 2);
			rectangle = new Rectangle(x, baseY, tileSize - 2, tileSize - 2);
			board.getBlueTokens().get(3).setTile(rectangle, Color.BLUE);

			// Yellow Home Tiles
			// Home 1
			t = 2 * Math.PI * 14 / 28;
			baseX = (int) Math.round(width + diameter * Math.cos(t)) - tileSize - radius;
			baseY = (int) Math.round(diameter + diameter * Math.sin(t)) - radius;
			rectangle = new Rectangle(baseX, baseY, tileSize - 2, tileSize - 2);
			board.getYellowTokens().get(0).setTile(rectangle, Color.YELLOW);

			// Home 2
			x = baseX - (tileSize - 2);
			rectangle = new Rectangle(x, baseY, tileSize - 2, tileSize - 2);
			board.getYellowTokens().get(1).setTile(rectangle, Color.YELLOW);

			// Home 3
			y = baseY + (tileSize - 2);
			rectangle = new Rectangle(baseX, y, tileSize - 2, tileSize - 2);
			board.getYellowTokens().get(2).setTile(rectangle, Color.YELLOW);

			// Home 4
			y = baseY - (tileSize - 2);
			rectangle = new Rectangle(baseX, y, tileSize - 2, tileSize - 2);
			board.getYellowTokens().get(3).setTile(rectangle, Color.YELLOW);

			// Green Home Tiles
			// Home 1
			t = 2 * Math.PI * 21 / 28;
			baseX = (int) Math.round(width + diameter * Math.cos(t)) - radius;
			baseY = (int) Math.round(height + diameter * Math.sin(t)) - tileSize - radius;
			rectangle = new Rectangle(baseX, baseY, tileSize - 2, tileSize - 2);
			board.getGreenTokens().get(0).setTile(rectangle, Color.GREEN);

			// Home 2
			y = baseY - (tileSize - 2);
			rectangle = new Rectangle(baseX, y, tileSize - 2, tileSize - 2);
			board.getGreenTokens().get(1).setTile(rectangle, Color.GREEN);

			// Home 3
			x = baseX - (tileSize - 2);
			rectangle = new Rectangle(x, baseY, tileSize - 2, tileSize - 2);
			board.getGreenTokens().get(2).setTile(rectangle, Color.GREEN);

			// Home 4
			x = baseX + (tileSize - 2);
			rectangle = new Rectangle(x, baseY, tileSize - 2, tileSize - 2);
			board.getGreenTokens().get(3).setTile(rectangle, Color.GREEN);*/

			// Linear Board Tokens

			/*
			for (int i = 0; i < 28; i++) {
				// sets the location for the tokens
				int tileSize = 25;
				if (i == 0) {
					int count = 0;
					for (int j = i; j < i + 4; j++) {
						Rectangle homeRectangle = new Rectangle(j * tileSize, tileSize - tileSize, tileSize - 3, tileSize - 3);
						board.getRedTokens().get(count).setTile(homeRectangle, Color.RED);
						count++;
					}
				} else if (i == 7) {
					int count = 0;
					for (int j = i; j < i + 4; j++) {
						Rectangle homeRectangle = new Rectangle(j * tileSize, tileSize - tileSize, tileSize - 3, tileSize - 3);
						board.getBlueTokens().get(count).setTile(homeRectangle, Color.BLUE);
						count++;
					}
				} else if (i == 14) {
					int count = 0;
					for (int j = i; j < i + 4; j++) {
						Rectangle homeRectangle = new Rectangle(j * tileSize, tileSize - tileSize, tileSize - 3, tileSize - 3);
						board.getYellowTokens().get(count).setTile(homeRectangle, Color.YELLOW);
						count++;
					}
				} else if (i == 21) {
					int count = 0;
					for (int j = i; j < i + 4; j++) {
						Rectangle homeRectangle = new Rectangle(j * tileSize, tileSize - tileSize, tileSize - 3, tileSize - 3);
						board.getGreenTokens().get(count).setTile(homeRectangle, Color.GREEN);
						count++;
					}
				}
			}
			*/
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			createTiles();
			createTokens();
			
			// Draw mainZone tiles
			for (Tile tile : board.getMainZone()) {
				if (tile.getColor() == Color.RED)
					g2d.setColor(TRANSPARENT_RED);
				else if (tile.getColor() == Color.BLUE)
					g2d.setColor(TRANSPARENT_BLUE);
				else if (tile.getColor() == Color.YELLOW)
					g2d.setColor(TRANSPARENT_YELLOW);
				else if (tile.getColor() == Color.GREEN)
					g2d.setColor(TRANSPARENT_GREEN);
				else
					g2d.setColor(Color.LIGHT_GRAY);
				g2d.draw(tile.getShape());
			}


			// Draw homeZone tiles
			for (Tile tile : board.getRedHomeZone()) {
				g2d.setColor(TRANSPARENT_RED);
				g2d.draw(tile.getShape());
			}
			for (Tile tile : board.getBlueHomeZone()) {
				g2d.setColor(TRANSPARENT_BLUE);
				g2d.draw(tile.getShape());
			}
			for (Tile tile : board.getYellowHomeZone()) {
				g2d.setColor(TRANSPARENT_YELLOW);
				g2d.draw(tile.getShape());
			}
			for (Tile tile : board.getGreenHomeZone()) {
				g2d.setColor(TRANSPARENT_GREEN);
				g2d.draw(tile.getShape());
			}

			// Draw endZone tiles
			for (Tile tile : board.getRedEndZone()) {
				g2d.setColor(TRANSPARENT_RED);
				g2d.draw(tile.getShape());
			}
			for (Tile tile : board.getBlueEndZone()) {
				g2d.setColor(TRANSPARENT_BLUE);
				g2d.draw(tile.getShape());
			}
			for (Tile tile : board.getYellowEndZone()) {
				g2d.setColor(TRANSPARENT_YELLOW);
				g2d.draw(tile.getShape());
			}
			for (Tile tile : board.getGreenEndZone()) {
				g2d.setColor(TRANSPARENT_GREEN);
				g2d.draw(tile.getShape());
			}


			// Draw tokens
			for (Tile token : board.getRedTokens()) {
				g2d.setColor(token.getColor());
				if (swingUI.getUser().isSelectedTile(token))
					g2d.setColor(token.getColor());
				else {
					if (token.getColor() == Color.RED)
						g2d.setColor(TRANSPARENT_RED);
					else if (token.getColor() == Color.BLUE)
						g2d.setColor(TRANSPARENT_BLUE);
					else if (token.getColor() == Color.GREEN)
						g2d.setColor(TRANSPARENT_GREEN);
					else if (token.getColor() == Color.YELLOW)
						g2d.setColor(TRANSPARENT_YELLOW);
				}
				g2d.fill(token.getShape());
			}
			for (Tile token : board.getBlueTokens()) {
				g2d.setColor(token.getColor());
				if (swingUI.getUser().isSelectedTile(token))
					g2d.setColor(token.getColor());
				else {
					if (token.getColor() == Color.RED)
						g2d.setColor(TRANSPARENT_RED);
					else if (token.getColor() == Color.BLUE)
						g2d.setColor(TRANSPARENT_BLUE);
					else if (token.getColor() == Color.GREEN)
						g2d.setColor(TRANSPARENT_GREEN);
					else if (token.getColor() == Color.YELLOW)
						g2d.setColor(TRANSPARENT_YELLOW);
				}
				g2d.fill(token.getShape());
			}
			for (Tile token : board.getYellowTokens()) {
				g2d.setColor(token.getColor());
				if (swingUI.getUser().isSelectedTile(token))
					g2d.setColor(token.getColor());
				else {
					if (token.getColor() == Color.RED)
						g2d.setColor(TRANSPARENT_RED);
					else if (token.getColor() == Color.BLUE)
						g2d.setColor(TRANSPARENT_BLUE);
					else if (token.getColor() == Color.GREEN)
						g2d.setColor(TRANSPARENT_GREEN);
					else if (token.getColor() == Color.YELLOW)
						g2d.setColor(TRANSPARENT_YELLOW);
				}
				g2d.fill(token.getShape());
			}
			for (Tile token : board.getGreenTokens()) {
				g2d.setColor(token.getColor());
				if (swingUI.getUser().isSelectedTile(token))
					g2d.setColor(token.getColor());
				else {
					if (token.getColor() == Color.RED)
						g2d.setColor(TRANSPARENT_RED);
					else if (token.getColor() == Color.BLUE)
						g2d.setColor(TRANSPARENT_BLUE);
					else if (token.getColor() == Color.GREEN)
						g2d.setColor(TRANSPARENT_GREEN);
					else if (token.getColor() == Color.YELLOW)
						g2d.setColor(TRANSPARENT_YELLOW);
				}
				g2d.fill(token.getShape());
			}

			g2d.dispose();
		}
	}
}
