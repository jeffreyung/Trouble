package troublegame.client;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import troublegame.client.model.User;
import troublegame.client.panels.GamePanel;
import troublegame.client.panels.GameRoomPanel;
import troublegame.client.panels.LobbyPanel;
import troublegame.client.panels.RegisterPanel;
import troublegame.communication.CommunicationHandler;

public class GameClient {

	/**
	 * The IP Address.
	 */
	public final static String IP_ADDRESS = "127.0.0.1";
	
	/**
	 * The port.
	 */
	public final static int port = 4321;
	
	/**
	 * The buffered reader.
	 */
	private BufferedReader in;
	
	/**
	 * The print writer.
	 */
    private PrintWriter out;
	
    /**
     * The socket.
     */
    private Socket socket;
    
	public static void main(String[] args) {		
		new GameClient(GameClient.IP_ADDRESS, GameClient.port);
	}
	
	/**
	 * Constructs a new game client.
	 * @param ip is the IP address.
	 * @param port is the port
	 */
	public GameClient(String ip, int port) {
		this.socket = null;
		try {
			socket = new Socket(ip, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    out = new PrintWriter(socket.getOutputStream(), true);
			SwingUI ui = new SwingUI(this, in , out);
			
			ui.setVisible(true);
		    while (true) {
		    	
		    	String input = in.readLine();
		    	System.out.println("Server returned:" + input);
	    		
		    	String[] inputSplit = input.split(" ");
		    	if (input.equals(CommunicationHandler.LOGOUT_SUCCESS)) {
		    		this.socket.close();
		    		this.restart();
		    		return;
		    	}
		    	switch (ui.getInterface()) {
		    		case START:
		    			break;
		    		case LOGIN:
		    			if (input.startsWith(CommunicationHandler.LOGIN_SUCCESS)) {
		    				String username = input.substring(CommunicationHandler.LOGIN_SUCCESS.length() + 1);
		    				ui.setUser(new User(username));
		    				ui.setInterface(Interface.LOBBY);
				    	} else if (input.startsWith(CommunicationHandler.LOGIN_ERROR)) {
				    		String errorMsg = input.substring(14);
				    		JOptionPane.showMessageDialog(null, errorMsg, "Please Try again", JOptionPane.PLAIN_MESSAGE);
				    	}
		    			break;
		    		case LOBBY:
		    			String[] lobbySplit = input.split("] ");
		    			LobbyPanel lobbyPanel = (LobbyPanel) ui.getCurrentPanel();
		    			if (input.startsWith(CommunicationHandler.GAME_ROOM_OPEN)) {
		    				lobbyPanel.addGameRoom(lobbySplit[1]);
		    			} else if (input.startsWith(CommunicationHandler.GAME_ROOM_NEW)) {
		    				ui.setInterface(Interface.PARTY);
		    				// query for game room name
		    				ui.send(CommunicationHandler.GAME_ROOM_INFO);
		    				lobbyPanel.clearGameRooms();
		    			} else if (input.startsWith(CommunicationHandler.GAME_ROOM_JOIN)) {
		    				ui.setInterface(Interface.PARTY);
		    				// query for game room name
		    				ui.send(CommunicationHandler.GAME_ROOM_INFO);
		    				lobbyPanel.clearGameRooms();
		    			} else if (input.startsWith(CommunicationHandler.GAME_ROOM_CLOSE)) {
		    				lobbyPanel.removeGameRoom(lobbySplit[1]);
		    			}
		    			break;
		    		case IN_GAME:
		    			GamePanel gamePanel = (GamePanel) ui.getCurrentPanel();
		    			if (input.equals(CommunicationHandler.GAME_START)) {
		    				gamePanel.setupPanel();
				    	} else if (input.startsWith(CommunicationHandler.GAME_COLORS)) {
				    		gamePanel.getPlayers().put(inputSplit[1], inputSplit[2]);
				    	} else if (input.startsWith(CommunicationHandler.GAME_ROLL)) {
				    		gamePanel.sendChatMessage(inputSplit[3] + " rolled a " + inputSplit[1]);
				    		gamePanel.updateToken(inputSplit[3], Integer.parseInt(inputSplit[2]), Integer.parseInt(inputSplit[4]), Integer.parseInt(inputSplit[5]));
				    	} else if (input.startsWith(CommunicationHandler.GAME_ROLL_AGAIN)) {
				    		gamePanel.sendChatMessage("You rolled a " + inputSplit[1] + ". Roll again to move.");
				    		gamePanel.updateToken(inputSplit[3], Integer.parseInt(inputSplit[2]), Integer.parseInt(inputSplit[4]), Integer.parseInt(inputSplit[5]));
				    	} else if (input.startsWith(CommunicationHandler.GAME_ROLL_SUCCESS)) {
				    		gamePanel.sendChatMessage("You rolled a " + inputSplit[1] + ". Moving your token into the end zone!.");
				    		gamePanel.updateToken(inputSplit[3], Integer.parseInt(inputSplit[2]), Integer.parseInt(inputSplit[4]), Integer.parseInt(inputSplit[5]));
				    	} else if (input.startsWith(CommunicationHandler.GAME_ROLL_FAIL)) {
				    		gamePanel.sendChatMessage("You rolled a " + inputSplit[1] + ". Unable to move.");
				    	} else if (input.startsWith(CommunicationHandler.GAME_EAT_TOKEN)) {
				    		gamePanel.updateToken(inputSplit[2], Integer.parseInt(inputSplit[1]), Integer.parseInt(inputSplit[3]), Integer.parseInt(inputSplit[1]));
				    	} else if (input.startsWith(CommunicationHandler.GAME_TURN)) {
				    		if (ui.getUser().getUsername().equals(inputSplit[1])) {
				    			gamePanel.updateMessage("Your turn.", 1);
				    		} else {
				    			gamePanel.updateMessage(inputSplit[1] + "'s turn.", 1);
				    		}
		    			} else if (input.startsWith(CommunicationHandler.GAME_CHAT)) {
		    				String chatMessage = input.substring(CommunicationHandler.GAME_CHAT.length());
		    				ui.pushGameChat(chatMessage);
		    			}
		    			break;
		    		case PARTY:
		    			GameRoomPanel gameRoomPanel = (GameRoomPanel) ui.getCurrentPanel();
		    			if (input.startsWith(CommunicationHandler.GAME_ROOM_MEMBER)) {
		    				gameRoomPanel.addUser(inputSplit[1]);
		    			} else if (input.startsWith(CommunicationHandler.GAME_ROOM_JOIN)) {
		    				gameRoomPanel.addUser(inputSplit[1]);
		    			} else if (input.equals(CommunicationHandler.GAME_SETUP)) {
		    				ui.setInterface(Interface.IN_GAME);
		    			} else if (input.startsWith(CommunicationHandler.GAME_ROOM_INFO)) {
		    				String name = input.substring(16);
		    				name = name.trim();
		    				ui.setGameRoomName(name);	
		    			} else if (input.startsWith(CommunicationHandler.GAME_ROOM_CHAT)) {
		    				String chatMessage = input.substring(CommunicationHandler.GAME_ROOM_CHAT.length());
		    				ui.pushChat(chatMessage);
		    			} else if (input.equals(CommunicationHandler.GAME_ROOM_LEAVE)) {
		    				ui.setInterface(Interface.LOBBY);
		    				gameRoomPanel.clearUsers();
		    			} else if (input.startsWith(CommunicationHandler.GAME_ROOM_LEAVE)) {
		    				gameRoomPanel.removeUser(inputSplit[1]);
		    			}  
		    			break;
		    		case USER_PROFILE:
		    			if (input.startsWith(CommunicationHandler.UPDATE_SUCCESS)) {
		    				String username = input.substring(CommunicationHandler.UPDATE_SUCCESS.length() + 1);
		    				ui.setUser(new User(username));
		    				JOptionPane.showMessageDialog(null, "Your details have been updated");
		    			} else if (input.startsWith(CommunicationHandler.UPDATE_FAIL)) {
		    				JOptionPane.showMessageDialog(null, "Error updating details");
		    			}
		    		case SIGN_UP:
		    			RegisterPanel registerPanel = (RegisterPanel) ui.getCurrentPanel();
		    			if (input.equals(CommunicationHandler.REGISTER_SUCCESS)) {
		    				ui.setInterface(Interface.LOGIN);
		    				JOptionPane.showMessageDialog(null, "Account has been successfully created.");
		    			} else if (input.equals(CommunicationHandler.REGISTER_ERROR)) {
		    				JOptionPane.showMessageDialog(null, "The email you have entered already exists!", "Please Try again", JOptionPane.ERROR_MESSAGE);
		    				registerPanel.setBackground(Color.RED);
		    			}
		    			break;
		    		default:
		    	}
		    }
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Restart the program
	 * @throws IOException 
	 */
	public void restart() throws IOException {
        StringBuilder cmd = new StringBuilder();
        cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            cmd.append(jvmArg + " ");
        }
        cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
        cmd.append(GameClient.class.getName()).append(" ");
        Runtime.getRuntime().exec(cmd.toString());
        System.exit(0);
	}
	
	/**
	 * Gets the socket.
	 */
	public Socket getSocket() {
		return socket;
	}
	
}