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
import javax.swing.*;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Paint;
import javafx.util.converter.IntegerStringConverter;

import java.awt.*; 
import java.awt.event.*;

public class ClientMain {
	private BufferedReader reader; 
	private PrintWriter writer;
	private static JFrame frame = new JFrame("Chat Over Java!");
	
	private static JPanel loginPanel;
	private JTextField usernameField;
	private JTextField passwordField;
	private static JLabel loginError;

	public void run() throws Exception {
		setupLoginPanel();
		setUpNetworking();
	} 
	private void setupLoginPanel() {
		//show first view, login page		
		loginPanel = new JPanel();
		loginPanel.setSize(1000, 800);
		loginPanel.setLayout(null);
		
		//make fields and labels for username and password
		JLabel usernameLabel = new JLabel("Username:");
		usernameLabel.setLocation(300, 350);
		usernameLabel.setSize(100, 40);
		usernameField = new JTextField();	
		usernameField.setLocation(400, 350);
		usernameField.setSize(250, 50);
		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setLocation(300, 400);
		passwordLabel.setSize(100, 40);
		passwordField = new JTextField();
		passwordField.setLocation(400, 400);		
		passwordField.setSize(250, 50);
		
		//make buttons to register and login a user
		JButton registerButton = new JButton("Register");
	    registerButton.setLocation(420, 450);
	    registerButton.setSize(100, 50);    
	    registerButton.addActionListener(new RegisterButtonListener());
	    JButton loginButton = new JButton("Login");
	    loginButton.setLocation(530, 450);
	    loginButton.setSize(100, 50);
	    loginButton.addActionListener(new LoginButtonListener());
	    loginError = new JLabel();
	    loginError.setLocation(445,500);
	    loginError.setSize(300, 70);
	    loginError.setForeground(Color.RED);
		
	    //add all components to login panel
	    loginPanel.add(registerButton);
	    loginPanel.add(loginButton);
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginError);
       
        //set the current frame to the login screen
        frame.getContentPane().add(loginPanel); 
		frame.setSize(1000, 800);
		frame.setResizable(false);
		frame.setVisible(true);
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
	class SendButtonListener implements ActionListener { 
		public void actionPerformed(ActionEvent ev) {
			writer.flush();
		}
	} 
	
	class RegisterButtonListener implements ActionListener { 
		public void actionPerformed(ActionEvent ev) {
			if(usernameField.getText().isEmpty()) {
				loginError.setText("Username can not be blank");
				frame.validate();
				return;
			}
			if(passwordField.getText().isEmpty()) {
				loginError.setText("Password can not be blank");
				frame.validate();
				return;
			}
			loginError.setText("");
			frame.validate();
			System.out.println("Register");
			writer.println("Register");
			writer.flush();
		}
	} 
	
	class LoginButtonListener implements ActionListener { 
		public void actionPerformed(ActionEvent ev) {
			if(usernameField.getText().isEmpty()) {
				loginError.setText("Username can not be blank");
			}
			else if(passwordField.getText().isEmpty()) {
				loginError.setText("Password can not be blank");
			}
			else {
				loginError.setText("");
			}
			System.out.println("Login");
			loginError.setText("");
			frame.validate();
			writer.println("Login");
			writer.flush();
		}
	} 
	public static void main(String[] args) {
		try {
			ClientMain client = new ClientMain();
			client.run();
		} 
		catch (Exception e) { e.printStackTrace(); }
	} 
		
	class IncomingReader implements Runnable {
		public void run() { 
			String message; 
			try {
				while ((message = reader.readLine()) != null) {
					System.out.println(message);
				}
			} 
			catch (IOException ex) { ex.printStackTrace(); }
		}
	}
	

}
