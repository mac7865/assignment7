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
import java.util.HashMap;
import java.util.HashSet;

import javafx.scene.control.TextArea;

public class Client {
	public String username;
	public String password;
	public ArrayList<Client> friends = new ArrayList<Client>();
	public HashMap<HashSet<String>, ChatGroup> chats = new HashMap<HashSet<String>, ChatGroup>();
	public Client(String u, String p) {
		username = u;
		password = p;
	}
	
	public void addFriend(Client f) {
		friends.add(f);
	}
	
	public void addChat(HashSet<String> mems) {
		ChatGroup cg = new ChatGroup(mems);
		chats.put(mems, cg);
	}
	
	public void addToChat(HashSet<String> mems, String m) {
		if(chats.containsKey(mems)) {
			ChatGroup cg = chats.get(mems);
			cg.addMessage(m);
		}
	}
	
	public ChatGroup getGroup(HashSet<String> mems) {
		if(chats.containsKey(mems))
			return chats.get(mems);
		
		return null;
	}
	
	public boolean containsChat(HashSet<String> mems) {
		if(chats.containsKey(mems)) {
			return true;
		}
		else {
			return false;
		}
	}
	class ChatGroup {
		public HashSet<String> members = new HashSet<String>();
		public HashSet<String> messsages = new HashSet<String>();
		public TextArea area = new TextArea();
		
		public ChatGroup(HashSet<String> mems) {
			members = mems;
			area.relocate(250, 200);
			area.setWrapText(true);
			area.setEditable(false);
		}
		
		public TextArea getTA() {
			return area;
		}
		
		public void addMessage(String m) {
			area.appendText(m);
		}
	}
}
