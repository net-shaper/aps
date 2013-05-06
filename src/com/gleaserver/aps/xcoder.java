package com.gleaserver.aps;

import java.util.ArrayList;

/**
 * Encoder
 * @author aGleason
 * @date April 22, 2013
 * @version 1.0
 * 
 * Intended to encode and decode XML Messages
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
public class xcoder 
{
	
	/**
	 * XML-Message (single)
	 * 
	 * Encode a single message into XML
	 * 
	 * @param id The Message ID
	 * @param from The Sender
	 * @param time Unix time message was received
	 * @param body Body of the message
	 * @return the formatted string
	 */
	public static String xmsg(int id, String from, int time, String body)
	{
		return "<?xml version=\"1.0\"?><message id=\""+ id + "\">" 
				+ "</id><from>" + from 
				+ "</from><time>" + time 
				+ "</time><body>" + body 
				+ "</body></message>\r\n";
	}
	
	/**
	 * XML-Messages (multiple)
	 * 
	 * Encode all messages into XML
	 * 
	 * @param messages List of all messages
	 * @return the formatted string
	 */
	public static String xmsgs(ArrayList<String[]> messages)
	{
		String toReturn = "<?xml version=\"1.0\"?><messages>";
		
		for (int i = 0; i < messages.size(); i ++)
		{
			toReturn +="<message id=\"" + i + "\">"  
					 + "<from>" + messages.get(i)[0] 
					 + "</from><time>" + messages.get(i)[2] 
					 + "</time><body>" + messages.get(i)[1]
					 + "</body></message>";
		}
		
		return toReturn + "</messages>\r\n";
	}
	
	/**
	 * XML-Contacts
	 * 
	 * Encode all contacts into XML
	 * 
	 * @param contacts ALl the contacts
 	 * @return the formatted string
	 */
	public static String xcon(ArrayList<String[]> contacts)
	{
		String toReturn = "<?xml version=\"1.0\"?><contacts>";
		
		for (int i = 0; i < contacts.size(); i ++)
		{
			toReturn +="<contact id=\"" + i + "\">"
					 	+ "<name>" + contacts.get(i)[0] 
					 	+ "</name><primarynumber>" + contacts.get(i)[1]
					 	+ "</primarynumber></contact>";
		}
		
		return toReturn + "</contacts>\r\n";
	}
	
	/**
	 * XML-Advanced-Contacts
	 * 
	 * Encodes all contacts with photo stream into XML
	 * 
	 * @param contacts
	 * @return the formatted string
	 */
	public static String xacon(ArrayList<String[]> contacts)
	{
		String toReturn = "<?xml version=\"1.0\"?><advcontacts>";
		
		for (int i = 0; i < contacts.size(); i ++)
		{
			toReturn +="<contact id=\"" + i + "\">"
					 	+ "<name>" + contacts.get(i)[0] 
					 	+ "</name><primarynumber>" + contacts.get(i)[1]
					 	+ "</primarynumber><picture>"+ contacts.get(i)[2]
					 	+ "</picture></contact>";
		}
		
		return toReturn + "</advcontacts>\r\n";
	}

}
