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
	int numberUsers = 0;
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
					e.printStackTrace();
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
		public void startGame(ClientThread client){
			Game game = new Game();
			games.add(game);
			game.connect(client);
			client.currentGame = game;
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
				if(this.currentGame != null) {
					this.currentGame.handleDC(this);
				}
				// Thread no longer needed, kill it
				this.interrupt();
			}

			/**
			 * Handles what is expected to be a chat message Message.
			 * @return
			 *  The chat message sent as a String.
			 */
			public void handleChat(Message msg){
				
				if(msg.messageType == 3){
					try {
						this.out.writeObject(ServerMessage.acceptChat());
					} catch (Exception e) {
						// TODO: handle exception
					}
					
					String user2 = msg.arguments.get(0);
					if (user2.equals("")) {
						for (ClientThread  client : clients) {
							try {
								client.out.writeObject(ServerMessage.updateChat(this.username, msg.arguments.get(1)));
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							
						}
					} else {
						for (ClientThread  client : clients) {
							if (client.username.equals(msg.arguments.get(0))) {
								try {
									client.out.writeObject(ServerMessage.updateChat(this.username, msg.arguments.get(1)));
								} catch (Exception e) {
									// TODO: handle exception
									e.printStackTrace();
								}
							}
						}
					}

				}
				else{
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
					numberUsers+=1;
					// Log Username for checks against others, ability to print users, etc.
					userNameList.add(username);
					server.logEvent("User #" + count + " signed up with username: " + username);
					for (ClientThread client:clients) {
						if (!this.username.equals(client.username)) {
							try {
								client.out.writeObject(ServerMessage.updateClientList(username));
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					}
					try{
						out.writeObject(ServerMessage.Login());
						out.writeObject(ServerMessage.sendClientList(new Integer(numGames), userNameList));
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
							try {
								if(currentGame.isFull()) {
									game.player2.out.writeObject(ServerMessage.inMatch(1));
									game.player1.out.writeObject(ServerMessage.inMatch(0));
								}
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
							server.logEvent("User #" + count + " attempted to connect to a full game.");
						}
					}
				}
				// Code still running == all games were full
				// Create a new game & add it to list
				server.startGame(this);
				
			}


			//check to see if move is valid, make a status update, update move on other cleints side
			public void makeMove(int player, int row){
				this.currentGame.makeMove(player, row);

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
							if (data.messageType == 0) {
								handleSignOn(data);
								//Log weird activity; User attempted to log in while signed in
								}
							else{
								server.logEvent("User #" + count + " attempted to interact with server without logging in. Using message type = " + data.messageType);
							}
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
									out.writeObject(ServerMessage.waiting());
									this.findGame();
								}
								// Attempting to connect while still connected to a game.
								else{
									server.logEvent("User #" + count + " attempted to connect to a game whilst already connected to one.");
								}
							}
							else if (data.messageType == 2) {
								System.out.println(data.arguments.get(0));
								if(this.currentGame.player1.username.equals(this.username)){

									makeMove(1, Integer.parseInt(data.arguments.get(0)));
								} else {
									makeMove(0, Integer.parseInt(data.arguments.get(0)));
									
								}
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


	
	

	
