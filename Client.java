package assignment7;

/* ChatRoom Client.java
 * EE422C Project 7 submission by
 * Mark Carter
 * mac7865
 * 16495
 * Slip days used: 0
 * Fall 2016
 */

import java.util.ArrayList;

public class Client {
	public String username;
	public String password;
	public ArrayList<Client> friends = new ArrayList<Client>();
	
	public Client(String u, String p) {
		username = u;
		password = p;
	}
	
	public void addFriend(Client f) {
		friends.add(f);
	}
	
	class ChatGroup {
		public ArrayList<String> members = new ArrayList<String>();
		public ArrayList<String> messsages = new ArrayList<String>();
		
	}
}
