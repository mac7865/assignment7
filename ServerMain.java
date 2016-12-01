package assignment7;

/* ChatRoom ServerGui.java
 * EE422C Project 7 submission by
 * Mark Carter
 * mac7865
 * 16495
 * Slip days used: 1
 * Fall 2016
 */

import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServerMain {
	public static TextArea console;
	
	public static void main(String args[]) {
		try {
			final CountDownLatch latch = new CountDownLatch(1);
			SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			        new JFXPanel(); // initializes JavaFX environment
			        latch.countDown();
			    }
			});
			latch.await();
			System.out.println("initialized");
			Platform.runLater(new Runnable() {
			    @Override
			    public void run() {
					Stage stage = new Stage();
					try {
						start(stage);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			});
			new Server().setUpNetworking();

		} 
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public static void start(Stage primaryStage) throws Exception {
		console = new TextArea();
		console.resize(500, 500);
		console.setWrapText(true);
		console.setEditable(false);
		Group consoleGroup = new Group();
		consoleGroup.getChildren().add(console);
		appendToConsole("starting up");
		Scene consoleScene = new Scene(consoleGroup, 500, 180);
		primaryStage.setScene(consoleScene);
		primaryStage.setResizable(false);
		primaryStage.sizeToScene();
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		       @Override
		       public void handle(WindowEvent e) {
		    	  System.out.println("exiting");
		          Platform.exit();
		          System.exit(0);
		       }
		});
		appendToConsole("Socket connected at " + Server.socketAddress);
		System.out.println("showing");
	}
	
	public static void appendToConsole(String m) {
		console.appendText(m + "\n");
	}
}
