package assignment7;

/* ChatRoom ClientMain.java
 * EE422C Project 7 submission by
 * Mark Carter
 * mac7865
 * 16495
 * Slip days used: 0
 * Fall 2016
 */

import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.EventListener;

import assignment5.Critter;
import javafx.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.scene.control.*;


public class ClientMain extends Application{
	private BufferedReader reader; 
	private PrintWriter writer;
	
	private static Scene scene;
	private static Stage stage;
	private TextField usernameField;
	private TextField passwordField;
	private static Label loginError;
	private Group loginGroup;
	private Scene loginScene;
	private Group chatGroup;
	private Scene chatScene;
	private String username;
	private String password;
	
	public void run() throws Exception {
		//setUpNetworking();
	} 
	/*
	private void initView() {
		frame = new JFrame("Chat Over Java"); 
		JPanel mainPanel = new JPanel(); 
		incoming = new JTextArea(15, 50); 
		incoming.setLineWrap(true); 
		incoming.setWrapStyleWord(true); 
		incoming.setEditable(false); 
		JScrollPane qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		outgoing = new JTextField(20); 
		JButton sendButton = new JButton("Send"); 
		sendButton.addActionListener(new SendButtonListener()); 
		mainPanel.add(qScroller); 
		mainPanel.add(outgoing); 
		mainPanel.add(sendButton); 
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel); 
		frame.setSize(1000, 800);
		frame.setResizable(false);
		frame.setVisible(true);
	} 
	*/
	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource") 
		Socket sock = new Socket(InetAddress.getLocalHost(), 4242); 
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader); 
		writer = new PrintWriter(sock.getOutputStream()); 
		System.out.println("networking established"); 
		Thread readerThread = new Thread(new IncomingReader()); 
		readerThread.start();
	}
	
	
	
	
	public static void main(String[] args) {
		try {
			ClientMain client = new ClientMain();
			client.run();
			launch(args);
		} 
		catch (Exception e) { e.printStackTrace(); }
	} 
		
	class IncomingReader implements Runnable {
		public void run() { 
			String message; 
			try {
				while ((message = reader.readLine()) != null) {
					System.out.println("incoming message " + message);
					String[] messageSplit = message.split("\\s+");
					if(messageSplit[0].equals("Login")) {
						//login attempt just made, update with results
						if(messageSplit[1].equals("Good")) {
							//good login, proceed to next screen with identity
							System.out.println("switching panels");
							username = messageSplit[2];
							password = messageSplit[3];
							Platform.runLater(new Runnable() {
							    @Override
							    public void run() {
							        //if you change the UI, do it here !
									stage.setScene(chatScene);
									stage.show();
							    }
							});
						}
						else {
							//bad login, update with warning
							Platform.runLater(new Runnable() {
							    @Override
							    public void run() {
							        //if you change the UI, do it here !
									loginError.setText("Invalid username or password");
							    }
							});
						}
					}
					else if(messageSplit[0].equals("Register")) {
						//registration attempt just made, update with results
						if(messageSplit[1].equals("Good")) {
							//new user created, log in with new identity
							System.out.println("switching panels");
							username = messageSplit[2];
							password = messageSplit[3];
							Platform.runLater(new Runnable() {
							    @Override
							    public void run() {
							        //if you change the UI, do it here !
									stage.setScene(chatScene);
									stage.show();
							    }
							});
						}
						else {
							//bad registration, username must be taken
							Platform.runLater(new Runnable() {
							    @Override
							    public void run() {
							        //if you change the UI, do it here !
									loginError.setText("Username already taken");
							    }
							});
						}
					}
					System.out.println(message);
				}
			} 
			catch (IOException ex) { ex.printStackTrace(); }
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		//set up connection
		setUpNetworking();
		// JavaFX should be initialized
    	//show first view, login page
		loginGroup = new Group();
		chatGroup = new Group();
		scene = new Scene(new Group(), 1000, 800);
		
		//make fields and labels for username and password
		Label usernameLabel = new Label("Username:");
		usernameLabel.relocate(300, 350);
		usernameLabel.resize(100, 40);
		usernameField = new TextField();	
		usernameField.relocate(400, 350);
		usernameField.resize(250, 50);
		Label passwordLabel = new Label("Password:");
		passwordLabel.relocate(300, 400);
		passwordLabel.resize(100, 40);
		passwordField = new TextField();
		passwordField.relocate(400, 400);		
		passwordField.resize(250, 50);
		
		//make buttons to register and login a user
		Button registerButton = new Button("Register");
	    registerButton.relocate(420, 450);
	    registerButton.resize(100, 50);    
	    
	    registerButton.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
	        public void handle(ActionEvent e) {
        		if(usernameField.getText().isEmpty()) {
    				loginError.setText("Username can not be blank");
    				return;
    			}
    			if(passwordField.getText().isEmpty()) {
    				loginError.setText("Password can not be blank");
    				return;
    			}
    			loginError.setText("");
    			System.out.println("Register");
    			writer.println("Register " + usernameField.getText() + " " + passwordField.getText());
    			writer.flush();
        	}
        });
	    Button loginButton = new Button("Login");
	    loginButton.relocate(530, 450);
	    loginButton.resize(100, 50);
	    loginButton.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
	        public void handle(ActionEvent e) {
        		if(usernameField.getText().isEmpty()) {
    				loginError.setText("Username can not be blank");
    			}
    			else if(passwordField.getText().isEmpty()) {
    				loginError.setText("Password can not be blank");
    			}
    			else {
    				loginError.setText("");
    				System.out.println("Login");
        			loginError.setText("");
        			writer.println("Login " + usernameField.getText() + " " + passwordField.getText());
        			writer.flush();
    			}
        	}
	    });
	    loginError = new Label();
	    loginError.relocate(445,500);
	    loginError.resize(300, 70);
	    loginError.setTextFill(Paint.valueOf("RED"));
		
	    //add all components to login group
	    loginGroup.getChildren().add(loginButton);
	    loginGroup.getChildren().add(registerButton);
	    loginGroup.getChildren().add(loginError);
	    loginGroup.getChildren().add(usernameLabel);
	    loginGroup.getChildren().add(passwordLabel);
	    loginGroup.getChildren().add(usernameField);
	    loginGroup.getChildren().add(passwordField);
	    loginScene = new Scene(loginGroup, 1000, 800);
        
	    //set up chat panel       
        Label lab = new Label("Chat time!");
        lab.resize(100, 100);
        lab.relocate(500, 400);
        chatGroup.getChildren().add(lab);
        chatScene = new Scene(chatGroup, 1000, 800);
        
        //set the current frame to the login screen
        stage = new Stage();
		stage.setTitle("Chat Over Java!");
		stage.setScene(loginScene);
		stage.sizeToScene();
		stage.setResizable(false);
		stage.show();		
	}
}
