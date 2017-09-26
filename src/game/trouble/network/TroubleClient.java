package game.trouble.network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TroubleClient {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("usage: java -jar TroubleClient.jar <ip> <port>");
			return;
		}
		try {
			Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
			while (true) {
				// do something
				break;
			}
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
