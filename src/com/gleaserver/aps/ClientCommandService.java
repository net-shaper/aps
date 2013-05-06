package com.gleaserver.aps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Client Command Service 
 * @author aGleason
 * @date April 22, 2013
 * @version 1.0
 * 
 * Intended to be run as an embedded thread, this class interprets and executes new commands seen across the network.
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
public class ClientCommandService implements Runnable
{
	/** The Input from the Client */
	private BufferedReader in;
	
	/** The Output from the Server */
	private PrintWriter out;
	
	/** The Client */
	private Socket client;
	
	/** The Queue of Commands */
	private Queue<String> commands = new ConcurrentLinkedQueue<String>();
	
	/** Controls Command loop */
	private boolean embeddedCommands = true;
	
	/** The Current command being viewed */
	private String curCommand;
	
	/** The Split Command */
	String[] splitCom;
	
	/** Switch control for case */
	int comCase = 0;
	
	/** List of Recipients to Send Messages To */
	ArrayList<String> toSendTo = new ArrayList<String>();
	
	/** The Message to send */
	String toSendMsg;
	
	/** A list of all contacts in the phone */
	static ArrayList<String[]> allContacts = new ArrayList<String[]>();
	
	/** A List of all messages in the phone */
	private ArrayList<String[]> messages = new ArrayList<String[]>();
	
	/**
	 * The Client Command Service
	 * 
	 * @param c The Client Socket
	 * @param b The Output
	 * @param p The Input
	 * @param q the Queue of commands
	 * @param ac the list of all contacts in the phone
	 */
	public ClientCommandService(Socket c, BufferedReader b, PrintWriter p, Queue<String> q, ArrayList<String[]> ac)
	{
		client = c;
		in = b;
		out = p;
		commands = q;
		allContacts = ac;
	}
	
	/**
	 * Update Messages
	 * 
	 * Updates the list of text messages stored
	 * @param messages The New Messages
	 */
	public void updateMessages(ArrayList<String[]> messages)
	{
		
	}

	@Override
	public void run() {
		while(embeddedCommands)
		{
			if(commands.peek() != null)
			{
				curCommand = commands.poll();
				Log.i("LOG","Next command: " + curCommand);
				
				// If the command has more than one word segregate the first word. 
				if(curCommand.contains(" "))
				{
					splitCom = curCommand.split(" ",2);
				}
				else
				{
					splitCom = new String[2];
					splitCom[0]=curCommand;
					splitCom[1]="";
				}
				
				switch (comCase)
				{
					// No Prior Commands Case
					case 0:
						// Init New Message 
						if(splitCom[0].equals("RCPT"))
						{
							// Sets the current case to 1 (looking for more message stuff)
							comCase = 1;
							if (splitCom[1].matches("[0-9]+") && splitCom[1].length() == 10) 
							{
								// Send The Text Message
								Log.i("LOG", "Recipient Added: " + splitCom[1]);
								toSendTo.add(splitCom[1]);
								out.print("Recipient Accepted\r\n");
							}
							else 
							{
								out.print("Invalid Recipient\r\n");
							}
							
						}
						
						// Server Greeting Command
						else if(splitCom[0].equals("HELO"))
						{
							out.print("Why Hello there! I'm an Android.\r\n");
						}
						
						// Exit the Server
						else if(splitCom[0].equals("EXIT"))
						{
							out.print("Goodbye!\r\n");
							out.flush();
							try {
								out.close();
								in.close();
								client.close();
							} catch (IOException e) {
								
								e.printStackTrace();
							}
						}
						
						// Retrieve List of new Text Messages
						else if(splitCom[0].equals("RET"))
						{
							out.print("Messages: \r\n");
							for(int i = 0; i < messages.size(); i ++)
							{
								out.print(i+":"+messages.get(i)[0]+":"+messages.get(i)[1]+"\r\n");
							}
							out.print("End of Messages\r\n");
						}
						
						// Retrieve list of new Text messages in XML
						else if(splitCom[0].equals("XRET"))
						{
							out.print(xcoder.xmsgs(messages));
						}
						
						// Retrieve list of Contacts 
						else if(splitCom[0].equals("CON"))
						{
							for( int i = 0; i < allContacts.size(); i ++)
							{
								out.print(allContacts.get(i)[0]+":"+allContacts.get(i)[1]+"\r\n");
							}
							out.print("End Contacts\r\n");
						}
						else if(splitCom[0].equals("XCON"))
						{
							out.print(xcoder.xacon(allContacts));
						}
						
						// get latest and make it pretty
						else if(splitCom[0].equals("get") && splitCom[1].equals("last"))
						{
							if(messages.size()!=0)
							{
								out.print("==============================\r\n");
								out.print("| " + messages.get(messages.size()-1)[0] + "                 |\r\n");
								out.print("==============================\r\n");
								out.print(messages.get(messages.size()-1)[1]+"\r\n");
								out.print("==============================\r\n");
							}
							else
							{
								out.print("No Messages!\r\n");
							}
						}
						
						// Gets the last 3 if there are any!
						else if(splitCom[0].equals("get") && splitCom[1].equals("last") && splitCom[2].equals("3"))
						{
							if(messages.size()!=3)
							{
								out.print("==============================\r\n");
								out.print("| " + messages.get(messages.size()-3)[0] + "                 |\r\n");
								out.print("==============================\r\n");
								out.print(messages.get(messages.size()-1)[1]+"\r\n");
								out.print("==============================\r\n");
							}
							
							if(messages.size()!=2)
							{
								out.print("==============================\r\n");
								out.print("| " + messages.get(messages.size()-2)[0] + "                 |\r\n");
								out.print("==============================\r\n");
								out.print(messages.get(messages.size()-1)[1]+"\r\n");
								out.print("==============================\r\n");
							}
							
							if(messages.size()!=0)
							{
								out.print("==============================\r\n");
								out.print("| " + messages.get(messages.size()-1)[0] + "                 |\r\n");
								out.print("==============================\r\n");
								out.print(messages.get(messages.size()-1)[1]+"\r\n");
								out.print("==============================\r\n");
							}
							else
							{
								out.print("No Messages!\r\n ");
							}
							
						}
						
						// The I Don't know what you are trying to do case
						else
						{
							out.print("Sorry, that was not understood.\r\n");
						}
						
						break;
					
					// Message IN Progress Case
					case 1:
						
						// Adding Multiple Recipients
						if(splitCom[0].equals("RCPT"))
						{
							if (splitCom[1].matches("[0-9]+") && splitCom[1].length() == 10) 
							{
								// Send The Text Message
								Log.i("LOG", "Recipient Added: " + splitCom[1]);
								toSendTo.add(splitCom[1]);
								out.print("Recipient Accepted\r\n");
							}
							else 
							{
								out.print("Invalid Recipient\r\n");
							}
						}
						
						// Adding Message Data and Sending
						else if(splitCom[0].equals("MSG"))
						{
							// Set to recieve data mode
							comCase=2;
							out.print("Please enter your message data. End with a period on its own line.\r\n");
							toSendMsg = "";
						}
						
						else
						{
							out.print("Sorry, that was not understood.\r\n");
						}
						break;
						
					case 2:
						if(splitCom[0].equals("."))
						{
							//TODO: Call Send Message Function MAKE IT BETTER
							comCase=0;
							Log.i("LOG","Message To Send: " + toSendMsg);
							Log.i("LOG","Final Recipients: " + toSendTo.toString());
							
							for(int i = 0; i < toSendTo.size(); i ++)
							{
								//PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(), 0); 
								SmsManager smsMan = SmsManager.getDefault();
								smsMan.sendTextMessage(toSendTo.get(i), null, toSendMsg, null, null);
							}
							
							toSendMsg = "";
							
							
							
							//toSendTo = new ArrayList<String>();
							toSendTo.clear();
							out.print("Message has been sent.\r\n");
						}
						else
						{
							toSendMsg += curCommand;
						}
						break;
						
						
						
						
					default: 
						out.print("Case Error");
						break;
				}
			}
			out.flush();
			curCommand = "";
			}
			Log.i("LOG", "ClientCommand had Exited");
		}
	
		/**
		 * Is Closed
		 * Checks if the connection is still active
		 * @return true if connection is closed
		 */
		public boolean isClosed()
		{
			return client.isClosed();
		}
		
		/**
		 * Close
		 * 
		 * Closes the connection to the client
		 * @throws IOException If there is an issue closing everything
		 */
		public void close() throws IOException
		{
			embeddedCommands=false;
			client.close();
			in.close();
			out.close();
			
			
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
		 * Push Messages
		 * 
		 * Allows the Message Listener to push new messages to be served to the client
		 * @param m
		 */
		public void pushMessages(ArrayList<String[]> m)
		{
			messages = m;
		}
		
}
