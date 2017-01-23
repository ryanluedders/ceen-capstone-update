package com.luedders.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/*=====================================================================
 * appFrame is the base User Interface class. It is the
 * frame that is loaded into the browser document. I have
 * used it to load all of the other business frames into and
 * out of. This requires the appFrame to be a clicklistener
 * for all of the frames it contains, to control flow of the
 * application
 *=====================================================================*/
public class appFrame extends VerticalPanel
{
	//variables to enable standard out to display debug info
	//and general program operation information
	public static boolean dbg = false;
	public static boolean info = false;

	//These are the business frames of the application that are
	//moved on and off screen by the appframe
	private uiOverview uiOv = new uiOverview();
	private uiLotDetails uiLD = new uiLotDetails();
	private uiSpotLocs uiSL = new uiSpotLocs();
	private uiAdminOverview uiAO = new uiAdminOverview();
	private uiAdminLotView uiLV = new uiAdminLotView();

	//This is the panel that is used inside the appframe to load
	//above panels into
	public VerticalPanel contentPane = new VerticalPanel();
	public authenticateUser auth = new authenticateUser();

	//The clickListener that will handle the buttons that are tasked
	//with moving the above frames on and off screen
	private appClkListener aL = new appClkListener();

	//a boolean to keep track of whether the user has authenticated
	//with a valid username/password or not.
	public boolean authenticated = false;
	public boolean isUpdating = false;

	//a timer to run periodically that will refresh the visible screen
	public class refreshTimer extends Timer
	{
		//the standard procedure to run for the timer
		public void run() 
		{
			//check which screen is visible
			if(uiLV.isVisible())
			{
				//and refresh only the visible screen
				uiLV.pageRefresh();
			}
			if(uiAO.isVisible())
			{
				uiAO.pageRefresh();
			}
			if(uiLD.isVisible())
			{
				uiLD.pageRefresh();
			}
			if(uiSL.isVisible())
			{
				uiSL.pageRefresh();
			}
			if(uiOv.isVisible() & !uiOv.isUpdating)
			{
				uiOv.pageRefresh();
			}
		}
	}
	
	public class notTimer extends Timer
	{
		//the standard procedure to run for the timer
		public void run() 
		{
			srvStartTimedNotifications();
		}
	}
	
	public class statTimer extends Timer
	{
		//the standard procedure to run for the timer
		public void run() 
		{
			srvStartTimedStats();
		}
	}

	//A timer in the appFrame to schedule periodic refreshing of screens
	refreshTimer rTimer = new refreshTimer();
	notTimer nTimer = new notTimer();
	statTimer sTimer = new statTimer();

	/*==============================================================
	 * Default Constructor
	 * Input: None
	 * Output: None
	 * Return: None
	 * Adds ClickListeners to all the buttons on the included panels
	 * and makes sure that the application will take up the whole 
	 * browser screen.
	 *==============================================================*/
	public appFrame()
	{
		//Add the clicklisteners to handle moving panels in and
		//out of the contentpane
		uiOv.btnViewDetails.addClickListener(aL);
		uiLD.btnBack.addClickListener(aL);
		uiLD.btnViewSpots.addClickListener(aL);
		uiSL.btnBack.addClickListener(aL);
		uiSL.btnEnterAdmin.addClickListener(aL);
		uiAO.btnLvAdmin.addClickListener(aL);
		uiOv.btnEnterAdmin.addClickListener(aL);
		uiLV.btnBack.addClickListener(aL);
		uiLV.btnLvAdmin.addClickListener(aL);
		uiLD.btnEnterAdmin.addClickListener(aL);
		uiAO.btnEditLot.addClickListener(aL);
		auth.btnCancel.addClickListener(aL);
		auth.btnSubmit.addClickListener(aL);

		//take up the whole browser screen
		this.setHeight("90%");
		this.setWidth("100%");
		//begin by showing the user overview screen
		contentPane.add(uiOv);
		this.add(contentPane);
		//set so the contentpane takes up the whole screen
		contentPane.setHeight("100%");
		contentPane.setWidth("100%");

		//start the stat-taking timer.
		//srvStartTimedStats(100000);
		//start the notification sending timer
		//srvStartTimedNotifications(15000);
		//shorter interval for testing
		//srvStartTimedStats(5000);
		rTimer.scheduleRepeating(10000);
		nTimer.scheduleRepeating(15000);
		sTimer.scheduleRepeating(60000);
	}

	/*==============================================================
	 * appClkListener
	 * implements ClickListener
	 * This is the interior class used to listen for mouse clicks
	 * to move the "business panels" on and off the screen
	 *==============================================================*/
	private class appClkListener implements ClickListener
	{
		/*==============================================================
		 * onClick
		 * Input: The sending Widget
		 * Output: None
		 * Return: None
		 * The onClick function waits for a click from any of the buttons
		 * on the panels it is listening for. After receiving a click,
		 * depending on the current state of the application, it will
		 * handle the moving of panels on and off the screen
		 *==============================================================*/
		public void onClick(Widget sender) 
		{	
			//On the authorization popup, if the users submits
			if(sender.equals(auth.btnSubmit))
			{
				//call the server to see if their password & username are valid
				srvEnterAdmin(auth.txtEnterUName.getText(),auth.txtEnterPWord.getText());
				//hide the login box after the user submits
				auth.hide();
			}
			//if the user cancels on the authorization popup, simply
			//return to the site overview screen
			if(sender.equals(auth.btnCancel))
			{
				//show the site overview
				uiOv.setVisible(true);
				contentPane.add(uiOv);
				//hide the login box after the user cancels.
				auth.hide();
			}
			//User Overview Screen - View Details Button
			if(sender.equals(uiOv.btnViewDetails))
			{
				//Remove Overview Screen
				contentPane.remove(uiOv);
				//Refresh components on Lot Details Screen
				//uiLD.refreshComponents();
				//Show Lot Details Screen
				uiOv.setVisible(false);
				uiLD.setVisible(true);
				contentPane.add(uiLD);

			}
			//Lot Details Screen - Back Button
			if(sender.equals(uiLD.btnBack))
			{
				//Remove Lot Details Screen
				contentPane.remove(uiLD);
				//Refresh Components on User Overview Screen
				//uiOv.pageRefresh();
				//Show User Overview Screen
				uiLD.setVisible(false);
				uiOv.setVisible(true);
				contentPane.add(uiOv);

				uiLD.imgDayChart.setVisible(false);
				uiLD.imgWeekChart.setVisible(false);
			}
			//Lot Details Screen - View Spots Button
			if(sender.equals(uiLD.btnViewSpots))
			{
				//Remove Lot Details Screen
				contentPane.remove(uiLD);
				uiSL.setLotName(uiLD.lstLots.getItemText(uiLD.lstLots.getSelectedIndex()));
				//Show Spot Location Screen
				uiLD.setVisible(false);
				uiSL.setVisible(true);
				contentPane.add(uiSL);

				uiLD.imgDayChart.setVisible(false);
				uiLD.imgWeekChart.setVisible(false);
			}
			//Spot Location Screen - Back Button
			if(sender.equals(uiSL.btnBack))
			{
				//Remove Spot Location Screen
				contentPane.remove(uiSL);
				//Refresh Components on Lot Details Screen
				//uiLD.refreshComponents();
				//Show Lot Details Screen
				uiSL.setVisible(false);
				uiLD.setVisible(true);
				contentPane.add(uiLD);

			}
			//Spot Location Screen - Enter Admin Button
			if(sender.equals(uiSL.btnEnterAdmin))
			{
				//Remove Spot Location Screen
				contentPane.remove(uiSL);
				//Refresh Components on Admin Screen
				//uiAO.refreshComponents();
				//show Admin Screen
				uiSL.setVisible(false);

				if(authenticated)
				{
					uiAO.setVisible(true);
					contentPane.add(uiAO);
				}
				else
				{
					auth.center();
					auth.show();
				}
			}
			//User Overview - Enter Admin Screen Button
			if(sender.equals(uiOv.btnEnterAdmin))
			{
				//Remove the user overview screen
				contentPane.remove(uiOv);
				//Refresh Components on Admin Overview Screen
				//uiAO.refreshComponents();
				//Show Admin Overview Screen
				uiOv.setVisible(false);

				if(authenticated)
				{
					uiAO.setVisible(true);
					contentPane.add(uiAO);
				}
				else
				{
					auth.center();
					auth.show();
				}

			}
			//Admin Overview Screen - Leave Admin Button
			if(sender.equals(uiAO.btnLvAdmin))
			{
				//Remove the Admin Overview screen
				contentPane.remove(uiAO);
				//refresh components on User overview screen
				//uiOv.pageRefresh();
				//show the User Overview screen
				uiAO.setVisible(false);
				uiOv.setVisible(true);
				authenticated = false;
				contentPane.add(uiOv);

			}
			//Lot View screen - back button
			if(sender.equals(uiLV.btnBack))
			{
				//remove the lot view screen
				contentPane.remove(uiLV);
				//refresh components on admin overview screen
				//uiAO.refreshComponents();
				//show the admin overview screen
				uiLV.setVisible(false);
				uiAO.setVisible(true);
				contentPane.add(uiAO);

				uiLV.lotImg.setVisible(false);
			}
			//Lot View Screen - Leave Admin Button
			if(sender.equals(uiLV.btnLvAdmin))
			{
				//remove the lot view screen
				contentPane.remove(uiLV);
				//refresh components on user overview screen
				//uiOv.pageRefresh();
				//show the user overview screen
				uiLV.setVisible(false);
				uiOv.setVisible(true);
				authenticated = false;
				contentPane.add(uiOv);

				uiLV.lotImg.setVisible(false);
			}
			//Lot Details Screen - Enter Admin Button
			if(sender.equals(uiLD.btnEnterAdmin))
			{
				//remove the lot details screen
				contentPane.remove(uiLD);
				//refresh components on admin overview screen
				uiAO.refreshComponents();
				//show the admin overview screen

				if(authenticated)
				{
					uiAO.setVisible(true);
					contentPane.add(uiAO);
				}
				else
				{
					auth.center();
					auth.show();
				}

				uiLD.imgDayChart.setVisible(false);
				uiLD.imgWeekChart.setVisible(false);
			}
			//Admin overview screen - edit lot button
			if(sender.equals(uiAO.btnEditLot))
			{
				//send the lot being edited to the next lot-edit scren
				uiLV.setCurLotName(uiAO.lstLots.getItemText(uiAO.lstLots.getSelectedIndex()));
				//refresh components on the lot view/edit screen
				//uiLV.refreshComponents();
				//remove the admin overview screen
				contentPane.remove(uiAO);
				uiAO.setVisible(false);
				//show the lot view/edit screen
				uiLV.setVisible(true);
				contentPane.add(uiLV);

				uiLD.imgDayChart.setVisible(false);
				uiLD.imgWeekChart.setVisible(false);
			}
		}
	}

	/*==============================================================
	 * srvStartTimedStats - starts the timer to monitor the system
	 * and record stats in the database
	 * Input: the interval (int)
	 * Output: Starts the timer
	 * Return: None
	 * Starts a timer to store stats in the database over a certain
	 * interval defined in the function call.
	 *==============================================================*/
	public void srvStartTimedStats()
	{
		//new asynchronous lot service
		lotServiceAsync lsA = (lotServiceAsync) GWT.create(lotService.class);
		ServiceDefTarget ep = (ServiceDefTarget) lsA;
		//url specified in the CSS file that shows where the server is
		String moduleRelativeURL = GWT.getModuleBaseURL()+"thesisServ";
		ep.setServiceEntryPoint(moduleRelativeURL);
		//create a new callback object that will do what is necessary
		//with the results of the server call
		AsyncCallback cb = new AsyncCallback()
		{
			public void onSuccess(Object result)
			{
				if(info){System.out.println("Started Timer");}
			}
			public void onFailure(Throwable caught)
			{
				System.out.println("Error :: Could Not Start Timed Stats " +
				"(appFrame.srvStartTimedStats)");
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results - delete the spot, by name
		lsA.takeStats(cb);
	}

	/*==============================================================
	 * srvStartTimedNotifications - starts the timer used to notify
	 * users via text message
	 * Input: the interval to run the timer
	 * Output: Starts the timer
	 * Return: None
	 * Starts a timer to run periodically, checking the database, and
	 * updating users via text message their requested information
	 *==============================================================*/
	public void srvStartTimedNotifications()
	{
		//new asynchronous lot service
		lotServiceAsync lsA = (lotServiceAsync) GWT.create(lotService.class);
		ServiceDefTarget ep = (ServiceDefTarget) lsA;
		//url specified in the CSS file that shows where the server is
		String moduleRelativeURL = GWT.getModuleBaseURL()+"thesisServ";
		ep.setServiceEntryPoint(moduleRelativeURL);
		//create a new callback object that will do what is necessary
		//with the results of the server call
		AsyncCallback cb = new AsyncCallback()
		{
			public void onSuccess(Object result)
			{
				//for system logging
				if(info){System.out.println("Started Notification Timer");}
			}
			public void onFailure(Throwable caught)
			{
				//error
				System.out.println("Error :: Could Not Start Timed " +
				"Notifications (appFrame.srvStartTimedNotifications)");
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results - delete the spot, by name
		lsA.checkNotifications(cb);
	}

	/*==============================================================
	 * srvEnterAdmin - checks username/password with the server to
	 * see authenticate an admin user.
	 * Input: username (String) password (String)
	 * Output: if valid, opens admin interface
	 * Return: None
	 * Queries the db with the user supplied username and password
	 * to see if they are a valid administrator, and if they are, it
	 * allows them to enter the administrative interface, if not, it
	 * will return them to the site overview screen.
	 *==============================================================*/
	public void srvEnterAdmin(String uName, String pWord)
	{
		//new asynchronous lot service
		lotServiceAsync lsA = (lotServiceAsync) GWT.create(lotService.class);
		ServiceDefTarget ep = (ServiceDefTarget) lsA;
		//url specified in the CSS file that shows where the server is
		String moduleRelativeURL = GWT.getModuleBaseURL()+"thesisServ";
		ep.setServiceEntryPoint(moduleRelativeURL);
		//create a new callback object that will do what is necessary
		//with the results of the server call
		AsyncCallback cb = new AsyncCallback()
		{
			public void onSuccess(Object result)
			{
				//access the variable keeping track of if the user is
				//authenticated
				appFrame.this.authenticated = ((Boolean)result).booleanValue();
				if(dbg){System.out.println(appFrame.this.authenticated);}

				//if the user is already authenticated or user/password was valid
				if(authenticated)
				{
					//enter that administrative interface
					appFrame.this.uiAO.setVisible(true);
					appFrame.this.add(uiAO);
				}
				//if they are not already authenticated & user/password was invalid
				else
				{
					//return to the site overview screen
					uiOv.setVisible(true);
					appFrame.this.add(uiOv);
					//for system information purposes, logging.
					if(info){System.out.println("Incorrect Username or Password");}
				}
			}
			public void onFailure(Throwable caught)
			{
				if(dbg){System.out.println("Error :: Could Enter Admin " +
				"(appFrame.srvEnterAdmin)");}
				//on failure, return to the overview screen
				uiOv.setVisible(true);
				appFrame.this.add(uiOv);
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results - delete the spot, by name
		lsA.authenticateAdmin(uName, pWord, cb);
	}
}
