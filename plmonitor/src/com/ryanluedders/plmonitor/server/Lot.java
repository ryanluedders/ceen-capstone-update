package com.ryanluedders.plmonitor.server;

/*=====================================================================
 * Lot - A Java Object representing a parking lot
 * This is a java object that represents a parking lot. It can be used
 * for storing values into the DB, or just keeping track of them within
 * the system.
 *=====================================================================*/
public class Lot 
{
	//Integer representing the lot-id this is
	//used as the primary key in the Database
	int lotID;
	//The lot name is used as the primary identifier in most
	//of the user interface lists, and should be unique.
	String lotName;
	
	/*==============================================================
	 * Default Constructor
	 * Input: Lot ID (Integer)
	 * Output: Constructs a Lot
	 * Return: None
	 * given the integer ID of a lot, will construct a new lot
	 *==============================================================*/
	public Lot(int id)
	{
		//set lot id to lot-id passed in.
		setLotID(id);
	}
	
	/*==============================================================
	 * setLotID - sets the lot ID
	 * Input: Lot ID (Integer)
	 * Output: None
	 * Return: None
	 * changes the lot ID to something new, or sets the lot ID.
	 *==============================================================*/
	public void setLotID(int id)
	{
		//set the lot id to the lot-ID passed in
		lotID = id;
	}
	
	/*==============================================================
	 * setLotName - sets the lot name
	 * Input: Lot name (String)
	 * Output: None
	 * Return: None
	 * sets the lot name to the value passed.
	 *==============================================================*/
	public void setLotName(String name)
	{
		//set object lot name to lot name passed in
		lotName = name;
	}
	
	/*==============================================================
	 * getLotID - gets the ID of the lot
	 * Input: None
	 * Output: None
	 * Return: Lot ID (Integer)
	 * returns the integer ID of the lot - used as primary key in DB
	 *==============================================================*/
	public int getLotID()
	{
		//return the lot ID
		return lotID;
	}
	
	/*==============================================================
	 * getLotName - gets the Name of the lot
	 * Input: None
	 * Output: None
	 * Return: Lot Name (String)
	 * returns the lot name as a string. This is used as a primary
	 * identifier in the user interface.
	 *==============================================================*/
	public String getLotName()
	{
		//return the lot name
		return lotName;
	}
	
	/*==============================================================
	 * toDB() - creates an insert statement
	 * Input: None
	 * Output: None
	 * Return: SQL Insert Statement (String)
	 * This method takes the data stored within the object to create
	 * an insert statement to insert a lot with these characteristics
	 * into the database.
	 *==============================================================*/
	public String toDB()
	{
		String rval="";
		//create the insert statement, using the lotID, and the lotName
		rval = "INSERT INTO Lots (ParkingLotID,ParkingLotName) \n" +
			"VALUES (" + Integer.toString(lotID) + ",'" +
			lotName + "')";
		return rval;		
	}
}
