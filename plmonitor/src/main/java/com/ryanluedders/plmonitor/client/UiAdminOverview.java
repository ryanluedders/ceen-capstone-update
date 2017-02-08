package com.ryanluedders.plmonitor.client;

import java.util.Arrays;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
//import com.luedders.server.curValues;
import com.google.gwt.user.client.ui.Widget;

/*=====================================================================
 * uiAdminOverview - Administrative Overview
 * extends - srvAccessor
 * This is the panel that is accessed to show the basic administrative
 * functions. This will have a list of lots contained in the site, as
 * well as the details associated with those lots. From this panel, the
 * Administrator will be able to add, delete, and edit lots.
 *=====================================================================*/
public class UiAdminOverview extends ServerAccessor
{
	public static boolean dgb = false;
	public static boolean info = false;
	
	//button to leave the admin area
	public Button btnLvAdmin = new Button();
	//button to edit the selected lot
	public Button btnEditLot = new Button();
	//button to delete the selected lot
	public Button btnDelLot = new Button();
	//button to add a new lot
	public Button btnNewLot = new Button();
	
	//a listbox containing a list of the lots in the current site
	public ListBox lstLots = new ListBox();
	
	//Grid to be used to hold description and values for lot details
	public Grid detailTitle = new Grid(1,1);
	public Grid detailValues = new Grid(4,2);
	public Grid spotTitle = new Grid(1,1);
	
	public Image waitImg = new Image("loadinfo.net.gif");
	
	//a listbox containing all the spots associated with the selected lot
	public ListBox lstSpotValues = new ListBox();
	
	//a dialogbox that can be used to get input from user on the name
	//of a new lot
	private DialogGetName dlgNewName = new DialogGetName(false,false,"Enter new name:");
	
	//a click listener to listen for mouse events coming from this panel
	//and the dialog box
	private uiAOClkListener clkLis = new uiAOClkListener();
	
	/*==============================================================
	 * Default Constructor
	 * Input: None
	 * Output: None
	 * Return: None
	 * does the normal srvAccessor construction, then intializes and
	 * places components
	 *==============================================================*/
	public UiAdminOverview()
	{
		//normal srvAccessor constructor
		super();
		//initialize components
		initComponents();
		//place components
		placeComponents();
	}
	
	/*==============================================================
	 * refreshComponents - Refresh components on this panel
	 * Input: None
	 * Output: None
	 * Return: None
	 * refreshes the active widgets on this panel
	 *==============================================================*/
	public void refreshComponents()
	{
		//clear the "spots" list until a lot is selected
		lstSpotValues.clear();
		//get an update list of lots from the server
		srvLotList();
		
		return;
	}
	
	public void setVisible(boolean vis)
	{
		refreshComponents();
		super.setVisible(vis);
	}
	
	/*==============================================================
	 * initComponents - Initialize Components
	 * Input: None
	 * Output: None
	 * Return: None
	 * this function takes the widgets that are part of this panel
	 * and places them where appropriate for the system
	 *==============================================================*/
	public void initComponents()
	{
		//start by having updated components
		refreshComponents();
		
		Image.prefetch("loadinfo.net.gif");
	
		//For the list of available lots, show 25
		lstLots.setVisibleItemCount(25);
		//Set the width of the field to a decent amount
		lstLots.setWidth("25ex");
		lstLots.setStylePrimaryName("gwtThesis-greyBackground");
		
		//For the list of spots associated with a lot, show 25
		lstSpotValues.setVisibleItemCount(25);
		//set the width of the field to a decent amount
		lstSpotValues.setWidth("25ex");
		lstSpotValues.setStylePrimaryName("gwtThesis-greyBackground");
		
		btnNewLot.setStylePrimaryName("gwtThesis-borderedButton");
		btnEditLot.setStylePrimaryName("gwtThesis-borderedButton");
		btnDelLot.setStylePrimaryName("gwtThesis-borderedButton");
		btnLvAdmin.setStylePrimaryName("gwtThesis-borderedButton");
		
		//Set the text of the new, edit, and delete buttons
		btnNewLot.setText("New Lot");
		btnEditLot.setText("Edit Lot");
		btnDelLot.setText("Delete Lot");
		//Set the width of the new, edit, and delete buttons
		btnNewLot.setWidth("25ex");
		btnEditLot.setWidth("25ex");
		btnDelLot.setWidth("25ex");
		//set the text of the leave admin area button
		btnLvAdmin.setText("Leave Admin Area");
	
		//set some fields of the grid to show basic lot details
		detailTitle.setText(0,0,"Details");
		//set the width of the border to surround details grid
		detailTitle.setStylePrimaryName("gwtThesis-tableTitle");
		detailTitle.setWidth("100%");
		//set description of details
		detailValues.setText(0,0,"Lot ID");
		detailValues.setText(1,0,"Number of Spots");
		detailValues.setText(2,0,"Number of Views");
		detailValues.setText(3,0,"Number of Open Spots");
		//set the width of the rest of the border grid
		detailValues.setStylePrimaryName("gwtThesis-tableBody");
		detailValues.getCellFormatter().setWidth(0, 0, "80%");
		detailValues.getCellFormatter().setWidth(0, 1, "20%");
		detailValues.getCellFormatter().setHorizontalAlignment(0, 1, HTML.ALIGN_RIGHT);
		detailValues.getCellFormatter().setHorizontalAlignment(1, 1, HTML.ALIGN_RIGHT);
		detailValues.getCellFormatter().setHorizontalAlignment(2, 1, HTML.ALIGN_RIGHT);
		detailValues.getCellFormatter().setHorizontalAlignment(3, 1, HTML.ALIGN_RIGHT);
		detailTitle.setWidth("100%");
		
		waitImg.setVisible(false);
	
		//set the title of the box with spot details
		spotTitle.setText(0,0,"Spot Details");
	
		//listen for clicks from the new and delete lot buttons
		btnNewLot.addClickListener(clkLis);
		btnDelLot.addClickListener(clkLis);
		//listen for clicks from the ok and cancel buttons on the
		//dialog box used for getting the new name
		dlgNewName.popLotNameCan.addClickListener(clkLis);
		dlgNewName.popLotNameOK.addClickListener(clkLis);
		//add a click listener to the list of lots to wait for
		//the user to click on it in order to update spots
		lstLots.addClickListener(clkLis);
	}

	public void placeComponents()
	{
		//set this panel to take up the whole browser
		this.setWidth("100%");
		this.setHeight("100%");
		
		//a bottom, middle, and top panel, for organizing widgets
		DockPanel topPan = new DockPanel();
		DockPanel midPan = new DockPanel();
		DockPanel botPan = new DockPanel();
		
		//set the bottom, middle, and top panel to take up whole
		//width of browser
		topPan.setWidth("100%");
		midPan.setWidth("100%");
		botPan.setWidth("100%");
		
		//To the top panel, add the system info label on the Left
		//topPan.add(lblSysInfo,DockPanel.WEST);
		//topPan.setCellHorizontalAlignment(lblSysInfo,DockPanel.ALIGN_LEFT);
		//To the top panel, add the current time label on the Right
		//topPan.add(lblCurTime,DockPanel.EAST);
		//topPan.setCellHorizontalAlignment(lblCurTime,DockPanel.ALIGN_RIGHT);
		//To the bottom panel, add the button to leave the admin area
		//on the left
		topPan.add(new Label(" "),DockPanel.CENTER);
		botPan.add(btnLvAdmin,DockPanel.WEST);
		botPan.setCellHorizontalAlignment(btnLvAdmin,DockPanel.ALIGN_LEFT);
		
		//Panels to add a stack of widgets on the Left, Middle, and Right
		VerticalPanel midL = new VerticalPanel();
		VerticalPanel midM = new VerticalPanel();
		VerticalPanel midR = new VerticalPanel();
		
		//to the Middle-left panel, add the list of lots, and the new
		//edit and delete lot buttons
		midL.add(lstLots);
		midL.add(btnNewLot);
		midL.add(btnEditLot);
		midL.add(btnDelLot);
		
		//to the Middle-middle panel, add the detail title, and the
		//lot details themselves
		midM.add(detailTitle);
		midM.add(detailValues);
		midM.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		midM.add(new Label("\n\n"));
		midM.add(waitImg);
		
		//to the Middle-right, add a title for the spot list, and the
		//spot list itself
		midR.add(spotTitle);
		midR.add(lstSpotValues);
		
		//Add the Three middle panels to the Left, Middle, and Right
		//Sections of this dockPanel
		midPan.add(midL,DockPanel.WEST);
		midPan.add(midM,DockPanel.CENTER);
		midPan.add(midR,DockPanel.EAST);
		
		//Align the middle-left to the left
		midPan.setCellHorizontalAlignment(midL,DockPanel.ALIGN_LEFT);
		//Align the middle-middle to the center
		midPan.setCellHorizontalAlignment(midM,DockPanel.ALIGN_CENTER);
		//Align the middle-right to the right
		midPan.setCellHorizontalAlignment(midR,DockPanel.ALIGN_RIGHT);
		
		//to this dockpanel, add the top, middle, and bottom panels
		this.add(topPan,DockPanel.NORTH);
		this.add(midPan,DockPanel.CENTER);
		this.add(botPan,DockPanel.SOUTH);
		
	}
	
	/*==============================================================
	 * fillLotList - will fill the list of lots on this panel
	 * Input: Names of Lots (String[])
	 * Output: Populate lstLots
	 * Return: None
	 * This function takes in an array of strings that are most
	 * likely from a database that represent the names of the lots
	 * that are available in this site
	 *==============================================================*/
	public void fillLotList(String[] lst)
	{
		//clear what is currently in the list
		lstLots.clear();
		//get all values out of the array
		for(int i=0; i<lst.length; i++)
		{
			//and insert them into the list of lots
			lstLots.insertItem(lst[i],i);
		}
	}
	
	/*==============================================================
	 * fillSpotList - will fill the list of spots on this panel
	 * Input: Names of Spots (String[])
	 * Output: Populate lstSpotValues
	 * Return: None
	 * This function will take an array of strings, most likely from
	 * a database, that represent the spots that are available, most
	 * likely for the currently selected lot, and place them into the
	 * list of spots.
	 *==============================================================*/
	public void fillSpotList(String[] lst)
	{
		//clear what is currently in the list
		lstSpotValues.clear();
		Arrays.sort(lst);
		//get all values from the array

		for(int i=0; i<lst.length; i++)
		{
			//and place them into the list of spots
			lstSpotValues.insertItem(lst[i],i);
		}
		if(lstSpotValues.getItemText(0).compareTo("null") == 0)
		{
			lstSpotValues.clear();
		}
	}
	
	/*==============================================================
	 * pageRefresh() - refreshes items on this panel
	 * Input: none
	 * Output: refreshes with data from server
	 * Return: None
	 * Calls server methods to update the UI with fresh information
	 * from the database
	 *==============================================================*/
	public void pageRefresh()
	{
		//if there is not a lot selected, don't refresh
		if(lstLots.getSelectedIndex() != -1)
		{
			//get the current selection
			String curSelection = lstLots.getItemText(lstLots.getSelectedIndex());
			//get the spots associated with the selection
			srvGetSpots(curSelection);
			//set the details & title of details grid.
			detailTitle.setText(0,0,curSelection + " Details");
			srvLotDetails(curSelection);
		}
	}
	
	/*==============================================================
	 * displayLotDetails - will fill the lot details display
	 * Input: Details String[]
	 * Output: Populate the details display
	 * Return: None
	 * This function will take an array of strings that represent
	 * details of the lot, and will display them on this panel 
	 *==============================================================*/
	public void displayLotDetails(String[] dets)
	{
		detailValues.setText(0,1,dets[0]);
		detailValues.setText(1,1,dets[1]);
		detailValues.setText(2,1,dets[2]);
		detailValues.setText(3,1,dets[3]);
	}
	
	/*==============================================================
	 * srvLotList - will get the lots from the database
	 * Input: None
	 * Output: Populate the list of lots
	 * Return: None
	 * This is a server call that will get the all of the lots in
	 * the current site, and will display them by filling the list
	 * of lots on the panel
	 *==============================================================*/
	public void srvLotList()
	{
		//new asynchronous lot service
		LotServiceAsync lsA = (LotServiceAsync) GWT.create(LotService.class);
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
				//fill the list-box with the spots from
				//the database for the selected view
				if(info){System.out.println("Updated uiAdminOverview.lstLots");}
				fillLotList((String[]) result);
			}
			public void onFailure(Throwable caught)
			{
				//System Logging
				System.out.println("Error :: Failed to get lots " +
						"(uiAdminOverview.srvLotList) :: " + caught.toString());
			}
		};
		//call the asynchronous service to get the lots,
		//providing the callback to handle the results
		lsA.getLots(cb);
	}

	
	/*==============================================================
	 * srvAddLot - will add a lot to the database
	 * Input: the name of a lot to insert
	 * Output: lot to database
	 * Return: None
	 * This server call will take a lot name, and add it to the
	 * database, then, update the list of spots
	 *==============================================================*/
	public void srvAddLot(String lotNameToInsert)
	{
		//new asynchronous lot service
		LotServiceAsync lsA = (LotServiceAsync) GWT.create(LotService.class);
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
				//System Logging
				if(info){System.out.println("Added lot to db");}
				//update the lot list on this panel
				srvLotList();
			}
			public void onFailure(Throwable caught)
			{
				//System logging
				System.out.println("Error :: Failed to add lot " +
						"(uiAdminOverview.srvAddLot) :: " + caught.toString());
			}
		};
		//call the asynchronous service to add a lot to the
		//database, given the name of the lot to insert
		lsA.addLot(lotNameToInsert, cb);
	}
	
	/*==============================================================
	 * srvDeleteLot - will delete a lot from the database
	 * Input: the name of the lot to delete (String)
	 * Output: delete lot from db
	 * Return: None
	 * This server call will take a name from the interface and
	 * delete the lot from the database.
	 *==============================================================*/
	public void srvDeleteLot(String lotNameToDelete)
	{
		//new asynchronous lot service
		LotServiceAsync lsA = (LotServiceAsync) GWT.create(LotService.class);
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
				//System logging
				if(info){System.out.println("Deleted lot from db");}
				//update the lot to reflect the deleted lot
				srvLotList();
			}
			public void onFailure(Throwable caught)
			{
				//System logging
				System.out.println("Error :: Failed to delete lot " +
						"(uiAdminOverview.srvDeleteLot) :: " + caught.toString());
			}
		};
		//call the asynchronous service to delete the lot to the
		//database, given the name of the lot to delete
		lsA.deleteLot(lotNameToDelete, cb);
	}
	
	/*==============================================================
	 * srvGetSpots - will get the spots for for a lot
	 * Input: the name of the lot to get spots in (String)
	 * Output: populate the list of spots
	 * Return: None
	 * This server call takes the name of a lots and calls the
	 * server to get all of the spots associated with that lot,
	 * filling in the list of spots.
	 *==============================================================*/
	public void srvGetSpots(String lotNameForSpots)
	{
		//new asynchronous lot service
		LotServiceAsync lsA = (LotServiceAsync) GWT.create(LotService.class);
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
				//Fill the spot list with the results
				//from the Database
				fillSpotList((String[])result);
			}
			public void onFailure(Throwable caught)
			{
				//System Logging
				System.out.println("Error :: Failed to get spots " +
						"(uiAdminOverview.srvGetSpots) :: " + caught.toString());
			}
		};
		//call the asynchronous service to get the list of spots
		//that are associated with the given lot.
		lsA.getSpotsForLot(lotNameForSpots, cb);
	}
	
	/*==============================================================
	 * srvLotDetails - will call server to get the details
	 * of the selected lot
	 * Input: the name of the lot to get details for (String)
	 * Output: will output details to screen
	 * Return: None
	 * This server call takes the name of the lot and calls the
	 * server to get the details regarding that lot, then displays
	 * them on the current UI.
	 *==============================================================*/
	public void srvLotDetails(String lotNameForDetails)
	{
		waitImg.setVisible(true);
		//new asynchronous lot service
		LotServiceAsync lsA = (LotServiceAsync) GWT.create(LotService.class);
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
				//display the lot details
				displayLotDetails((String[])result);
				waitImg.setVisible(false);
			}
			public void onFailure(Throwable caught)
			{
				//System Logging
				System.out.println("Error :: Failed to get lot details " +
						"(uiAdminOverview.srvLotDetails) :: " + caught.toString());
				waitImg.setVisible(false);
			}
		};
		//call the asynchronous service to get the list of spots
		//that are associated with the given lot.
		lsA.getLotDetails(lotNameForDetails, cb);
	}
	
	/*==============================================================
	 * uiAOClkListener - click Listener for this panel
	 * implements ClickListener
	 * Input: Sending Widget
	 * Output: none
	 * Return: None
	 * This function watches all the various buttons and lists on
	 * this panel and will update them, or direct the system in
	 * an appropriate manner
	 *==============================================================*/
	private class uiAOClkListener implements ClickListener
	{
		//onClick - waits for a click
		public void onClick(Widget sender) 
		{
			//if the click was on the button to create a new lot
			if(sender.equals(btnNewLot))
			{
				//show the dialog to get the name for the new lot
				dlgNewName.show();
			}
			//if the click was on the button to delete the
			//selected lot
			if(sender.equals(btnDelLot))
			{
				//clear the list of values, so it can be updated
				lstSpotValues.clear();
				//call the server to delete the presently selected lot
				srvDeleteLot(lstLots.getItemText(lstLots.getSelectedIndex()));
			}
			//if the click was on the dialog box - cancel button
			if(sender.equals(dlgNewName.popLotNameCan))
			{
				//hide the newname dialog box
				dlgNewName.hide();
				//call the server to update the list of lots
				srvLotList();
			}
			//if the click was on the dialog box - ok button
			if(sender.equals(dlgNewName.popLotNameOK))
			{
				//call the server to add the new lot, with the
				//name input in the dialog box
				srvAddLot(dlgNewName.txtGetLotName.getText());
				//hide the dialog box
				dlgNewName.hide();
			}
			//if the click was in the list of lots
			if(sender.equals(lstLots))
			{
				//clear the list of spots to prepare for
				//updating
				lstSpotValues.clear();
				//call the server to get the spots associated with
				//the presently selected lot
				if(lstLots.getSelectedIndex() != -1)
				{
					String curSelection = lstLots.getItemText(lstLots.getSelectedIndex());
					srvGetSpots(curSelection);
					detailTitle.setText(0,0,curSelection + " Details");
					srvLotDetails(curSelection);
				}
			}
		}
	}
}
