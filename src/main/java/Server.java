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
	ArrayList<Game> gamesWaitingForPlayers;

	int count = 1;	
	int numberOfUsers = 0;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	ArrayList<Game> Games;
	TheServer server;
	Integer numberOfGames;
	
	
	Server(){

		userNameList = new ArrayList<String>();
		hashedPasswords = new ArrayList<Integer>();
		gamesWaitingForPlayers = new ArrayList<Game>();
		Games = new ArrayList<Game>();
		numberOfGames = 0;

//		fillSavedUsers();

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
	}
	

		class ClientThread extends Thread{
			
		
			Socket connection;
			int count;
			String username = "";
			boolean loggedIn = false;
			Game associatedGame;

			ObjectInputStream in;
			ObjectOutputStream out;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}

			public void handleDC(){
				server.logEvent("User " + count + " unexpectedly disconnected from the server!");
				clients.remove(this);
				server.logEvent("User removed from current records, closing thread.");
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


			public void findGame(){
				if (gamesWaitingForPlayers.isEmpty()) {
					Game currGame = new Game(numberOfGames);
					numberOfGames +=1;
					currGame.users.add(username);
					gamesWaitingForPlayers.add(currGame);
				}else {
					Game currGame = gamesWaitingForPlayers.remove(0);
					currGame.users.add(username);
					Games.add(currGame);

					MessageServer response = new MessageServer();
	

					int clientIn = -1;
					for (ClientThread client : clients) {
						if (client.username.equals(currGame.users.get(0))){
							response.setUpGame(currGame.gameID,currGame.representBoard(),6,7,new ArrayList<String>(), 0, 0);
							clientIn = 0;

						} else if (client.username.equals(currGame.users.get(1))){
							response.setUpGame(currGame.gameID,currGame.representBoard(),6,7,new ArrayList<String>(), 0, 1);
							clientIn = 1;
						}

						try {
						if (clientIn != -1) {
							out.writeObject(response);
							client.associatedGame = currGame;
						}
						} catch (Exception e) {
							e.printStackTrace();
							if (clientIn == 0) {
								findGame();
							} else {
								client.findGame();
							}
						}
					}



					
				}
			}

			//change to connect to a specific game
			//needs to check the game array and the games that are trying to load
			public void connectToGame(int id) {
				if (gamesWaitingForPlayers.isEmpty()) {
					Game currGame = new Game(numberOfGames);
					numberOfGames +=1;
					currGame.users.add(username);
					gamesWaitingForPlayers.add(currGame);
				}else {
					Game currGame = gamesWaitingForPlayers.remove(0);
					currGame.users.add(username);
					Games.add(currGame);


					
					MessageServer response = new MessageServer();
	

					int clientIn = -1;
					for (ClientThread client : clients) {
						if (client.username.equals(currGame.users.get(0))){
							response.setUpGame(currGame.gameID,currGame.representBoard(),6,7,new ArrayList<String>(), 0, 0);
							clientIn = 0;

						} else if (client.username.equals(currGame.users.get(1))){
							response.setUpGame(currGame.gameID,currGame.representBoard(),6,7,new ArrayList<String>(), 0, 1);
							clientIn = 1;
						}

						try {
							if (clientIn != -1) {
							out.writeObject(response);}
						} catch (Exception e) {
							e.printStackTrace();
							if (clientIn == 0) {
								findGame();
							} else {
								client.findGame();
							}
						}
					}
					
				}

			}


			//check to see if move is valid, make a status update, update move on other cleints side
			public void makeMove(){

			}

			public void recieveChat(){

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

						// Sign In Attempt Message
						if (data.messageType == 0) {
							if(!loggedIn)
								handleSignOn(data);
							// Log weird activity; User attempted to log in while signed in
							else
								server.logEvent("User " + this.username + " with internal ID " + this.count + " attempted to sign in while logged in.");
						} else if (data.messageType == 1) {
							findGame();
						}else if (data.messageType == 2) {
							connectToGame(Integer.parseInt(data.arguments.get(0)));
						}else if (data.messageType == 3) {
							makeMove();
						}else if (data.messageType == 4) {
							recieveChat();
						}else if (data.messageType == 5) {
							
						}else if (data.messageType == 6) {
							
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					    updateClients("Client #"+count+" has left the server!");
					    clients.remove(this);
					    break;
					}
				}


				}//end of run
			
			
		}//end of client thread
}


	
	

	
