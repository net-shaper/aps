package com.gleaserver.aps;


import java.io.IOException;
import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Main Service
 * @author aGleason
 * @date April 17, 2013
 * @version 1.0
 * 
 * This class is intended to Start and manage the Server Threads;
 * to be the common place to manage everything service side. Also
 * allows for Android functions to be implemented in a cleaner fashion.
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
public class MainService extends Service
{
	/** The port to connect on. Set to default value */
	private int port;
	
	/** The connection threshold number. Set to default value */
	private int conThreshold = 5;
	
	/** The Custom Client Greeting. Set to default value */
	private String message = "Welcome to my Server!";
	
	/** The Server Object */
	Server server;
	
	/** The Server Thread */
	Thread serverTh;
	
	/** List of all incoming Messages seen by the server */
	static ArrayList<String[]> messageList= new ArrayList<String[]>();
	
	/** A list of all contacts in the phone */
	static ArrayList<String[]> allContacts = new ArrayList<String[]>();

	/** The Receiver */
	Receiver receiver; 
	
	/** The Passkey to access the server */
	private String key;
	
	/** The Preferences set in main activity */
	SharedPreferences preferences;
	
	
	/**
	 * onStart
	 * 
	 * Stuff that happens when the Service is Started
	 * 
	 * @param intent The Intent
	 * @param flags The Flags
	 * @param startId The Start ID
	 */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) 
    {
    	Log.i("LOG", "Main Service has Started");
    	preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	port = Integer.parseInt(preferences.getString("port", "5555"));
    	key = preferences.getString("passkey", "bestproject");
    	message = preferences.getString("welcomemessage", "Welcome to my server!");
    	
    	this.acquireContacts();
    	IntentFilter intentFilter = new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED");
    	receiver = new Receiver();
    	this.registerReceiver(receiver, intentFilter);
    	// Create and start the server
    	server = new Server(port, conThreshold, message, allContacts, receiver, key);
    	serverTh = new Thread(server);
    	serverTh.start();

    	return super.onStartCommand(intent, flags, startId);
    }

    /**
     * onDestroy
     * 
     * Stuff that happens when the Service is Killed
     */
    @Override
    public void onDestroy() {
    	this.unregisterReceiver(receiver);
    	try {
			server.stopServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Log.i("LOG", "Main Service has Ended");
    }

    /**
     * onBind
     * 
     * What happens when the service is bound
     * 
     * @param arg0 Intent Argument
     * @return IBinder an IBinder
     */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Acquire Contacts
	 * 
	 * Gets a list of contact information
	 */
	public void acquireContacts()
	{
		Log.i("LOG","I Got All the Contacts!");
		Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
		while (phones.moveToNext())
		{
			String temp = "";
			if(ContactsContract.CommonDataKinds.Photo.PHOTO.equals(null))
			{
				temp = android.util.Base64.encodeToString(phones.getBlob(phones.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO)),0);
			}
			else
			{
				temp = "NONE";
			}
			allContacts.add(new String[]{
					phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
					phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
					temp});		
		}
		phones.close();
	}
	
	/**
	 * Get Contacts
	 * 
	 * Gets all the contacts
	 * 
	 * @return the contacts
	 */
	public static ArrayList<String[]> getContacts()
	{
		return allContacts;
	}
	
	

}
