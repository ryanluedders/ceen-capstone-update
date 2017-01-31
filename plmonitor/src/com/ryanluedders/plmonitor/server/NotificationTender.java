package com.ryanluedders.plmonitor.server;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Timer;

/*=====================================================================
 * NotificationTender
 * This is a java object that will be created in the system. This object
 * utilizes a timer, and specifies a very specific action to take at 
 * specified intervals of the timer, which will send the users SMS
 * messages
 *=====================================================================*/
public class NotificationTender 
{
	//booleans to enable output of debug and info Standard output
	public static boolean dbg = false;
	public static boolean info = false;
	
	//an action to perform when the timer hits its
	//specified interval. this is used to take statistics of
	//the system on a regular interval
	Action sendNotifications = new AbstractAction()
	{
		//the action to take
		public void actionPerformed(ActionEvent e) 
		{
			if(true){System.out.println("notification timer action");}
			//query db to find all requested notifications
			String getNotifications = "SELECT * FROM Notifications " +
				"WHERE NotTime < " + Long.toString(System.currentTimeMillis());
			ResultSet nots = DBMethods.queryDB(getNotifications);
			
			//check on what the notification requested
			try
			{
				//make sure there is something in the resultset
				if(nots.first())
				{
					//go through the entire results set.
					do
					{
						//get the record id
						int recordID = nots.getInt(1);
						//get the mobile number
						String mobileNum = nots.getString(2);
						//get the mobile provider
						String provider = nots.getString(3);
						//get the lot name
						String lotName = nots.getString(4);
						//get the number of spots (user specified threshold)
						int notLevel = nots.getInt(5);
						//get the time of notification
						long notTime = nots.getLong(6);
						//get the number of times to recur
						int recurTimes = nots.getInt(7);
						//get the interval they are to recur at.
						long recurInterval = nots.getLong(8);
						
						//hold the # of open spots
						int numSpots = 0;
						//get the new lot name (if they chose "Any")
						String NewLotName = "";
						
						//if they chose a specific lot, it will find the
						//number of opens in that lot.
						if(lotName.compareToIgnoreCase("Any") != 0)
						{
							//get the number of spots
							numSpots = ServerMethods.statGetOpenSpots(lotName);
							//store the lot name, to be sent to user
							NewLotName = lotName;
						}
						//if they didn't choose a specific lot
						else
						{
							//get the most open lot, and use that lot
							NewLotName = ServerMethods.statGetMostOpenLot();
							if(dbg){System.out.println("ANY - most open: " + NewLotName);}
							//get the number of spots for that lot.
							numSpots = ServerMethods.statGetOpenSpots(NewLotName);
						}
						
						//decrement the recur times
						recurTimes = recurTimes - 1;
						//if more notifications are requested.
						if(recurTimes > 0)
						{
							if(dbg){System.out.println("Adding recurring record: " + recurTimes + " more times");}
							//increase the notification time by the notification interval (in minutes a.k.a 60000 ms)
							notTime = System.currentTimeMillis() + (recurInterval * 60000);
						}
						
						if(dbg){System.out.println("deleting recordID " + recordID);}
						//get rid of the previous notification
						ServerMethods.statDeleteNotsForRecordID(recordID);
						
						//again, if more notifications are requested
						if(recurTimes > 0)
						{
							//add the next notification to the databse
							ServerMethods.statAddNotification(mobileNum, provider, lotName, 
									notLevel, notTime, recurTimes, recurInterval);
						}
						
						//system info purposes, notification of msg sent
						if(info){System.out.println("Sending notification to " + mobileNum + " at " + provider);}
						//if number of spots is above the user specified threshold
						if(numSpots >= notLevel)
						{
							//go ahead and send them the notification containing the lot
							//name, and the number of spots open in that lot.
							SendSms.sendSMSMessage(mobileNum, provider, "Lot Update", 
									"**P.L.M.S.**\n" + NewLotName + ": " + Integer.toString(numSpots) + " " +
									"Open Spots");
						}
					}
					//go through all notifications in this manner
					while(nots.next());
				}
			}
			catch(SQLException E)
			{
				System.out.println("Error :: Timed Notifications Not Sent " +
						"(NotificationTender.sendNotifications) :: " + E.toString());
			}
			//exports the mobile version of the current info
			ExportMobilePage.createMobilePage();
			//send the notification
		}
	};
	
	//A timer to keep the action happening over an interval
	Timer notificationTimer;
	
	/*==============================================================
	 * Default Constructor 
	 * Input: interval-milliseconds (int)
	 * Output: None
	 * Return: None
	 * The constructor takes an interval and constructs the timer
	 * for that interval - it doesn't start it, however.
	 *==============================================================*/
	public NotificationTender(int interval)
	{
		//Construct the timer, for the given interval
		notificationTimer = new Timer(interval, sendNotifications);
	}
	
	/*==============================================================
	 * Start
	 * Input: None
	 * Output: Starts the timer
	 * Return: None
	 * This starts the timer
	 *==============================================================*/
	public void start()
	{
		//Start the timer
		notificationTimer.start();
	}
}
