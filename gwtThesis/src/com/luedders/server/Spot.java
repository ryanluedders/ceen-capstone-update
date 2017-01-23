package com.luedders.server;

/*=====================================================================
 * Spot - A java object representing a Spot
 * This is a java object that represents a spot. This will keep track
 * of the spotID, the primary key of the Database, and the spot name, 
 * the primary identifier for the user interface, as well as the viewID,
 * which references the views already in the database, to associate this
 * spot with a certain view.
 *=====================================================================*/
public class Spot 
{
	//int spotID, the spot ID and primary key for the DB
	int spotID;
	//the spot name is the primary identifier for the user
	String spotName;
	//the viewID references the Views table, and correlates this
	//spot to a specific view within a lot.
	int viewID;
	//the physical location of the parking spot - the row
	int spotPhysicalRow;
	//the physical location of the parking spot - the column
	int spotPhysicalCol;
	//the X & Y locations on the image
	int spotViewTopX;
	int spotViewTopY;
	int spotViewBotX;
	int spotViewBotY;
	//spot special conditions - such as handicapped, compact, etc...
	String spotSpecialCondition;
	
	/*==============================================================
	 * Default Constructor
	 * Input: Spot ID (Integer)
	 * Output: None
	 * Return: None
	 * given the integer ID of the spot, this will create a new spot
	 * with a blank name.
	 *==============================================================*/
	public Spot(int id)
	{
		//set the spot ID to what is passed
		setSpotId(id);
		//start with a blank name
		setSpotName("");
	}
	
	/*==============================================================
	 * setSpotID - set the ID number of the spot
	 * Input: Spot ID (Integer)
	 * Output: None
	 * Return: None
	 * given the integer ID of the spot, this will set the spot ID
	 * to what is passed in. The spot ID is used as the primary key
	 * of the spot in the database
	 *==============================================================*/
	public void setSpotId(int id)
	{
		//set the spotID to what is passed in.
		spotID = id;
	}
	
	/*==============================================================
	 * setSpotName - set the name of the spot
	 * Input: Spot Name (String)
	 * Output: None
	 * Return: None
	 * given the desired name of the spot, this object's spotName
	 * will be set to that name.
	 *==============================================================*/
	public void setSpotName(String name)
	{
		//set the spot name to the desired name
		spotName = name;
	}
	
	/*==============================================================
	 * setView - set the view ID the spot is associated with
	 * Input: view ID (Integer)
	 * Output: None
	 * Return: None
	 * given the view ID of the view that this spot is a part of
	 * (because the spot must belong to a view) per the database
	 * foreign key requirements, this function will set viewID to
	 * the desired value
	 *==============================================================*/
	public void setView(int id)
	{
		//set viewID to passed in value
		viewID = id;
	}
	
	/*==============================================================
	 * setSpotPhysicalLoc - sets the location of a spot in the lot
	 * Input: row # (int), col # (int)
	 * Output: None
	 * Return: None
	 * given the row and column that this spot is physically located
	 * in, this function will set the object to note its location.
	 *==============================================================*/
	public void setSpotPhysicalLoc(int row, int col)
	{
		//set row & column to passed in values
		spotPhysicalRow = row;
		spotPhysicalCol = col;
	}
	
	/*==============================================================
	 * setSpotViewLoc - sets the spot location within an image
	 * Input: x,y coordinates (both int)
	 * Output: None
	 * Return: None
	 * given the x and y coordinates on an image that this spot is
	 * located at, it will set the objects to know where on the image
	 * this spot is located at.
	 *==============================================================*/
	public void setSpotViewLoc(int TopX, int TopY, int BotX, int BotY)
	{
		//set spot x and y image coordinates to passed in values
		spotViewTopX = TopX;
		spotViewTopY = TopY;
		spotViewBotX = BotX;
		spotViewBotY = BotY;
	}
	
	/*==============================================================
	 * setSpotConditions - sets any special conditions
	 * Input: any special condition (String)
	 * Output: None
	 * Return: None
	 * sets the object to keep track of any special conditions that
	 * this spot may have. such as being handicapped or a motorcyle spot
	 *==============================================================*/
	public void setSpotConditions(String x)
	{
		//sets the special conditions to any passed in value
		spotSpecialCondition = x;
	}
	
	/*==============================================================
	 * getName - returns the name of the spot
	 * Input: None
	 * Output: None
	 * Return: The name of the spot (String)
	 * returns the name of the spot
	 *==============================================================*/
	public String getName()
	{
		//return the name of the spot
		return spotName;
	}
	
	/*==============================================================
	 * getView - returns the viewID of the associated view
	 * Input: None
	 * Output: None
	 * Return: the associated viewID (int)
	 * returns the viewID of the view that this spot is in.
	 *==============================================================*/
	public int getView()
	{
		//return the viewID
		return viewID;
	}
	
	/*==============================================================
	 * getPhysicalLoc - returns the row & column of this spot
	 * Input: None
	 * Output: None
	 * Return: the row and column of this spot (int[])
	 * returns the row first, and then the column in the array
	 *==============================================================*/
	public int[] getPhysicalLoc()
	{
		int[] rval = new int[2];
		//return row as array[0]
		rval[0] = spotPhysicalRow;
		//return column as array[1]
		rval[1] = spotPhysicalCol;
		return rval;
	}
	
	/*==============================================================
	 * getSpotViewLoc - returns the x & y coords of the spot on an image
	 * Input: None
	 * Output: None
	 * Return: the x & y coord's of the spot (int[])
	 * returns the x & y coordinates of a spot on the associated view image.
	 *==============================================================*/
	public int[] getSpotViewLoc()
	{
		int[] rval = new int[4];
		//return top x coord. in array[0]
		rval[0] = spotViewTopX;
		//return top y coord. in array[1]
		rval[1] = spotViewTopY;
		//return top x coord. in array[2]
		rval[2] = spotViewBotX;
		//return top y coord. in array[3]
		rval[3] = spotViewBotY;
		return rval;
	}
	
	/*==============================================================
	 * getConditions - returns and special conditions
	 * Input: None
	 * Output: None
	 * Return: special conditions (String)
	 * returns any special conditions associated with this spot -
	 * this would include handicapped spots, motorcycle spots, compact
	 * spots, etc...
	 *==============================================================*/
	public String getConditions()
	{
		//return any special conditions
		return spotSpecialCondition;
	}
	
	/*==============================================================
	 * toDB() - creates an insert statement
	 * Input: None
	 * Output: None
	 * Return: SQL Insert Statement (String)
	 * This method takes the data stored within the object to create
	 * an insert statement to insert a spot with these characteristics
	 * into the database.
	 *==============================================================*/
	public String toDB()
	{
		String rval="";
		rval = "INSERT INTO Spots (SpotID,SpotName,spViewID,SpotPhysicalRow," +
				"SpotPhysicalCol,SpotViewTopX,SpotViewTopY,SpotViewBotX,"+
				"SpotViewBotY,SpecialSpotCond) \n" +
				"VALUES (" + Integer.toString(spotID) + ",'" +
				spotName + "'," +
				Integer.toString(viewID) + "," +
				Integer.toString(spotPhysicalRow) + "," + 
				Integer.toString(spotPhysicalCol) + "," +
				Integer.toString(spotViewTopX) + "," + 
				Integer.toString(spotViewTopY) + "," +
				Integer.toString(spotViewBotX) + "," +
				Integer.toString(spotViewBotY) + ",'" +
				spotSpecialCondition + "')";
		return rval;
	}
}
