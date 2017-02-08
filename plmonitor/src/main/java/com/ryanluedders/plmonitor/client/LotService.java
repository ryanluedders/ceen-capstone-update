package com.ryanluedders.plmonitor.client;

import java.util.Vector;

import com.google.gwt.user.client.rpc.RemoteService;

/*=====================================================================
 * lotService
 * extends- RemoteService
 * This is a very important part of using the GWT. This helps to connect
 * the client, Javascript, part to the Server, Java, run part. This
 * interface contains all the functions as they are implemented by the
 * serverMethods.java file on the server side, which correspond to the
 * Asynchronous methods in the lotServiceAsync.java file on the client
 * side.
 *=====================================================================*/
public interface LotService extends RemoteService
{
	/* SYSTEM METHODS */
	//Get the current site name
	public String getSiteName();
	//Get the current system time
	public String getSysTime();
	
	
	/* LOT METHODS */
	//Add a lot by name
	public void addLot(String lname);
	//Delete a lot by name
	public void deleteLot(String lname);
	//get the number of full lots
	int getFullLots();
	//Get the number of not full lots
	int getNotFullLots();
	//get the average number of spots open per lot
	int getAvgOpenPerLot();
	//get the most spots open per lot
	int getMostOpenPerLot();
	//get the least spots open per lot
	int getLeastOpenPerLot();
	//get the most open lot
	String getMostOpenLot();
	//get a list of all the lots
	String[] getLots();
	//get the total number of spots in a lot
	int getTotalSpots(String lot);
	//get the total number of spots in a lot
	int getTotalSpots(int lID);
	//get the total number of open spots in a lot
	int getOpenSpots(String lot);
	//Get the lot details for a lot
	String[] getLotDetails(String lName);
	//will get all spots with row/col assigned & if they available or not
	public Vector getColRowAvailable(String lname);
	
	
	/* VIEW METHODS */
	//Add a view by name, and parent lot
	public void addView(String vName, String lName, String fName);
	//get a list of all the views
	String[] getViews();
	//get a list of all the views for a specified lot
	String[] getViews(String lname);
	//get an image name associated with a view
	public String getViewImage(String vName);
	//update the processing threshold associated with a view
	public void updateViewThreshold(String vname, double thresh);
	//get the processing threshold associated with a view
	public double getViewThreshold(String vName);
	public Vector getSpotInfoForView(String vName);
	
	
	/* SPOT METHODS */
	//Add a spot by name, and parent view
	public void addSpot(String sName, String vName, int topX, int topY, int botX, int botY);
	//delete a spot by name
	public void delSpot(String sName);
	//update the info associated with a spot - associated with spot edit dialog
	public void updateSpotInfo(String sname, int tX, int tY, int bX, int bY, int pRow, int pCol, String special);
	//Get the number of open spots
	int getTotalOpenSpots();
	//get a list of all the spots
	String[] getSpots();
	//get a list of all the spots for a specified view
	String[] getSpots(String vName);
	//get a list of all the spots for a specified lot
	String[] getSpotsForLot(String lName);
	//get the X/Y coordinate on the associated image for this spot
	int[] getSpotXY(String sName);
	//get the row and column of the lot that this spot is in
	int[] getSpotRowCol(String sname);
	//get any special conditions associated with this spot
	String getSpotSpecial(String sname);
	//get the image processing analysis in string form
	public String getSpotAnalysis(String sName);
	//get a String array with a description of spot details for a lot
	String[][] getSpotsDetails(String lot);
	//get a String array with a description of spot details for a lot
	String[][] getSpotsDetails(int lID);
	//get the threshold for a spot
	public double getSpotThreshold(String sName);
	
	/* OTHER METHODS */
	//start the timer to take stats given an interval
	public void startTimedStats(int interval);
	//get the URL to google charts to show charts
	public String[] getChartsURL(String lName, String dayName);
	//add a notification to the DB
	public void addNotification(String mobNum, String prov, String lname, int lvl, 
			long time, int recurTimes, long recurInterval);
	//delete notifications for a specified mobile
	public void deleteNotsForMobile(String mobNum);
	//start the timer to check if there are any notifications pending
	public void startTimedNotifications(int interval);
	//get a list of mobile providers notifications are supported for
	public String[] getProviders();
	//get a set of strings with the info for the site overview.
	public String[] getOverviewInfo();
	//authenticate an administrator by checking their username
	//and password with the users stored in the database.
	public boolean authenticateAdmin(String uName, String pWord);
	
	public void takeStats();
	public void checkNotifications();
}

