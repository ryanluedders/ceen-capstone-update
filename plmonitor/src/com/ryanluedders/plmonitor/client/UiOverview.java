package com.ryanluedders.plmonitor.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/*=====================================================================
 * uiOverview - General Site Overview
 * extends - srvAccessor
 * This is the panel that is loaded into the application from the start
 * It will display very basic information about the site as a whole
 *=====================================================================*/
public class UiOverview extends ServerAccessor implements ClickListener
{
	public static boolean dbg = false;
	public static boolean info = false;
	
	//A grid to hold the two center values - the site overview
	//title, and some of the information about the site
	public Grid cntGrid = new Grid(2,1);
	
	//One grid square to hold the title of the overview table
	public Grid tblSiteTitle = new Grid(1,1);
	//A grid to hold some information descriptions and the
	//corresponding values that go with them.
	public Grid tblSiteOverview = new Grid(7,2);
	
	//A button to view further details on lots, and a button
	//to enter the admin area from this panel.
	public Button btnAddNotification = new Button();
	public Button btnViewDetails = new Button();
	public Button btnEnterAdmin = new Button();

	public CreateNotification crNot = new CreateNotification();
	
	public Image waitImg = new Image("loadinfo.net.gif");
	public boolean isUpdating = false;
	
	/*==============================================================
	 * Default Constructor
	 * Input: None
	 * Output: None
	 * Return: None
	 * does the normal srvAccessor construction, then intializes and
	 * places components
	 *==============================================================*/
	public UiOverview()
	{
		//call the srvAccessor constructor
		super();
		//initialize the components on this panel
		initComponents();
		//place the components on this panel
		placeComponents();
	}
	
	/*==============================================================
	 * refreshComponents
	 * Input: None
	 * Output: None
	 * Return: None
	 * Right now, this does nothing
	 *==============================================================*/
	public void pageRefresh()
	{
		srvSiteInfo();
	}
	
	public void setVisible(boolean vis)
	{
		if(vis)
			pageRefresh();
		if(!vis)
			clearComponents();
		super.setVisible(vis);
	}
	
	public void clearComponents()
	{
		tblSiteOverview.setText(0,1,"");
		tblSiteOverview.setText(1,1,"");
		tblSiteOverview.setText(2,1,"");
		tblSiteOverview.setText(3,1,"");
		tblSiteOverview.setText(4,1,"");
		tblSiteOverview.setText(5,1,"");
		tblSiteOverview.setText(6,1,"");
	}
	
	/*==============================================================
	 * initComponents - Initialize components
	 * Input: None
	 * Output: Initializes components on this panel
	 * Return: None
	 * This function will take all of the widgets included in this
	 * panel, and will set them to their appropriate values.
	 *==============================================================*/
	public void initComponents()
	{
		//set the style of this panel - to be in the CSS file
		this.setStyleName("gwtThesis-uiOverview");
	
		//set the style of the center grid - to be in the css file
		cntGrid.setStylePrimaryName("gwtThesis-GridCenter");
		
		//set the border width and site overview title in the
		//grid that shows the site informations
		tblSiteTitle.setText(0, 0, "Site Overview");
		
		//Set the descriptions of the pieces of information that
		//will be displayed on the overview table
		tblSiteOverview.setText(0,0,"Total Spots");
		tblSiteOverview.setText(1,0,"Full Lots");
		tblSiteOverview.setText(2,0,"Not Full Lots");
		tblSiteOverview.setText(3,0,"Avg. Spots Open per Lot");
		tblSiteOverview.setText(4,0,"Most Spots Open per Lot");
		tblSiteOverview.setText(5,0,"Least Spots Open per Lot");
		tblSiteOverview.setText(6,0,"Most Open Lot");
		tblSiteOverview.getCellFormatter().setAlignment(0, 1, HTML.ALIGN_RIGHT, VerticalPanel.ALIGN_MIDDLE);
		tblSiteOverview.getCellFormatter().setAlignment(1, 1, HTML.ALIGN_RIGHT, VerticalPanel.ALIGN_MIDDLE);
		tblSiteOverview.getCellFormatter().setAlignment(2, 1, HTML.ALIGN_RIGHT, VerticalPanel.ALIGN_MIDDLE);
		tblSiteOverview.getCellFormatter().setAlignment(3, 1, HTML.ALIGN_RIGHT, VerticalPanel.ALIGN_MIDDLE);
		tblSiteOverview.getCellFormatter().setAlignment(4, 1, HTML.ALIGN_RIGHT, VerticalPanel.ALIGN_MIDDLE);
		tblSiteOverview.getCellFormatter().setAlignment(5, 1, HTML.ALIGN_RIGHT, VerticalPanel.ALIGN_MIDDLE);
		tblSiteOverview.getCellFormatter().setAlignment(6, 1, HTML.ALIGN_RIGHT, VerticalPanel.ALIGN_MIDDLE);
		
		//add the title and information to the center grid.
		tblSiteTitle.setWidth("35ex");
		tblSiteOverview.setWidth("35ex");
		tblSiteTitle.setStylePrimaryName("gwtThesis-tableTitle");
		tblSiteOverview.setStylePrimaryName("gwtThesis-tableBody");
		cntGrid.setStylePrimaryName("gwtThesis-cntGrid");
		cntGrid.setCellSpacing(0);
		cntGrid.setCellPadding(0);
		cntGrid.setWidget(0, 0, tblSiteTitle);
		cntGrid.setWidget(1, 0, tblSiteOverview);
		
		//set the text on the buttons to view lot details and
		//to enter the admin area
		btnViewDetails.setStylePrimaryName("gwtThesis-borderedButton");
		btnEnterAdmin.setStylePrimaryName("gwtThesis-borderedButton");
		btnAddNotification.setStylePrimaryName("gwtThesis-borderedButton");
		btnViewDetails.setText("View Lot Details");
		btnEnterAdmin.setText("Enter Admin Area");
		btnAddNotification.setText("Add SMS Notification");
		btnAddNotification.addClickListener(this);
		
		crNot.hide();
		crNot.setVisible(false);
		crNot.clearValues();
		
		waitImg.setVisible(false);
		
		//call the server function to display the site info
		srvSiteInfo();
	}
	
	/*==============================================================
	 * placeComponents - Place the components on this panel
	 * Input: None
	 * Output: None
	 * Return: None
	 * does the normal srvAccessor construction, then intializes and
	 * places components
	 *==============================================================*/
	public void placeComponents()
	{	
		//A panel to hold the system information label and 
		//the current time label.
		DockPanel topPan = new DockPanel();
		//A stack of widgets to put in the center of the screen
		VerticalPanel cntPan = new VerticalPanel();
		DockPanel botPan = new DockPanel();
		
		//make this panel take up the entire browser screen
		this.setWidth("100%");
		this.setHeight("100%");
		
		//make the top panel take up the whole width of the browser
		topPan.setWidth("100%");
		//put the system info label on the top-left
		topPan.add(lblSysInfo,DockPanel.WEST);
		//with left alignment
		topPan.setCellHorizontalAlignment(lblSysInfo,DockPanel.ALIGN_LEFT);
		//put the current time label on the top-right
		topPan.add(lblCurTime,DockPanel.EAST);
		//with right alignment
		topPan.setCellHorizontalAlignment(lblCurTime,DockPanel.ALIGN_RIGHT);
		
		//make the center panel take up the whole width of the browser
		cntPan.setWidth("100%");
		cntPan.setHeight("100%");
		//set the center panel to center alignment
		cntPan.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
		//add the grid with the table
		cntPan.add(cntGrid);
		cntPan.setCellVerticalAlignment(cntGrid, VerticalPanel.ALIGN_BOTTOM);
		
		//to the same panel, add a button that will allow the
		//user to view details on lots
		//cntPan.add(btnViewDetails);
		
		botPan.setWidth("100%");
		botPan.add(btnEnterAdmin,DockPanel.CENTER);
		botPan.add(btnViewDetails,DockPanel.EAST);
		botPan.add(btnAddNotification,DockPanel.WEST);
		botPan.add(waitImg,DockPanel.NORTH);
		botPan.setCellWidth(btnAddNotification, "30%");
		botPan.setCellWidth(btnEnterAdmin, "40%");
		botPan.setCellWidth(btnViewDetails, "30%");
		botPan.setCellHorizontalAlignment(btnAddNotification, DockPanel.ALIGN_LEFT);
		botPan.setCellHorizontalAlignment(btnEnterAdmin, DockPanel.ALIGN_CENTER);
		botPan.setCellHorizontalAlignment(btnViewDetails, DockPanel.ALIGN_RIGHT);
		botPan.setCellHorizontalAlignment(waitImg, DockPanel.ALIGN_CENTER);
		botPan.setCellVerticalAlignment(btnAddNotification, DockPanel.ALIGN_BOTTOM);
		botPan.setCellVerticalAlignment(btnEnterAdmin, DockPanel.ALIGN_BOTTOM);
		botPan.setCellVerticalAlignment(btnViewDetails, DockPanel.ALIGN_BOTTOM);
		botPan.setCellVerticalAlignment(waitImg, DockPanel.ALIGN_TOP);
		botPan.setCellHeight(waitImg, "15ex");
		
		//to this panel, add the top panel to the top
		//this.add(topPan, DockPanel.NORTH);
		//this.setCellVerticalAlignment(topPan,DockPanel.ALIGN_TOP);
		//to the center of this panel, add the center components
		this.add(cntPan, DockPanel.CENTER);
		this.setCellHorizontalAlignment(cntPan,DockPanel.ALIGN_CENTER);
		this.setCellVerticalAlignment(cntPan,DockPanel.ALIGN_MIDDLE);
		//to the bottom of this panel, add the button to enter the
		//admin area
		this.add(botPan, DockPanel.SOUTH);
		this.setCellHorizontalAlignment(botPan,DockPanel.ALIGN_CENTER);
		this.setCellVerticalAlignment(botPan,DockPanel.ALIGN_BOTTOM);
		
		this.setCellHeight(cntPan, "65%");
		this.setCellHeight(botPan, "35%");
	}
	
	/*==============================================================
	 * srvSiteInfo - will get the site info from the server
	 * Input: None
	 * Output: will display site info on the screen
	 * Return: None
	 * This server call will get the site information from the
	 * server and then display it on the grid on the this panel
	 *==============================================================*/
	public void srvSiteInfo()
	{
		isUpdating = true;
		waitImg.setVisible(true);
		//new asynchronous lot service
		LotServiceAsync lsA = (LotServiceAsync) GWT.create(LotService.class);
		ServiceDefTarget ep = (ServiceDefTarget) lsA;
		//url specified in the CSS file that shows where the server is
		String moduleRelativeURL = GWT.getModuleBaseURL()+"thesisServ";
		ep.setServiceEntryPoint(moduleRelativeURL);
		//create a new callback object that will do what is necessary
		//with the results of the server call
		AsyncCallback cb = new AsyncCallback(){
			public void onSuccess(Object result)
			{
				String[] x = (String[]) result;
				tblSiteOverview.setText(0,1,x[0]);
				tblSiteOverview.setText(1,1,x[1]);
				tblSiteOverview.setText(2,1,x[2]);
				tblSiteOverview.setText(3,1,x[3]);
				tblSiteOverview.setText(4,1,x[4]);
				tblSiteOverview.setText(5,1,x[5]);
				tblSiteOverview.setText(6,1,x[6]);
				waitImg.setVisible(false);
				isUpdating = false;
			}
			public void onFailure(Throwable caught)
			{
				System.out.println("Error :: Failed to get site info " +
						"(uiOverview.srvSiteInfo) :: " + caught.toString());
				isUpdating = false;
			}
		};
		//this can be changed later to get more information
		lsA.getOverviewInfo(cb);
	}

	/*==============================================================
	 * onClick - listen for clicks
	 * Input: Widget that was clicked on (Widget)
	 * Output: performs the specified action
	 * Return: None
	 * This will listen for clicks on widgets, and when a widget is
	 * clicked on, will specify which action to perform
	 *==============================================================*/
	public void onClick(Widget sender) 
	{
		//if the button is to add a notification
		if(sender.equals(btnAddNotification))
		{
			//set the create notification box visible
			crNot.setVisible(true);
			//clear all the values on the box
			crNot.clearValues();
			//center and show the create notification box
			crNot.center();
			crNot.show();
		}
	}
}
