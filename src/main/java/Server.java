import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

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

		String test = new String("adminTest");
		userNameList.add(test);
		hashedPasswords.add(test.hashCode());

		fillSavedUsers();
		

		server = new TheServer();
		server.start();
	}
	
	//eventually fills the two arrays from either a file or a database figure it out
	public void fillSavedUsers(){

	}
	
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
					ArrayList<String> info = new ArrayList<String>();
					info.add(msg.arguments.get(0));
					info.add(msg.arguments.get(1));

					Boolean userInList = false;

					for (String user : userNameList ) {
						if (user.equals(username)){
							userInList = true;
						}
					}

					MessageServer response = new MessageServer();
					if (info.get(0) != null && info.get(1) != null) {
						
						if (userInList) {
							if (hashedPasswords.contains(info.get(1).hashCode())){
								response.signInReturn(0,0,0, userNameList, numberOfUsers);
								loggedIn = true;
								this.username = info.get(0);
							}
							response.reply("Incorrect password try again", false);
						} else {
							numberOfUsers +=1;
							this.username = info.get(0);
							userNameList.add(username);
							hashedPasswords.add(info.get(1).hashCode());
							loggedIn = true;
							response.signInReturn(0,0,0, userNameList, numberOfUsers);
							
						}
					}
					else{
						System.err.println("Invalid Sign On Attempt!");
						response.reply("Incorrect password try again", false);
					}

					try {
						out.writeObject(response);
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					    updateClients("Client #"+count+" has left the server!");
					    clients.remove(this);
					}
				}

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
				
				updateClients("new client on server: client #"+count);

				while(true) {
					try {
						Message data = (Message) in.readObject();

						System.out.println(this.username + ": " + handleChat(data));
					    updateClients(this.username + " said: " + data);

						if (data.messageType == 0) {
							handleSignOn(data);
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


	
	

	
