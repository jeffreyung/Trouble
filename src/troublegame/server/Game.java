package troublegame.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import troublegame.communication.CommunicationHandler;

public class Game {
	
	public static final int MAX_PLAYERS = 4;
	
	private GameEngine engine;
	private Board board;
	private Player[] players;
	private Calendar startTime;
	private ArrayList<Color> availableColours;
	private int turnNum;
	private boolean started;
	private Map<Color, String> humans;
	private Map<Color, String> computers;
	private int tick;
	private ArrayList<String> aiNames;
	
	public Game(GameEngine engine) {
		this.tick = 0;
		this.engine = engine;
		setAvailableColours();
		genAiNames();
		humans = new HashMap<Color, String>();
		computers = new HashMap<Color, String>();
		started = false;
	}
	
	public void start() {
		
		startTime = Calendar.getInstance();
		players = new Player[Game.MAX_PLAYERS];
		int i = 0;
		// make the humans players
		for (Map.Entry<Color, String> entry : humans.entrySet()) {
			Color c = entry.getKey();
		    String name = entry.getValue();
			players[i] = createHumanPlayer(i, c, name);
			i++;
		}
		// make the AI players
		for (Map.Entry<Color, String> entry : computers.entrySet()) {
			Color c = entry.getKey();
		    String name = entry.getValue();
			players[i] = createAIPlayer(i, c, name);
			i++;
		}
		board = new Board(players);
		this.started = true;
		
		for (Player p : players) {
			String col = "";
			switch (p.getColour()) {
				case RED:
					col = "red";
					break;
				case BLUE:
					col = "blue";
					break;
				case YELLOW:
					col = "yellow";
					break;
				default: 
					col = "green";
			}
			engine.broadcast(this, CommunicationHandler.GAME_COLORS + " " + p.getUsername() + " " + col);
		}
		
	}
	
	public void join(String username, Color color, boolean computer) {
		if (computer) {
			computers.put(color, username);
			return;
		}
		humans.put(color, username);
	}
	
	/**
	 * Sets every variable to null.
	 */
	public void destruct() {
		this.players = null;
		this.board = null;
		this.started = false;
		this.humans = null;
		this.computers = null;
	}
	
	/**
	 * Create a humans player with a random colour, the given id and the name Mr noName.
	 * @param id The index of the array in which the player sits. Will be used as player id
	 * @return A newly created player
	 */
	public Player createHumanPlayer(int id, Color c, String username) {
		
		Player tmp = new Player(id, username, c, Player.HUMAN);
		return tmp;
	}
	
	/**
	 * Creates a new AI
	 * @return The newly created AI
	 */
	public AI createAIPlayer(int id, Color c, String username) {
		AI tmp = new AI(id, username, c);
		return tmp;
	}
	
	/**
	 * Add the four playable colours to the list of available colours
	 */
	public void setAvailableColours() {
		availableColours = new ArrayList<Color>();
		availableColours.add(Color.RED);
		availableColours.add(Color.BLUE);
		availableColours.add(Color.GREEN);
		availableColours.add(Color.YELLOW);
	}
	
	/**
	 * If player wants a random colour give out random colour from available colours otherwise assign player requested colour
	 * @param color Colour the player has requested
	 * @return The next available colour
	 */
	public Color assignPlayerColour(Color color) {
		
		if(color == Color.RANDOM || availableColours.contains(color) == false) {
			int rand = new Random().nextInt(availableColours.size());
			color = availableColours.get(rand);
		}
		
		availableColours.remove(color);
		return color;
	}
	
	/**
	 * Applies a roll to a player's token given the playerID, tokenID and the roll value
	 * @param playerID
	 * @param tokenID
	 * @param diceValue
	 */
	public String movePlayerToken(int playerID, int tokenID) {
		String command = null;
		Player p = players[playerID];
		Token token = p.getToken(tokenID);
		Slot currentSlot = board.getTokenLoc(token);
		Color col = token.getColour();
		int currPos = -1;
		int target = -1;
		int startIndex = -1;
		int diceValue = board.getDie().getLastRolledValue();
		// first we check if token is already in play
		switch (currentSlot.getSlotZone()) {
			case Board.SLOT_HOME:
				// sorry can't move
				// Was 6, changed to -1 to allow all rolls
				if (diceValue == -1) {
					command = CommunicationHandler.GAME_ROLL_FAIL + " " + diceValue;
					engine.updateTurns(this);
				} else {
					startIndex = board.getStartIndex(col);
					if (board.getSlot(startIndex).isOccupied()) {
						Token tokenToEat = board.getSlot(startIndex).getOccupyingToken();
						Player owner = tokenToEat.getOwner();
						board.setTokenLoc(tokenToEat, Board.SLOT_HOME, tokenToEat.getTokenID());
						engine.broadcast(this, CommunicationHandler.GAME_EAT_TOKEN + " " + tokenToEat.getTokenID() + " " + owner.getUsername() + " " + Board.SLOT_HOME);
					}
					
					command = CommunicationHandler.GAME_ROLL_AGAIN + " " + diceValue + " " + tokenID + " " + p.getUsername() + " " + Board.SLOT_MAIN + " " + startIndex;
					board.setTokenLoc(token, Board.SLOT_MAIN, startIndex);
					turnNum--;
				}
				break;
			case Board.SLOT_MAIN:
				startIndex = board.getStartIndex(col);
				int endIndex = board.getEndIndex(col);
				int currZone = Board.SLOT_MAIN;
				currPos = currentSlot.getSlotIndex();	
				if (currPos == endIndex) { // move into endzone
					switch (diceValue) {
						case 1: 
							if (board.getSlot(diceValue - 1, currZone, col).getOccupyingToken() == null) {
								command = CommunicationHandler.GAME_ROLL_SUCCESS + " " + diceValue + " " + tokenID + " " + p.getUsername() + " " + Board.SLOT_END + " " + 0;
								board.setTokenLoc(token, Board.SLOT_END, 0);
							} else {
								command = CommunicationHandler.GAME_ROLL_FAIL + " " + diceValue + " , token already occupying end slot";
							}
							break;
						case 2:
							if (board.getSlot(diceValue - 1, currZone, col).getOccupyingToken() == null) {
								command = CommunicationHandler.GAME_ROLL_SUCCESS + " " + diceValue + " " + tokenID + " " + p.getUsername() + " " + Board.SLOT_END + " " + 1;
								board.setTokenLoc(token, Board.SLOT_END, 1);
							} else {
								command = CommunicationHandler.GAME_ROLL_FAIL + " " + diceValue + " , token already occupying end slot";
							}
							break;
						case 3:
							if (board.getSlot(diceValue -1, currZone, col).getOccupyingToken() == null) {
								command = CommunicationHandler.GAME_ROLL_SUCCESS + " " + diceValue + " " + tokenID + " " + p.getUsername() + " " + Board.SLOT_END + " " + 2;
								board.setTokenLoc(token, Board.SLOT_END, 2);
							} else {
								command = CommunicationHandler.GAME_ROLL_FAIL + " " + diceValue + " , token already occupying end slot";
							}
							break;
						case 4:
							if (board.getSlot(diceValue - 1, currZone, col).getOccupyingToken() == null) {
								command = CommunicationHandler.GAME_ROLL_SUCCESS + " " + diceValue + " " + tokenID + " " + p.getUsername() + " " + Board.SLOT_END + " " + 3;
								board.setTokenLoc(token, Board.SLOT_END, 3);
							} else {
								command = CommunicationHandler.GAME_ROLL_FAIL + " " + diceValue + " , token already occupying end slot";
							}
							break;
						default:
							command = CommunicationHandler.GAME_ROLL_FAIL + " " + diceValue + " , must roll a value of 1-4 to enter the end zone";
					}
				} else { // keep moving along mainzone
					target = currPos + diceValue;
					if (startIndex < endIndex) { // ONLY TRUE FOR RED
						if (target > endIndex) target = endIndex;
					} else { // EVERY OTHER COLOUR
						if (target > (Board.NUM_MAIN_SLOTS - 1)) target = target % Board.NUM_MAIN_SLOTS;
						if (currPos < endIndex) {
							if (target > endIndex) target = endIndex;
						}
					}
					if (board.getSlot(target).isOccupied()) {
						Token tokenToEat = board.getSlot(target).getOccupyingToken();
						Player owner = tokenToEat.getOwner();
						board.setTokenLoc(tokenToEat, Board.SLOT_HOME, tokenToEat.getTokenID());
						engine.broadcast(this, CommunicationHandler.GAME_EAT_TOKEN + " " + tokenToEat.getTokenID() + " " + owner.getUsername() + " " + Board.SLOT_HOME);
					}
				}
				
				break;
		}
		
		// actually do the move
		if (target != -1) {
			command = CommunicationHandler.GAME_ROLL + " " + diceValue + " " + tokenID + " " + p.getUsername() + " " + Board.SLOT_MAIN + " " + target;
			board.setTokenLoc(token, Board.SLOT_MAIN, target);
		}
		
		turnNum++;
		engine.updateTurns(this);
		
		return command;
	}
	
	// uses turn number to determine who's turn it is, then returns the player object
	// by using the store players - this means players must always be ordered red blue yellow green as 0-3
	public Player getWhoseTurn() {
		int turnID = turnNum % Game.MAX_PLAYERS;
		return players[turnID];
	}
	
	// checks if the game is over by looking at all the player's home slots
	public boolean isOver() {
		if (!started)
			return false;
		for (Player p: players) {
			ArrayList<Slot> homeslot = board.getPlayerEndZone(p.getColour());
			int filledSlots = 0;
			for (Slot s: homeslot) {
				if (s.isOccupied()) filledSlots++;
			}
			if (filledSlots == Player.NUM_TOKENS) return true;
		}
		return false;
	}
	
	/**
	 * If game is over, returns the winner, else returns null
	 * @return
	 */
	public Player getWinner() {
		for (Player p: players) {
			ArrayList<Slot> homeslot = board.getPlayerEndZone(p.getColour());
			int filledSlots = 0;
			for (Slot s: homeslot) {
				if (s.isOccupied()) filledSlots++;
			}
			if (filledSlots == Player.NUM_TOKENS) return p;
		}
		return null;
	}
		
	public int getTurnNum() {
		return this.turnNum;
	}
	
	public String getStartTimeMessage() {
		
		String message = "No game started";
		
		if(startTime != null) {
			
			String date = new SimpleDateFormat("EEEE, dd/MM/yyyy").format(startTime.getTime());
			String time = new SimpleDateFormat("hh:mm:ss.SSS a").format(startTime.getTime());
			message = CommunicationHandler.GAME_INFO + " Game was started on " + date + " at " + time;
			 
		}
		
		return message;
		
	}
	
	// returns an arraylist containing all human players
	public ArrayList<Player> getHumanPlayers() {
		ArrayList<Player> human = new ArrayList<Player>();
		for(int i =0; i<players.length; i++) {
			if (players[i].getType() == Player.HUMAN)
				human.add(players[i]);
		}
		
		return human;
	}
	
	
	/**
	 * Creates a name pool for ais to draw names from
	 */
	public void genAiNames() {
		aiNames = new ArrayList<>();
		aiNames.add("Aseihar");
		aiNames.add("Adrarelind");
		aiNames.add("Galeish");
		aiNames.add("Ocigoron");
		aiNames.add("Falian");
		aiNames.add("Mireidric");
		aiNames.add("Faeri");
		aiNames.add("Etohaw");
		aiNames.add("Cadelalith");
		aiNames.add("Tulian");
		aiNames.add("Haelannor");
		aiNames.add("Jeroameth");
	}
	
	/**
	 * @return A random name for the ai player
	 */
	public String getRandomAiName() {
		
		int index = new Random().nextInt(aiNames.size() - 1);
		if (index < 0) index = 0;
		String aiName = aiNames.get(index);
		aiNames.remove(index);
		return aiName;
		
	}
	
	public void showPlayers() {
		System.out.print(CommunicationHandler.GAME_INFO + " Players: ");
		for(int i = 0; i < Game.MAX_PLAYERS; i++) {
			System.out.print(players[i].getUsername()+" ");
		}
		System.out.print("\n");
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public int rollDie() {
		return board.getDie().rollDie();
	}
	
	public Board getBoard() {
		return board;
	}
	
	public void incrementTurn() {
		turnNum++;
	}

	public int getTick() {
		return tick;
	}

	public void setTick(int tick) {
		this.tick = tick;
	}
	
}