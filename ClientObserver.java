package assignment7;

/* ChatRoom ClientObserver.java
 * EE422C Project 7 submission by
 * Mark Carter
 * mac7865
 * 16495
 * Slip days used: 1
 * Fall 2016
 */

import java.io.OutputStream; 
import java.io.PrintWriter; 
import java.util.Observable; 
import java.util.Observer;
public class ClientObserver extends PrintWriter implements Observer {
	public ClientObserver(OutputStream out) {
		super(out);
	} 
	
	public void update(Observable o, Object arg) {
		System.out.println("updating");
		String[] commandSplit = ((String) arg).split("\\s+");
		if(commandSplit[0].equals("Login")) {
			this.println(arg);
			this.flush();
		}
		else {
			this.println("what up client");
			this.println(arg); //writer.println(arg);
			this.flush(); //writer.flush(); 
		}
	}
}

