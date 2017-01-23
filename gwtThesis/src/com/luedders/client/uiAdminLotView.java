package com.luedders.client;

import net.sphene.gwt.widgets.slider.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/*=====================================================================
 * uiAdminLotView - Administrative Lot View Panel
 * extends - srvAccessor
 * This is the panel that shows the views and spots associated with any
 * given lot. This screen is accessed from the previous screen, which
 * passes the information about which lot the administrator is currently
 * working on. From here, the user can add views, and then once a view
 * is selected, add, or remove spots from that view. This is also where
 * the X/Y coordinates of the spot will be entered into the system
 *=====================================================================*/
public class uiAdminLotView extends srvAccessor
{	
	public static boolean dbg = false;
	public static boolean info = false;
	
	//The name of the current lot
	public String curLotName;
	
	//A button to leave the admin section of the system
	public Button btnLvAdmin = new Button();
	
	//A listbox (dropdown) to display the views
	public ListBox lstViews = new ListBox();
	//A button to add a view to the system
	public Button btnAddView = new Button();
	
	//A listbox to display the spots associated with a view
	public ListBox lstSpots = new ListBox();
	//A button to add a spot to the current view
	public Button btnAddSpot = new Button();
	//A button to delete a spot from the current view
	public Button btnDelSpot = new Button();
	//A button to edit a spot (add row/col info)
	public Button btnEditSpot = new Button();
	//A button to go back to the admin overview screen
	public Button btnBack = new Button();
	
	//The image associated with the current view
	public Image lotImg = new Image();
	
	//A Label to display detailed spot info
	public Label lblSpotInfo = new Label();
	
	//Change listener, to watch when the dropdown changes
	public chgListen cList = new chgListen();
	//Click listener, to watch for mouse clicks
	public clkListen clkList = new clkListen();
	
	//A dialog box used to get the name of a new view
	public dlgGetName dlgGName = new dlgGetName(false,false,"Enter new name:");
	//A dialog box used to get the name of a new spot
	public dlgGetName dlgGNameSpot = new dlgGetName(false,false,"Enter new name:");
	//A dialog box used to get the filename for the img
	public dlgGetName dlgImg = new dlgGetName(false,false,"Enter image name:");
	
	//a spot detailed info box
	public pnlEditSpot editSpot = new pnlEditSpot("");
	
	//A mouse listener to see where user clicks on image
	public msListener msList = new msListener();
	
	//temporary values for adding a view
	public String tmpViewName;
	public String tmpLotName;
	public String tmpFileName;
	
	//temporary values for adding a spot
	public String tmpSptSpotName;
	public String tmpSptViewName;
	public int tmpTopX;
	public int tmpTopY;
	public int tmpBotX;
	public int tmpBotY;
	
	//for getting X/Y off picture
	public boolean getSpot = false;
	
	//notificationBox to instruct users (such as "enter top right corner" etc...)
	notificationBox nB = new notificationBox(true,false,"");
	//popupPanel to display spot info
	PopupPanel rect = new PopupPanel(true,false);
	
	//A horizontal panel to hold the slider system for entering processing threshold
	HorizontalPanel sliderHolder = new HorizontalPanel();
	//a label to inform the user
	Label lblThresh = new Label("Threshold:  ");
	//a slider for admin to change threshold
	HorizontalSlider hSlide = new HorizontalSlider();
	//a textbox to display current threshold
	TextBox txtThreshValue = new TextBox();
	

	/*==============================================================
	 * Default Constructor
	 * Input: None
	 * Output: None
	 * Return: None
	 * does the normal DockPanel construction, then intializes and
	 * places components
	 *==============================================================*/
	public uiAdminLotView()
	{
		//calls standard DockPanel constructor
		super();
		//initializes the components
		initComponents();
		//places the components on the current panel
		placeComponents();
	}
	
	/*==============================================================
	 * setCurLotName - set the name of the lot
	 * Input: lot name (String)
	 * Output: None
	 * Return: None
	 * sets the lot name that is being edited
	 *==============================================================*/
	public void setCurLotName(String lname)
	{
		//set lot name
		curLotName = lname;
	}
	
	/*==============================================================
	 * initComponents - initialize all widget components
	 * Input: None
	 * Output: None
	 * Return: None
	 * initializes all of the various widgets and information that
	 * are used on this panel
	 *==============================================================*/
	public void initComponents()
	{
		//Add click listeners to the buttons on the two dialog
		//boxes that are used to gather input
		dlgGName.popLotNameCan.addClickListener(clkList);
		dlgGName.popLotNameOK.addClickListener(clkList);
		dlgGNameSpot.popLotNameOK.addClickListener(clkList);
		dlgGNameSpot.popLotNameCan.addClickListener(clkList);
		dlgImg.popLotNameCan.addClickListener(clkList);
		dlgImg.popLotNameOK.addClickListener(clkList);
		
		btnLvAdmin.setStylePrimaryName("gwtThesis-borderedButton");
		btnBack.setStylePrimaryName("gwtThesis-borderedButton");
		btnAddView.setStylePrimaryName("gwtThesis-borderedButton");
		btnAddSpot.setStylePrimaryName("gwtThesis-borderedButton");
		btnDelSpot.setStylePrimaryName("gwtThesis-borderedButton");
		btnEditSpot.setStylePrimaryName("gwtThesis-borderedButton");
		lotImg.setStylePrimaryName("gwtThesis-borderedImage");
		
		//Set the text of the leave admin button
		btnLvAdmin.setText("Leave Admin Area");
		btnLvAdmin.setAccessKey('l');
		//set the text of the button to go back to overview
		btnBack.setText("Go back to site overview");
		btnBack.setAccessKey('b');
		
		//set the text of the button to add a view
		btnAddView.setText("Add A View");
		//Add a click listener to the dbutton to add a view.
		btnAddView.addClickListener(clkList);
		
		//Add a "dummy" variable telling the user to select a view
		lstViews.addItem("Select a View...");
		//Add a change listener and Click Listener to the list of views
		//so other components can be update on a change or click of the
		//drop-down view box
		lstViews.addChangeListener(cList);
		lstViews.addClickListener(clkList);
		lstViews.setStylePrimaryName("gwtThesis-borderedDropDown");
		
		//Set the list of spots in a view to show 25 values
		lstSpots.setVisibleItemCount(25);
		//Set the width of the spot list to a decent amount
		lstSpots.setWidth("25ex");
		lstSpots.addClickListener(clkList);
		lstSpots.addChangeListener(cList);
		lstSpots.setStylePrimaryName("gwtThesis-greyBackground");
		//Set the text of the Add & Delete Spot Buttons
		btnAddSpot.setText("Add Spot");
		btnDelSpot.setText("Delete Spot");
		btnEditSpot.setText("Edit Spot");
		//Add click listeners to the add & delete spot buttons, so
		//those actions can be performed
		btnAddSpot.addClickListener(clkList);
		btnDelSpot.addClickListener(clkList);
		btnEditSpot.addClickListener(clkList);
		//set the width of the button to the same as the list
		btnAddSpot.setWidth("25ex");
		btnDelSpot.setWidth("25ex");
		btnEditSpot.setWidth("25ex");
		
		//Set the URL of the image to be displayed
		//some examples of images that can be used for testing are
		//pki-lot-001.jpg
		//pki-lot-002.jpg
		lotImg.addMouseListener(msList);
		lotImg.setVisible(false);
		
		//set the min and max of the slider 1-1500 currently
		hSlide.setMaxValue(1);
		hSlide.setMinValue(0);
		//make the slider visible
		hSlide.setVisible(true);
		//make it increment by 1
		hSlide.setInterval(0.01);
		//set it be 20 spaces wide
		hSlide.setWidth("20ex");
		//watch for a change on the slider
		hSlide.addChangeListener(cList);
		
		//a textbox to display the value of the current threshold
		txtThreshValue.setReadOnly(true);
		txtThreshValue.setWidth("6ex");
		txtThreshValue.setStylePrimaryName("gwtThesis-greyBackground");
		
		lblSpotInfo.setWordWrap(true);
		lblSpotInfo.setWidth("15ex");
		
		rect.setStylePrimaryName("gwtThesis-borderedPanel");
	}
	
	/*==============================================================
	 * refreshComponents - refresh widget components
	 * Input: None
	 * Output: None
	 * Return: None
	 * calls the server to update and clear widgets appropriately
	 *==============================================================*/
	public void refreshComponents()
	{
		//clear the list of spots, blank until a view is selected
		lstSpots.clear();
		//update the drop-down box with the current views from Database
		srvGetViewListing(curLotName);
		pageRefresh();
		lblSpotInfo.setText("");
		//System logging
		if(dbg){System.out.println("Viewing Lot Details for - " + curLotName);}
		return;
	}
	
	public void setVisible(boolean vis)
	{
		refreshComponents();
		super.setVisible(vis);
	}
	
	/*==============================================================
	 * placeComponents - places Widgets on the panel
	 * Input: None
	 * Output: None
	 * Return: None
	 * places the widgets in their appropriate spots
	 *==============================================================*/
	public void placeComponents()
	{
		//Support the panel into a top, middle, and bottom, dock panel
		DockPanel topPan = new DockPanel();
		DockPanel midPan = new DockPanel();
		DockPanel botPan = new DockPanel();
		
		//A horizontal panel and vertical panel, both for the middle
		//panel for greater layout flexibility
		HorizontalPanel topMidPan = new HorizontalPanel();
		VerticalPanel midLPan = new VerticalPanel();
		
		//Have the panels take up the whole screen
		this.setWidth("100%");
		this.setHeight("100%");
		
		//set top, mid, and bottom to take up whole width
		topPan.setWidth("100%");
		midPan.setWidth("100%");
		botPan.setWidth("100%");
		
		//To the top-middle, Add the drop down box of views
		topMidPan.add(lstViews);
		//To the top-middle, add the "add view" button
		topMidPan.add(btnAddView);
		
		//to the middle-left, add the list of spots
		midLPan.add(lstSpots);
		//to the middle-left, add the "add spot" button
		midLPan.add(btnAddSpot);
		//to the middle left, add the "edit spot" button
		midLPan.add(btnEditSpot);
		//to the middle left, add the "delete spot" button
		midLPan.add(btnDelSpot);
		
		
		//To the top panel, add the system info on the left.
		//topPan.add(lblSysInfo,DockPanel.WEST);
		//Left alignment for system info
		//topPan.setCellHorizontalAlignment(lblSysInfo, DockPanel.ALIGN_LEFT);
		//top alignment for all top items
		topPan.setVerticalAlignment(DockPanel.ALIGN_TOP);
		//Add drop down, and add view buttons to top middle
		topPan.add(topMidPan,DockPanel.CENTER);
		//Align the center using middle
		topPan.setCellHorizontalAlignment(topMidPan, DockPanel.ALIGN_CENTER);
		//Add the time label to the left
		//topPan.add(lblCurTime,DockPanel.EAST);
		//right alignment for the time
		//topPan.setCellHorizontalAlignment(lblCurTime, DockPanel.ALIGN_RIGHT);
		
		//to the middle-left, add the list of spots and buttons
		midPan.add(midLPan,DockPanel.WEST);
		//to the middle-center, add the image
		midPan.add(lotImg,DockPanel.CENTER);
		//to the middle-right, add a place to show spot details
		midPan.add(lblSpotInfo,DockPanel.EAST);
		
		//set the layout of the middle of the page
		midPan.setCellWidth(midLPan, "15%");
		midPan.setCellWidth(lotImg, "70%");
		midPan.setCellHorizontalAlignment(lotImg, DockPanel.ALIGN_CENTER);
		midPan.setCellWidth(lblSpotInfo, "15%");
		
		//to the bottom-left, add the button to leave the admin area
		botPan.add(btnLvAdmin,DockPanel.WEST);
		//left alignment for the leave admin button
		botPan.setCellHorizontalAlignment(btnLvAdmin,DockPanel.ALIGN_LEFT);
		//to the bottom-right, add the back button
		botPan.add(btnBack,DockPanel.EAST);
		//right alignment for the back button
		botPan.setCellHorizontalAlignment(btnBack,DockPanel.ALIGN_RIGHT);
		
		//a panel to hold all the slider related components
		sliderHolder.add(lblThresh);
		sliderHolder.add(hSlide);
		sliderHolder.add(new Label(" "));
		sliderHolder.add(txtThreshValue);
		
		//to the bottom middle, add the slider
		botPan.add(sliderHolder,DockPanel.CENTER);
		botPan.setCellHorizontalAlignment(sliderHolder, DockPanel.ALIGN_CENTER);
		
		//add the top panel to the top
		this.add(topPan,DockPanel.NORTH);
		//add the middle panel to the center
		this.add(midPan,DockPanel.CENTER);
		//add the bottom panel to bottom
		this.add(botPan,DockPanel.SOUTH);
	}
	
	/*==============================================================
	 * fillViewList - fill the drop down list with views from DB
	 * Input: View List (String Array)
	 * Output: Views into drop-down box
	 * Return: None
	 * This will take a string array of views (probably from database)
	 * and will put them into the view drop down box
	 *==============================================================*/
	public void fillViewList(String[] lst)
	{
		//clear the drop down box of values to start with
		lstViews.clear();
		//"dummy variable"
		lstViews.addItem("Select a View...");
		//add all values in the array to the drop-down
		for(int i=0; i<lst.length; i++)
		{
			lstViews.insertItem(lst[i],(i+1));
		}
	}
	
	/*==============================================================
	 * fillSpotList - fill the list with spots from DB for selected view
	 * Input: Spot List (String Array)
	 * Output: Spots into list
	 * Return: None
	 * This will take an array of strings returned from the database
	 * for the selected view and place them into the listbox
	 *==============================================================*/
	public void fillSpotList(String[] lst)
	{
		//clear the drop down box of values to start with
		lstSpots.clear();
		//add all spot values in the array to the list
		for(int i=0; i<lst.length; i++)
		{
			lstSpots.insertItem(lst[i],i);
		}
	}
	
	public void pageRefresh()
	{
		if(lstViews.getItemText(lstViews.getSelectedIndex()).compareTo("Select a View...") != 0)
		{
			if(info){System.out.println("Img Updated!");}
			srvLoadImage(lstViews.getItemText(lstViews.getSelectedIndex()));
		}
		else
		{
			lotImg.setVisible(false);
		}
	}
	
	/*==============================================================
	 * srvGetViewListing - get Views from the server
	 * Input: Lot Name (String)
	 * Output: Views into dropdown box
	 * Return: None
	 * This function will take a lot, query the database, and return
	 * all views associated with the lot.
	 *==============================================================*/
	public void srvGetViewListing(String lotNameForViews)
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
				//fill the view drop-down with the views from
				//the database
				fillViewList((String[])result);
				//system logging
				if(dbg){System.out.println("uiAdminLotView - getViewListing");}
			}
			public void onFailure(Throwable caught)
			{
				//system logging, 
				System.out.println("Error :: Could not get view listing " +
						"(uiAdminLotView.srvGetViewListing) :: " + caught.toString());
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results
		lsA.getViews(lotNameForViews, cb);
	}
	
	/*==============================================================
	 * srvGetSpotListing - get Spots from the server
	 * Input: View Name (String)
	 * Output: Spots into list box
	 * Return: None
	 * This function will take a view, query the database, and return
	 * all spots that are associated with the specified view.
	 *==============================================================*/
	public void srvGetSpotListing(String viewNameForSpots)
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
				//fill the list-box with the spots from
				//the database for the selected view
				fillSpotList((String[])result);
				//System Logging
				if(dbg){System.out.println("uiAdminLotView-getSpotListing");}
			}
			public void onFailure(Throwable caught)
			{
				//System Logging
				System.out.println("Error :: Could not get spot listing " +
						"(uiAdminLotView.srvGetSpotListing) :: " + caught.toString());
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results
		lsA.getSpots(viewNameForSpots, cb);
	}
	
	/*==============================================================
	 * srvAddView - Add a view to the database
	 * Input: View Name (String), Lot Name (String)
	 * Output: Updated view listing, View to Database
	 * Return: None
	 * This function will take the name of a view, and the name of
	 * a lot to associate the view with, and it will call the server
	 * to store it in the database
	 *==============================================================*/
	public void srvAddView(String vName, String lName, String imgFile)
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
				//get the update view listings for
				//the current lot from the server
				srvGetViewListing(curLotName);
			}
			public void onFailure(Throwable caught)
			{
				//System Logging
				System.out.println("Error :: Failed to Add View " +
						"(uiAdminLotView.srvAddView) :: " + caught.toString());
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results
		lsA.addView(vName, lName, imgFile, cb);
	}
	
	/*==============================================================
	 * srvAddSpot - Add a Spot to the database
	 * Input: Spot Name (String), View Name (String)
	 * Output: Spot to database, updated spot listing
	 * Return: None
	 * This function will take the name of a spot, and the name
	 * of a view to associate it with. It will add it to the 
	 * database, and then update the spot listing on this panel
	 *==============================================================*/
	public void srvAddSpot(String sName, String vName, int topX, int topY, int botX, int botY)
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
				//After updating the database, get the new spots for the
				//selected view
				srvGetSpotListing(lstViews.getItemText(lstViews.getSelectedIndex()));
			}
			public void onFailure(Throwable caught)
			{
				//System logging
				System.out.println("Error :: Failed to Add Spot " +
						"(uiAdminLotView.srvAddSpot) :: " + caught.toString());
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results - add the spot
		lsA.addSpot(sName, vName, topX, topY, botX, botY, cb);
	}
	
	/*==============================================================
	 * srvLoadImage - loads the appropriate image for the selected
	 * view
	 * Input: View Name (String)
	 * Output: Updated image
	 * Return: None
	 * This function will update the image shown on the page so
	 * it reflects the image that corresponds with the selected view
	 *==============================================================*/
	public void srvLoadImage(String vName)
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
				//set the image to the image that goes
				//with the view.
				lotImg.setUrl((String)result + "?variable=" + System.currentTimeMillis());
				//lotImg.setUrl("C:/xampp/tomcat/webapps/thesisApp/" + (String)result + "?variable=" + System.currentTimeMillis());
				lotImg.setVisible(true);
			}
			public void onFailure(Throwable caught)
			{
				System.out.println("Error :: Failed to Load Image " +
						"(uiAdminLotView.srvLoadImage) :: " + caught.toString());
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results - add the spot
		lsA.getViewImage(vName,cb);
	}
	
	/*==============================================================
	 * srvDelSpot - Delete a spot from the database
	 * Input: Spot Name (String)
	 * Output: Spot from database, updated spot listing
	 * Return: None
	 * This function will take the name of a spot, and delete the
	 * spot from the database, then it will update the spot listing
	 *==============================================================*/
	public void srvDelSpot(String sName)
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
				//After updating the database, get the new spots for the
				//selected view
				srvGetSpotListing(lstViews.getItemText(lstViews.getSelectedIndex()));
			}
			public void onFailure(Throwable caught)
			{
				//System logging
				System.out.println("Error :: Failed to Delete Spot " +
						"(uiAdminLotView.srvDelSpot) :: " + caught.toString());
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results - delete the spot, by name
		lsA.delSpot(sName, cb);
	}
	
	/*==============================================================
	 * srvDrawSpotXY - draw the popup on the image with spot info
	 * from the server
	 * Input: Spot Name (String)
	 * Output: a box over the spot on the corresponding image
	 * Return: None
	 * this method takes a spot name, and will draw an info box
	 * on top of the image with the spot's info.
	 *==============================================================*/
	public void srvDrawSpotXY(final String spotName)
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
				//get the bounds of the spot
				int[] spotLocs = (int[]) result;
				//draw the box with the given bounds
				imgDrawSpot(spotLocs[4],spotName,spotLocs[0],spotLocs[1],spotLocs[2],spotLocs[3]);
			}
			public void onFailure(Throwable caught)
			{
				//System logging
				System.out.println("Error :: Failed to Draw Spot " +
						"(uiAdminLotView.srvDrawSpotXY) :: " + caught.toString());
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results - delete the spot, by name
		lsA.getSpotXY(spotName, cb);
	}
	
	/*==============================================================
	 * srvUpdateThresh - updates the server with modified threshold
	 * Input: view name (String), new threshold (int)
	 * Output: updates the server with new threshold
	 * Return: None
	 * this method takes a view name and an integer and updates the
	 * processing threshold on the server for the view to the integer.
	 *==============================================================*/
	public void srvUpdateThresh(String vName, double thresh)
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
				//if successfully updated
				if(info){System.out.println("updated view threshold");}
			}
			public void onFailure(Throwable caught)
			{
				//System logging
				System.out.println("Error :: Failed to Update View Threshold " +
						"(uiAdminLotView.srvUpdateThresh) :: " + caught.toString());
			}
		};
		//call servermethods to update database
		lsA.updateViewThreshold(vName, thresh, cb);
	}
	
	/*==============================================================
	 * srvGetThresh - gets the current threshold from the database
	 * Input: view name (string)
	 * Output: updates the text box & slider
	 * Return: None
	 * this method takes a view name, and updates the text box and
	 * slider with the current threshold value for the specified view
	 *==============================================================*/
	public void srvGetThresh(String vName)
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
				//set the threshold value in the textbox
				txtThreshValue.setText(((Double)result).toString());
				//set the slider position
				hSlide.setValue(((Double)result).doubleValue());
			}
			public void onFailure(Throwable caught)
			{
				//System logging
				System.out.println("Error :: Failed to Get View Threshold " +
						"(uiAdminLotView.srvGetThresh) :: " + caught.toString());
			}
		};
		//call server to get current threshold
		lsA.getViewThreshold(vName, cb);
	}
	
	public void srvGetSpotAnalysis(String sname)
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
				//get the result as a string
				String an = (String) result;
				//place the results in the info label
				lblSpotInfo.setText(an);
			}
			public void onFailure(Throwable caught)
			{
				System.out.println("Error :: Failed to Get Spot Analysis " +
						"(uiAdminLotView.srvGetSpotAnalysis) :: " + caught.toString());
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results - delete the spot, by name
		lsA.getSpotAnalysis(sname,cb);
	}
	
	/*==============================================================
	 * imgDrawSpot - draw a spot on the screen
	 * Input: X/Y coords for top and bottom of box
	 * Output: draws spot to screen
	 * Return: None
	 * This function takes X/Y coordinates relative to where the spot
	 * is located on the image, and adjusts them to draw a box on the
	 * screen with the primary purpose of showing the user where a
	 * spot is located at.
	 *==============================================================*/
	public void imgDrawSpot(int avail, String sName,int topX, int topY, int botX, int botY)
	{
		//A new vertical panel to hold all info
		VerticalPanel x = new VerticalPanel();
		//labels to hold name & availability
		Label y = new Label(sName);
		Label z = new Label();
		//start out with a default of unknown
		z.setText("Unknown");
		//check availability, update accordingly
		if(avail == 1)
		{
			z.setText("Avail.");
		}
		if(avail == 0)
		{
			z.setText("N.A.");
		}
		//set primary name to style in CSS file
		y.setStylePrimaryName("spotBox");
		y.setWordWrap(true);
		z.setStylePrimaryName("spotBox");
		z.setWordWrap(true);
		//add two labels to the panel
		x.add(y);
		x.add(z);
		
		//rect being a general use popuppanel already created
		//for this purpose
		rect.setStylePrimaryName("spotBox");
		
		//get the location (on the screen, rather than the image) to place
		//the popup panel
		int absTopX = lotImg.getAbsoluteLeft() + topX;
		int absTopY = lotImg.getAbsoluteTop() + topY;
		int absBotX = lotImg.getAbsoluteLeft() + botX;
		int absBotY = lotImg.getAbsoluteTop() + botY;
		//for system logging purposes
		if(dbg){System.out.println("drawing rect @ " + absTopX + " " + absTopY +
				" " + absBotX + " " + absBotY);}
		//set the position to the calculated values
		rect.setPopupPosition(absTopX, absTopY);
		//calculate the height
		rect.setHeight(Integer.toString(absBotY - absTopY) + "px");
		//calculate the width
		rect.setWidth(Integer.toString(absBotX -absTopX) + "px");
		//make the panel the widget for the popupbox
		rect.setWidget(x);
		//show the box on top of the spot!
		rect.setVisible(true);
		rect.show();
	}
	
	/*==============================================================
	 * chgListen - change listener for this panel
	 * implements ChangeListener
	 * Input: Sending Widget
	 * Output: none
	 * Return: None
	 * This function will take the name of a spot, and delete the
	 * spot from the database, then it will update the spot listing
	 *==============================================================*/
	public class chgListen implements ChangeListener
	{
		//waits for a change in this panel
		public void onChange(Widget sender)
		{
			//if someone changes the drop-down box with the views
			if(sender.equals(lstViews))
			{
				//clear the list of spots
				lstSpots.clear();
				//get the currently selected view
				String curItem = lstViews.getItemText(lstViews.getSelectedIndex());
				//if they didn't choose "select a view"
				if(curItem.compareTo("Select a View...") != 0)
				{
					//call the service to get an update spot listing
					srvGetSpotListing(lstViews.getItemText(lstViews.getSelectedIndex()));
					srvLoadImage(lstViews.getItemText(lstViews.getSelectedIndex()));
					srvGetThresh(lstViews.getItemText(lstViews.getSelectedIndex()));
				}
			}
			//if someone clicks on the list of spots
			if(sender.equals(lstSpots))
			{
				//hide any spots currently being displayed
				rect.hide();
				
				//reset the current spot
				String curSpot = "";
				//get the selected value on the spot list
				if(lstSpots.getSelectedIndex() != -1)
				{
					//set the current spot to that value
					curSpot = lstSpots.getItemText(lstSpots.getSelectedIndex());
					//draw a box on it
					srvDrawSpotXY(curSpot);
					srvGetSpotAnalysis(curSpot);
				}
			}
			//if someone uses the slider
			if(sender.equals(hSlide))
			{
				//update the textbox with the present value of the slider
				txtThreshValue.setText(Double.toString(hSlide.getValue()));
				txtThreshValue.setMaxLength(7);
				//update the threshold on the server
				srvUpdateThresh(lstViews.getItemText(lstViews.getSelectedIndex()),hSlide.getValue());
			}
		}
	}
	
	/*==============================================================
	 * clkList - click Listener for this panel
	 * implements ClickListener
	 * Input: Sending Widget
	 * Output: none
	 * Return: None
	 * This function watches all the various buttons and lists on
	 * this panel and will update them, or direct the system in
	 * an appropriate manner
	 *==============================================================*/
	public class clkListen implements ClickListener
	{
		//onclick, waits for a click
		public void onClick(Widget sender)
		{			
			//if the click was on the drop-down with the views
			if(sender.equals(lstViews))
			{
				lblSpotInfo.setText("");
				//clear the list of spots
				lstSpots.clear();
				//get the currently selected view
				String curItem = lstViews.getItemText(lstViews.getSelectedIndex());
				//if they didn't choose "Select a View...
				if(curItem.compareTo("Select a View...") != 0)
				{
					//get an updated spot listing for the chosen view
					srvGetSpotListing(lstViews.getItemText(lstViews.getSelectedIndex()));
				}
				lblSpotInfo.setText("");
				lotImg.setUrl(lotImg.getUrl());
			}
			//if someone clicks on the list of spots
			if(sender.equals(lstSpots))
			{
				lblSpotInfo.setText("");
				//if there is more that one spot
				if(lstSpots.getItemCount() == 1)
				{
					//go ahead and update spot info
					cList.onChange(sender);
				}
				else
				{
					cList.onChange(sender);
				}
				lotImg.setUrl(lotImg.getUrl());
			}
			//if the click was on the button to add a view
			if(sender.equals(btnAddView))
			{
				//show the dialog to ask for the view name
				dlgGName.show();
			}
			//if the click was on the button to cancel the [view] dialog
			if(sender.equals(dlgGName.popLotNameCan))
			{
				//clear the textbox
				dlgGName.txtGetLotName.setText("");
				//hide the dialog
				dlgGName.hide();
			}
			//if the click was on the button to "Ok" the [view] dialog
			if(sender.equals(dlgGName.popLotNameOK))
			{
				//call the server to add the new view, using the input view &
				//current lot
				tmpViewName = dlgGName.txtGetLotName.getText();
				tmpLotName = curLotName;
				//clear the textbox
				dlgGName.txtGetLotName.setText("");
				//hide the dialog
				dlgGName.hide();
				dlgImg.show();
			}
			//after closing the get view name box - on the get img name box
			//if the click was on the OK button
			if(sender.equals(dlgImg.popLotNameOK))
			{
				//get the image file name from the user
				tmpFileName = dlgImg.txtGetLotName.getText();
				//add the view with the image name, lot name, and view name
				srvAddView(tmpViewName,tmpLotName,tmpFileName);
				//clear the textbox
				dlgImg.txtGetLotName.setText("");
				//hide the dialog
				dlgImg.hide();				
			}
			//if the click was on the Cancel button
			if(sender.equals(dlgImg.popLotNameCan))
			{
				//clear the textbox
				dlgImg.txtGetLotName.setText("");
				//hide the dialog
				dlgImg.hide();
			}
			//if the click was on the button to add a spot
			if(sender.equals(btnAddSpot))
			{
				//show the dialog box to ask for a spot name
				dlgGNameSpot.show();
			}
			//if the click was on the button to delete a spot
			if(sender.equals(btnDelSpot))
			{
				//call the server to delete the currently selected spot
				srvDelSpot(lstSpots.getItemText(lstSpots.getSelectedIndex()));
			}
			if(sender.equals(btnEditSpot))
			{
				if(lstSpots.getSelectedIndex() != -1)
				{
					editSpot.show(lstSpots.getItemText(lstSpots.getSelectedIndex()));
				}
					
			}
			//if the click is on the [spot] dialog box "OK"
			if(sender.equals(dlgGNameSpot.popLotNameOK))
			{
				//call the server to add the spot
				tmpSptSpotName = dlgGNameSpot.txtGetLotName.getText();
				tmpSptViewName = lstViews.getItemText(lstViews.getSelectedIndex());
				//srvAddSpot(tmpSptSpotName,tmpSptViewName,tmpTopX,tmpTopY,tmpBotX,tmpBotY);
				//clear the textbox
				dlgGNameSpot.txtGetLotName.setText("");
				//hide the dialog
				dlgGNameSpot.hide();
				
				//tell user to click on the top left corner
				nB.setText("Click on Top Left Corner");
				nB.show();
				//allow a spot to be stored.
				getSpot = true;
			}
			//if the click is on the [spot] dialog box "Cancel"
			if(sender.equals(dlgGNameSpot.popLotNameCan))
			{
				//clear the textbox
				dlgGNameSpot.txtGetLotName.setText("");
				//hide the dialog box
				dlgGNameSpot.hide();
			}
		}
	}
	
	/*==============================================================
	 * msListener - to listen for mouse events
	 * implements MouseListenerAdapter
	 * Input: Sending Widget, x (int), y (int)
	 * Output: none
	 * Return: None
	 * This function will primarily be used to listen for people
	 * to click on the image, and use the coordinates of the click
	 * to store where a spot is located.
	 *==============================================================*/
	public class msListener extends MouseListenerAdapter
	{
		int cnt = 0;
		
		//if someone clicks the mouse (on the image)
		public void onMouseDown(Widget sender, int x, int y)
		{		
			//if the program is NOT currently looking for a spot
			if(getSpot == false)
			{
				//log the error to system.out
				if(dbg){System.out.println("not adding a spot yet");}
				//keep zero values
				tmpTopX = 0;
				tmpTopY = 0;
				tmpBotX = 0;
				tmpBotY = 0;
			}
			//if the program IS currently looking for a spot
			else
			{
				//if they did, indeed click on the image, and it is the first click
				if(sender.equals(lotImg) && (cnt % 2) == 0)
				{
					if(dbg){System.out.println(Integer.toString(x) + " " + Integer.toString(y));}
					//set the coordinates to the top value
					tmpTopX = x;
					tmpTopY = y;
					
					//tell the user to then click on the bottom right corner
					nB.setText("Click on Bottom Right Corner");
					//using the notification box
					nB.show();
				}
				//if they did, indeed click on the image and it is the second click
				else if(sender.equals(lotImg) && (cnt % 2) == 1)
				{
					if(dbg){System.out.println(Integer.toString(x) + " " + Integer.toString(y));}
					//set the coordinates to the bottom value
					tmpBotX = x;
					tmpBotY = y;
					
					//add the spot to the server
					srvAddSpot(tmpSptSpotName,tmpSptViewName,tmpTopX,tmpTopY,tmpBotX,tmpBotY);
					//go back to not looking for a spot
					getSpot = false;
				}
				//increment the count (for purposes of deciding whether to store top/
				//or bottom coord's. on click.
				cnt++;
			}
		}
	}
}
