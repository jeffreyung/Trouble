package game.trouble;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import game.trouble.model.Colour;
import game.trouble.network.Connection;
import game.trouble.model.Player;

// gameengine will call methods from board to manipulate game.
public class GameEngine {
	
	private ArrayList<Connection> gameConn;
	private Game g;
	private boolean allPlayersConnected;
	private Queue<String> inputQueue;
	
	public GameEngine() {
		System.out.println("[GameEngine] Initializing game engine...");
		gameConn = new ArrayList<Connection>();
		allPlayersConnected = false;
		inputQueue = new LinkedList<String>();
	}
	
	public void init() {
		g = new Game(this);
	}
	
	public void testGame() {
		g.join("test1", Colour.BLUE, true);
		g.join("test2", Colour.YELLOW, true);
		g.join("test3", Colour.GREEN, true);
		g.start();
		g.showPlayers();
		
		for (Connection c : gameConn) {
			c.getOutputStream().println("START_GAME");
			updateMessages();
		}
	}
	
	public void testGame2() {
		g.start();
		g.showPlayers();
		
		for (Connection c : gameConn) {
			c.getOutputStream().println("START_GAME");
			updateMessages();
		}
	}

	public void add(Connection c) {
		gameConn.add(c);
		switch (gameConn.size()) {
			case 1:
				g.join(c.getUsername(), Colour.RED, false);
				c.getOutputStream().println("COLORS " + c.getUsername() + " " + "red");
				break;
			case 2:
				g.join(c.getUsername(), Colour.BLUE, false);
				c.getOutputStream().println("COLORS " + c.getUsername() + " " + "blue");
				break;
			case 3:
				g.join(c.getUsername(), Colour.YELLOW, false);
				c.getOutputStream().println("COLORS " + c.getUsername() + " " + "yellow");
				break;
			case 4:
				g.join(c.getUsername(), Colour.GREEN, false);
				c.getOutputStream().println("COLORS " + c.getUsername() + " " + "green");
				break;
			default:
		}
		
		// test game, game only starts if single player's name is bob
		if (c.getUsername().equalsIgnoreCase("bob"))
			testGame();
		if (gameConn.size() == 4) {
			testGame2();
		}
	}
	
	// process runs the game
	public void process() {
		
		// not processing game if not all players connected or game has not started
		if (!g.isStarted()) {
			return;
		} else if (!allPlayersConnected) {
			checkPlayerConnections();	
			return;
		}
		
		if (!g.isOver()) {
			Player curr = g.getWhoseTurn();
			//if (g.getHumanPlayers().contains(curr.getUsername())) {
				int playerID = curr.getID();
				Connection clientConn = getConnection(curr.getUsername());
				PrintWriter clientOutput = clientConn.getOutputStream();
				
				// process his moves 
				while (!inputQueue.isEmpty()) {
					String in = inputQueue.poll();
					
					// die rolls
					if (in.startsWith("ROLLED")) {	
						String[] input = in.split("\\s+");
						int tokenID = Integer.parseInt(input[1]);
						System.out.println("rolling token ID: "+tokenID);
						
						int roll = g.rollDie();
						clientOutput.println(g.movePlayerToken(playerID, tokenID, roll));
						// ROLLED <roll> <tokenID> <username>
						//clientOutput.println("ROLLED " + roll + " " + tokenID + " " + curr.getUsername());
					}
				}
			//} else {
				// HANDLE AI HERE
			//}
		}
	}
	
	/**
	 * Sends a message to all client on whose move.
	 */
	public void updateMessages() {
		for (Connection clientConn : gameConn)
			clientConn.getOutputStream().println("TURN " + g.getWhoseTurn().getUsername());
	}
	
	/**
	 * Filter the input and add it to the queue so process will handle it later.
	 * @param c
	 * @param input
	 */
	public void handleInput(Connection c, String input) {
		// ignore the input if it's not user's turn
		if(g.isStarted()) {
			Player curr = g.getWhoseTurn();
			
			if (c.getUsername().equals(curr.getUsername())) {
				inputQueue.add(input);
			}
		}
		
	}
	
	/**
	 * Gets the connection associated with the username
	 * @param username
	 * @return Connection or null if no such connection
	 */
	private Connection getConnection(String username) {
		for (Connection c: gameConn) {
			if (c.getUsername().equals(username)) return c;
		}
		return null;
	}
	
	// checks all if all human players have a connection, if it does, sets this.allPlayersConnected to true
	private void checkPlayerConnections() {
		ArrayList<Player> humanPlayers = g.getHumanPlayers();
		for (Player p: humanPlayers) {
			if (!hasConnection(p.getUsername())) return;
		}
		allPlayersConnected = true;
	}
	
	// given a username, checks if connection object has been established.
	private boolean hasConnection(String username) {
		for (Connection c: gameConn) {
			if (c.getUsername().equals(username)) return true;
		}
		return false;
	}
	
	
}
