package com.luedders.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*=====================================================================
 * dbMethods - Methods for interacting with the System Database
 * This is a class that is used to interact with the database, which
 * performs pretty low-level functions, the higher level functions will
 * be found in serverMethods.java, and will reference the methods
 * in this file.
 *=====================================================================*/
public class dbMethods 
{
	//booleans to enable display of dbg and info output
	public static boolean dbg = false;
	public static boolean info = false;
	
	//Connection used to access the database
	public static Connection conn;

	//The string that will be sent to all panels with the site name
	public static String siteName = "Test Lot";

	/*==============================================================
	 * insertIntoDB - Insert a spot into the DB
	 * Input: Spot to Insert (com.luedders.server.Spot)
	 * Output: None
	 * Return: success? (boolean)
	 * The input value must be of type Spot, which implements the
	 * toDB() method to create an insert statement. This method just
	 * uses the toDB() method to create and run the insert statement
	 * against the database.
	 *==============================================================*/
	public static boolean insertIntoDB(Spot x)
	{
		//the return value
		boolean rval = true;
		//send the insert statement to the database
		statementToDB(x.toDB());
		return rval;
	}
	
	/*==============================================================
	 * insertIntoDB - add a lot into the database
	 * Input: the Lot object to insert into the database
	 * Output: None
	 * Return: success? (boolean)
	 * the input object must be of type "lot" which implements the
	 * toDB() method, because this function just uses the toDB() method
	 * of the object passed in to run an insert statement against
	 * the database and insert the lot.
	 *==============================================================*/
	public static boolean insertIntoDB(Lot x)
	{
		boolean rval = true;
		//use toDB method and run the insert statement
		statementToDB(x.toDB());
		return rval;		
	}
	
	/*==============================================================
	 * insertIntoDB - Insert a view into the DB
	 * Input: View to Insert (com.luedders.server.View)
	 * Output: None
	 * Return: success? (boolean)
	 * The input value must be of type View, which implements the
	 * toDB() method to create an insert statement. This method just
	 * uses the toDB() method to create and run the insert statement
	 * against the database.
	 *==============================================================*/
	public static boolean insertIntoDB(View x)
	{
		boolean rval = true;
		//use toDB() method and run insert statement
		statementToDB(x.toDB());
		return rval;		
	}
	
	public static boolean insertIntoDB(Statistic x)
	{
		boolean rval = true;
		//use toDB() method and run insert statement
		statementToDB(x.toDB());
		return rval;		
	}
	
	public static boolean insertIntoDB(Notification x)
	{
		boolean rval = true;
		//use toDB() method and run insert statement
		statementToDB(x.toDB());
		return rval;		
	}
	
	/*==============================================================
	 * removeFromDB - remove a lot from the db
	 * Input: Lot name to remove (String)
	 * Output: None
	 * Return: success? (boolean)
	 * Here, the name of a lot is input, and a remove statement is
	 * created and run against the database which will remove the
	 * lot with that name from the database.
	 *==============================================================*/
	public static boolean removeFromDB(String lName)
	{
		boolean rval = true;
		//add the lot name into the remove statement
		String removeStatement = "DELETE FROM Lots WHERE ParkingLotName = '" +
				lName + "'";
		//run the remove statement to remove the lot from the database.
		statementToDB(removeStatement);
		return rval;
	}
	
	/*==============================================================
	 * removeSpotFromDB - remove a spot from the db
	 * Input: spot name to remove (String)
	 * Output: None
	 * Return: success? (boolean)
	 * the name of a spot is input into the function, then added to
	 * the remove statement to remove the spot with that name from
	 * the spots table in the database.
	 *==============================================================*/
	public static boolean removeSpotFromDB(String sName)
	{
		boolean rval = true;
		//add the spot name into the remove statement.
		String removeStatement = "DELETE FROM Spots WHERE SpotName = '" +
				sName + "'";
		//run the remove statement against the database.
		statementToDB(removeStatement);
		return rval;
	}
	
	/*==============================================================
	 * removeViewFromDB - remove a view from the Database
	 * Input: view Name to remove (String)
	 * Output: None
	 * Return: success? (boolean)
	 * the name of a view to remove is brought into the function. It
	 * is then added into a remove statement and run against the
	 * database to remove the view from the database.
	 *==============================================================*/
	public static boolean removeViewFromDB(String vName)
	{
		boolean rval = true;
		//add the view name into the remove statement
		String removeStatement = "DELETE FROM Views WHERE ViewName = '" +
				vName + "'";
		//run the remove statement against the database
		statementToDB(removeStatement);
		return rval;
	}
	
	/*==============================================================
	 * createConn - create the connection to the database
	 * Input: None
	 * Output: None
	 * Return: success? (boolean)
	 * Accesses the connection variable declared as part of the
	 * dbMethods class, and creates a connection to the Java Derby
	 * JDBC Driver.
	 *==============================================================*/
	public static boolean createConn()
	{
		try
		{
			//create the connection (DERBY)
			/*Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			conn = DriverManager.getConnection("jdbc:derby:slicr;create=true");*/
			
			//create the connection (MYSQL)
			Class.forName("com.mysql.jdbc.Driver");
			String url ="jdbc:mysql://localhost:3306/gwtThesis";
			conn = DriverManager.getConnection(url,"gwtThesis", "thesis");
			
		    if(info){System.out.println("URL: " + url);}
		    if(info){System.out.println("Connection: " + conn);}
		}
		catch(ClassNotFoundException e)
		{
			System.out.println("Error :: DB Connect class not found " +
					"(dbMethods.createConn) :: " + e.toString());
		}
		catch(SQLException e)
		{
			System.out.println("Error :: DB Connect SQL Exception " +
					"(dbMethods.createConn) :: " + e.toString());
		}
		return true;
	}

	/*==============================================================
	 * statementToDB - send a statement to the DB
	 * Input: Statement (String)
	 * Output: None
	 * Return: success? (boolean)
	 * This function will take a string that is passed in and run
	 * it as a statement agains the database. This would be for
	 * create, insert, delete statements. There is a separate function
	 * for running queries agains the database.
	 *==============================================================*/
	public static boolean statementToDB(String statement)
	{
		boolean rval = true;
		try
		{
			//Create a new derby statement using the connection
			//that needs to be created prior to running this function
			Statement derbyStatement = conn.createStatement();
			//execute the statement passed in.
			derbyStatement.execute(statement);
		}
		catch(SQLException e)
		{
			//in case of an exception, return a false, and print to system
			System.out.println("Error :: Statement to DB Unsuccessful " +
					"(dbMethods.statementToDB) :: " + e.toString());
			rval = false;
		}
		return rval;
	}

	/*==============================================================
	 * initDB - initialize the Datbase used with this system
	 * Input: None
	 * Output: None
	 * Return: success? (boolean)
	 * This function executes all of the statements necessary
	 * to create the tables, so they are set up using the necessary
	 * database integrity constraints.
	 *==============================================================*/
	public static boolean initDB()
	{	
		//Create the Lots table, with primary key ParkingLotID
		statementToDB("CREATE TABLE Lots( \n" +
				"ParkingLotID int , \n" +
				"ParkingLotName char(20) , \n" +
		"PRIMARY KEY (ParkingLotID))");
		
		//Create the Views table, with primary key ViewID, and
		//foreign key ViewLot - referencing a ParkingLotID
		statementToDB("CREATE TABLE Views( \n" +
				"ViewID int, \n" +
				"ViewName char(20), \n" +
				"ViewLot int, \n" +
				"ImgFile char(50), \n" +
				"detectionThreshold int, \n" +
				"PRIMARY KEY (ViewID), \n" +
		"FOREIGN KEY (ViewLot) references Lots(ParkingLotID))");
	
		//Create the Spots table, with primary key SpotID and
		//foreign key spViewID - referencing a ViewID
		statementToDB("CREATE TABLE Spots( \n" +
				"SpotID int, \n" +
				"SpotName char(20), \n" +
				"spViewID int, \n" +
				"SpotPhysicalRow int, \n" +
				"SpotPhysicalCol int, \n" +
				"SpotViewTopX int, \n" +
				"SpotViewTopY int, \n" +
				"SpotViewBotX int, \n" +
				"SpotViewBotY int, \n" +
				"SpecialSpotCond char(20), \n" +
				"PRIMARY KEY (SpotID), \n" +
		"FOREIGN KEY (spViewID) references Views(ViewID))");
		
		//Create the Statistics table, with primary key statID
		//and foreign key Lot - referencing a ParkingLotID
		statementToDB("CREATE TABLE Stats( \n" +
				"StatID int, \n" +
				"StatTime bigint, \n" +
				"LotID int, \n" +
				"SpotsOpen int, \n" +
				"SpotsTaken int, \n" +
				"PRIMARY KEY (StatID), \n" +
		"FOREIGN KEY (LotID) references Lots(ParkingLotID))");
		
		//Create the events table, primary key eventID
		//and foreign key Lot - referencing a lotID
		statementToDB("CREATE TABLE Events( \n" +
				"EventID int, \n" +
				"EventName char(25), \n" +
				"LotAffected int, \n" + 
				"StartDate date, \n" +
				"StartTime int, \n" +
				"EndDate date, \n" +
				"EndTime int, \n" +
				"PRIMARY KEY (EventID), \n" +
		"FOREIGN KEY (LotAffected) references Lots(ParkingLotID))");
		
		statementToDB("CREATE TABLE Providers( \n" +
				"ProviderName char(50), \n" +
				"PRIMARY KEY (ProviderName))");
		
		statementToDB("CREATE TABLE Notifications( \n" +
				"notNum int, \n" +
				"mobileNum char(15), \n" +
				"Provider char(50), \n" +
				"LotName char(50), \n" +
				"NotLevel int, \n" +
				"NotTime bigint, \n" +
				"RecurTimes int, \n" +
				"RecurInterval bigint, \n" +
				"PRIMARY KEY (notNum), \n" +
		"FOREIGN KEY (Provider) references Providers(ProviderName))");
		
		return true;
	}

	/*==============================================================
	 * queryDB - run a query against the database
	 * Input: A query to run (String)
	 * Output: None
	 * Return: The results of the query (ResultSet)
	 * This function takes a string which represents a standard SQL
	 * query to run against this database, and runs it, returning the
	 * results that it receives back to the user.
	 *==============================================================*/
	public static ResultSet queryDB(String query)
	{
		//a new result set
		ResultSet rs = null;
		try
		{
			//create a new statement
			Statement derbyStatement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			//execute the query contained in the string
			rs = derbyStatement.executeQuery(query);
		}
		catch(SQLException e)
		{
			//if there is an error, print it to system.out
			System.out.println("Error :: Query DB Unsuccessful " +
					"(dbMethods.queryDB) :: " + e.toString());
		}
		//return the results of the query, or null if no results.
		return rs;
	}

	/*==============================================================
	 * printDB - print the Database
	 * Input: None
	 * Output: None
	 * Return: None
	 * This function will query to get all the Lots, Views, and Spots,
	 * and then go through and print all the results to System.out
	 *==============================================================*/
	public static void printDB()
	{
		//Query to get Lots
		ResultSet x = queryDB("Select * From Lots");
		//print results to system.out
		printResultSet(x,2);
		//Query to get Views
		ResultSet y = queryDB("Select * From Views");
		//print results to system.out
		printResultSet(y,4);
		//Query to get Spots
		ResultSet z = queryDB("Select * From Spots");
		//print results to system.out
		printResultSet(z,10);
	}
	
	/*==============================================================
	 * printResultSet - prints a resultSet
	 * Input: resultSet (resultSet) number of columns (int)
	 * Output: None
	 * Return: None
	 * Given a ResultSet, this function will print all values within
	 * it.
	 *==============================================================*/
	public static void printResultSet(ResultSet rs, int cols)
	{
		try 
		{
			//while there is another record in the set
			while(rs.next())
			{
				//for all the columns
				for(int i=1; i<=cols; i++)
				{
					//print out the result value
					if(dbg){System.out.print(rs.getString(i) + " " );}
				}
				//after each record, print a newline
				if(dbg){System.out.println("");}
			}
		} 
		catch (SQLException e) 
		{
			//in case of an exception, print to system.out
			System.out.println("Error :: Couldn't Print ResultSet " +
					"(dbMethods.printResultSet) :: " + e.toString());
		}
	}

	/*==============================================================
	 * dropTables - drops the Tables contained in this DB.
	 * Input: None
	 * Output: None
	 * Return: None
	 * This is useful mostly for testing purposes, it will drop
	 * the three tables contained in this system Database.
	 *==============================================================*/
	public static void dropTables()
	{
		//Drop Spots, Views, and Lots Tables
		statementToDB("DROP TABLE Spots");
		statementToDB("DROP TABLE Views");
		statementToDB("DROP TABLE Lots");
	}

	/*==============================================================
	 * insertTestData - inserts test data
	 * Input: None
	 * Output: None
	 * Return: None
	 * This is useful mostly for testing purposes, it inserts some
	 * test data into the database.
	 *==============================================================*/
	public static void insertTestData()
	{
		//insert some lots
		Lot lA = new Lot(001);
		lA.setLotName("Lot_1");
		Lot lB = new Lot(002);
		lB.setLotName("Lot_2");
		Lot lC = new Lot(003);
		lC.setLotName("Lot_3");

		insertIntoDB(lA);
		insertIntoDB(lB);
		insertIntoDB(lC);

		//insert some views
		View vA = new View(001,001);
		vA.setViewName("Lot_1-View_A");
		View vB = new View(002,001);
		vB.setViewName("Lot_1-View_B");
		View vC = new View(003,002);
		vC.setViewName("Lot_2-View_C");

		insertIntoDB(vA);
		insertIntoDB(vB);
		insertIntoDB(vC);

		//insert some spots
		Spot lAsA = new Spot(001);
		lAsA.setSpotName("Lot_1-Spot_A");
		lAsA.setView(001);
		Spot lAsB = new Spot(002);
		lAsB.setSpotName("Lot_1-Spot_B");
		lAsB.setView(001);
		Spot lAsC = new Spot(003);
		lAsC.setSpotName("Lot_1-Spot_C");
		lAsC.setView(001);
		Spot lCsA = new Spot(004);
		lCsA.setSpotName("Lot_3-Spot_A");
		lCsA.setView(003);

		insertIntoDB(lAsA);
		insertIntoDB(lAsB);
		insertIntoDB(lAsC);
		insertIntoDB(lCsA);
	}
}
