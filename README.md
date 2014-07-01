NOTE
===
This version of the application is very inefficient. It is currently being rebuilt from the ground up to have more support and be a lot more efficient. 

APS
===

Android Phone Server

The Android Phone Server is a means for conecting your Android device remotely to other devices using a wifi socket. 
At the moment this allows you to check incoming messages, write sms messages, look at your contacts and more.
It is currently in development so please bear with me as changes are made.

Temporary Protocol List (By Command):

"HELO" => Connection Confirmation

"CON" => Returns list of contacts. Colon Delimited
"XCON" => Returns list of contacts. XML Format

"RET" => Returns list of messages seen by the application. Colon Delimited
"XRET" => Returns list of messages seen by the application. XML Format
"get last" => Returns the last message seen by application. Beutified by Ascii

"RCPT ##########" => Adds a Recipient to list of contacts to send next message to. Recursive (Will accept new numbers until MSG command)
    '#' Represents Integer Digit
"MSG" => Inititates Message Body Mode. Upon completion will send message to all Recipients. MSG Mode terminated by period on its own line.




Copyright 2013 Alec Gleason

This file is part of Android Phone Server.

    Android Phone Server is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Android Phone Server is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Android Phone Server.  If not, see <http://www.gnu.org/licenses/>.
