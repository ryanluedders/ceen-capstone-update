package com.ryanluedders.plmonitor.server;

/*=====================================================================
 * Statistic
 * This class is a java object that represents a statistic that is
 * taken by the parking lot monitoring system, it has a primary key, time
 * taken, and lot ID as identifiers, and it keeps track of the number of
 * spots open and the number of spots taken.
 *=====================================================================*/
public class Statistic 
{
	//the ID of the stat (Primary Key)
	int statID;
	//the time the stat was taken
	long statTime;
	//the lot ID the stat is associated with
	int lotID;
	
	//number of spots open
	int spotsOpen;
	//number of spots taken
	int spotsTaken;
	
	/*==============================================================
	 * Default Constructor
	 * Input: none
	 * Output: none
	 * Return: None
	 * This method constructs the new statistic, giving it default
	 * values, letting key & time be handled elsewhere in the program
	 *==============================================================*/
	public Statistic()
	{
		//set to default values.
		setStatID(-1);
		setStatTime(-1);
	}
	
	/*==============================================================
	 * Constructor
	 * Input: stat ID (int) stat Time (long) lot ID (int) 
	 * number of spots open (int) number of spots taken (int)
	 * Output: creates a new stat object
	 * Return: None
	 * This method constructs the new statistic, giving it default
	 * values, letting key & time be handled elsewhere in the program
	 *==============================================================*/
	public Statistic(int sID, long sTime, int lID, int open, int taken)
	{
		//set the stat ID
		setStatID(sID);
		//set the stat Time
		setStatTime(sTime);
		//set the lot ID
		setLotID(lID);
		//set the # of spots open
		setSpotsOpen(open);
		//set the # of spots taken
		setSpotsTaken(taken);
	}
	
	/*==============================================================
	 * setStatID
	 * Input: statistic ID (SQL Table Key) (int)
	 * Output: none
	 * Return: None
	 * this method takes a stat ID and sets this object to have
	 * that stat ID
	 *==============================================================*/
	public void setStatID(int sID)
	{
		//assign the specified ID
		statID = sID;
	}
	
	/*==============================================================
	 * getStatID
	 * Input: none
	 * Output: none
	 * Return: the Statistic ID (int)
	 * this method returns the stat ID stored in the object.
	 *==============================================================*/
	public int getStatID()
	{
		return statID;
	}
	
	/*==============================================================
	 * setStatTime
	 * Input: stat time (long)
	 * Output: none
	 * Return: none
	 * sets the time stored in this stat object to the specified time
	 *==============================================================*/
	public void setStatTime(long sTime)
	{
		//assign the stat time
		statTime = sTime;
	}
	
	/*==============================================================
	 * getStatTime
	 * Input: none
	 * Output: none
	 * Return: stat time (long)
	 * gets the stat time stored in this stat object
	 *==============================================================*/
	public long getStatTime()
	{
		//return stored stat time
		return statTime;
	}
	
	/*==============================================================
	 * setLotID
	 * Input: lot ID (int)
	 * Output: none
	 * Return: none
	 * takes an integer for the lot ID and stores it as the Lot ID
	 * of this stat object
	 *==============================================================*/
	public void setLotID(int lID)
	{
		//assign the lot ID
		lotID = lID;
	}
	
	/*==============================================================
	 * getLotID
	 * Input: none
	 * Output: none
	 * Return: lot ID (int)
	 * returns the lot ID associated with this Stat object
	 *==============================================================*/
	public int getLotID()
	{
		//return the associated lot ID
		return lotID;
	}
	
	/*==============================================================
	 * setSpotsOpen
	 * Input: number of open spots (int)
	 * Output: none
	 * Return: none
	 * sets the number of spots open in the lot associated with this
	 * stat
	 *==============================================================*/
	public void setSpotsOpen(int open)
	{
		//set the # of spots open
		spotsOpen = open;
	}
	
	/*==============================================================
	 * getSpotsOpen
	 * Input: none
	 * Output: none
	 * Return: number of open spots (int)
	 * gets the number of spots that are open in this stat object.
	 *==============================================================*/
	public int getSpotsOpen()
	{
		return spotsOpen;
	}
	
	/*==============================================================
	 * setSpotsTaken
	 * Input: number of taken spots (int)
	 * Output: none
	 * Return: none
	 * gets the number of spots that are taken in this stat object
	 *==============================================================*/
	public void setSpotsTaken(int taken)
	{
		//return the number of taken spots
		spotsTaken = taken;
	}
	
	/*==============================================================
	 * getSpotsTaken
	 * Input: none
	 * Output: none
	 * Return: the number of taken spots (int)
	 * gets the number of spots that are taken in this stat object
	 *==============================================================*/
	public int getSpotsTaken()
	{
		return spotsTaken;
	}
	
	/*==============================================================
	 * getTotalSpots()
	 * Input: none
	 * Output: none
	 * Return: the total number of spots
	 * gets the total number of spots associated with this stat object.
	 *==============================================================*/
	public int getTotalSpots()
	{
		return spotsOpen + spotsTaken;
	}
	
	/*==============================================================
	 * toString()
	 * Input: none
	 * Output: none
	 * Return: A string representation of this statistic
	 * returns a string showing the stat & lot ID's as well as the 
	 * number of open and taken spots
	 *==============================================================*/
	public String toString()
	{
		//return the string
		return getStatID() + " " + getStatTime() +
		" " + getLotID() + " OPEN:" + getSpotsOpen() + " TAKEN:" + getSpotsTaken();
	}
	
	/*==============================================================
	 * toDB()
	 * Input: none
	 * Output: none
	 * Return: an SQL insert string
	 * returns a string that can be run as an SQL insert statement to
	 * insert this statistic and all of its associated data into
	 * the SQL database.
	 *==============================================================*/
	public String toDB()
	{
		return "INSERT INTO Stats (StatID, StatTime, LotID," +
				"SpotsOpen, SpotsTaken) \n" +
				"VALUES (" + getStatID() + "," + getStatTime() + "," + getLotID() + "," + getSpotsOpen() +
				"," + getSpotsTaken() + ")";
	}
}
