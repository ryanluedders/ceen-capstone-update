package com.ryanluedders.plmonitor.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/*=====================================================================
 * pnlEditSpot
 * extends - PopupPanel
 * This is a panel that is shown by the program when in the admin
 * area of the system. This panel shows all of the information for
 * the currently selected spot, as well as making the information
 * available to change by using text boxes, and accessing the
 * database to make the change if necessary.
 *=====================================================================*/
public class PanelEditSpot extends PopupPanel implements ClickListener
{
	public static boolean dbg = false;
	public static boolean info = false;
	
	//name of the spot that is currently being edited
	String spotName = new String();
	
	//a save button, to save changes to spot details
	public Button btnSave = new Button();
	//a cancel button, to discard changes to spot details
	public Button btnCancel = new Button();
	
	//labels to direct the user to txtboxes with bounds
	public Label lblTopX = new Label();
	public Label lblTopY = new Label();
	public Label lblBotX = new Label();
	public Label lblBotY = new Label();
	//textboxes containing the bounds of the spot being edited
	public TextBox txtTopX = new TextBox();
	public TextBox txtTopY = new TextBox();
	public TextBox txtBotX = new TextBox();
	public TextBox txtBotY = new TextBox();
	
	//labels to direct user to textboxes with row/column
	public Label lblPhysRow = new Label();
	public Label lblPhysCol = new Label();
	//textboxes containing the row and column of the spot
	public TextBox txtPhysRow = new TextBox();
	public TextBox txtPhysCol = new TextBox();
	
	//a label to hold info passed from the server, such as
	//results of image processing.
	public Label lblInfo = new Label();
	
	//a label to direct user to special textbox
	public Label lblSpecial = new Label();
	//a textbox containing any special conditions of the spot
	public TextBox txtSpecial = new TextBox();
	
	//the main panel to hold all components
	public DockPanel contentPan = new DockPanel();
	//a vertical panel to stack components on the right
	public VerticalPanel rightPan = new VerticalPanel();
	//a vertical panel to stack components on the left
	public VerticalPanel leftPan = new VerticalPanel();
	
	//a horizontal panel to line up X values of bounds
	public HorizontalPanel xVals = new HorizontalPanel();
	//a horizontal panel to line up Y values of bounds
	public HorizontalPanel yVals = new HorizontalPanel();
	
	//a horizontal panel to line up label & txtbox for row
	public HorizontalPanel physRow = new HorizontalPanel();
	//a horizontal panel to line up label & txtbox for col
	public HorizontalPanel physCol = new HorizontalPanel();
	//a vertical panel to stack label & txtbox for special conditions
	public VerticalPanel special = new VerticalPanel();
	//a horiztonal panel to line up save & cancel buttons
	public HorizontalPanel btnPan = new HorizontalPanel();
	
	/*==============================================================
	 * Default Constructor
	 * Input: the name of the spot (String)
	 * Output: None
	 * Return: None
	 * constructs the edit panel, initializes and places components
	 * then shows the panel
	 *==============================================================*/
	public PanelEditSpot(String spotName)
	{
		//initialize components
		initComponents();
		//place all components in panels
		placeComponents();
		//make this box not visible until shown
		this.setVisible(false);
		//make sure it's not showing initially, as it is constructed
		//when the system starts up
		this.hide();
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
		//set the CSS style, so colors & other aspects can be specified
		//in thesis CSS file
		this.setStylePrimaryName("dlgGetName");
		//set the save button text
		btnSave.setText("Save Changes");
		//set the cancel button text
		btnCancel.setText("Cancel");
		
		//set the labels showing where the bounds are
		lblTopX.setText("Top X");
		lblTopY.setText("Top Y");
		lblBotX.setText("Bot X");
		lblBotY.setText("Bot Y");
		//set the max length and size of the text boxes
		txtTopX.setMaxLength(4);
		txtTopX.setWidth("5ex");
		txtBotX.setMaxLength(4);
		txtBotX.setWidth("5ex");
		txtTopY.setMaxLength(4);
		txtTopY.setWidth("5ex");
		txtBotY.setMaxLength(4);
		txtBotY.setWidth("5ex");
		
		//set the labels showing where the row/col info is
		lblPhysRow.setText("Physical Row");
		lblPhysCol.setText("Physical Col");
		//set the max length and size of the text boxes
		txtPhysRow.setMaxLength(3);
		txtPhysRow.setWidth("4ex");
		txtPhysCol.setMaxLength(3);
		txtPhysCol.setWidth("4ex");
		
		//set the label showing where special conditions are found
		lblSpecial.setText("Special");
		//set the max length and size of the text boxes
		txtSpecial.setMaxLength(20);
		txtSpecial.setWidth("20ex");
		//give the info label a default value
		lblInfo.setText("info");
	}
	
	/*==============================================================
	 * placeComponents - place all widget components on this panel
	 * Input: None
	 * Output: None
	 * Return: None
	 * organizes all widgets using an elaborate system of panels to
	 * get them lined up
	 *==============================================================*/
	public void placeComponents()
	{
		//add labels and text boxes to x panel
		xVals.add(lblTopX);
		xVals.add(txtTopX);
		xVals.add(lblBotX);
		xVals.add(txtBotX);
		//add labels and text boxes to y panel
		yVals.add(lblTopY);
		yVals.add(txtTopY);
		yVals.add(lblBotY);
		yVals.add(txtBotY);
		
		//set the info label to something default
		lblInfo.setText("info: \n");
		
		//on the left, stack x panel, y panel, and then
		//info label
		leftPan.add(xVals);
		leftPan.add(yVals);
		leftPan.add(lblInfo);
		
		//line up label and text box to row panel
		physRow.add(lblPhysRow);
		physRow.add(txtPhysRow);
		
		//line up label and text box to column panel
		physCol.add(lblPhysCol);
		physCol.add(txtPhysCol);
		
		//stack label and text box in special panel
		special.add(lblSpecial);
		special.add(txtSpecial);
		
		//line up cancel & save buttons in button panel
		btnPan.add(btnCancel);
		btnPan.add(btnSave);
		//make this panel listen for clicks from these buttons
		btnCancel.addClickListener(this);
		btnSave.addClickListener(this);
		
		//make right side components aligned with the bottom
		rightPan.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
		//stack the row/col information, special information,and buttons
		VerticalPanel rowcol = new VerticalPanel();
		rowcol.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
		rowcol.add(physRow);
		rowcol.add(physCol);
		rowcol.setHeight("100%");
		rightPan.add(rowcol);
		rightPan.add(new Label("\n"));
		rightPan.add(btnPan);
		
		leftPan.add(special);
		
		//make content align with the bottom
		contentPan.setVerticalAlignment(DockPanel.ALIGN_TOP);
		//add the left pan to the left side
		contentPan.add(leftPan,DockPanel.WEST);
		//add a space in the middle to separate left & right
		contentPan.add(new Label("    "),DockPanel.CENTER);
		//add right panel to the right side
		contentPan.add(rightPan,DockPanel.EAST);
		
		//set this panel to contain the whole thing
		this.setWidget(contentPan);
		//center the panel on the screen
		this.center();
	}
	
	/*==============================================================
	 * setSpotName - set the name of the spot
	 * Input: Spot name (String)
	 * Output: None
	 * Return: None
	 * Takes in the name of a spot from the previous screen and
	 * sets the panel to display information about that spot
	 *==============================================================*/
	public void setSpotName(String sname)
	{
		//set the name to the new name
		spotName = sname;
	}
	
	/*==============================================================
	 * refreshComponents - refresh all values on the panel
	 * Input: None
	 * Output: Refreshes all components with fresh data
	 * Return: None
	 * Using the currently stored name of the spot, set using the
	 * setSpotName method, this method is a all-in-one method for
	 * updating all components on the panel.
	 *==============================================================*/
	public void refreshComponents()
	{
		//clear the values
		clearValues();
		//call the server to update the row & the column
		srvGetSpotRowCol(spotName);
		//call the server to update the bounds
		srvGetSpotBounds(spotName);
		//call the server to update special conditions
		srvGetSpotSpecial(spotName);
		//call the server to process the spot & return an analysis
		//srvGetSpotAnalysis(spotName);
	}
	
	/*==============================================================
	 * show - show the panel
	 * Input: Spot Name (String)
	 * Output: shows the panel, with current data on a spot
	 * Return: None
	 * Takes in the name of a spot to show data for when showing
	 * this panel
	 *==============================================================*/
	public void show(String sname)
	{
		//set spot name to the name specified
		setSpotName(sname);
		//update all information
		refreshComponents();
		//for logging purposes
		if(dbg){System.out.println("Editing Spot: " + sname);}
		
		//set the panel visible
		this.setVisible(true);
		//call the default show() method that is inherited from dockpanel
		super.show();
		//center the panel on the screen
		this.center();
	}
	
	/*==============================================================
	 * clearValues - clear all values on the panel
	 * Input: None
	 * Output: clear all values on the panel
	 * Return: None
	 * All-in-one method to clear all values contained on the panel
	 *==============================================================*/
	public void clearValues()
	{
		//set all values to blank
		txtTopX.setText("");
		txtTopY.setText("");
		txtBotX.setText("");
		txtBotY.setText("");
		txtPhysRow.setText("");
		txtPhysCol.setText("");
		txtSpecial.setText("");
		lblInfo.setText("");
	}
	
	/*==============================================================
	 * srvGetSpotRowCol - updates the row & column values associated
	 * with a spot
	 * Input: Spot Name (String)
	 * Output: Updates row & column info on the panel
	 * Return: None
	 * Calls the server to get row and column associated with a
	 * specified spot, and updates the panel to show that information.
	 *==============================================================*/
	public void srvGetSpotRowCol(String sname)
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
				//values are returned in an integer array
				int[] vals = (int []) result;
				//set the row & col values to what is returned
				txtPhysRow.setText(Integer.toString(vals[0]));
				txtPhysCol.setText(Integer.toString(vals[1]));
				//System Logging
				if(dbg){System.out.println("pnlEditSpt-getRowCol");}
			}
			public void onFailure(Throwable caught)
			{
				//System Logging
				System.out.println("Error :: (pnlEditSpot.srvGetSpotRowCol) :: " +
						caught.toString());
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results
		lsA.getSpotRowCol(sname, cb);
	}
	
	/*==============================================================
	 * srvGetSpotBounds - updates the X/Y bounds associated with spot
	 * Input: Spot Name (String)
	 * Output: Updates bounds on the panel
	 * Return: None
	 * Calls the server to get X/Y information for the bounds of the
	 * spot on the current view image, and updates the panel to show
	 * this information
	 *==============================================================*/
	public void srvGetSpotBounds(String sname)
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
				//the values are returned in an integer array
				int[] vals = (int []) result;
				//set all textboxes to display associated row/col
				txtTopX.setText(Integer.toString(vals[0]));
				txtTopY.setText(Integer.toString(vals[1]));
				txtBotX.setText(Integer.toString(vals[2]));
				txtBotY.setText(Integer.toString(vals[3]));
				//System Logging
				if(dbg){System.out.println("pnlEditSpt-getXY");}
			}
			public void onFailure(Throwable caught)
			{
				//System Logging
				System.out.println("Error :: (pnlEditSpot.srvGetSpotBounds) :: " + 
						caught.toString());
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results
		lsA.getSpotXY(sname, cb);
	}
	
	/*==============================================================
	 * srvGetSpotSpecial - updates special conditions field
	 * Input: Spot Name (String)
	 * Output: Updates special conditions on the panel
	 * Return: None
	 * Calls the server to get any special conditions associated
	 * with this spot, and updates the panle to show this information
	 *==============================================================*/
	public void srvGetSpotSpecial(String sname)
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
				//get the result from the server as a string
				String vals = (String) result;
				//if it is null, then no special conditions
				if(vals.trim().compareTo("null") == 0)
					txtSpecial.setText("");
				//else, there are special conditions, show them in
				//the special text box
				else
					txtSpecial.setText(vals);
				
				//System Logging
				if(dbg){System.out.println("pnlEditSpt-getSpotSpecial");}
			}
			public void onFailure(Throwable caught)
			{
				//System Logging
				System.out.println("Error :: (pnlEditSpot.srvGetSpotSpecial) :: " + 
						caught.toString());
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results
		lsA.getSpotSpecial(sname, cb);
	}
	
	/*==============================================================
	 * srvUpdateSpot - updates all information in database for a spot
	 * to values passed into the method
	 * Input: Spot Name (String), top X (int), top Y (int), bottom x (int)
	 * botton y (int), Spot Row (int), Spot Col (int), Special notes (string)
	 * Output: Updates database with info from panel
	 * Return: None
	 * This method takes all values that could be changed on a spot,
	 * and updates the database with this information.
	 *==============================================================*/
	public void srvUpdateSpot(String sname, int tX, int tY, int bX, int bY, int pRow, int pCol, String special)
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
				if(dbg){System.out.println("pnlEditSpt-successfully updated DB with spot info");}
			}
			public void onFailure(Throwable caught)
			{
				//System Logging
				System.out.println("Error :: (pnlEditSpot.srvUpdateSpot) :: " + 
						caught.toString());
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results
		lsA.updateSpotInfo(sname, tX, tY, bX, bY, pRow, pCol, special, cb);
	}
	
	/*==============================================================
	 * srvGetSpotAnalysis - calls server to get image analysis.
	 * displays metrics indicating results on the panel
	 * Input: Spot Name (String)
	 * Output: Updates panel with image processing information
	 * Return: None
	 * This method takes the name of a string, and sends it to the
	 * server to have the image analysis performed on it. The server
	 * returns some metrics, and this method displays them on the panel
	 *==============================================================*/
	public void srvGetSpotAnalysis(String sname)
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
				//get the result as a string
				String an = (String) result;
				//place the results in the info label
				lblInfo.setText(an);
			}
			public void onFailure(Throwable caught)
			{
				System.out.println("Error :: (pnlEditSpot.srvGetSpotAnalysis) :: " + 
						caught.toString());
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results - delete the spot, by name
		lsA.getSpotAnalysis(sname,cb);
	}
	
	/*==============================================================
	 * onClick - listens to clicks on buttons
	 * Input: sender (widget)
	 * Output: performs some action
	 * Return: None
	 * This method listens for clicks on the save or cancel buttons
	 *==============================================================*/
	public void onClick(Widget sender) 
	{
		//if it is the cancel button
		if(sender.equals(btnCancel))
		{
			//clear values & hide the panel. not recording
			//any changes to the database
			clearValues();
			this.hide();
		}
		//if it is the save button
		if(sender.equals(btnSave))
		{
			//update the database with the data from the panel
			srvUpdateSpot(spotName,Integer.valueOf(txtTopX.getText()).intValue(),
					Integer.valueOf(txtTopY.getText()).intValue(),
					Integer.valueOf(txtBotX.getText()).intValue(),
					Integer.valueOf(txtBotY.getText()).intValue(),
					Integer.valueOf(txtPhysRow.getText()).intValue(),
					Integer.valueOf(txtPhysCol.getText()).intValue(),
					txtSpecial.getText());
			//then clear values & hide the panel
			clearValues();
			this.hide();
		}
	}
}
