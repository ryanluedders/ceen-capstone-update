package com.luedders.server;

/*=====================================================================
 * View - A java object representing a View
 * This is a java object representing a view. This object will keep
 * track of a viewID, viewName, and corresponding lotID for all the views
 * in the system database. A view will also be associated with an image
 * or image location that will be used to interpret to get spot information.
 *=====================================================================*/
public class View 
{
	//the view ID of a view, used as primary key in DB table
	int viewID;
	//the name of the view, used as primary identifier to user
	String viewName;
	//the ID of the associated lot - used as foreign key in DB
	int lotID;
	
	//the image file associated with the view
	String imgFile;
	
	/*==============================================================
	 * Default Constructor
	 * Input: View ID (int), Lot ID(int)
	 * Output: None
	 * Return: None
	 * Constructs a new view with specified view ID and corresponding
	 * Lot ID
	 *==============================================================*/
	public View(int vID, int lID)
	{
		//set the view and lot ID to specified values
		setViewID(vID);
		setLotID(lID);
	}
	
	/*==============================================================
	 * setViewName - sets the name of the view
	 * Input: view Name (String)
	 * Output: None
	 * Return: None
	 * Sets the object view name to whatever is passed into the
	 * function. The view name is used by the user to recognize
	 * different views in the interface.
	 *==============================================================*/
	public void setViewName(String name)
	{
		//set the view name to the passed value
		viewName = name;
	}
	
	/*==============================================================
	 * setViewID - sets the ID of the view
	 * Input: view ID (int)
	 * Output: None
	 * Return: None
	 * sets the view ID to the value passed in. The view ID is used
	 * as the primary key for storing a view into the database. so,
	 * it is important that this view ID be unique.
	 *==============================================================*/
	public void setViewID(int vID)
	{
		//set the viewID to the passed in value
		viewID = vID;
	}
	
	/*==============================================================
	 * setLotID - sets which lot ID corresponds to this view
	 * Input: Lot ID (int)
	 * Output: None
	 * Return: None
	 * sets the Lot ID to the ID of the lot that this view is
	 * associated with. This must match a lot that is already
	 * in the database for database integrity reasons.
	 *==============================================================*/
	public void setLotID(int lID)
	{
		//set the lotID of this object to the passed value.
		lotID = lID;
	}
	
	/*==============================================================
	 * setImageFile - sets the view to associate with an image
	 * Input: file name (String)
	 * Output: None
	 * Return: None
	 * takes in a filename/path as an argument and sets the
	 * image associated with this view to that filename.
	 *==============================================================*/
	public void setImageFile(String fName)
	{
		//set the image file of this name to the passed in value
		imgFile = fName;
	}
	
	/*==============================================================
	 * getViewID - gets the view ID
	 * Input: None
	 * Output: None
	 * Return: View ID (int)
	 * returns the integer ID of this view
	 *==============================================================*/
	public int getViewID()
	{
		//return the viewID
		return viewID;
	}
	
	/*==============================================================
	 * getLotID - gets the associated Lot ID
	 * Input: None
	 * Output: None
	 * Return: Lot ID (int)
	 * returns the ID of the lot associated with this view
	 *==============================================================*/
	public int getLotID()
	{
		//return the lotID associated with the view
		return lotID;
	}
	
	/*==============================================================
	 * getViewName - gets the name of the view
	 * Input: None
	 * Output: None
	 * Return: The view Name (String)
	 * returns the name of the view
	 *==============================================================*/
	public String getViewName()
	{
		//return the name of the view
		return viewName;
	}
	
	/*==============================================================
	 * toDB - returns an insert statement used to put into DB
	 * Input: None
	 * Output: None
	 * Return: SQL Insert Statement (String)
	 * This function returns an SQL insert statement to insert a view
	 * with this object's properties into an SQL database.
	 *==============================================================*/
	public String toDB()
	{
		String rval="";
		//create the insert statement
		rval = "INSERT INTO Views (ViewID,ViewName,ViewLot,ImgFile) \n" +
			"VALUES (" + Integer.toString(viewID) + ",'" +
			viewName + "'," +
			Integer.toString(lotID) + ",'" +
			imgFile + "')";
		return rval;
	}
}
