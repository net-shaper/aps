package com.gleaserver.aps;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Receiver
 * @author aGleason
 * @date April 21, 2013
 * @version 1.0
 * 
 * Listens on the phone for new messages from the network
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
public class Receiver extends BroadcastReceiver{

	/** The list of messages seen */
    private ArrayList<String[]> messages = new ArrayList<String[]>();
    
    /** The number of messages */
    private AtomicInteger num = new AtomicInteger(0);
    
    /** The state of the phone */
    private int state = 0;

    /**
     * On Receive
     * 
     * Gets called when there is a new Intent on the phone
     */
    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	Log.i("LOG", "I've gotten an Intent!" + intent.getAction());
    	// If Phone State Changes (incoming Call etc)
    	
    	// If it is a text message seen...
        if(intent.getAction()=="android.provider.Telephony.SMS_RECEIVED")
        {
            Bundle bundle = intent.getExtras();          
            SmsMessage[] msgs = null;
            String msg_from = "";
            String msgBody = "";
            // If the intent data isn't empty
            if (bundle != null)
            {
                
            	// Convert it to strings for the messages list
                try
                {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        msgBody = msgs[i].getMessageBody();
                    }
                    
                }
                catch(Exception e)
                {
//                            Log.d("Exception caught",e.getMessage());
                }
                
                // Add the new message to the list
                messages.add(new String[] {msg_from, msgBody, (""+ System.currentTimeMillis() / 1000L)});
                Log.i("LOG", "Newest Message: " + messages.get(messages.size()-1)[0] + " : "+ messages.get(messages.size()-1)[1] + " : "+ messages.get(messages.size()-1)[2]);
                num.getAndIncrement();
            }
        }
    }
    
    /**
     * Get Num Messages
     * 
     * Gets the number of messages seen
     *
     * @return the number of messages 
     */
    public int getNumMessages()
    {
    	return num.get();
    }
    
    /**
     * Get Messages
     * 
     * Gets the messages seen across the network
     * @return the list of messages
     */
    public ArrayList<String[]> getMessages()
    {
    	return messages;
    }
    
    /**
     * Returns true if there is an incoming cal
     * 
     * @return if there is an incoming call
     */
    public boolean newCall()
    {
    	return(state == 1);
    }
}