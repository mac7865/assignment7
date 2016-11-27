package assignment7;

import java.io.OutputStream; 
import java.io.PrintWriter; 
import java.util.Observable; 
import java.util.Observer;
public class ClientObserver extends PrintWriter implements Observer {
	public ClientObserver(OutputStream out) {
		super(out);
	} 
	
	@Override 
	public void update(Observable o, Object arg) {
		System.out.println("updating");
		this.println("what up client");
		this.println(arg); //writer.println(arg);
		this.flush(); //writer.flush(); 
	}
}

