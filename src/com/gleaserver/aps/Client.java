package com.gleaserver.aps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

/**
 * Client
 * @author aGleason
 * @date April 22, 2013
 * @version 1.0
 * 
 * This class is intended to hold and manage a Client Socket
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
 *
 */
public class Client implements Runnable
{
	/** The Client ID */
	private int id;
	
	/** The Socket the client exists at */
	private Socket client;
	
	/** The Custom Greeting */
	private String message;
	
	/** The Input from the Client */
	private BufferedReader in;
	
	/** THe Output from the Server */
	private PrintWriter out;
	
	/** The Command Queue */
	private Queue<String> commands = new ConcurrentLinkedQueue<String>();
	
	/** Embedded Process control */
	private boolean listenOn = true;
	
	/** The Command Interpretation thread */
	private ClientCommandService comExe;
	
	/** A list of all contacts in the phone */
	static ArrayList<String[]> allContacts = new ArrayList<String[]>();
	
	/** A list of all messages seen by this client */
	private ArrayList<String[]> messages = new ArrayList<String[]>();
	
	/** Passed Receiver Object */
	private Receiver receiver; 
	
	/** The Client Message Service */
	private ClientMessageService cms;
	
	/** The Key to access the Server */
	private String key;
	
	/**
	 * Client
	 * 
	 * Manages a new client connection to the server
	 * @param cid
	 * @param mySocket
	 */
	public Client(int cid, Socket mySocket, String msg, ArrayList<String[]> ac, Receiver r, String k)
	{
		id = cid;
		client = mySocket;
		message = msg;
		allContacts = ac;
		receiver = r;
		key = k;
		
		try 
		{
			// Create I/O objects
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
			comExe = new ClientCommandService(client, in, out, commands, allContacts);
			cms = new ClientMessageService(receiver,messages,comExe);
			new Thread(cms).start();
		} 
		catch (IOException e) 
		{
			Log.i("LOG", "There was an issue creating the I/O Connection: " + e.toString());
		}
		
	}

	/**
	 * Run
	 * 
	 * What runs when this is started as a Thread
	 */
	@Override
	public void run() {
		this.startListening();
		Log.i("LOG", "Embedded start for client: " + this.getId());
	}
	
	/**
	 * Start Listening
	 * 
	 * Starts an embedded process to listen for new commands across the network.
	 * New commands are queued up for execution in the Command Execution thread. 
	 */
	private void startListening(){
		
		try {
			
			// Authenticate before allowing access
			if(this.authenticate())
			{
				// Print connection acceptance
				out.print("Connection Accepted\r\n" + message +"\r\n");
				out.flush();
				
				new Thread(comExe).start();
				String temp;
				try 
				{
					while(listenOn)
					{
							temp = in.readLine();
							if (!temp.equals(null))
							{
								commands.add(temp);
								Log.i("LOG","Command Recieved from Client #" + id +": "+temp);
								temp = null;
							}
							if(comExe.isClosed()){
								comExe.close();
								this.disconnect();
							}
					} 	
				}
				
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				out.print("You have attempted too many times and are now being disconnected.\r\n");
				out.flush();
				this.disconnect();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Get ID
	 * 
	 * Gets the ID of the client
	 * @return the ID
	 */
	public int getId(){
		return id;
	}

	/**
	 * Alert
	 * 
	 * Sends an alert to the client
	 * @param alert The Alert
	 */
	public void alert(String alert)
	{
		out.print("!ALERT! "+ alert + " !ALERT!\r\n");
		out.flush();
	}
	
	/**
	 * Disconnect
	 * 
	 * Disconnect the client and close I/O
	 * @throws IOException If there is an issue closing it
	 */
	public void disconnect() throws IOException
	{
		this.alert("You are being disconnected by the Server\r\n");
		listenOn=false;
		comExe.close();
		out.close();
		in.close();
		client.close();
		
	}
	
	/**
	 * Disconnect
	 * 
	 * Disconnect the client and close I/O
	 * @param message The Disconnection reason
	 * @throws IOException If there is an issue closing it
	 */
	public void disconnect(String message) throws IOException
	{
		listenOn=false;
		this.alert(message);
		comExe.close();
		client.close();
		out.close();
		in.close();
		
		
	}
	
	/**
	 * Update Messages
	 * 
	 * Updates the list of Messages
	 * @param alert The New Message Alert
	 * @param messages List of Messages
	 */
	public void updateMessages(ArrayList<String[]> messages)
	{
		this.alert("New Text Message");
		
	}
	
	/**
	 * Get Client
	 * 
	 * Returns the Client Object
	 * @return The Client Object
	 */
	public boolean isClosed()
	{
		return client.isClosed() && comExe.isClosed();
	}
	
	/**
	 * Authenticate
	 * 
	 * This method is to authenticate a user on the phone. SECURITY!
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean authenticate() throws IOException
	{
		out.print("Please authenticate using server key\r\n");
		out.flush();
		int tries = 0;
		for(tries=0; tries < 3; tries ++)
		{
			String pass = in.readLine();
			if(pass.equals(key))
			{
				return true;
			}
			out.print("Incorrect Key. Please try again\r\n");
			out.flush();
		}
		return false;
	}
}
