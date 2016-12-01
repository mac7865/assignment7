package assignment7;

/* ChatRoom ServerMain.java
 * EE422C Project 7 submission by
 * Mark Carter
 * mac7865
 * 16495
 * Slip days used: 1
 * Fall 2016
 */

import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader; 
import java.net.ServerSocket; 
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class Server extends Observable { 
	public HashMap<String, Client> userDatabase = new HashMap<String, Client>();
	public HashMap<String, ClientObserver> userObservers = new HashMap<String, ClientObserver>();
	public static String socketAddress = "";
	
	
	public void setUpNetworking() throws Exception {
		@SuppressWarnings("resource") 
		ServerSocket serverSock = new ServerSocket(4242); 
		socketAddress = serverSock.getLocalSocketAddress().toString();
		while (true) { 
			Socket clientSocket = serverSock.accept();
			ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
			Thread t = new Thread(new ClientHandler(clientSocket)); 
			t.start(); 
			this.addObserver(writer); 
			ServerMain.appendToConsole("got a connection");
		}
	} 
	
	class ClientHandler implements Runnable { 
		private BufferedReader reader;
		public ClientHandler(Socket clientSocket) { 
			Socket sock = clientSocket;
			try {
				reader = new BufferedReader(new InputStreamReader(sock.getInputStream())); 
			} 
			catch (IOException e) { e.printStackTrace(); }
		}
		public void run() { 
			String command; 
			try {
				while ((command = reader.readLine()) != null) { 
					String[] commandSplit = command.split("\\s+");
					if(commandSplit[0].equals("Register")) {
						if(userDatabase.containsKey(commandSplit[1])) {
							//username already taken, bad registration attempt
							setChanged(); 
							notifyObservers("Register Bad " + commandSplit[1]);
						}
						else {
							ServerMain.appendToConsole("new registration username: " + commandSplit[1] + " password: " + commandSplit[2] );
							Client c = new Client(commandSplit[1], commandSplit[2]);
							userDatabase.put(c.username, c);
							setChanged(); 
							notifyObservers("Register Good " + c.username + " " + c.password);
						}
					}
					else if(commandSplit[0].equals("Login")) {
						ServerMain.appendToConsole("Login attempt username: " + commandSplit[1] + " password: " + commandSplit[2] );
						if(userDatabase.containsKey(commandSplit[1]) && userDatabase.get(commandSplit[1]).password.equals(commandSplit[2])) {
							//correct Login
							setChanged(); 
							notifyObservers("Login Good " + commandSplit[1] + " " + commandSplit[2]);
						}
						else {
							//bad login attempt
							setChanged(); 
							notifyObservers("Login Bad " + commandSplit[1]);
						}
					}
					else if(commandSplit[0].equals("Message")) {
						//about to send a message, need to read in recipient and message
						ServerMain.appendToConsole(command);
						String recp = commandSplit[1];
						int x = 2;
						boolean recipientsExist = true;
						while(!recp.equals("Message") && recipientsExist) {
							if(userDatabase.containsKey(recp)) {
								recp = commandSplit[x];
								x++;
							}
							else {
								recipientsExist = false;
								ServerMain.appendToConsole("bad recp " + recp);
							}
						}
						if(recipientsExist) {
							setChanged(); 
							notifyObservers(command);
						}
						else {
							setChanged(); 
							notifyObservers("Message Bad " + commandSplit[1]);
						}
					}
				}
			} 
			catch (IOException e) { e.printStackTrace(); }
		}
	}
}
