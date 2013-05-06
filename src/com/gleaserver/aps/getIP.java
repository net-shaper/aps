package com.gleaserver.aps;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

/**
 * Get IP
 * @author aGleason
 * @date 30/04/2013
 * @version 1.0
 * 
 * Get IP Class is a callable to just get an IP Address for the phone
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
public class getIP implements Callable<String>{

	/**
	 * Call
	 * 
	 * Gets an IP Address
	 * 
	 * @return the IP Address
	 */
	@Override
	public String call() throws Exception {
		try {
			InetAddress iA=InetAddress.getLocalHost();
		    return iA.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
