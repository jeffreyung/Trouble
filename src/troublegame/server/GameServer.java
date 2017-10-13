package troublegame.server;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * The main class for the trouble game server.
 *
 */
public class GameServer {
	
	private static final int TIME_PERIOD = 500;
	
	/**
	 * The port.
	 */
	public final static int PORT = 4321;
	
	/**
	 * The main function.
	 * @param args are the arguments.
	 */
	public static void main(String args[]) {
		GameServer GS = new GameServer();
		Runner gameRunner = GS.new Runner();
		Timer serverTimer = new Timer();
		
		serverTimer.schedule(gameRunner, 0, TIME_PERIOD);
	}
	
	/**
	 * The socket listener.
	 */
	private SocketListener socketListener;
	
	/**
	 * The game engine.
	 */
	private GameEngine gameEngine;
	
	/**
	 * The login handler.
	 */
	private LoginHandler loginHandler;
	
	/**
	 * The lobby.
	 */
	private Lobby lobby;
	
	/**
	 * Constructs a new server.
	 */
	public GameServer() {
		System.out.println("[Game Server] Starting game server...");
		
		/**
		 * Initializes the game engine.
		 */
		this.gameEngine = new GameEngine();
		this.gameEngine.init();
		
		this.lobby = new Lobby(this);
		
		/**
		 * Initializes the socket listener and login handler.
		 */
		this.socketListener = new SocketListener(GameServer.PORT);
		this.loginHandler = new LoginHandler(this);
		this.lobby = new Lobby(this);
		this.socketListener.setLoginHandler(loginHandler);
		this.socketListener.setLobby(lobby);
		this.socketListener.init();
		
		/**
		 * Add the gameengine to the socketListener (temporary)
		 */
		this.socketListener.addGameEngine(this.gameEngine);
	}
	
	public void login(Connection user) {
		lobby.addUser(user);
	}
	
	/*
	 * finds which game room the user is in and returns the name of the game room
	 */
	public GameRoom getGameRoomName(Connection user) {
		ArrayList<GameRoom> gameRooms = lobby.getGameRooms();
		for (GameRoom g: gameRooms) {
			if (g.isMember(user)) {
				return g;
			}
		}
		return null;
	}
	
	/**
	 * @return the socket listener.
	 */
	public SocketListener getSocketListener() {
		return socketListener;
	}
	
	public Lobby getLobby() {
		return this.lobby;
	}
	
	class Runner extends TimerTask {
		public void run() {
			gameEngine.process();
			loginHandler.process();
		}
	}
}

