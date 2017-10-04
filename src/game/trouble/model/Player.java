package game.trouble.model;

import java.util.Arrays;

import game.trouble.model.board.Token;

public class Player {
	
	public static final int NUM_TOKENS = 4;
	public static final int HUMAN = 0;
	public static final int BOT = 1;
	
	private String username;
	private int id;
	private Colour colour;
	private Token[] tokens;
	private int type;
	
	public Player(int pid, String username, Colour colour, int type) {
		this.id = pid;
		this.username = username;
		this.colour = colour;
		this.type = type;
		createPlayerTokens();
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setColour(Colour col) {
		this.colour = col;
	}
	
	public Colour getColour() {
		return this.colour;
	}
	
	/**
	 * Generates 4 tokens for the current player with ids relative to their 
	 * position in tokens[] and owner equal to the current player
	 */
	private void createPlayerTokens() {
		if(tokens == null) {
			tokens = new Token[NUM_TOKENS];
			
			for(int i = 0; i < NUM_TOKENS; i++) {
				tokens[i] = new Token(i, this);
			}
		}
	}
	
	/**
	 * Get all tokens managed by this player
	 * @return Token array containing all of this players tokens
	 */
	public Token[] getPlayerTokens() {
		return tokens;
	}
	
	/**
	 * Get a specific token belonging to this player
	 * @param tokenId The id of the token to get
	 * @return The token with the specified id
	 */
	public Token getToken(int tokenId) {
		if(tokenId < 0 || tokenId >= tokens.length) {
			return null;
		} else {
			return tokens[tokenId];
		}
	}
	
	/**
	 * @return player type either player.BOT or player.HUMAN
	 */
	public int getType() {
		return this.type;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Player) {
			if (((Player)o).getColour() == this.getColour() ){
	            return true;
	        }
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + colour.hashCode();
		result = prime * result + id;
		result = prime * result + Arrays.hashCode(tokens);
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}
	
}
