package com.ryanluedders.plmonitor.server;

/*=====================================================================
 * Notification - A Java Object representing an sms notification
 * This is a java object that represents an sms message schedule.
 * it includes the mobile number to send the notification to, the
 * provider the user specifies for their number. The name of the lot
 * to be notified of. The number of spots that must be available for a
 * notification to occur. The time at chich a notification is supposed
 * to occur, the number of times it is set to recur, and the interval
 * at which the notification should recur.
 *=====================================================================*/
public class Notification 
{
	//db primary key (ID number)
	int notificationNumber;
	//mobile number to notify with updated info
	String mobileNumber;
	//provider for the specified mobile number
	String mobileProvider;
	//name of the lot to be notified of, or "Any"
	//for the most open lot.
	String lotName;
	//number of spots that have to be open for the notification to occur
	int notificationLevel;
	//time for the system to try and send out the notification
	long notificationTime;
	//the number of times for the notification to be sent
	int timesToRecur;
	//the interval between successive notifications.
	long intervalToRecur;
	
	/*==============================================================
	 * Default Constructor
	 * Input: Mobile number (string) mobile provider (string) lot name (string)
	 * number of spots (int) notification time (long) times to recur (int)
	 * interval to recur (long)
	 * Output: constructs a notification object
	 * Return: None
	 * given all the parameters required to send an SMS message through
	 * the system, this will create a notification object.
	 *==============================================================*/
	public Notification(String mN, String mP, String lN, 
			int nL, long nT, int tR, long iR)
	{
		//query the DB to automatically find the next Key (ID #)
		notificationNumber = ServerMethods.getNextKeyNotifications();
		//assign the mobile number
		mobileNumber = mN;
		//assign the mobile provider
		mobileProvider = mP;
		//assign the lot name
		lotName = lN;
		//assign the number of spots
		notificationLevel = nL;
		//assign the notification time
		notificationTime = nT;
		//assign the times to recur
		timesToRecur = tR;
		//assign the interval to recur.
		intervalToRecur = iR;
	}
	
	/*==============================================================
	 * toDB - constructs an SQL INSERT statement
	 * Input: none
	 * Output: none
	 * Return: A string to INSERT the notification into the DB
	 * Will take all of the object's stored parameters, and construct
	 * an SQL insert statement to record the notification in the DB
	 *==============================================================*/
	public String toDB()
	{
		//start out simple
		String rval="";
		//create the insert statement
		rval = "INSERT INTO Notifications (notNum,mobileNum,Provider,LotName," +
				  "NotLevel,NotTime,RecurTimes,RecurInterval) \n" +
				"VALUES (" +
				   Integer.toString(notificationNumber) + "," +
				   "'" + mobileNumber + "'," +
				   "'" + mobileProvider + "'," +
				   "'" + lotName + "'," +
				   Integer.toString(notificationLevel) + "," +
				   Long.toString(notificationTime) + "," +
				   Integer.toString(timesToRecur) + "," +
				   Long.toString(intervalToRecur) + ")";
		//return the statement
		return rval;
	}
}
