package com.ryanluedders.plmonitor.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/*=====================================================================
 * lotServiceAsync
 * Another very important part of GWT. This interface corresponds to the
 * "normal" version of lotService. The difference is that this is the
 * Asynchronous version, so it implements a sort of form of message passing
 * that lends itself to the client/server configuration. The difference
 * between this interface and the last is that these functions all require
 * that an AsyncCallback object must be passed into the function. This will
 * allow the program to keep track of when the results are ready.
 *=====================================================================*/
public interface LotServiceAsync 
{
	/* SYSTEM METHODS */
	void getSiteName(AsyncCallback cb);
	void getSysTime(AsyncCallback cb);
	
	/* LOT METHODS */
	void addLot(String lName, AsyncCallback cb);
	void deleteLot(String lName, AsyncCallback cb);
	void getFullLots(AsyncCallback cb);
	void getNotFullLots(AsyncCallback cb);
	void getAvgOpenPerLot(AsyncCallback cb);
	void getMostOpenPerLot(AsyncCallback cb);
	void getLeastOpenPerLot(AsyncCallback cb);
	void getMostOpenLot(AsyncCallback cb);
	void getLots(AsyncCallback cb);
	void getTotalSpots(String lot, AsyncCallback cb);
	void getTotalSpots(int lID, AsyncCallback cb);
	void getOpenSpots(String lot, AsyncCallback cb);
	void getLotDetails(String lName,AsyncCallback cb);
	void getColRowAvailable(String lname,AsyncCallback cb);
	
	/* VIEW METHODS */
	void addView(String vName, String lName, String fName, AsyncCallback cb);
	void getViews(AsyncCallback cb);
	void getViews(String lname, AsyncCallback cb);
	void getViewImage(String vName, AsyncCallback cb);
	void updateViewThreshold(String vname, double thresh, AsyncCallback cb);
	void getViewThreshold(String vName, AsyncCallback cb);
	void getSpotInfoForView(String vName, AsyncCallback cb);
	
	/* SPOT METHODS */
	void addSpot(String sName, String vName, int topX, int topY, int botX, int botY, AsyncCallback cb);
	void delSpot(String sName, AsyncCallback cb);
	void updateSpotInfo(String sname, int tX, int tY, int bX, int bY, int pRow, int pCol, String special, AsyncCallback cb);
	void getTotalOpenSpots(AsyncCallback cb);
	void getSpots(AsyncCallback cb);
	void getSpots(String vName, AsyncCallback cb);
	void getSpotsForLot(String lName, AsyncCallback cb);
	void getSpotXY(String sName,AsyncCallback cb);
	void getSpotRowCol(String sname,AsyncCallback cb);
	void getSpotSpecial(String sname, AsyncCallback cb);
	void getSpotAnalysis(String sName, AsyncCallback cb);
	void getSpotsDetails(String lot, AsyncCallback cb);
	void getSpotsDetails(int lID, AsyncCallback cb);
	void getSpotThreshold(String sName, AsyncCallback cb);
	
	/* OTHER METHODS */
	void startTimedStats(int interval, AsyncCallback cb);
	void getChartsURL(String lName, String dayName, AsyncCallback cb);
	void addNotification(String mobNum, String prov, String lname, int lvl, 
			long time, int recurTimes, long recurInterval, AsyncCallback cb);
	void deleteNotsForMobile(String mobNum, AsyncCallback cb);
	void startTimedNotifications(int interval, AsyncCallback cb);
	void getProviders(AsyncCallback cb);
	void getOverviewInfo(AsyncCallback cb);
	void authenticateAdmin(String uName, String pWord, AsyncCallback cb);
	
	void takeStats(AsyncCallback cb);
	void checkNotifications(AsyncCallback cb);
}
