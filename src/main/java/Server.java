import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.Date;

import javafx.application.Platform;
import javafx.scene.control.ListView;


public class Server{

	ArrayList<String> userNameList;
	ArrayList<Integer> hashedPasswords;

	int count = 1;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	ArrayList<Game> games;
	int numGames;
	
	Server(){
		// Init relevant data
		userNameList = new ArrayList<String>();
		hashedPasswords = new ArrayList<Integer>();
		games = new ArrayList<Game>();
		numGames = 0;

		server = new TheServer();
		server.start();
	}
	
	//eventually fills the two arrays from either a file or a database figure it out
	// Yeah we are not doing this, we have two days.
//	public void fillSavedUsers(){
//
//	}
	
	public class TheServer extends Thread{
		
		public void run() {

			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("Server is waiting for a client!");
			
		    while(true) {
		
				ClientThread c = new ClientThread(mysocket.accept(), count);
				clients.add(c);
				c.start();
				count++;
			    }
			} catch(Exception e) {
					System.err.println("Server did not launch");
				}
		}

		/**
		 * Logs Notable Events ; Server Log Building
		 * @param message
		 * 	The message to log into the server log.
		 */
		public void logEvent(String message){
			Date currentTime = new Date();
			System.out.println(currentTime + ": " + message);
		}

		/**
		 * startGame()
		 * Makes a new, publicly accessible game.
		 */
		public void startGame(){
			Game game = new Game();
			games.add(game);
		}
	}

		class ClientThread extends Thread{

			Socket connection;
			int count;
			String username = "";
			boolean loggedIn = false;
			Game currentGame = null;

			ObjectInputStream in;
			ObjectOutputStream out;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}

			public void handleDC(){
				server.logEvent("User " + count + " disconnected from the server!");
				clients.remove(this);
				// Thread no longer needed, kill it
				this.interrupt();
			}

			/**
			 * Handles what is expected to be a chat message Message.
			 * @return
			 *  The chat message sent as a String.
			 */
			public String handleChat(Message msg){
				if(msg.messageType == 3){
					return msg.arguments.get(0);
				}
				else{
					return "Invalid Message Returned! Type: " + msg.messageType;
				}
			}

			/**
			 * Handle signOn request; checks message and checks if the user is in the user Array
			 */
			public void handleSignOn(Message msg){
				if(msg.messageType == 0){
					// arguments = {username}
					String username = msg.arguments.get(0);

					// Iterate through all usernames, return error if current username is duplicate.
					for (String user : userNameList ) {
                        if (user.equals(username)) {
							// If username was a duplicate, reject!
							try {
								server.logEvent("User #" + count + " attempted to sign in with duplicate username: " + username);
								out.writeObject(ServerMessage.reject());
							}
							catch (Exception e) {
								e.printStackTrace();
								this.handleDC();
							}
							return;
                        }
					}

					// Code still running == Username is unique, allow it & update information thusly
					this.loggedIn = true;
					this.username = username;
					server.logEvent("User #" + count + " signed up with username: " + username);
					try{
						out.writeObject(ServerMessage.accept());
						return;
					} catch (Exception e){
						e.printStackTrace();
						this.handleDC();
					}
				}
				server.logEvent("Invalid Message. Attempted to handle SignOn, instead got message of type " + msg.messageType);
			}


			//what is this supposed to do
			public void updateClients(String message) {
				//TODO implement
			}


			/**
			 * Find an empty game, then connect.
			 * If no empty game found, have server create new game, then attempt connection again
			 */
			public void findGame(){
				for(Game game : games){
					// If game is not full
					// i.e. if Game is able to be connected to...
					if(!(game.isFull())){
						if(game.connect(this))
						{
							this.currentGame = game;
							// Alert user they have been connected
							try {
								out.writeObject(ServerMessage.accept());
							}
							catch (Exception e) {
								e.printStackTrace();
							}
							server.logEvent("User #" + count + " connected to a game!");
							System.out.print("Players: ");
							if(currentGame.player1 != null){
								System.out.print(currentGame.player1.username);
							}
							else{
								System.out.print("N/A");
							}
							System.out.print(" ");
							if(currentGame.player2 != null){
								System.out.print(currentGame.player2.username);
							}
							else{
								System.out.print("N/A");
							}
							System.out.println();
							return;
						}
						else{
							server.logEvent("User #" + count + " attmpted to connect to a full game.");
						}
					}
				}
				// Code still running == all games were full
				// Create a new game & add it to list
				server.startGame();
				// Now there is an empty game, connect!
				// Use this method again bcs another client could TECHNICALLY
				// connect between creation and us attempting to connect bcs of multithreading
				this.findGame();
			}


			//check to see if move is valid, make a status update, update move on other cleints side
			public void makeMove(String msg){

			}

			public void recieveChat(Message msg){
				if(msg.messageType == 4){
					// Chat Message Argv:
					// index 0: message
					String chatMessage = msg.arguments.get(0);

					// TODO: filters or whatever you want to validate messages here later

					// Code still running == message is allowed to go through
					// TODO: Send chat message to server
				}
				else{
					server.logEvent("Attempted to handle Chat Message, instead got message of type " + msg.messageType);
				}
			}
			
			public void run(){
					
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
				
				server.logEvent("New client on server! Client #"+count);

				while(true) {
					try {
						Message data = (Message) in.readObject();

						// If user is not logged in, only legal message is to attempt to sign in!
						if (!loggedIn) {
							if (data.messageType == 0)
								handleSignOn(data);
								// Log weird activity; User attempted to log in while signed in
							else
								server.logEvent("User #" + count + " attempted to interact with server without logging in.");
						}
						// Case where user is logged in
						else{
							// Sign On Message whilst logged in
							if(data.messageType == 0){
								server.logEvent("User #" + count + " attempted to log in whilst logged in.");
							}
							// Handle game connection request
							else if(data.messageType == 1){
								// Not already connected to a game case
								if(currentGame == null){
									this.findGame();
								}
								// Attempting to connect while still connected to a game.
								else{
									server.logEvent("User #" + count + " attempted to connect to a game whilst already connected to one.");
								}
							}
							else if (data.messageType == 2) {
								makeMove("figure it out");
							}
							// Chat Message Case
							else if(data.messageType == 3){
								handleChat(data);
							}
						}
					} catch (Exception e) {
						this.handleDC();
					    break;
					}
				}


				}//end of run
			
			
		}//end of client thread
}


	
	

	
