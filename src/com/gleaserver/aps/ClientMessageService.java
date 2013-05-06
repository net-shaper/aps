package com.gleaserver.aps;

import java.util.ArrayList;

import android.util.Log;

/**
 * Client Message Service
 * @author aGleason
 * @version 1.0
 * @date April 22, 2013
 * 
 * This class is intended to listen to and manage new messages coming across the network
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
public class ClientMessageService implements Runnable
{
	/** The Receiver of new messages */
	private Receiver receiver;
	
	/** The list of all messages */
	private ArrayList<String[]> messages = new ArrayList<String[]>();
	
	/** The control for the embedded listener */
	private boolean embeddedListen = true;
	
	/** The Command Service for the client */
	private ClientCommandService com;
	
	/**
	 * New Client Message Service
	 * @param r The shared receiver object
	 * @param m the list of messages seen by the server
	 * @param c the the command service
	 */
	public ClientMessageService(Receiver r, ArrayList<String[]> m, ClientCommandService c)
	{
		messages = m;
		receiver = r;
		com = c;
	}
	
	/**
	 * Run
	 * 
	 * Runs the embedded listener for new messages 
	 */
	@Override
	public synchronized void run() 
	{
		Log.i("LOG", "Started CMS");

		while(embeddedListen)
		{

			int temp = receiver.getNumMessages();
			if (temp > messages.size())
			{
				messages = receiver.getMessages();
				com.pushMessages(messages);
				com.alert("New Message");
			}
			else if (temp != messages.size())
			{
				messages = receiver.getMessages();
				com.pushMessages(messages);
				com.alert("New Message");
			}

		}
		
	}

}
