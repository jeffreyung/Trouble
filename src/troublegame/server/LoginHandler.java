package troublegame.server;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import troublegame.server.io.FileHandler;

/**
 * 
 * A connection that has been entered in the server will be handled
 * in this class.
 * 
 * @author Jeffrey Ung
 * 
 */
public class LoginHandler {

	/**
	 * The login queue.
	 */
	private Queue<Connection> loginQueue;
	
	/**
	 * The game engine.
	 */
	private GameEngine gameEngine;
	
	/**
	 * Constructs a new login handler.
	 */
	public LoginHandler(GameEngine gameEngine) {
		this.gameEngine = gameEngine;
		this.loginQueue = new LinkedList<Connection>();
	}
	
	/**
	 * Adds a connection to the login queue.
	 * @param connection being added to the queue.
	 */
	public void addConnectionToQueue(Connection connection) {
		loginQueue.add(connection);
	}
	
	/**
	 * @param player is the user connecting the server.
	 * @return true if the player is new or has the correct credential and
	 * 			false if the credential is invalid
	 */
	public boolean login(Connection connection) {
		/**
		 * if (invalid password then)
		 * 		return false
		 */
		FileHandler fileHandler = new FileHandler(new File("./data/users/" + connection.getUsername()));
		FileHandler.files.put(connection, fileHandler);
		if (fileHandler.load(connection) && !connection.getPassword().equals(fileHandler.get("password"))) {
			connection.getOutputStream().println("INVALID"); // invalid username or password
			return false;
		}
		connection.load(fileHandler);
		this.gameEngine.add(connection);
		connection.getOutputStream().println("SUCCESS"); 
		fileHandler.save(connection); // temporary
		return true;
	}
	
	/**
	 * The process method runs in an infinite loop until
	 * the server stops.
	 */
	public void process() {
		if (loginQueue.isEmpty())
			return;
		login(loginQueue.poll());
	}
	
}
