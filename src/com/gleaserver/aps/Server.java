package com.gleaserver.aps;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import android.util.Log;

/**
 * Server
 * @author aGleason
 * @date April 22, 2013
 * @version 1.0
 * 
 * A Server Object that is intended to be run as an embedded thread. It listens for new  connections and handles them accordingly. 
 *
 * Copyright 2013 Alec Gleason
 *
 * This file is part of Android Phone Server.
 * 
 * Android Phone Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Android Phone Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Android Phone Server.  If not, see <http://www.gnu.org/licenses/>.
 */
public class Server implements Runnable
{
	
	/** The Server Object */
	private ServerSocket server;
	
	/** The port number */
	private int port;
	
	/** The custom greeting */
	String message;
	
	/** The Max number of connections */
	private int conThreshold; 
	
	/** Incoming Client connection to be handled */
	private Socket incomingClient;

	/** List of Client Connections */
	private ArrayList<Client> clients = new ArrayList<Client>(); 
	
	/** The Embedded Listener */
	private boolean embeddedOn = true;
	
	/** The Intent Receiver */
	private Receiver receiver;

	/** A list of all contacts in the phone */
	static ArrayList<String[]> allContacts = new ArrayList<String[]>();
	
	/** The key to access the server */
	private String key;
	
	
	/**
	 * Server Thread
	 * 
	 * Creates a new Server Thread object 
	 * @param p port to start on
	 * @param nc The New Connection Threshold
	 */
	public Server(int p, int nc, String msg, ArrayList<String[]> ac, Receiver r, String k)
	{
		port = p;
		conThreshold = nc;
		message = msg;
    	//receiver = new Receiver();
    	allContacts = ac;
    	receiver = r;
    	key = k;
	}
	
	/**
	 * Run
	 * 
	 * What is run as a Thread: 
	 * Starts the Server Object and starts listening for connections
	 */
	@Override
	public void run() 
	{
		try 
		{
			server = new ServerSocket(port);
			Log.i("LOG", "Server started normally");
			while(embeddedOn)
			{
				incomingClient = server.accept();
				if(incomingClient != null)
				{
					this.createClient(incomingClient);
				}
				incomingClient = null;
				
				Log.i("LOG", "Collected Garbage: " + this.garbageCollect());
				
			}
			server.close();
		} 
		catch (IOException e) 
		{
			Log.i("LOG", "There was an Error with the Server Thread: " + e.toString());
			e.printStackTrace();
		}
		Log.i("LOG", "The Server has stopped exited successfully");
		
		
		
	}
	
	/**
	 * Create Client
	 * 
	 * Creates and starts a new Client Thread from the new connection passed
	 * @param newConnection The new Client
	 * @throws IOException If there is an issue with creating the rejection
	 */
	private void createClient(Socket newConnection) throws IOException
	{
		// If there are less clients than the Threshold then allow a full connection
		if(clients.size() < conThreshold)
		{
			clients.add(new Client(clients.size()+1, newConnection, message, allContacts, receiver, key));
			new Thread(clients.get(clients.size()-1)).start();
		}
		
		// Otherwise connect and tell them that there are no more available connections
		else
		{
			PrintWriter rejection = new PrintWriter(new OutputStreamWriter(newConnection.getOutputStream()), true);
			rejection.print("The Maximum number of connections have been reached. Disconnecting. \r\n");
			rejection.flush();
			rejection.close();
			newConnection.close();
		}
		
	}
	
	/**
	 * Alert
	 * 
	 * Sends an Alert to every client
	 * @param alert The Alert Message
	 */
	public void alert(String alert)
	{
		for(int i = 0; i < clients.size(); i ++)
		{
			clients.get(i).alert(alert);
		}
		Log.i("LOG", "Server Thread recieved Alert: " + alert);
	}
	
	/**
	 * Targeted Alert
	 * 
	 * Sends an Alert to the client you specify
	 * @param alert The Alert Message
	 * @param targetClient The client to send the alert to
	 */
	public void alert(String alert, int targetClient)
	{
		clients.get(targetClient).alert(alert);
	}
	
	/**
	 * Update Message
	 * 
	 * @param alert The New Message Alert
	 * @param msg The New list of messages
	 */
	public void updateMsg(ArrayList<String[]> msg)
	{
		for(int i = 0; i < clients.size(); i ++)
		{
			clients.get(i).updateMessages(msg);
		}
	}
	
	/**
	 * Garbage Collect
	 * 
	 * Goes through and cleans up after connections that are no longer being used
	 * @return How many connections were cleaned this collection cycle
	 */
	public int garbageCollect()
	{
		int numCollected = 0;
		
		for(int i = 0; i < clients.size(); i ++)
		{
			if(clients.get(i).isClosed())
			{
				clients.remove(i);
				numCollected ++;
			}
		}
		
		return numCollected;
	}
	
	public void stopServer() throws IOException{
		for(int i = 0; i < clients.size(); i ++)
		{
			clients.get(i).disconnect("You are being Disconnected. The Server is shutting down");
		}
	}

}
