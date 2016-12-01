package assignment7;

/* ChatRoom ClientMain.java
 * EE422C Project 7 submission by
 * Mark Carter
 * mac7865
 * 16495
 * Slip days used:1
 * Fall 2016
 */

import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;

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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.converter.IntegerStringConverter;
import javafx.scene.control.*;


public class ClientMain extends Application{
	private BufferedReader reader; 
	private PrintWriter writer;
	private Client c;
	private static Stage stage;
	private TextField usernameField;
	private TextField passwordField;
	private Label userLabel;
	private TextField recipient;
	private TextField messageField;
	private TextArea currentChat = null;
	private Label currentRecipients;
	private static Label loginError;
	private Label messageWarn;
	private Group loginGroup;
	private Scene loginScene;
	private Group chatGroup;
	private Scene chatScene;
	private Group networkGroup;
	private Scene networkScene;
	private Label networkWarn;
	private TextField ipAdressField;
	private TextField portNumField;
	private String username;
	private String password;
	
	public void run() throws Exception {
		//setUpNetworking();
	} 
	
	private void setUpNetworking(String ip, int port) throws Exception {
		@SuppressWarnings("resource") 
		
		Socket sock = new Socket(InetAddress.getByName(ip), port); 
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader); 
		writer = new PrintWriter(sock.getOutputStream()); 
		System.out.println("networking established"); 
		Thread readerThread = new Thread(new IncomingReader()); 
		readerThread.start();
		stage.setScene(loginScene);
    	stage.show();
	}
	
	
	
	
	public static void main(String[] args) {
		try {
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
							//good login, check to see if its this client, proceed to next screen with identity
							if(messageSplit[2].equals(usernameField.getText())) {
								System.out.println("switching panels");
								username = messageSplit[2];
								password = messageSplit[3];
								c = new Client(username, password);
								Platform.runLater(new Runnable() {
								    @Override
								    public void run() {
					        			userLabel.setText("Current user is " + username);
										stage.setScene(chatScene);
										stage.show();
								    }
								});
							}
						}
						else {
							//bad login, update with warning
							if(messageSplit[2].equals(usernameField.getText())) {
								Platform.runLater(new Runnable() {
								    @Override
								    public void run() {
								        
										loginError.setText("Invalid username or password");
								    }
								});
							}
						}
					}
					else if(messageSplit[0].equals("Register")) {
						//registration attempt just made, update with results
						if(messageSplit[1].equals("Good")) {
							//new user created, log in with new identity if applies
							if(messageSplit[2].equals(usernameField.getText())) {
								System.out.println("switching panels");
								username = messageSplit[2];
								password = messageSplit[3];
								c = new Client(username, password);
								Platform.runLater(new Runnable() {
								    @Override
								    public void run() {
					        			userLabel.setText("Current user is " + username);
										stage.setScene(chatScene);
										stage.show();
								    }
								});
							}
						}
						else {
							if(messageSplit[2].equals(usernameField.getText())) {
								//bad registration, username must be taken
								Platform.runLater(new Runnable() {
								    @Override
								    public void run() {								   
										loginError.setText("Username already taken");
								    }
								});
							}
						}
					}
					else if(messageSplit[0].equals("Message")) {
						if(messageSplit[1].equals("Bad")) {
							if(usernameField.getText().equals(messageSplit[2])) {
								Platform.runLater(new Runnable() {
								    @Override
								    public void run() {
								    	messageWarn.setText("Could not send message.");
								    }
								});
							}
						}
						else {
							ArrayList<String> recps = new ArrayList<String>();
							HashSet<String> recs = new HashSet<String>();
							int i = 1;
							while(!messageSplit[i].equals("Message")) {
								recps.add(messageSplit[i]);
								recs.add(messageSplit[i]);
								i++;
							}
							i++;
							if(recps.contains(username)) {
								Platform.runLater(new Runnable() {
								    @Override
								    public void run() {
								    	messageWarn.setText("");
								    	if(usernameField.getText().equals(messageSplit[1])) {
								    		messageField.setText("");
								    	}
								    }
								});
								//message applies to this user, retrieve message and display to proper chat
								String mes = messageSplit[1] + ": ";
								while(i < messageSplit.length) {
									mes += messageSplit[i] + " ";
									i++;
								}
								mes += "\n";
										
								if(c.containsChat(recs)) {
									c.addToChat(recs, mes);
								}
								else {
									c.addChat(recs);
									c.addToChat(recs, mes);
								}
								Platform.runLater(new Runnable() {
								    @Override
								    public void run() {
								    	if(chatGroup.getChildren().contains(currentChat)) {
								    		chatGroup.getChildren().remove(currentChat);															    		
								    	}
								    	currentChat = c.getGroup(recs).getTA();
								    	chatGroup.getChildren().add(currentChat);
								    	String recsString = recs.toString()
								    		    			.replace("[", "")  //remove the right bracket
								    		    			.replace("]", "")  //remove the left bracket
								    		    			.trim();           //remove trailing spaces from partially initialized arrays
								    	currentRecipients.setText("Currently chatting with " + recsString);
								    	stage.show();
								    }
								});
							}
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
		// JavaFX should be initialized
    	//show first view, login page
		loginGroup = new Group();
		chatGroup = new Group();
		networkGroup = new Group();
		
		//make fields and labels for username and password
		Label usernameLabel = new Label("Username:");
		usernameLabel.relocate(350, 350);
		usernameLabel.resize(100, 40);
		usernameField = new TextField();	
		usernameField.relocate(450, 350);
		usernameField.resize(250, 50);
		Label passwordLabel = new Label("Password:");
		passwordLabel.relocate(350, 400);
		passwordLabel.resize(100, 40);
		passwordField = new TextField();
		passwordField.relocate(450, 400);		
		passwordField.resize(250, 50);
		
		//make buttons to register and login a user
		Button registerButton = new Button("Register");
	    registerButton.relocate(450, 450);
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
	    loginButton.relocate(560, 450);
	    loginButton.resize(100, 25);
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
        messageWarn = new Label("");
        messageWarn.resize(100, 100);
        messageWarn.relocate(500, 600);
        messageWarn.setTextFill(Paint.valueOf("RED"));
        recipient = new TextField();
        recipient.relocate(300, 500);
        messageField = new TextField();
        messageField.relocate(500, 500);
        Button sendButton = new Button("Send");
        sendButton.relocate(700, 500);
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
	        public void handle(ActionEvent e) {
        		if(recipient.getText().isEmpty()) {
        			messageWarn.setText("WHO ARE YOU TALKING TO?????");
    			}
    			else if(messageField.getText().isEmpty()) {
    				messageWarn.setText("WHAT ARE YOU SAYING?????");
    			}
    			else {
    				messageWarn.setText("");
    				System.out.println("Message " + recipient.getText() + " " + messageField.getText());
        			writer.println("Message " + username + " " + recipient.getText() + " Message " + messageField.getText());
        			writer.flush();
    			}
        	}
        });
        currentRecipients = new Label();
        currentRecipients.setText("Send a message to start chatting!");
        currentRecipients.setTextAlignment(TextAlignment.CENTER);
        currentRecipients.setPrefSize(500, 100);
        currentRecipients.relocate(400, 50);
        userLabel = new Label();
        userLabel.relocate(800, 0);
        userLabel.resize(100, 100);
        userLabel.setText("Current user is ");
        Button logout = new Button("Logout");
        logout.relocate(850, 25);
        logout.resize(100, 50);
        logout.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
	        public void handle(ActionEvent e) {
        		System.out.println("loggin out");
        		//go through logout procedure, clear fields, reset credentials, etc...
        		messageWarn.setText("");
        		username = "";
        		usernameField.setText("");
        		passwordField.setText("");
        		messageField.setText("");
        		recipient.setText("");
        		currentRecipients.setText("Send a message to start chatting!");
        		if(chatGroup.getChildren().contains(currentChat)) {
        			chatGroup.getChildren().remove(currentChat);
        		}
        		stage.setScene(loginScene);
        		stage.show();
        	}
        });
        chatGroup.getChildren().add(logout);
        chatGroup.getChildren().add(userLabel);
        chatGroup.getChildren().add(messageWarn);
        chatGroup.getChildren().add(recipient);
        chatGroup.getChildren().add(messageField);
        chatGroup.getChildren().add(sendButton);
        chatGroup.getChildren().add(currentRecipients);
        chatScene = new Scene(chatGroup, 1000, 800);
        
        //set up network scene
        ipAdressField = new TextField();
        ipAdressField.relocate(400, 300);
        portNumField = new TextField();
        portNumField.relocate(400, 350);
        Label ipLabel = new Label();
        ipLabel.setText("IP Address: ");
        ipLabel.relocate(300, 300);
        Label portLabel = new Label();
        portLabel.setText("Port: ");
        portLabel.relocate(300, 350);
        networkWarn = new Label();
        networkWarn.relocate(400, 600);
        networkWarn.setTextFill(Paint.valueOf("RED"));
        Button connect = new Button("Connect");
        connect.relocate(500, 450);
        connect.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
	        public void handle(ActionEvent e) {
        		try {
        			setUpNetworking(ipAdressField.getText(), Integer.parseInt(portNumField.getText()));
        			networkWarn.setText("");
        		} catch(Exception ex) {
        			networkWarn.setText("Could not connect to that IP and Port. Are you sure theres a server running there?");
        		}
        	}
        });
        networkGroup.getChildren().add(networkWarn);
        networkGroup.getChildren().add(portLabel);
        networkGroup.getChildren().add(portNumField);
        networkGroup.getChildren().add(ipLabel);
        networkGroup.getChildren().add(ipAdressField);
        networkGroup.getChildren().add(connect);
        networkScene = new Scene(networkGroup, 1000, 800);
        //set the current frame to the network screen, need to connect to a server
        stage = new Stage();
		stage.setTitle("Chat Over Java!");
		stage.setScene(networkScene);
		stage.sizeToScene();
		stage.setResizable(false);
		stage.show();	
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		       @Override
		       public void handle(WindowEvent e) {
		    	  System.out.println("exiting");
		          Platform.exit();
		          System.exit(0);
		       }
		});
	}
}
