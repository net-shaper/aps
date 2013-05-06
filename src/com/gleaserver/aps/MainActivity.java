package com.gleaserver.aps;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Main Activity
 * @author aGleason
 * @date April 17, 2013
 * @version 1.0
 * 
 * This class is intended to be the "Front End" for the Android Phone Server
 * It is what Starts and stops the Service that manages everything. This class handles the
 * GUI and the User Interface.
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
public class MainActivity extends Activity 
{
	/** Shared Settings for this App */
	SharedPreferences settings;

	/**
	 * onCreate
	 * 
	 * What happens when the Activity is Created
	 * 
	 * @param savedInstanceState The Savestate of the App
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		
		ExecutorService pool = Executors.newFixedThreadPool(3);
		Future<String> future = pool.submit(new getIP());
		TextView t = new TextView(this);
		t=(TextView)findViewById(R.id.incoming); 
		
		try {
			t.setText(future.get()+ ":" + settings.getString("port", "5555"));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}

	/**
	 * onCreateOptionsMenu
	 * 
	 * Populates the "Options" menu
	 * 
	 * @param menu The Menu
	 * @return Options Active
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	  
	  Intent intent = new Intent();
	        intent.setClass(this, Prefs.class);
	        startActivityForResult(intent, 0); 
	  
	        return true;
	 }
	/**
	 * connect
	 * 
	 * What happens when the Start button is hit
	 * 
	 * @param view The view of this class
	 */
	public void connect(View view)
	{
		startService(new Intent(this, MainService.class));
	}
	
	/**
	 * Disconnect
	 * 
	 * What happens when the Stop button is hit
	 * 
	 * @param view The view of this class
	 */
	public void disconnect(View view)
	{
		stopService(new Intent(this, MainService.class));
	}

}
