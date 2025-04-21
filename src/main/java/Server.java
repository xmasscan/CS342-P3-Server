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

	int count = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	
	
	Server(){

		server = new TheServer();
		server.start();
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
			 * Handle signOn request ; Username only variant
			 * TODO: Implement Username checking on other threads
			 * @return
			 */
			public String handleSignOn(Message msg){
				if(msg.messageType == 0){
					return msg.arguments.get(0);
				}
				else{
					System.err.println("Invalid Message Returned! Type: " + msg.messageType);
					return null;
				}
			}

			public void updateClients(String message) {
				//TODO implement
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

				while(!loggedIn) {
					try {
						Message loginAttempt = (Message) in.readObject();
						String attemptedUsername = handleSignOn(loginAttempt);
						if (attemptedUsername != null) {
							this.username = attemptedUsername;
							loggedIn = true;
							Message accept = Message.accept();
							out.writeObject(accept);
						}
						else{
							System.err.println("Invalid Sign On Attempt!");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				updateClients("Client #"+count + " has logged in. Username: " + username);
				System.out.println(this.username + " has logged in!");

				 while(true) {
					    try {
					    	Message data = (Message) in.readObject();
					    	System.out.println(this.username + ": " + handleChat(data));
					    	updateClients(this.username + " said: " + data);
					    	}
					    catch(Exception e) {
					    	System.err.println("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					    	updateClients("Client #"+count+" has left the server!");
					    	clients.remove(this);
					    	break;
					    }
					}
				}//end of run
			
			
		}//end of client thread
}


	
	

	
