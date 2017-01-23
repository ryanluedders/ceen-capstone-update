package com.luedders.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/*=====================================================================
 * uiLotDetails - The general user interface lot details
 * extends - srvAccessor
 * this is the panel that is used to show the user the details on
 * a lot, including detailed statistics and current information on
 * the spots in a selected lot
 *=====================================================================*/
public class uiLotDetails extends srvAccessor
{
	//booleans to enable info & debug output
	public static boolean dbg = false;
	public static boolean info = false;
	
	//A dropdown box to display the available lots
	public ListBox lstLots = new ListBox();
	//list of days of the week
	public ListBox lstDays = new ListBox();
	
	//A grid to show the title of the lot
	public Grid lotTitle = new Grid(1,1);
	//A grid to show the details of the lot
	public Grid lotValues = new Grid(2,2);
	
	//A grid to show the title of the current events chart
	public Grid eventTitle = new Grid(1,1);
	//A grid to show the events that are coming up
	public Grid eventValues = new Grid(3,2);
	
	//Buttons to view spots, enter the admin interface, and go
	//back to the user interface overview
	public Button btnViewSpots = new Button();
	public Button btnBack = new Button();
	public Button btnEnterAdmin = new Button();
	
	public Image waitImg = new Image("loadinfo.net.gif");
	public Image imgWeekChart = new Image();
	public Image imgDayChart = new Image();
	
	public uiLDChgListener chgList = new uiLDChgListener();
	
	//A string showing the name of the current lot.
	public String curLot = "Lot Details";
	
	/*==============================================================
	 * Default Constructor
	 * Input: None
	 * Output: None
	 * Return: None
	 * the default constructor for this panel. initializes the
	 * components and places them in the correct place.
	 *==============================================================*/
	public uiLotDetails()
	{
		//call the constructor for a srvAccessor
		super();
		//initialize the components
		initComponents();
		//place the components
		placeComponents();
	}
	
	/*==============================================================
	 * refreshComponents
	 * Input: None
	 * Output: will update the components
	 * Return: None
	 * updates the list of lots
	 *==============================================================*/
	public void refreshComponents()
	{
		//update the list of lots on this panel
		srvLotList();
		lstDays.setSelectedIndex(0);
		return;
	}
	
	/*==============================================================
	 * initComponents
	 * Input: None
	 * Output: initialize all the components on this panel
	 * Return: None
	 * updates the list of lots
	 *==============================================================*/
	public void initComponents()
	{
		//start by refreshing the components
		refreshComponents();
		
		//Set the text of the button to enter admin area
		btnEnterAdmin.setText("Enter Admin Area");
		
		//set the title of the lot in the details section
		lotTitle.setText(0,0,curLot);
		lotTitle.setStylePrimaryName("gwtThesis-tableTitle");
		lotTitle.setWidth("20ex");
		//set the descriptions of the details
		lotValues.setText(0,0,"Total Spots");
		lotValues.setText(1,0,"Open Spots");
		lotValues.getCellFormatter().setHorizontalAlignment(0, 1, HTML.ALIGN_RIGHT);
		lotValues.getCellFormatter().setHorizontalAlignment(1, 1, HTML.ALIGN_RIGHT);
		lotValues.setStylePrimaryName("gwtThesis-tableBody");
		lotValues.setWidth("20ex");
		
		//set the title of the events section
		eventTitle.setText(0,0,"Upcoming Events");
		eventTitle.setBorderWidth(3);
		
		btnViewSpots.setStylePrimaryName("gwtThesis-borderedButton");
		btnBack.setStylePrimaryName("gwtThesis-borderedButton");
		btnEnterAdmin.setStylePrimaryName("gwtThesis-borderedButton");
		
		//set the text of the button to view spot locations
		btnViewSpots.setText("View Spot Locations");
		btnViewSpots.setEnabled(false);
		//set the text of the button to return to the overview
		btnBack.setText("Return to Overview");
		
		lstDays.setStylePrimaryName("gwtThesis-borderedDropDown");
		lstLots.setStylePrimaryName("gwtThesis-borderedDropDown");
		
		//add the days of the week to get charts for
		lstDays.addItem("Select A Day...");
		lstDays.addItem("Sunday");
		lstDays.addItem("Monday");
		lstDays.addItem("Tuesday");
		lstDays.addItem("Wednesday");
		lstDays.addItem("Thursday");
		lstDays.addItem("Friday");
		lstDays.addItem("Saturday");
		lstDays.setEnabled(false);
		
		//show the chart images (from google charts)
		imgWeekChart.setVisible(false);
		imgDayChart.setVisible(false);

		lstLots.addChangeListener(chgList);
		lstDays.addChangeListener(chgList);
	}
	
	/*==============================================================
	 * placeComponents
	 * Input: None
	 * Output: places all components on this panel
	 * Return: None
	 * arranges all the widgets on this panel using a series of grids
	 * and panels
	 *==============================================================*/
	public void placeComponents()
	{
		//make this panel take up the whole browser
		this.setWidth("100%");
		this.setHeight("100%");
		
		//A stack of widgets for the Left
		VerticalPanel lPan = new VerticalPanel();
		//A stack of widgets for the Right
		VerticalPanel rPan = new VerticalPanel();
		//A line of widgets for the top of the middle
		HorizontalPanel midTop = new HorizontalPanel();
		//A line of widgets for the bottom of the middle
		DockPanel midBot = new DockPanel();
		//A dockpanel for the middle
		FlowPanel midBut = new FlowPanel();
		//A stack of widgets for the middle
		VerticalPanel midPan = new VerticalPanel();
		//A panel to hold chart images
		HorizontalPanel chartPan = new HorizontalPanel();
		
		//A panel for the top
		DockPanel topPan = new DockPanel();
		//make top panel use the entire width of the browser
		topPan.setWidth("100%");
		
		//Set all panels to take up the entire width of the browser
		midTop.setWidth("100%");
		midBot.setWidth("100%");
		midPan.setWidth("100%");
		midBut.setWidth("100%");
		
		//to the middle-top-left, add lot title and details
		lPan.add(lotTitle);
		lPan.add(lotValues);
		
		//to the middle-top-right, add event title and upcoming
		//rPan.add(eventTitle);
		//rPan.add(eventValues);
		
		//add the lot details and events to the middle-top with
		//center alignment
		topPan.add(lPan,DockPanel.WEST);
		topPan.setCellHorizontalAlignment(lPan,DockPanel.ALIGN_LEFT);
		topPan.add(rPan,DockPanel.EAST);
		topPan.setCellHorizontalAlignment(rPan,DockPanel.ALIGN_RIGHT);
		
		chartPan.add(imgWeekChart);
		chartPan.add(new Label("              "));
		chartPan.add(imgDayChart);
		
		//to the middle-bottom, add the availability grid
		midBot.setHeight("100%");
		midBot.add(chartPan, DockPanel.NORTH);
		midBot.add(lstDays, DockPanel.SOUTH);
		midBot.setCellHorizontalAlignment(chartPan, DockPanel.ALIGN_CENTER);
		midBot.setCellHorizontalAlignment(lstDays, DockPanel.ALIGN_CENTER);
		midBot.setCellHeight(chartPan, "85%");
		midBot.setCellHeight(lstDays, "15%");
		midBot.setCellVerticalAlignment(chartPan, DockPanel.ALIGN_TOP);
		midBot.setCellVerticalAlignment(lstDays, DockPanel.ALIGN_BOTTOM);
		
		//add all of these (details, events, availability, buttons) to the middle
		//panel
		//midPan.add(midTop);
		midPan.add(midBot);
		midPan.setCellVerticalAlignment(midBot,VerticalPanel.ALIGN_BOTTOM);
		midPan.setCellVerticalAlignment(midTop,VerticalPanel.ALIGN_BOTTOM);
		midPan.setHeight("100%");
		
		//add the system info label to the top-left
		//topPan.add(lblSysInfo,DockPanel.WEST);
		//with left & top alignment
		//topPan.setCellHorizontalAlignment(lblSysInfo, DockPanel.ALIGN_LEFT);
		//topPan.setCellVerticalAlignment(lblSysInfo, DockPanel.ALIGN_TOP);
		//add the current time label to the top-right
		//topPan.add(lblCurTime,DockPanel.EAST);
		//with right & top alignment
		//topPan.setCellHorizontalAlignment(lblCurTime, DockPanel.ALIGN_RIGHT);
		//topPan.setCellVerticalAlignment(lblCurTime, DockPanel.ALIGN_TOP);
		//add the drop down box of the lots to the top-center
		VerticalPanel lstLotPnl = new VerticalPanel();
		lstLotPnl.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		lstLotPnl.add(lstLots);
		lstLotPnl.add(new Label("\n\n"));
		lstLotPnl.add(waitImg);
		waitImg.setVisible(false);
		topPan.add(lstLotPnl,DockPanel.CENTER);
		//with center-top alignment
		topPan.setCellHorizontalAlignment(lstLotPnl, DockPanel.ALIGN_CENTER);
		topPan.setCellVerticalAlignment(lstLotPnl, DockPanel.ALIGN_TOP);
		
		topPan.setCellWidth(lPan, "40%");
		topPan.setCellWidth(lstLotPnl, "20%");
		topPan.setCellWidth(rPan, "40%");

		//add the top panel, middle panel to this panel
		this.add(topPan,DockPanel.NORTH);
		//this.setCellHeight(topPan, "25%");
		this.add(midPan,DockPanel.CENTER);
		this.setCellVerticalAlignment(midPan,DockPanel.ALIGN_BOTTOM);
		this.setCellHorizontalAlignment(midPan, DockPanel.ALIGN_CENTER);
		
		DockPanel botPan = new DockPanel();
		botPan.add(btnEnterAdmin,DockPanel.CENTER);
		botPan.add(btnViewSpots,DockPanel.EAST);
		botPan.add(btnBack,DockPanel.WEST);
		botPan.setCellHorizontalAlignment(btnBack, DockPanel.ALIGN_LEFT);
		botPan.setCellHorizontalAlignment(btnEnterAdmin, DockPanel.ALIGN_CENTER);
		botPan.setCellHorizontalAlignment(btnViewSpots, DockPanel.ALIGN_RIGHT);
		botPan.setWidth("100%");
		//to the bottom-left of this panel, add a button to enter the admin
		//interface from this panel
		this.add(botPan,DockPanel.SOUTH);
		this.setCellVerticalAlignment(botPan, DockPanel.ALIGN_BOTTOM);
		
		this.setCellHeight(topPan, "25%");
		this.setCellHeight(midPan, "60%");
		this.setCellHeight(botPan, "15%");
	}
	
	/*==============================================================
	 * fillLotList
	 * Input: List of Lots (String[])
	 * Output: fills the list of lots on this panel
	 * Return: None
	 * This will take an array of strings and place them into
	 * the drop down box that displays the lots available in
	 * this site.
	 *==============================================================*/
	public void fillLotList(String[] lst)
	{
		//clear the drop-down of lots
		lstLots.clear();
		lstLots.insertItem(" ", 0);
		//go through the array of strings
		for(int i=0; i<lst.length; i++)
		{
			//and place them into the drop-down lots
			lstLots.insertItem(lst[i],i+1);
		}
	}
	
	/*==============================================================
	 * fillLotDetails
	 * Input: list of lot details (String[])
	 * Output: shows the spot details for selected lot on UI
	 * Return: None
	 * Takes a string[] and places two values into the lot details
	 * grid
	 *==============================================================*/
	public void fillLotDetails(String[] result)
	{
		lotValues.setText(0, 1, result[1]);
		lotValues.setText(1, 1, result[3]);
	}
	
	/*==============================================================
	 * pageRefresh
	 * Input: none
	 * Output: refreshes widgets on the UI
	 * Return: None
	 * refreshes the details of the selected lot, as long as there is
	 * a lot selected
	 *==============================================================*/
	public void pageRefresh()
	{
		//if there is not lot selected
		if(lstLots.getItemText(lstLots.getSelectedIndex()).compareTo(" ") != 0)
		{
			//change the current lot
			curLot = lstLots.getItemText(lstLots.getSelectedIndex());
			//change the title of the lot details
			lotTitle.setText(0,0,curLot);
			//get updated details for the current lot.
			srvLotDetails(curLot);
		}
	}
	
	/*==============================================================
	 * setVisible (improves the current setVisible)
	 * Input: visible (boolean)
	 * Output: shows or hides the panel
	 * Return: None
	 * improves the current setVisible method, by performing updating
	 * and refreshing of the UI.
	 *==============================================================*/
	public void setVisible(boolean sv)
	{
		//if the panel is being hidden
		if(sv == false);
		{
			//disable the button to view spots
			btnViewSpots.setEnabled(false);
			//disable the listbox to choose a day
			lstDays.setEnabled(false);
			//de-select any lots
			lstLots.setSelectedIndex(0);
			//clear the lot details
			lotTitle.setText(0,0,"Lot Details");
			lotValues.setText(0,1,"");
			lotValues.setText(1,1,"");
			
		}
		//set the panel visible or not
		super.setVisible(sv);
		
	}
	
	/*==============================================================
	 * srvLotList
	 * Input: None
	 * Output: will update the drop down with lots from database
	 * Return: None
	 * This server call will access the database to get all the lots
	 * available to the site, and place them into the drop-down box
	 * on this panel.
	 *==============================================================*/
	public void srvLotList()
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
				//fills the drop down box with the results from the server
				fillLotList((String[]) result);
			}
			public void onFailure(Throwable caught)
			{
				//System logging
				System.out.println("Error :: Failed to get lot list " +
						"(uiLotDetails.srvLotList) :: " + caught.toString());
			}
		};
		//call the asynchronous service to get all of the lots
		//that are associated with the current site
		lsA.getLots(cb);
	}
	
	/*==============================================================
	 * srvLotDetails - gets the details of the lot, updates screen
	 * Input: lot name (String)
	 * Output: fills in details of the lot
	 * Return: None
	 * This takes the name of a lot, and gets the details from the
	 * server, updating the screen with those details.
	 *==============================================================*/
	public void srvLotDetails(String lName)
	{
		//show a wait image, as the query & processing can take a few
		//seconds
		waitImg.setVisible(true);
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
				//fills in the details grid with details
				fillLotDetails((String[]) result);
				//remove wait image after data is read
				waitImg.setVisible(false);
			}
			public void onFailure(Throwable caught)
			{
				//System logging
				System.out.println("Error :: Failed to get lot details " +
						"(uiLotDetails.srvLotDetails) :: " + caught.toString());
				waitImg.setVisible(false);
			}
		};
		//call the asynchronous service to get all of the lots
		//that are associated with the current site
		lsA.getLotDetails(lName, cb);
	}
	
	/*==============================================================
	 * srvUpdateCharts - updates the UI with new charts
	 * Input: lot name (String) day name (String)
	 * Output: sets a new URL for charts
	 * Return: None
	 * calls the server to change the URL of the charts, to show 
	 * refreshed data from the server.
	 *==============================================================*/
	public void srvUpdateCharts(String lName, String dayName)
	{
		//if there is a day, and a lot selected
		if(dayName.compareTo("Select A Day...") != 0 && lName.compareTo(" ") != 0)
		{
			//show the spinny image to indicate waiting
			waitImg.setVisible(true);
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
					//remove the wait image
					waitImg.setVisible(false);
					//get the URL's from the server
					String[] urls = (String[]) result;
					//set the URL's of the two charts.
					imgWeekChart.setUrl(urls[0]);
					imgDayChart.setUrl(urls[1]);
				}
				public void onFailure(Throwable caught)
				{
					//still remove the wait image
					waitImg.setVisible(false);
					System.out.println("Error :: Failed to update charts " +
							"(uiLotDetails.srvUpdateCharts) :: " + caught.toString());
				}
			};
			//call the asynchronous service to get all of the lots
			//that are associated with the current site
			lsA.getChartsURL(lName,dayName,cb);
		}
	}
	
	//A change listener to listen for when the lot selected from
	//the lot list changes
	public class uiLDChgListener implements ChangeListener
	{
		/*==============================================================
		 * onChange - watches for a change in a widget
		 * Input: sender (Widget)
		 * Output: takes appropriate action upon change
		 * Return: None
		 * This watches for a change in one of the widgets and performs
		 * appropriate action for the change
		 *==============================================================*/
		public void onChange(Widget sender)
		{
			//if the change occured in the lot list
			if(sender.equals(lstLots))
			{
				//change the current lot
				curLot = lstLots.getItemText(lstLots.getSelectedIndex());
				//change the title of the lot details
				lotTitle.setText(0,0,curLot);
				//get updated details for the current lot.
				srvLotDetails(curLot);
				//if both a lot and a day are selected
				if(curLot.compareTo(" ") != 0 & lstDays.getItemText(lstDays.getSelectedIndex()).compareTo("Select A Day...") != 0)
				{
					//update the charts
					srvUpdateCharts(curLot,lstDays.getItemText(lstDays.getSelectedIndex()));
					//show the charts
					imgDayChart.setVisible(true);
					imgWeekChart.setVisible(true);
				}
				//if they have selected a lot
				if(curLot.compareTo(" ") != 0)
				{
					//enable the button to view spots
					btnViewSpots.setEnabled(true);
					//enable the list to choose days
					lstDays.setEnabled(true);
				}
				//otherwise
				else
				{
					//disable the view spots button
					btnViewSpots.setEnabled(false);
					//disable the list of days
					lstDays.setEnabled(false);
				}
			}
			//if a change has occured in the list of days
			if(sender.equals(lstDays))
			{
				//get the currently selected lot
				curLot = lstLots.getItemText(lstLots.getSelectedIndex());
				//if both a lot and a day are selected
				if(curLot.compareTo(" ") != 0 & lstDays.getItemText(lstDays.getSelectedIndex()).compareTo("Select A Day...") != 0)
				{
					//update the charts
					srvUpdateCharts(curLot,lstDays.getItemText(lstDays.getSelectedIndex()));
					//show the charts
					imgDayChart.setVisible(true);
					imgWeekChart.setVisible(true);
				}
			}
		}
	}
}
