package com.ryanluedders.plmonitor.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.ryanluedders.plmonitor.client.LotService;

/*=====================================================================
 * serverMethods - Methods for client/server interaction. This class
 * acts in between the dbMethods class and the lotService class to
 * perform useful client functions.
 *=====================================================================*/
public class ServerMethods extends RemoteServiceServlet implements LotService
{
	public static boolean dbg = false;
	public static boolean info = false;
	
	private static final long ONEWEEK = 604800000;
	private static final long TWOWEEKS = ONEWEEK * 2;
	
	public static boolean runningOV = false;
	
	public Runtime rt = Runtime.getRuntime();

	/*==============================================================
	 * Default Constructor 
	 * Input: None
	 * Output: None
	 * Return: None
	 * This constructor utilizes the dbMethods class to create
	 * the connection, initialize the database by creating tables
	 * if they do not already exist, and then prints the database
	 * out, mostly for testing purposes.
	 *==============================================================*/
	public ServerMethods()
	{
		//create the connection with the database
		DBMethods.createConn();
		//dbMethods.dropTables();
		//initialize the tables of the database
		DBMethods.initDB();
		//dbMethods.insertTestData();
		//print out the database contents
		DBMethods.printDB();
	}

	/*==============================================================
	 * addLot - add a lot to the database 
	 * Input: Lot name (String)
	 * Output: None
	 * Return: None
	 * This function takes an argument that is used as the name of
	 * the new lot. With the name, it creates a lot object, and then
	 * uses the dbMethods class to insert the Lot into the running DB
	 *==============================================================*/
	public void addLot(String lName)
	{
		//Print out for logging/testing purposes
		if(info){System.out.println("Adding Lot " + lName);}
		//new lot, getting the key from the key function
		Lot x = new Lot((getNextKeyLots()));
		//set the name to the argument passed in
		x.setLotName(lName);
		//insert the Lot into the database.
		DBMethods.insertIntoDB(x);
		DBMethods.printDB();
	}

	/*==============================================================
	 * deleteLot - remove a lot from the database
	 * Input: Lot name to delete (String)
	 * Output: None
	 * Return: None
	 * This function takes in an argument that represents the lot
	 * name which is to be deleted, it then deletes all the spots
	 * associated with the lot, and all of the views associated
	 * with the lot.
	 *==============================================================*/
	public void deleteLot(String lName)
	{
		//get all the spots associated with the lot
		String[] x = getSpotsForLot(lName);
		//delete the spots
		for(int i=0; i<x.length; i++)
		{
			delSpot(x[i]);
		}
		//get all views associated with the lot
		String[] y = getViews(lName);
		//delete all the views
		for(int i=0; i<y.length; i++)
		{
			delView(y[i]);
		}

		//print for logging/testing purposes
		if(info){System.out.println("Deleting Lot " + lName);}
		
		//remove all stats associated with the lot.
		String qry = "DELETE FROM Stats " +
			"WHERE LotID = " + getLotID(lName);
		DBMethods.statementToDB(qry);
		
		//remove any events associated with the lot
		qry = "DELETE FROM Events " +
			"WHERE LotAffected = " + getLotID(lName);
		DBMethods.statementToDB(qry);
		
		//remove the lot from the database
		DBMethods.removeFromDB(lName);
		//print the database for logging/testing purposes.
		DBMethods.printDB();
	}

	/*==============================================================
	 * addView - add a view to the database
	 * Input: name of the view (String), name of corresponding lot (String)
	 * Output: None
	 * Return: None
	 * This function takes in a name for a view to insert, and a string
	 * for the lot name that it belongs to. It then creates a new view
	 * object and inserts that view into the system database.
	 *==============================================================*/
	public void addView(String vName, String lName, String imgName)
	{
		//print for logging/testing purposes
		if(info){System.out.println("Adding view " + vName);}
		//create a new view object with the next available key for
		//the view, and the lot id for the lot name specified
		View x = new View(getNextKeyViews(),getLotID(lName));
		//Set the view name to the value passed into the function
		x.setViewName(vName);
		//set the image name to the arg passed
		x.setImageFile(imgName);
		//insert the view into the database.
		DBMethods.insertIntoDB(x);
		//print the DB for logging/testing purposes.
		DBMethods.printDB();
	}

	/*==============================================================
	 * delView - delete a view from the database.
	 * Input: name of the view (String)
	 * Output: None
	 * Return: None
	 * This function takes the name of a view to be deleted from the
	 * database, and sends the command to delete it.
	 *==============================================================*/
	public void delView(String vName)
	{
		//print for logging/testing purpose
		if(info){System.out.println("Deleting view " + vName);}
		//send the remove command to the database
		DBMethods.removeViewFromDB(vName);
		//print the database for logging/testing purposes.
		DBMethods.printDB();
	}

	/*==============================================================
	 * addSpot - add a spot to the database
	 * Input: name of the spot (String), associated view (String)
	 * Output: None
	 * Return: None
	 * This function will add a spot to the database, it takes, as
	 * arguments, the name of the new spot, and the name of the view
	 * that the spot will be associated with. To do this, it gets the
	 * next available spotID from the db, then creates a spot and adds
	 * it to the database.
	 *==============================================================*/
	public void addSpot(String sName, String vName, int topX, int topY, int botX, int botY)
	{
		//print for testing/logging purposes
		if(info){System.out.println("Adding Spot " + sName + " " + vName);}	
		//create a new spot object, with the next available key
		Spot x = new Spot(getNextKeySpots());
		//set the new spot name to the value supplied
		x.setSpotName(sName);
		//set the associated view ID by looking up the view
		//corresponding the the view name supplied
		x.setView(getViewID(vName));
		x.setSpotViewLoc(topX, topY, botX, botY);
		//insert the spot object into the database
		DBMethods.insertIntoDB(x);
		//print the database for logging/testing purposes
		DBMethods.printDB();
	}

	/*==============================================================
	 * delSpot - delete a spot from the database
	 * Input: name of the spot to delete (String)
	 * Output: None
	 * Return: None
	 * This function takes the name of a spot in the function call,
	 * then removes the corresponding spot from the database
	 *==============================================================*/
	public void delSpot(String sName)
	{
		//print for testing/logging purposes
		if(info){System.out.println("Deleting Spot " + sName);}
		//use the dbMethods to remove the spot by name
		DBMethods.removeSpotFromDB(sName);
		//print for testing/logging purposes.
		DBMethods.printDB();
	}

	/*==============================================================
	 * getSiteName - get the current site name
	 * Input: None
	 * Output: None
	 * Return: the current site name (String)
	 * This function will access the dbMethods class to get the
	 * current site name, and will return it in a string.
	 *==============================================================*/
	public String getSiteName()
	{
		//get the site name from dbMethods, return it.
		return DBMethods.siteName;
	}

	/*==============================================================
	 * getSysTime - get the current system time
	 * Input: None
	 * Output: None
	 * Return: the current server time (String)
	 * This function returns the current server date and time
	 *==============================================================*/
	public String getSysTime()
	{
		//return the current system date & time.
		return (new Date(System.currentTimeMillis())).toString();
	}

	/*==============================================================
	 * getTotalOpenSpots - get the # of open spots in the site
	 * Input: None
	 * Output: None
	 * Return: the # of open spots (int)
	 * This function returns the number of open spots
	 * INCOMPLETE
	 *==============================================================*/
	public int getTotalOpenSpots()
	{
		int rval = 0;

		String dbQuery = "Select SpotID, spViewID, SpotViewTopX, SpotViewTopY, "
			+ "SpotViewBotX, SpotViewBotY From Spots";
		ResultSet rs = DBMethods.queryDB(dbQuery);

		try
		{
			if(rs.first())  //added 4-13
			{
				while(!rs.isAfterLast())
				{
					rval++;
					rs.next();
				}
			}
		}
		catch(SQLException e)
		{
			System.out.println("Error :: couldn't get open spots " +
					"(serverMethods.getTotalOpenSpots) :: " + e.toString());
		}
		return rval;
	}

	/*==============================================================
	 * getFullLots - get the # of full lots in the site
	 * Input: None
	 * Output: None
	 * Return: the # of full lots (int)
	 * This function returns the number of full lots in the site
	 * INCOMPLETE
	 *==============================================================*/
	public int getFullLots()
	{
		int rval = 0;
		
		String[] x = getLots();
		for(int i=0; i<x.length; i++)
		{
			if(getOpenSpots(x[i]) == 0)
				rval++;
		}

		return rval;
	}

	/*==============================================================
	 * getNotFullLots - get the # of non-full lots in the site
	 * Input: None
	 * Output: None
	 * Return: the # of non-full lots (int)
	 * This function returns the number of non-full lots in the site
	 * INCOMPLETE
	 *==============================================================*/
	public int getNotFullLots()
	{
		int rval = 0;
		
		String[] x = getLots();
		rval = x.length - getFullLots();

		return rval;
	}
	
	public int[] getFullNotFullLots()
	{
		int[] rval = new int[2];
		rval[0] = 0;
		
		String[] x = getLots();
		for(int i=0; i<x.length; i++)
		{
			if(getOpenSpots(x[i]) == 0)
				rval[0]++;
		}
		rval[1] = x.length - rval[0];

		return rval;
	}

	/*==============================================================
	 * getAvgOpenPerLot - get the average # of spots open in lots
	 * Input: None
	 * Output: None
	 * Return: the avg # of spots open per lots (int)
	 * This function returns the average number of spots open per lot
	 * INCOMPLETE
	 *==============================================================*/
	public int getAvgOpenPerLot()
	{
		String[] x = getLots();
		
		int tot = 0;
		int ctr = 0;
		
		for(int i=0; i<x.length; i++)
		{
			tot += getOpenSpots(x[i]);
			ctr++;
		}

		return (tot / ctr);
	}
	
	public int[] getAvgMostLeastOpenPerLot()
	{
		String[] x = getLots();
		
		int tot = 0;
		int ctr = 0;
		
		int largest = 0;
		int smallest = 10000;
		
		for(int i=0; i<x.length; i++)
		{
			int y = getOpenSpots(x[i]);
			tot += y;
			ctr++;
			
			if(y > largest)
				largest = y;
			if(y < smallest)
				smallest = y;
			
		}

		int[] rval = new int[3];
		rval[0] = tot/ctr;
		rval[1] = largest;
		rval[2] = smallest;
		
		return (rval);
	}

	/*==============================================================
	 * getMostOpenPerLot - get the most # of spots open in any lot
	 * Input: None
	 * Output: None
	 * Return: the # of spots open in most open lot (int)
	 * This function returns the number of spots that are open in
	 * the most open lot.
	 * INCOMPLETE
	 *==============================================================*/
	public int getMostOpenPerLot()
	{
		String[] x = getLots();
		
		int largest = 0;
		
		for(int i=0; i<x.length; i++)
		{
			int y = getOpenSpots(x[i]);
			if(y > largest)
				largest = y;
		}

		return largest;
	}

	/*==============================================================
	 * getLeastOpenPerLot - get the least # of spots open per lot
	 * Input: None
	 * Output: None
	 * Return: the # of spots open in least open lot (int)
	 * This function returns the number of spots that are open in
	 * the least open lot.
	 * INCOMPLETE
	 *==============================================================*/
	public int getLeastOpenPerLot()
	{
		String[] x = getLots();
		
		int smallest = 10000;
		
		for(int i=0; i<x.length; i++)
		{
			int y = getOpenSpots(x[i]);
			if(y < smallest)
				smallest = y;
		}

		return smallest;
	}

	/*==============================================================
	 * getMostOpenLot - get the most open lot
	 * Input: None
	 * Output: None
	 * Return: the lot with the most spots open (string)
	 * This function returns the lot with the most spots open
	 * INCOMPLETE
	 *==============================================================*/
	public String getMostOpenLot()
	{
		return statGetMostOpenLot();
	}
	//static version
	public static String statGetMostOpenLot()
	{
		String[] x = statGetLots();
		
		String largest = "";
		int larg = 0;
		
		for(int i=0; i<x.length; i++)
		{
			int y = statGetOpenSpots(x[i]);
			if(y > larg)
			{
				largest = x[i];
				larg = y;
			}
		}

		return largest;
	}
	
	/*==============================================================
	 * getOverviewInfo - gets basic site information
	 * Input: none
	 * Output: none
	 * Return: overview info (String[])
	 * This method takes no arguments, and just gets the basic info
	 * about a site, returning it in an array of strings
	 *==============================================================*/
	public String[] getOverviewInfo()
	{
		//array to return
		String[] rval = new String[7];
	
		if(!runningOV)
		{
			runningOV = true;
			//get basic site info (pretty self explanatory)
			rval[0] = Integer.toString(getTotalOpenSpots());
			int[] lotvals = getFullNotFullLots();
			rval[1] = Integer.toString(lotvals[0]);
			rval[2] = Integer.toString(lotvals[1]);
			int[] vals = getAvgMostLeastOpenPerLot();
			rval[3] = Integer.toString(vals[0]);
			rval[4] = Integer.toString(vals[1]);
			rval[5] = Integer.toString(vals[2]);
			rval[6] = getMostOpenLot();
			runningOV = false;
		}
		
		//return the array
		return rval;
	}

	/*==============================================================
	 * getNumViews - get the total number of views
	 * Input: None
	 * Output: None
	 * Return: the number of views (int)
	 * This function returns the total number of views in the DB
	 *==============================================================*/
	public int getNumViews(String lName)
	{
		//get an array with the views, return the length (# of views)
		return getViews(lName).length;
	}

	/*==============================================================
	 * getViews - get a list of all views in the site
	 * Input: None
	 * Output: None
	 * Return: a list of the views (String[])
	 * This function returns a list of all of the views that are
	 * available for the current site, by querying the database for
	 * all views, not specifying a lot or anything.
	 *==============================================================*/
	public String[] getViews()
	{
		return statGetViews();
	}
	//static version
	public static String[] statGetViews()
	{
		//run a query to get all the viewnames in the views table
		ResultSet x = DBMethods.queryDB("select ViewName from Views");
		//Create a new vector to put all the viewnames in
		Vector<String> y = new Vector<String>();
		try 
		{
			if(x.first())  //added 4-13
			{
				//go through all results
				do
				{
					//add each name to the vector
					y.add(x.getString(1));
				}
				while(x.next());
			}
		} 
		catch (SQLException e) 
		{
			//catch any exceptions, print to system.out
			System.out.println("Error :: couldn't get views " +
					"(serverMethods.statGetViews) :: " + e.toString());
		}
		//sort the results in alphabetical order
		java.util.Collections.sort(y);
		//new String[] to return values in
		String[] rval = new String[y.size()];
		//move all values from vector into the return array
		for(int i=0; i<y.size(); i++)
		{
			rval[i] = y.elementAt(i).toString().trim();
		}
		//return the array
		return rval;
	}

	/*==============================================================
	 * getLots - get a list of all lots in the site
	 * Input: None
	 * Output: None
	 * Return: a list of the lots (String[])
	 * This function returns a list of all of the lots that are
	 * available for the current site, by querying the database for
	 * all lots, not specifying a lot or anything.
	 *==============================================================*/
	public String[] getLots()
	{
		return statGetLots();
	}
	//static version
	public static String[] statGetLots()
	{
		//run a query to get all lotnames
		ResultSet x = DBMethods.queryDB("select ParkingLotName from Lots");
		//vector to place results in
		Vector<String> y = new Vector<String>();
		try 
		{
			if(x.first())  //added 4-13
			{
				//go through all results
				do
				{
					//add them to the vector
					y.add(x.getString(1));
				}
				while(x.next());
			}
		} 
		catch (SQLException e) 
		{
			//catch any exceptions and print them to system.out
			System.out.println("Error :: couldn't get lots " +
					"(serverMethods.statGetLots) :: " + e.toString());
		}
		//sort the results in alphabetical order
		java.util.Collections.sort(y);
		//new String[] to put the results in
		String[] rval = new String[y.size()];
		//for all the results
		for(int i=0; i<y.size(); i++)
		{
			//place them into the array
			rval[i] = y.elementAt(i).toString().trim();
		}
		//return the array
		return rval;
	}

	/*==============================================================
	 * getNumLots - get the number of lots
	 * Input: None
	 * Output: None
	 * Return: the number of lots (int)
	 * This function queries to get all lots, then counts the number
	 * that are returned, returning that number.
	 *==============================================================*/
	public int getNumLots()
	{
		//return the length of the results when querying all lots
		return getLots().length;
	}

	/*==============================================================
	 * getNumSpots - get the number of spots
	 * Input: None
	 * Output: None
	 * Return: the number of spots (int)
	 * This function queries to get all spots, then counts the number
	 * that are returned, returning that number.
	 *==============================================================*/
	public int getNumSpots()
	{
		//return the length of the results when querying all spots
		return getSpots().length;
	}

	/*==============================================================
	 * getNextKeyLots - get the next available primaryKey
	 * Input: None
	 * Output: None
	 * Return: the next available primary key for Lots table (int)
	 * This function gets all the primary key (lotID's) currently in
	 * the lots table, and sorts them small to large, it then checks
	 * the largest one, and returns the integer above that as the next
	 * primary key.
	 *==============================================================*/
	public int getNextKeyLots()
	{
		//start with zero for case of no stored values.
		int rval = 0;
		try
		{
			//get all the parkinglotID's out of the lots table - sort
			ResultSet x = DBMethods.queryDB("SELECT ParkingLotID FROM Lots ORDER BY ParkingLotID");
			//go to the last ID
			x.last();
			//get the last ID
			rval = x.getInt(1);

		}
		catch(SQLException e)
		{
			//in case of no records
			rval = 0;
		}
		//return 1+the largest existing key
		return (rval+1);
	}

	/*==============================================================
	 * getNextKeySpots - get the next available primaryKey
	 * Input: None
	 * Output: None
	 * Return: the next available primary key for Spots table (int)
	 * This function gets all the primary key (spotID's) currently in
	 * the spots table, and sorts them small to large, it then checks
	 * the largest one, and returns the integer above that as the next
	 * primary key.
	 *==============================================================*/
	public int getNextKeySpots()
	{
		//start with zero for case of no stored values.
		int rval = 0;
		try
		{
			//get all the spotID's out of the lots table - sort
			ResultSet x = DBMethods.queryDB("SELECT SpotID FROM Spots ORDER BY SpotID");
			//go to the last ID
			x.last();
			//get the last ID
			rval = x.getInt(1);

		}
		catch(SQLException e)
		{
			//in case of no records
			rval = 0;
		}
		//return 1+the largest existing key
		return (rval+1);
	}

	/*==============================================================
	 * getNextKeyViews - get the next available primaryKey
	 * Input: None
	 * Output: None
	 * Return: the next available primary key for Views table (int)
	 * This function gets all the primary key (viewID's) currently in
	 * the views table, and sorts them small to large, it then checks
	 * the largest one, and returns the integer above that as the next
	 * primary key.
	 *==============================================================*/
	public int getNextKeyViews()
	{
		//start with zero for case of no stored values.
		int rval = 0;
		try
		{
			//get all the spotID's out of the lots table - sort
			ResultSet x = DBMethods.queryDB("SELECT ViewID FROM Views ORDER BY ViewID");
			//go to the last ID
			x.last();
			//get the last ID
			rval = x.getInt(1);

		}
		catch(SQLException e)
		{
			//in case of no records
			rval = 0;
		}
		//return 1+the largest existing key
		return (rval+1);
	}
	
	/*==============================================================
	 * getNextKeyNotifications - gets next key for notifications table
	 * Input: none
	 * Output: none
	 * Return: next available key (int)
	 * this method queries the notifications table to find the next
	 * available primary key
	 *==============================================================*/
	public static int getNextKeyNotifications()
	{
		//start with zero for case of no stored values.
		int rval = 0;
		try
		{
			//get all the spotID's out of the lots table - sort
			ResultSet x = DBMethods.queryDB("SELECT notNum FROM Notifications ORDER BY notNum");
			//go to the last ID
			x.last();
			//get the last ID
			rval = x.getInt(1);

		}
		catch(SQLException e)
		{
			//in case of no records
			rval = 0;
		}
		//return 1+the largest existing key

		return (rval+1);
	}
	
	/*==============================================================
	 * getNextKeyStats - gets next key for statistics table
	 * Input: none
	 * Output: none
	 * Return: next available key (int)
	 * this method queries the statistics table to find the next
	 * available primary key
	 *==============================================================*/
	public static int getNextKeyStats()
	{
		//start with zero for case of no stored values.
		int rval = 0;
		try
		{
			//get all the spotID's out of the lots table - sort
			ResultSet x = DBMethods.queryDB("SELECT StatID FROM Stats ORDER BY StatID");
			//go to the last ID
			x.last();
			//get the last ID
			rval = x.getInt(1);

		}
		catch(SQLException e)
		{
			//in case of no records
			rval = 0;
		}
		//return 1+the largest existing key
		return (rval+1);
	}

	/*==============================================================
	 * getSpots - get a list of all spots
	 * Input: None
	 * Output: None
	 * Return: a list of all spots (String[])
	 * This function will query the database to get a list of all
	 * spots without restricting the lot or view of any sort.
	 *==============================================================*/
	public String[] getSpots()
	{
		//Select all spots from the spots table
		ResultSet x = DBMethods.queryDB("select SpotName from Spots");
		//vector to place results in
		Vector<String> y = new Vector<String>();
		try 
		{
			if(x.first())  //added 4-13
			{
				//go through all results
				do
				{
					//add it to the vector
					y.add(x.getString(1));
				}
				while(x.next());
			}
		} 
		catch (SQLException e) 
		{
			System.out.println("Error :: failed to get spots " +
					"(serverMethods.getSpots) :: " + e.toString());
		}
		//sort the vector of results
		java.util.Collections.sort(y);
		//a new String[] to return values in
		String[] rval = new String[y.size()];
		//for all values in the vector
		for(int i=0; i<y.size(); i++)
		{
			//add them to the string[]
			rval[i] = y.elementAt(i).toString().trim();
		}
		//return the array
		return rval;
	}
	
	/*==============================================================
	 * getSpotXY - get X/Y Coordinates of a spot.
	 * Input: spot name (string)
	 * Output: none
	 * Return: topX, topY, botX, botY, available? (int[])
	 * This function queries the database to find the coordinates
	 * and view filename for a spot, returning the X/Y coordinates
	 * of the spot, and also whether or not the spot is available
	 *==============================================================*/
	public int[] getSpotXY(String sName)
	{
		return statGetSpotXY(sName);
	}
	//static version
	public static int[] statGetSpotXY(String sName)
	{
		//array of values to return
		final int[] rval = new int[5];
		
		//Select x's & y's from the spots table
		final ResultSet x = DBMethods.queryDB("SELECT SpotViewTopX, SpotViewTopY, " +
				"SpotViewBotX, SpotViewBotY FROM Spots WHERE SpotName = '" +
				sName + "'");
		
		try
		{
			//go to the first (and hopefully only) record
			x.first();
			//topX
			rval[0] = x.getInt(1);
			//topY
			rval[1] = x.getInt(2);
			//botX
			rval[2] = x.getInt(3);
			//botY
			rval[3] = x.getInt(4);
		}
		catch(SQLException e)
		{
			//If there is an error, return 0
			System.out.println("Error :: failed to get spot XY coordinates " +
					"(serverMethods.statGetSpotXY) :: " + e.toString());
			rval[0] = 0;
			rval[1] = 0;
			rval[2] = 0;
			rval[3] = 0;
		}
		
		//in the 4th spot of the array, return if it is available.
		rval[4] = isSpotAvailable(sName);

		//return the array
		return rval;
	}
	
	/*==============================================================
	 * getSpotRowCol - get row & column of spot
	 * Input: spot name (string)
	 * Output: none
	 * Return: row & column (int[])
	 * This method takes a spot name, queries the db to find its
	 * row and column, returning them in an integer array
	 *==============================================================*/
	public int[] getSpotRowCol(String sname)
	{
		int[] rval = new int[2];
		
		ResultSet x = DBMethods.queryDB("SELECT SpotPhysicalRow, SpotPhysicalCol " +
				"FROM Spots WHERE SpotName = '" + sname + "'");
		
		try
		{
			//go to the first (and hopefully only) record
			x.first();
			//Row
			rval[0] = x.getInt(1);
			//Col
			rval[1] = x.getInt(2);
		}
		catch(SQLException e)
		{
			//If there is an error, return 0
			System.out.println("Error :: failed to get spot row & column " +
					"(serverMethods.getSpotRowCol) :: " + e.toString());
			rval[0] = 0;
			rval[1] = 0;
		}
		return rval;
	}
	
	/*==============================================================
	 * getSpotSpecial - get any unique conditions associated w/ spot
	 * Input: spot name (string)
	 * Output: none
	 * Return: special conditions
	 * This method takes a spot name, queries db for that spot and
	 * returns any unique conditions associated with the spot.
	 *==============================================================*/
	public String getSpotSpecial(String sname)
	{
		String rval = new String();
		
		//query for the specified spot
		ResultSet x = DBMethods.queryDB("SELECT SpecialSpotCond " +
				"FROM Spots WHERE SpotName = '" + sname + "'");
		
		try
		{
			//go to the first (and hopefully only) record
			x.first();
			//Row
			rval = x.getString(1);
		}
		catch(SQLException e)
		{
			//If there is an error, return 0
			if(dbg){System.out.println("Error :: failed to get spot special conditions " +
					"(serverMethods.getSpotSpecial) :: " + e.toString());}
			rval = "";
		}
		//return the string
		return rval;
	}
	
	/*==============================================================
	 * getSpotAnalysis - perform image processing on spot, and
	 * return the results in a string
	 * Input: spot name (string)
	 * Output: none
	 * Return: results of analysis (string)
	 * This method takes a spot name, queries the database to find
	 * the image and runs it through image processing, returning the
	 * results in a string.
	 *==============================================================*/
	public String getSpotAnalysis(String sName)
	{
		int viewNum = 0;
		int topX = 0;
		int topY = 0;
		int botX = 0;
		int botY = 0;
		String imageFile = "";
		
		//get the view ID, and spot coordinates
		ResultSet x = DBMethods.queryDB("SELECT SpotViewTopX, SpotViewTopY, " +
				"SpotViewBotX, SpotViewBotY, spViewID FROM Spots WHERE SpotName = '" +
				sName + "'");
		try
		{
			//the first (and hopefully only) record
			x.first();
			//the view ID is the last column in the record
			viewNum = x.getInt(5);
			//topX
			topX = x.getInt(1);
			//topY
			topY = x.getInt(2);
			//botX
			botX = x.getInt(3);
			//botY
			botY = x.getInt(4);
		}
		catch(SQLException e)
		{
			//if there is an error, print it out.
			System.out.println("Error :: failed to get spot analysis " +
					"(serverMethods.getSpotAnalysis) :: " + e.toString());
		}
		
		//get the filename of the image from the views table.
		ResultSet y = DBMethods.queryDB("SELECT ImgFile FROM Views Where ViewID=" +
				Integer.toString(viewNum));
		
		try
		{
			//get the filename out of the record
			y.first();
			imageFile = y.getString(1);
		}
		catch(SQLException e)
		{
			//if there is an error, print it to the console
			System.out.println("Error :: failed to get view imagefilename " +
					"(serverMethods.getSpotAnalysis) :: " + e.toString());
		}
		
		//create a new image with the pic of the view.
		String rval = "";
		try
		{
			ParkingLotImage lotImg = new ParkingLotImage(imageFile);
			rval = ImageProcessor.getAnalysis(lotImg.lotImage.getSubimage(topX, topY, (botX - topX), (botY - topY))); 
		}
		catch(Exception e)
		{
			rval = "";
		}
		
		return rval;
	}

	/*==============================================================
	 * updateSpotInfo - update the information in the database
	 * Input: spot name (string), topX (int), topY (int), botX (int)
	 * botY (int), row (int), column (int), special (string)
	 * Output: updates info in database
	 * Return: None
	 * this method takes a spot name and all values associated with
	 * that spot, and updates the database with those values.
	 *==============================================================*/
	public void updateSpotInfo(String sname, int tX, int tY, int bX, int bY, int pRow, int pCol, String special)
	{
		//really big query with all values passed in.
		String statement = "UPDATE Spots " +
		"SET SpotViewTopX = " + Integer.toString(tX) + ", \n" +
		"SpotViewTopY = " + Integer.toString(tY) + ", \n" +
		"SpotViewBotX = " + Integer.toString(bX) + ", \n" +
		"SpotViewBotY = " + Integer.toString(bY) + ", \n" +
		"SpotPhysicalRow = " + Integer.toString(pRow) + ", \n" +
		"SpotPhysicalCol = " + Integer.toString(pCol) + ", \n" +
		"SpecialSpotCond = '" + special + "' \n" +
		"WHERE SpotName = '" + sname + "'";
		//run the query
		DBMethods.statementToDB(statement);
	}
	
	/*==============================================================
	 * updateViewThreshold - update processing threshold
	 * Input: view name (string), new threshold (int)
	 * Output: updates info in database
	 * Return: None
	 * This method takes a view, and a processing threshold (for use
	 * in image processing) that is associated with the view, and
	 * stores it in the database.
	 *==============================================================*/
	public void updateViewThreshold(String vName, double thresh)
	{
		//query to update detectionThreshold in views table
		String statement = "UPDATE Views " +
		"SET detectionThreshold = " + Double.toString(thresh) + " \n" +
		"WHERE ViewName = '" + vName + "'";
		//run the query
		if(dbg){System.out.println(statement);}
		DBMethods.statementToDB(statement);
	}
	
	/*==============================================================
	 * isSpotAvailable - is the spot available
	 * Input: spot name (string)
	 * Output: none
	 * Return: 0 if unavailable, 1 if available (int)
	 * This function queries the database to find the coordinates on
	 * the image to send a subimage to the image processor, so it can
	 * try and figure out whether or not the spot contains a car.
	 *==============================================================*/
	public static int isSpotAvailable(String sName)
	{
		int isAvailable = 0;
		int cached = inCache(sName);
		if(sName != null & cached == -1)
		{
			//0 = spot unavailable
			//1 = spot available
			
			int viewNum = 0;
			int topX = 0;
			int topY = 0;
			int botX = 0;
			int botY = 0;
			String imageFile = "";
			
			//get the view ID, and spot coordinates
			ResultSet x = DBMethods.queryDB("SELECT SpotViewTopX, SpotViewTopY, " +
					"SpotViewBotX, SpotViewBotY, spViewID FROM Spots WHERE SpotName = '" +
					sName + "'");
			try
			{
				//the first (and hopefully only) record
				if(x.first())  //changed 4-13
				{
					//the view ID is the last column in the record
					viewNum = x.getInt(5);
					//topX
					topX = x.getInt(1);
					//topY
					topY = x.getInt(2);
					//botX
					botX = x.getInt(3);
					//botY
					botY = x.getInt(4);
				}
			}
			catch(SQLException e)
			{
				//if there is an error, print it out.
				System.out.println("Error :: failed to get view id & spot coords " +
						"(serverMethods.isSpotAvailable) :: " + e.toString());
			}
			
			//get the filename of the image from the views table.
			ResultSet y = DBMethods.queryDB("SELECT ImgFile FROM Views Where ViewID=" +
					Integer.toString(viewNum));
			
			try
			{
				//get the filename out of the record
				y.first();
				imageFile = y.getString(1);
			}
			catch(SQLException e)
			{
				//if there is an error, print it to the console
				System.out.println("Error :: failed to get view filename " +
						"(serverMethods.isSpotAvailable) :: " + e.toString());
			}
			
			//create a new image with the pic of the view.
			try
			{
				ParkingLotImage lotImg = new ParkingLotImage(imageFile);
				
				//system logging.
				/*System.out.println("Checking " + imageFile.trim() + " to see if spot at " + 
						topX + " " + topY + " " + botX + " " + botY + " is available");*/
				if(dbg){System.out.println(lotImg.lotImage.getHeight());}
				//double per = ((double)topY / (0.5 * lotImg.lotImage.getHeight()));
				double per = 1;
				
				//call the image processing to decide if it is a car.
				boolean isCar = ImageProcessor.isCarByEdges(lotImg.lotImage.getSubimage(topX, topY, (botX - topX), (botY - topY)),statGetSpotThreshold(sName),per);
				
				//if it is, the spot is unavailable, return 0
				if(isCar)
				{
					isAvailable = 0;
				}
				//if it is not, the spot is available, return 1
				if(!isCar)
				{
					isAvailable = 1;
				}
				cacheAvailability(sName,isAvailable);
			}
			catch(Exception e)
			{
				isAvailable = 0;
			}
		}
		else
		{
			isAvailable = cached;
		}
		
		//return the number
		return isAvailable;
		
	}
	
	

	/*==============================================================
	 * getTotalSpots - get the number of spots in a lot
	 * Input: lot name (String)
	 * Output: None
	 * Return: the number of spots in a lot (int)
	 * This function takes a lot, by name, finds the Lot ID and gets
	 * the number of spots (total) in that lot.
	 * INCOMPLETE
	 *==============================================================*/
	public int getTotalSpots(String lot)
	{
		int rval = 0;
		if(getSpotsForLot(lot)[0] != null)
		{
			rval = getSpotsForLot(lot).length;
		}
		else
		{
			rval = 0;
		}

		return rval;
	}

	/*==============================================================
	 * getTotalSpots - get the number of spots in a lot
	 * Input: Lot ID (int)
	 * Output: None
	 * Return: the number of spots in a lot (int)
	 * This function takes a lot, by ID, and finds the total number
	 * of spots contained in this lot.
	 * INCOMPLETE
	 *==============================================================*/
	public int getTotalSpots(int lID)
	{
		//get the total number of spots in a lot
		int rval = 0;

		return rval;
	}

	/*==============================================================
	 * getOpenSpots - get the number of open spots in a lot
	 * Input: Lot name (String)
	 * Output: None
	 * Return: the number of spots open in a lot (int)
	 * This function takes a lot (by name), finds the ID, and then
	 * calls the getOpenSpots by ID function to find the number of
	 * open spots in a lot
	 * INCOMPLETE
	 *==============================================================*/
	public int getOpenSpots(String lot)
	{
		return statGetOpenSpots(lot);
	}
	//static version
	public static int statGetOpenSpots(String lot)
	{
		String[] spots = statGetSpotsForLot(lot);
		int ctr = 0;
		for(int i=0; i<spots.length; i++)
		{
			if(isSpotAvailable(spots[i]) == 1)
				ctr++;
		}
		return ctr;
	}

	/*==============================================================
	 * getSpotsDetails - get the spot details of a lot
	 * Input: lot name (String)
	 * Output: None
	 * Return: the details of spots (String[][])
	 * This function takes a lot by name, and finds the lotID
	 * corresponding to that name, and calls the getSpotsDetails
	 * by ID function.
	 * INCOMPLETE
	 *==============================================================*/
	public String[][] getSpotsDetails(String lot)
	{
		//find the lotID corresponding to lotName provided
		int getLotID = 0;

		//call byID function.
		return getSpotsDetails(getLotID);
	}

	/*==============================================================
	 * getSpotsDetails - get the spot details of a lot
	 * Input: lot ID (int)
	 * Output: None
	 * Return: the details of spots (String[][])
	 * This function takes a lot, by ID, and finds the details
	 * associated with the lot, returning them in an array
	 * INCOMPLETE
	 *==============================================================*/
	public String[][] getSpotsDetails(int lID)
	{
		String[][] spD = new String[0][0];
		return spD;
	}

	/*==============================================================
	 * getLotDetails - get the details of a lot
	 * Input: lot name (String)
	 * Output: None
	 * Return: the details of the lot (String[])
	 * This function takes a lot by name and returns some details
	 * regarding this lot. At present, it returns the lot ID by name
	 * and the number of open spots, with two spots still open.
	 *==============================================================*/
	public String[] getLotDetails(String lName)
	{
		//A new string array to return details in
		String[] rval = new String[4];
		//detail 1: lot ID
		int temp = getLotID(lName);
		if(temp != 0)
		{
			rval[0] = Integer.toString(temp);
		}
		else
		{
			rval[0] = "0";
		}
		//detail 2: total spots
		temp = getTotalSpots(lName);
		if(temp != 0)
		{
			rval[1] = Integer.toString(temp);
		}
		else
		{
			rval[1] = "0";
		}
		//detail 3: number of views
		temp = getNumViews(lName);
		if(temp != 0)
		{
			rval[2] = Integer.toString(temp);
		}
		else
		{
			rval[2] = "0";
		}
		//detail 4: open spots
		temp = getOpenSpots(lName);
		if(getTotalSpots(lName) != 0 & temp != 0)
		{
			rval[3] = Integer.toString(getOpenSpots(lName));
		}
		else
		{
			rval[3] = "0";
		}
		//return details
		return rval;
	}

	/*==============================================================
	 * getSpots - get the spots associated with a view
	 * Input: view name (String)
	 * Output: None
	 * Return: a list of spots (String[])
	 * This function takes the name of a view, and finds all spots
	 * that are associated with the specified view in an array of
	 * Strings
	 *==============================================================*/
	public String[] getSpots(String viewName)
	{
		//get the viewID for the given viewName
		int z = getViewID(viewName);

		//query the database to find all spots matching that view ID
		ResultSet x = DBMethods.queryDB("SELECT SpotName FROM Spots WHERE spViewID = "
				+ Integer.toString(z));
		//create a new vector to hold the results while processing
		Vector<String> y = new Vector<String>();
		try 
		{
			if(x.first()) //added 4-13
			{
				//go through all results
				do
				{
					//add spot name to vector
					y.add(x.getString(1));
				}
				while(x.next());
			}
		} 
		catch (SQLException e) 
		{
			System.out.println("Error :: failed to get spots " +
					"(serverMethods.getSpots) :: " + e.toString());
		}
		//sort the results alphabetically before returning
		java.util.Collections.sort(y);
		//a new String array to return values in
		String[] rval = new String[y.size()];
		//go through entire vector
		for(int i=0; i<y.size(); i++)
		{
			//move into the string array
			rval[i] = y.elementAt(i).toString().trim();
		}
		//return the array
		return rval;
	}

	/*==============================================================
	 * getSpots - get the spots associated with a lot
	 * Input: lot name (String)
	 * Output: None
	 * Return: a list of spots (String[])
	 * This function takes the name of a lot, and then goes through,
	 * finding all views associated with that lot, and then all the
	 * spots associated with all of those views, to return all spots
	 * that are associated with the specified lot in a String array
	 *==============================================================*/
	public String[] getSpotsForLot(String lotName)
	{
		return statGetSpotsForLot(lotName);
	}
	//static version
	public static String[] statGetSpotsForLot(String lotName)
	{
		ResultSet x = DBMethods.queryDB("SELECT SpotName FROM Spots, Views, Lots " +
				"WHERE Lots.ParkingLotName = '" + lotName + "' AND Lots.ParkingLotID = Views.ViewLot " +
				"AND Views.ViewID = Spots.spViewID");
		
		Vector<String> vals = new Vector<String>();
		String[] rArr = new String[1];
		try
		{
			if(x.first())
			{
				x.absolute(1);
				do
				{
					vals.add(x.getString(1));
					x.next();
				}
				while(!x.isAfterLast());
			}
		}
		catch(SQLException e)
		{
			System.out.println("Error :: failed to get spots for lot " +
					"(serverMethods.getSpotsForLot) :: " + e.toString());
		}
		
		return vals.toArray(rArr);
	}
	
	/*==============================================================
	 * getSpotInfoForView - get all spots & info associated with
	 * a view
	 * Input: view name (String)
	 * Output: none
	 * Return: spots & info (Vector<String[]>)
	 * This method takes as an argument, the view name, and finds
	 * all spots that are associated with the view, and returns in
	 * a vector, the spot name, row/col, X/Y coords.
	 *==============================================================*/
	public Vector getSpotInfoForView(String vName)
	{
		//vector to return
		Vector<String[]> rval = new Vector<String[]>();
		
		//query to get all required information
		ResultSet x = DBMethods.queryDB("SELECT SpotName, SpotPhysicalRow, " +
				"SpotPhysicalCol, SpotViewTopX, SpotViewTopY, SpotViewBotX, " +
				"SpotViewBotY FROM Spots, Views " +
				"WHERE Views.ViewName = '" + vName + "' AND Spots.spViewID = Views.ViewID " +
				"ORDER BY SpotPhysicalRow, SpotPhysicalCol");
		try
		{
			//if there are some records in the database
			if(x.first())
			{
				do
				{
					//string to store the data in
					String[] temp = new String[8];
					
					//spot name
					temp[0] = x.getString(1);
					//spot row
					temp[1] = x.getString(2);
					//spot column
					temp[2] = x.getString(3);
					//spot top x
					temp[3] = x.getString(4);
					//spot top y
					temp[4] = x.getString(5);
					//spot bottom x
					temp[5] = x.getString(6);
					//spot bottom y
					temp[6] = x.getString(7);
					//spot availability
					temp[7] = Integer.toString(isSpotAvailable(temp[0]));
					
					//add the array of strings
					rval.add(temp);
				}
				//go through all records
				while(x.next());
			}
		}
		catch(SQLException e)
		{
			System.out.println("Error :: failed to get info for view " +
					"(serverMethods.getSpotInfoForView) :: " + e.toString());
		}
		
		//return the vector full of arrays
		return rval;
	}

	/*==============================================================
	 * getLotID - get the lot ID associated with a lot name
	 * Input: lot name (String)
	 * Output: None
	 * Return: lot ID (int)
	 * This function takes the name of a lot as a String, queries the
	 * database to find that lot name, and returns the primary key,
	 * or lot ID, as an int, so it can be used in other lookups and such.
	 *==============================================================*/
	public static int getLotID(String lotName)
	{
		//in case of failure
		int x = 9999;
		try
		{
			if(dbg){System.out.println(lotName);}
			//set up the query, looking for a specific lot name
			String query = "SELECT ParkingLotID FROM Lots WHERE ParkingLotName = '" +
			lotName + "'";
			//get the results by running the query
			ResultSet a = DBMethods.queryDB(query);
			//go to the first (and hopefully only) value in the results
			if(a.first())
			{
				//set it to be returned
				x = a.getInt(1);
			}
		}
		catch(SQLException e)
		{
			//print any errors out to System.out
			System.out.println("Error :: failed to get lot ID # " +
					"(serverMethods.getLotID) :: " + e.toString());
		}
		//return the value that was found.
		return x;
	}

	/*==============================================================
	 * getViewID - get the View ID associated with a View name
	 * Input: View name (String)
	 * Output: None
	 * Return: View ID (int)
	 * This function takes the name of a view, and queries the 
	 * database to find the view by name, and return the ID (primary
	 * key) that is associated with that view
	 *==============================================================*/
	public int getViewID(String viewName)
	{
		//in case of failure
		int x = 9999;
		try
		{
			//set up the query, looking for a specific view name
			String query = "SELECT ViewID FROM Views WHERE ViewName = '" +
			viewName + "'";
			//get the results by running the query
			ResultSet a = DBMethods.queryDB(query);
			//go to the first (and hopefully only) record.
			a.first();
			//return that first record which should be the ID we're looking for.
			x = a.getInt(1);
		}
		catch(SQLException e)
		{
			System.out.println("Error :: failed to get view ID # " +
					"(serverMethods.getViewID) :: " + e.toString());
		}
		//return it.
		return x;
	}

	/*==============================================================
	 * getViews - get the views associated with a lot Name
	 * Input: lot name (String)
	 * Output: None
	 * Return: list of views (String[])
	 * This function takes a lot name, and queries the database to
	 * find all views associated with the lot.
	 *==============================================================*/
	public String[] getViews(String lotName)
	{
		//Print for testing/logging purposes
		if(dbg){System.out.println(lotName + " " + getLotID(lotName));}

		//Set up the query to look for views by lotID
		String query = "SELECT ViewName from Views WHERE ViewLot = " +
		Integer.toString(getLotID(lotName)) + "";

		//get the results by running the query
		ResultSet x = DBMethods.queryDB(query);
		//a vector to place the results in
		Vector<String> y = new Vector<String>();
		try 
		{
			if(x.first())  //changed 4-13
			{
				//go through all results
				do
				{
					//add all view names into the vector
					y.add(x.getString(1));
				}
				while(x.next());
			}
		} 
		catch (SQLException e) 
		{
			//print any errors to system.out
			System.out.println("Error :: failed to get views " +
					"(serverMethods.getViews) :: " + e.toString());
		}
		//a new string[] to return the list of views in
		String[] rval = new String[y.size()];
		//for all the values in the vector
		for(int i=0; i<y.size(); i++)
		{
			//place them all in the String array, remove whitespace
			rval[i] = y.elementAt(i).toString().trim();
		}
		//return the string array
		return rval;
	}
	
	/*==============================================================
	 * getViewImage - gets the image associated with a view
	 * Input: view name (String)
	 * Output: None
	 * Return: the filename of the image (String)
	 * This function queries the database to get the filename of
	 * an image associated with a view.
	 *==============================================================*/
	public String getViewImage(String lName)
	{
		String rval = "";
		try
		{
			//get the filename associated with a view
			String query = "SELECT ImgFile from Views WHERE ViewName = '" +
				lName + "'";
	
			//get the results by running the query
			ResultSet x = DBMethods.queryDB(query);
			
			x.last();
			rval = x.getString(1);
		}
		catch(SQLException e)
		{
			System.out.println("Error :: failed to get view image " +
					"(serverMethods.getViewImage) :: " + e.toString());
		}
		return rval;		
	}
	
	/*==============================================================
	 * getViewThreshold - gets the threshold associated with a view
	 * Input: view name (String)
	 * Output: None
	 * Return: the associated threshold (int)
	 * This method takes in the name of a view, and returns an
	 * integer that is the processing threshold that corresponds
	 * to the specified view.
	 *==============================================================*/
	public double getViewThreshold(String vName)
	{
		//start out with a default 0
		double rval = 0;
		try
		{
			//get the threshold associated with a view
			String query = "SELECT detectionThreshold from Views WHERE ViewName = '" +
				vName + "'";
	
			//get the results by running the query
			ResultSet x = DBMethods.queryDB(query);
			
			//get the threshold from the query results
			x.last();
			rval = x.getFloat(1);
		}
		catch(SQLException e)
		{
			System.out.println("Error :: failed to get view threshold " +
					"(serverMethods.getViewThreshold) :: " + e.toString());
		}
		//return it.
		return rval;		
	}
	
	/*==============================================================
	 * getSpotThreshold - gets the threshold associated with a spot
	 * Input: spot Name
	 * Output: None
	 * Return: the associated threshold (int)
	 * This method takes in the name of a spot, and returns an
	 * integer that is the processing threshold that corresponds
	 * to the specified spot.
	 *==============================================================*/
	public double getSpotThreshold(String sName)
	{
		return statGetSpotThreshold(sName);		
	}
	//static version
	public static double statGetSpotThreshold(String sName)
	{
		//start out with a default 0
		double rval = 0;
		try
		{
			//get the threshold associated with a spot
			String query = "SELECT detectionThreshold from Spots," +
					"Views WHERE Spots.SpotName = '" + sName + "' AND " +
					"Spots.spViewID = Views.ViewID";
	
			//get the results by running the query
			ResultSet x = DBMethods.queryDB(query);
			
			//get the threshold from the query results
			x.last();
			rval = x.getDouble(1);
		}
		catch(SQLException e)
		{
			System.out.println("Error :: failed to get spot threshold " +
					"(serverMethods.statGetSpotThreshold) :: " + e.toString());
		}
		
		//System.out.println(rval);
		//return it.
		return rval;		
	}
	
	/*==============================================================
	 * getColRowAvailable - gets info on all spots for a lot
	 * Input: lot name (String)
	 * Output: None
	 * Return: A Vector of int[] with row/col/avail.
	 * This method gets all spots associated with a passed in lot
	 * name, and finds their row, column, and availability. and places
	 * these three into an int[] for each spot, it returns a vector
	 * of these int[]
	 *==============================================================*/
	public Vector getColRowAvailable(String lname)
	{
		//big query to get all spots associated with a lot.
		ResultSet x = DBMethods.queryDB("SELECT DISTINCT SpotName, SpotPhysicalRow, " +
				"SpotPhysicalCol FROM Spots, Views, Lots WHERE ParkingLotName = '" +
				lname + "' AND Spots.spViewID=Views.ViewID AND Views.ViewLot = Lots." +
						"ParkingLotID");
		
		//A query to hold the results.
		Vector<int[]> rows = new Vector<int[]>();
		
		//catch any SQLExceptions
		try
		{
			//start at row 1 of results
			int i=1;
			do
			{
				//move to next row
				x.absolute(i);
				//make a new array
				int[] y = new int[3];
				//arr[0] is the row
				y[0] = x.getInt(2);
				//arr[1] is the column
				y[1] = x.getInt(3);
				//arr[2] is whether or not it is available
				y[2] = isSpotAvailable(x.getString(1));
				
				//print for logging purposes
				//System.out.println(i + " " + x.getString(1) + " " + Arrays.toString(y));
				
				//add the int[] with results to the vector
				rows.add(y);
				//increment rows
				i++;				
			}
			//continue until going through all rows
			while(!x.isLast());
		}
		catch(SQLException e)
		{
			System.out.println("Error :: failed to get spot col, row & availability " +
					"(serverMethods.getColRowAvailable) :: " + e.toString());
		}
		//return the vector with the results.
		return rows;
	}
	
	
	
	/*==============================================================
	 * addStat - adds a statistic to the DB
	 * Input: Date (Date) Time (Time) Lot Name (String)
	 * Spots Open (int) Spots taken (int)
	 * Output: Stat to database
	 * Return: none
	 * Takes information that would be in a statistic and places it
	 * into the database
	 *==============================================================*/
	public static void addStat(long time, String lName, int open, int taken)
	{
		Statistic toRecord = new Statistic( getNextKeyStats(),
				time, getLotID(lName),open,taken);
		DBMethods.insertIntoDB(toRecord);
	}
	
	/*==============================================================
	 * startTimedStats - starts taking timed statistics
	 * Input: interval-milliseconds (int)
	 * Output: none
	 * Return: none
	 * Starts a timer to keep timed stats
	 *==============================================================*/
	public void startTimedStats(int interval)
	{
		//create a new "stat tender" to run a timer
		//and sample the system at a regular interval
		final StatTender stats = new StatTender(interval);
		//start the timer
		stats.start();
	}
	
	public void takeStats()
	{
		System.out.println("Taking Stats");
		//get the lots from the server
		String[] lots = ServerMethods.statGetLots();
		for(int i=0; i<lots.length; i++)
		{
			int openSpots = 0;
			int takenSpots = 0;
			openSpots = statGetOpenSpots(lots[i]);
			takenSpots = statGetSpotsForLot(lots[i]).length - openSpots;
			
			addStat(System.currentTimeMillis(),
					lots[i], openSpots, takenSpots);
		}
		if(info){System.out.println("Updating Stats");}
	}
	
	/*==============================================================
	 * startTimedNotification - starts the timer to check if there
	 * are any pending notifications
	 * Input: interval-milliseconds (int)
	 * Output: starts the timer
	 * Return: none
	 * Starts a timer to check if there are any notifications to send
	 *==============================================================*/
	public void startTimedNotifications(int interval)
	{
		//create a new "stat tender" to run a timer
		//and sample the system at a regular interval
		final NotificationTender nt = new NotificationTender(interval);
		//start the timer
		nt.start();
	}
	
	public void checkNotifications()
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
	
	
	
	/*==============================================================
	 * getChartsURL - gets the URL for the two Google Charts charts
	 * that are used to display availabiliy information on the 
	 * lot overview page
	 * Input: Lot name (String) Day Name (String: mon, tues, etc...)
	 * Output: starts the timer
	 * Return: URL of charts
	 * This will take a lot name, and a day name, then query the
	 * database to find the data that is to be shown on charts. Once
	 * it has that data, it will utilize the google charts, and
	 * formulate a URL containing all the necessary data to show to
	 * the user on a google chart.
	 *==============================================================*/
	public String[] getChartsURL(String lName, String dayName)
	{
		if(info){System.out.println("**srvGetChartsURL");}
		//return container
		String[] rval = new String[2];

		int sh = 8; //starting hour
		//number of data points
		int numDatas = 15;
		//number of times (in a day)
		int[] dayData = new int[numDatas];
		//number of days in a week
		int[] weekData = new int[7];
		//what percentage of the day, horizontally for each data point
		double hDayPercent = (100.0/(numDatas-1))*2.0;
		//what percentage of the day, vertically, for each data point
		double vDayPercent = (100.0/5.0);
		//what percentage, horizontally, each of the day week should get
		double hWeekPercent = (100.0/6.0);
		//what percentage, vertically to use for days of week chart.
		double vWeekPercent = (100.0/5.0);
		
		//in int array with server provided values to get the availability
		//for a given lot on a given day
		int[] av = getAvailabilityPerHour(lName,dayName);
		//an int array with server provided values to get the typical
		//availability for a day of the week
		int[] avd = getAvailabilityPerDay(lName);
		
		//add the data into the URL to send to google charts, which will return an image
		rval[0] = "http://chart.apis.google.com/chart?chs=300x200&chd=t:"+av[0]+","+av[1]+","+av[2]+","+av[3]+","+av[4]+","+av[5]+","+av[6]+","+av[7]+","+av[8]+","+av[9]+","+av[10]+","+av[11]+","+av[12]+","+av[12]+","+av[14]+"&cht=lc&chds=0,100&chf=c,ls,0,CCCCCC," + hDayPercent/100.0 + ",FFFFFF," + hDayPercent/100.0 + "&chxt=x,y&chxl=0:|6|8|10|12|2|4|6|8|10:|0|20|40|60|80|100&chg=" + (hDayPercent/2.0) + "," + vDayPercent + "&chtt=Typical+" + dayName + "+Percent+Availability";
		rval[1] = "http://chart.apis.google.com/chart?chs=300x200&chd=t:"+avd[0]+","+avd[1]+","+avd[2]+","+avd[3]+","+avd[4]+","+avd[5]+","+avd[6]+"&cht=lc&chds=0,100&chf=c,ls,0,CCCCCC," + hWeekPercent/100.0 + ",FFFFFF," + hWeekPercent/100.0 + "&chxt=x,y&chxl=0:|Sun|Mon|Tue|Wed|Thu|Fri|Sat|1:|0|20|40|60|80|100&chg=" + (hWeekPercent) + "," + vWeekPercent + "&chtt=Typical+Weekly+Percent+Availability";
		
		//return the URL's to the image
		return rval;
	}
	
	/*==============================================================
	 * getAvailabilityPerHour - gets hourly availabilty (using historic data)
	 * Input: Lot name (String) Day Name (String: mon, tues, etc...)
	 * Output: none
	 * Return: historic availability (int[])
	 * this method queries the database, over the past two weeks, and
	 * returns the typical availability, per hour, for a specified lot
	 * and day.
	 *==============================================================*/
	public int[] getAvailabilityPerHour(String lName, String dayName)
	{
		//container to hold the return values
		int[] rval = new int[15];
		
		//get the current time
		long curTime = System.currentTimeMillis();
		
		//start time for gathering historic data (past two weeks)
		long startTime = curTime - TWOWEEKS;
		//end time for gathering historic data (now)
		long endTime = curTime;
		
		//get the lot ID, to be used in Query
		int lotID = getLotID(lName);
		
		//Query the database to find some data
		String qry = new String("");
		//get all fields from stats, for specified lot ID
		//between start time & end time.
		qry = "SELECT * FROM Stats "
			+ "WHERE LotID = " + lotID + " AND StatTime "
			+ "BETWEEN " + startTime + " AND " + endTime
			+ " ORDER BY StatTime";
		
		//run the query, get the results
		ResultSet x = DBMethods.queryDB(qry);
		
		//working set of results
		Vector<long[]> working = new Vector<long[]>();
		
		try
		{
			//make sure there is some stat returned
			if(x.first())
			{
				//go through all stats
				do
				{
					//if it is for the day of the week specified
					if(new Date(x.getLong(2)).getDay() == getDayOfWeek(dayName))
					{
						//add the data into the working stat keeper
						long[] temp = new long[5];
						//get statID
						temp[0] = x.getLong(1);
						//Stattime
						temp[1] = x.getLong(2);
						//lotID
						temp[2] = x.getLong(3);
						//Spots Open
						temp[3] = x.getLong(4);
						//Spots Taken
						temp[4] = x.getLong(5);
						//actually add it to working vector
						working.add(temp);
					}
					
				}
				//while there are still records left
				while(x.next());
			}
			//return to the first record
			x.first();
		}
		catch(SQLException e)
		{
			System.out.println("Error :: failed to get hourly availability " +
					"(serverMethods.getAvailabilityPerHour) :: " + e.toString());
		}
		
		if(dbg){
		for(int i=0; i<working.size(); i++)
		{
			System.out.println(Arrays.toString(working.get(i)));
		}}
		
		//go through and figure the availability for specified hours
		for(int j=5; j<=19; j++)
		{
			//straight up counter
			int ctr = 0;
			//cumulative to keep track
			double cumulative = 0;
			
			//go through all of working vector
			for(int i=0; i<working.size(); i++)
			{		
				//get the "hour" of the stat
				int curHours = new Date(working.get(i)[1]).getHours();
				//if the hour is in the one we're working on currently
				if((curHours > j) & (curHours <= (j+1)))
				{
					//add the percentage available to the cumulative
					cumulative += (((double)working.get(i)[3]) / (((double)working.get(i)[3]) + ((double)working.get(i)[4])));
					//increase the counter
					ctr++;
					if(dbg){System.out.println(curHours + " " + working.get(i)[3] + " " + working.get(i)[4] + " " + cumulative + " " + ctr + " lot: " + working.get(i)[2]);}
				}
			}
			
			//if counter is not zero, find the average availability over the hour
			if(ctr != 0)
			{
				rval[j-5] = (int)((cumulative*100) / ctr);
			}
			//if it is zero, there are no stats. return 0
			else
			{
				rval[j-5] = 0;
			}
		}
		
		//dbMethods.printResultSet(x,5);
		
		if(dbg){System.out.println(Arrays.toString(rval));}
		
		//return availability array.
		return rval;
	}
	
	/*==============================================================
	 * getAvailabilityPerHour - gets day of week availability
	 * Input: Lot name (String)
	 * Output: none
	 * Return: historic availability (int[])
	 * this method queries the database, over the past two weeks, and
	 * returns the typical availability, for each day of the week,
	 * over the entire day.
	 *==============================================================*/
	public int[] getAvailabilityPerDay(String lName)
	{
		//return container - 7 days in the week
		int[] rval = new int[7];
		
		//the current system time
		long curTime = System.currentTimeMillis();
		
		//starting period of historic data, two weeks ago
		long startTime = curTime - TWOWEEKS;
		//ending period of historic data, now
		long endTime = curTime;
		
		//the lot ID, for use in the query
		int lotID = getLotID(lName);
		
		//new query
		String qry = new String("");
		//select all fields from stats, for the specified lot ID
		//and the time between two weeks ago and now, ordered by time.
		qry = "SELECT * FROM Stats "
			+ "WHERE LotID = " + lotID + " AND StatTime "
			+ "BETWEEN " + startTime + " AND " + endTime
			+ " ORDER BY StatTime";
		
		//get the results
		ResultSet x = DBMethods.queryDB(qry);
		
		//go through each day
		for(int i=0; i<7; i++)
		{
			//cumulative for averaging purposes
			double cumulative = 0;
			//counter, again for averaging purposes.
			int ctr = 0;
			
			try
			{
				//make sure there is at least one record
				if(x.first())
				{
					do
					{
						//if date is on the day of the week we're looking at
						if(new Date(x.getLong(2)).getDay()== i)
						{
							//add percentage available to cumulative
							cumulative += (x.getDouble(4)) / (x.getDouble(4) + x.getDouble(5));
							//System.out.println(x.getDouble(4) + " " + x.getDouble(5) + " " + cumulative);
							//increment counter
							ctr++;
						}
					}
					//go through all records
					while(x.next());
				}	
			}
			catch(SQLException e)
			{
				System.out.println("Error :: failed to get daily availability " +
						"(serverMethods.getAvailabilityPerDay) :: " + e.toString());
			}
			//record the return percentage availability for each day.
			rval[i] = (int)((cumulative /  ctr) * 100);
		}
		
		if(dbg){System.out.println(Arrays.toString(rval));}
		
		//return the availability
		return rval;
	}

	/*==============================================================
	 * getDayOfWeek - returns int equivalent of String of Day
	 * Input: day name (String)
	 * Output: none
	 * Return: integer representing day (int)
	 * this method takes a string of the day of the week, and turns
	 * it into the integer equivalent, used by java.util.Date
	 *==============================================================*/
	public static int getDayOfWeek(String dayName)
	{
		//value to return answer in
		int dayOfWeek = 0;
		
		//pretty self explanatory...
		if(dayName.compareToIgnoreCase("Sunday") == 0)
		{
			dayOfWeek = 0;
		}
		else if(dayName.compareToIgnoreCase("Monday") == 0)
		{
			dayOfWeek = 1;
		}
		else if(dayName.compareToIgnoreCase("Tuesday") == 0)
		{
			dayOfWeek = 2;
		}
		else if(dayName.compareToIgnoreCase("Wednesday") == 0)
		{
			dayOfWeek = 3;
		}
		else if(dayName.compareToIgnoreCase("Thursday") == 0)
		{
			dayOfWeek = 4;
		}
		else if(dayName.compareToIgnoreCase("Friday") == 0)
		{
			dayOfWeek = 5;
		}
		else if(dayName.compareToIgnoreCase("Saturday") == 0)
		{
			dayOfWeek = 6;
		}
		
		//return the integer
		return dayOfWeek;
	}
	
	/*==============================================================
	 * addNotification - adds an SMS notification request into the DB
	 * Input: mobile number (String) mobile provider (String)
	 * lot name (String) num of spots (int) time (long) 
	 * times to recur (int) interval to recur at (long)
	 * Output: adds notification to database
	 * Return: none
	 * this method takes the pieces passed to it, and adds a new
	 * notification into the database
	 *==============================================================*/
	public void addNotification(String mobNum, String prov, String lname, int lvl, 
			long time, int recurTimes, long recurInterval)
	{
		//call the static version of this method
		statAddNotification(mobNum, prov, lname, lvl, time, recurTimes, recurInterval);
	}
	
	/*==============================================================
	 * statAddNotification (STATIC) - adds an SMS notification request 
	 * into the DB
	 * Input: mobile number (String) mobile provider (String)
	 * lot name (String) num of spots (int) time (long) 
	 * times to recur (int) interval to recur at (long)
	 * Output: adds notification to database
	 * Return: none
	 * this method takes the pieces passed to it, and adds a new
	 * notification into the database
	 *==============================================================*/
	public static void statAddNotification(String mobNum, String prov, String lname, int lvl, 
			long time, int recurTimes, long recurInterval)
	{
		//create a new notification object
		Notification toAdd = new Notification(mobNum, prov, lname, lvl,
				time, recurTimes, recurInterval);
		//use the toDB method of the object to run an insert statement
		//on the database.
		DBMethods.insertIntoDB(toAdd);
	}
	
	/*==============================================================
	 * deleteNotsForMobile - Delete notifications for a mobile #
	 * Input: mobile number (String)
	 * Output: deletes notifications from DB
	 * Return: none
	 * this method deletes all notifications associated with a 
	 * mobile number from the database.
	 *==============================================================*/
	public void deleteNotsForMobile(String mobNum)
	{
		//SQL remove statement, all records matching the mobile #
		String removeStatement = "DELETE FROM Notifications " +
			"WHERE mobileNum = '" + mobNum + "'";
		//run the remove statement
		DBMethods.statementToDB(removeStatement);
	}
	
	/*==============================================================
	 * statDeleteNotsForRecordID (STATIC) - Delete notifications for 
	 * a mobile #
	 * Input: A record ID
	 * Output: deletes notifications from DB
	 * Return: none
	 * given a record ID, deletes the notification
	 *==============================================================*/
	public static void statDeleteNotsForRecordID(int id)
	{
		//SQL remove statement
		String removeStatement = "DELETE FROM Notifications " +
			"WHERE notNum = " + Integer.toString(id);
		//run the statement against the DB
		DBMethods.statementToDB(removeStatement);
	}
	
	/*==============================================================
	 * getProviders - gets a string array of mobile providers
	 * Input: none
	 * Output: none
	 * Return: list of mobile providers (String[])
	 * This method will query the database to return a list of
	 * all available mobile providers currently in the database.
	 *==============================================================*/
	public String[] getProviders()
	{
		//select all fields from providers table
		String queryStatement = "SELECT * FROM providers";
		//run the query, get the results
		ResultSet x = DBMethods.queryDB(queryStatement);
		
		//a vector to hold the providers
		Vector<String> prov = new Vector<String>();
		String[] rval = new String[0];
		
		try
		{
			//if there is at least 1 result
			if(x.first())
			{
				do
				{
					//add it to the working vector
					prov.add(x.getString(1));
				}
				//go through all records
				while(x.next());
			}
		}
		catch(SQLException e)
		{
			System.out.println("Error :: failed to get providers " +
					"(serverMethods.getProviders) :: " + e.toString());
		}
		//return the working vector as a string array
		return prov.toArray(rval);
		
	}
	
	/*==============================================================
	 * authenticateAdmin - takes a username/password, returns
	 * boolean to indicate authorized or not
	 * Input: username (String) password (String)
	 * Output: none
	 * Return: authorization (boolean)
	 * this method takes a username and password, then queries the
	 * database with the username to find the associated password
	 * then, checking it, will return "TRUE" if the user is authorized.
	 * "FALSE" otherwise.
	 *==============================================================*/
	public boolean authenticateAdmin(String uName, String pWord)
	{
		//value ot return
		boolean rval = false;
		
		//select the password for the db record with matching username
		String dbQuery = "SELECT password FROM adminusers " +
				"WHERE username = '" + uName + "'";
		ResultSet x = DBMethods.queryDB(dbQuery);
		
		try
		{
			//make sure there is at least 1 result
			if(x.first())
			{
				//check the password
				if(pWord.compareTo(x.getString(1)) == 0)
				{
					//if it matches, authorized
					rval = true;
				}
				else
				{
					//otherwise, not.
					rval = false;
				}
			}
		}
		catch(SQLException e)
		{
			if(dbg){System.out.println("Error :: failed to authenticate " +
					"(serverMethods.authenticateUser) :: " + e.toString());}
			rval = false;
		}
		//return authorization results.
		return rval;
	}
	
	/*==============================================================
	 * cacheAvailability - added to significantly improve performance
	 * Input: spot name (String) availability (int)
	 * Output: implements a cache in the db
	 * Return: none
	 * This method helps the program out, because it used to be that
	 * every time a calculation was performed that depended on the
	 * program using image recognition to figure out whether a spot is
	 * available, it had to re-run the image recognition software, 
	 * this way, it can store calculations in the database for a 
	 * small amount of time, elimination a lot of redundant calculations
	 *==============================================================*/
	public static void cacheAvailability(String sName, int avail)
	{
		//add the result to the DB cache, storing the spot name, the
		//calculated availability, and the expire time for the calculation
		String dbStatement = "INSERT INTO availabilityCache " +
			"VALUES('" + sName + "'," + avail + "," + (System.currentTimeMillis() + 2000) + ")";
		DBMethods.statementToDB(dbStatement);
		
		//remove any expired cache records from the database, so the file system
		//doesn't get too huge, since there is no reason to keep them around.
		String delStatement = "DELETE FROM availabilityCache " +
			"WHERE expireTime < " + Long.toString(System.currentTimeMillis());
		DBMethods.statementToDB(delStatement);
	}
	
	/*==============================================================
	 * inCache - checks the cache to see if a calculation is in it
	 * Input: spot name (String)
	 * Output: none
	 * Return: availability (int 0 or 1) or not found (-1)
	 * This method checks the DB implemented cache to see if a calc
	 * about spot availability has already been performed within the
	 * last second or so (depending on what the expiration date) is
	 * on the cache record.
	 *==============================================================*/
	public static int inCache(String sName)
	{
		//assume not found (to be safe)
		int rval = -1;
		//see if there is a matching record in the cache, sort by the
		//most recent (although there shouldn't be more than one)
		String queryStatement = "SELECT available, expireTime FROM availabilityCache " +
			"WHERE spotName = '" + sName + "' " +
			"ORDER BY expireTime DESC";
		ResultSet x = DBMethods.queryDB(queryStatement);
		
		try
		{
			//if there is something
			if(x.first())
			{
				if(x.getLong(2) > System.currentTimeMillis())
				{
					rval = x.getInt(1);
				}
			}
			else
			{
				rval = -1;
			}
		}
		catch(SQLException e)
		{
			System.out.println("Error :: Couldn't query for availability cache " +
					"(serverMethods.inCache)");
		}
		
		String delStatement = "DELETE FROM availabilityCache " +
		"WHERE expireTime < " + Long.toString(System.currentTimeMillis());
		DBMethods.statementToDB(delStatement);
	
		return rval;
	}
	
}
