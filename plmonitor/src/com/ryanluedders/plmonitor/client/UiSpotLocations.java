package com.ryanluedders.plmonitor.client;

import java.util.Vector;

import org.gwtwidgets.client.style.Color;
import org.gwtwidgets.client.wrap.JsGraphicsPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LoadListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/*=====================================================================
 * uiSpotLocs - For a Lot, the spot locations
 * extends - srvAccessor
 * This is the panel that will eventually display a graphic
 * representation of the parking lot, showing which spots are available
 * and which are unavailable on a picture of the lot.
 *=====================================================================*/
public class UiSpotLocations extends ServerAccessor implements LoadListener
{	
	public static boolean dbg = false;
	public static boolean info = false;
	
	//A button to go back
	public Button btnBack = new Button();
	//A button to enter the administrative interface
	public Button btnEnterAdmin = new Button();
	//label to show what lot is being displayed
	public Label lblDispLot = new Label();
	//a spinner to show that the page is loading
	public Image waitImg = new Image("loadinfo.net.gif");
	public Grid gr = new Grid();
	
	//an Image to show the parking lot image
	public Image viewImg = new Image();
	//a button to go to the next view
	public Button btnForward = new Button();
	//a button to go to the previous view
	public Button btnBackward = new Button();
	//a label to show the view name
	public Label lblViewName = new Label();
	
	//set the current lot to blank
	public String curLot = " ";
	public String curView = "";
	//the current views to blank
	public String[] lotViews = {" "};
	//and the view counter to 0
	public int viewCtr = 0;
	
	//a click listener to listen for clicks
	public uiSLClkListener clkList = new uiSLClkListener();
	//a javascript graphics panl to allow the program to draw
	//the red & green boxes on the image
	public JsGraphicsPanel jsPan = new JsGraphicsPanel("g");
	
	/*==============================================================
	 * Default Constructor
	 * Input: None
	 * Output: None
	 * Return: None
	 * does the normal srvAccessor construction, then intializes and
	 * places components
	 *==============================================================*/
	public UiSpotLocations()
	{
		//call the srvAccessor constructor
		super();
		//initialize all components
		initComponents();
		//place all components
		placeComponents();
		refreshComponents();
	}
	
	/*==============================================================
	 * refreshComponents - a one stop, refresh all components fcn
	 * Input: None
	 * Output: None
	 * Return: None
	 * right now, this does nothing.
	 *==============================================================*/
	public void refreshComponents()
	{
		//srvDrawSpots();
		lblDispLot.setText(curLot);
		srvGetViewImage(curLot);
		return;
	}
	
	/*==============================================================
	 * initComponents - initialize all components in this panel
	 * Input: None
	 * Output: Initialize components
	 * Return: None
	 * Initializes all components that are involved in this panel
	 *==============================================================*/
	public void initComponents()
	{
		//set the buttons to the style used through the UI
		btnEnterAdmin.setStylePrimaryName("gwtThesis-borderedButton");
		btnBack.setStylePrimaryName("gwtThesis-borderedButton");
		//set the button to enter the admin area
		btnEnterAdmin.setText("Enter Admin Area");
		//set the button to go back to lot details
		btnBack.setText("Go Back to Lot Details");
		
		//a button to go to the next view
		btnForward.setText(" View --> ");
		btnForward.setStylePrimaryName("gwtThesis-borderedButton");
		//a button to go to the previous view
		btnBackward.setText(" <-- View ");
		btnBackward.setStylePrimaryName("gwtThesis-borderedButton");
		//a label to show the current view
		lblViewName.setText(" Current View ");
		lblViewName.setStylePrimaryName("gwtThesis-borderedLabel");
		viewImg.setStylePrimaryName("gwtThesis-borderedImage");
		viewImg.addLoadListener(this);
		
		//make the click listener listen to these buttons for clicks
		btnForward.addClickListener(clkList);
		btnBackward.addClickListener(clkList);
	}

	/*==============================================================
	 * placeComponents - place all components in this panel
	 * Input: None
	 * Output: place components
	 * Return: None
	 * places all components that are involved in this panel
	 *==============================================================*/
	public void placeComponents()
	{
		//set this panel to stretch across the entire browser
		this.setWidth("100%");
		this.setHeight("100%");
		
		//A dockpanel to have in the top
		DockPanel topPan = new DockPanel();
		VerticalPanel midPan = new VerticalPanel();
		DockPanel midNav = new DockPanel();
		
		//set the top-panel to stretch across entire browser
		topPan.setWidth("100%");
		topPan.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
		topPan.add(lblDispLot,DockPanel.CENTER);

		//A dockpanel to hold the bottom stuff
		DockPanel botPan = new DockPanel();

		//add the button to enter the admin area to the center
		botPan.add(btnEnterAdmin,DockPanel.CENTER);
		botPan.setCellHorizontalAlignment(btnEnterAdmin, DockPanel.ALIGN_CENTER);
		botPan.setCellVerticalAlignment(btnEnterAdmin, DockPanel.ALIGN_BOTTOM);
		//add the back button to the left
		botPan.add(btnBack,DockPanel.WEST);
		botPan.setCellHorizontalAlignment(btnBack, DockPanel.ALIGN_LEFT);
		botPan.setCellVerticalAlignment(btnBack, DockPanel.ALIGN_BOTTOM);
		//put a filler inthe bottom-right
		Label filler = new Label("");
		botPan.add(filler,DockPanel.EAST);
		//set bottom panel to be across browser
		botPan.setWidth("100%");
		botPan.setCellWidth(btnBack, "25%");
		botPan.setCellWidth(btnEnterAdmin, "50%");
		botPan.setCellWidth(filler, "25%");
		
		//a middle panel to show the naviagational buttons
		midNav.add(btnBackward,DockPanel.WEST);
		midNav.add(lblViewName,DockPanel.CENTER);
		midNav.add(btnForward,DockPanel.EAST);
		midNav.setCellHorizontalAlignment(btnBackward, DockPanel.ALIGN_LEFT);
		midNav.setCellHorizontalAlignment(lblViewName, DockPanel.ALIGN_CENTER);
		midNav.setCellHorizontalAlignment(btnForward, DockPanel.ALIGN_RIGHT);
		midNav.setVerticalAlignment(DockPanel.ALIGN_BOTTOM);
		midNav.setWidth("65%");
		midNav.setCellWidth(btnBackward, "25%");
		midNav.setCellWidth(btnForward, "25%");
		midNav.setCellWidth(lblViewName, "50%");
		
		//to the middle, add the view image
		midPan.add(viewImg);
		//then, below the image, add navigation panel
		midPan.add(midNav);
		//align the image and nav panel in the center
		midPan.setCellHorizontalAlignment(viewImg, VerticalPanel.ALIGN_CENTER);
		midPan.setCellHorizontalAlignment(midNav, VerticalPanel.ALIGN_CENTER);
				
		//add the top & bottom panels to the top & bottom of this panel
		this.add(topPan,DockPanel.NORTH);
		this.add(midPan,DockPanel.CENTER);
		this.add(botPan,DockPanel.SOUTH);
		
		//set the bottom panel to be aligned on the bottom.
		this.setCellVerticalAlignment(botPan, DockPanel.ALIGN_BOTTOM);
		this.setCellHorizontalAlignment(midPan, DockPanel.ALIGN_CENTER);
	}
	
	/*==============================================================
	 * setVisible - modifies default actions upon setvisible
	 * Input: visible/not visible (true)
	 * Output: refreshes components, removes any grids upon hiding
	 * Return: None
	 * will be called from appFrame, for the purpose of removing any
	 * existing grids from the center frame
	 *==============================================================*/
	public void setVisible(boolean x)
	{		
		//if it is being hidden
		if(x == false)
		{
			//hide the image
			viewImg.setVisible(false);
			//clear the drawing panel
			jsPan.clear();
			jsPan.setVisible(false);
			//reset the view counter
			viewCtr = 0;
		}
		//if it is being shown
		if(x == true)
		{
			//show the drawing panel
			jsPan.clear();
			jsPan.paint();
			jsPan.setVisible(true);
			viewImg.setVisible(false);
			//refresh the components
			refreshComponents();
		}
		
		//call the inherited setvisible method to continue.
		super.setVisible(x);
	}
	
	/*==============================================================
	 * pageRefresh - refreshes all components on the page
	 * Input: none
	 * Output: refreshes components
	 * Return: None
	 * called to refresh all things on page
	 *==============================================================*/
	public void pageRefresh()
	{
		//call to refresh components
		refreshComponents();
	}
	
	/*==============================================================
	 * setLotName - sets the current lot name
	 * Input: lot name (String)
	 * Output: None
	 * Return: None
	 * gets name, sets name associated with this panel to that name
	 *==============================================================*/
	public void setLotName(String lname)
	{
		//set the name
		curLot = lname;
	}
	
	/*==============================================================
	 * addGrid - adds a grid displaying spots to this panel
	 * Input: grid displaying spots (Grid)
	 * Output: outputs the grid on the UI
	 * Return: None
	 * displays a specified grid to the user to show which spots
	 * are available, and which are unavailable.
	 *==============================================================*/
	public void addGrid(Grid x)
	{
		//clear out any existing grids
		this.clear();
		//re-place the components
		placeComponents();
		//align everything in the center
		this.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
		//add the grid in the middle
		this.add(x,DockPanel.CENTER);
		
	}
	
	/*==============================================================
	 * addSpinner - displays a spinner for purposes of telling
	 * the user to wait
	 * Input: none
	 * Output: outputs a "wait spinner" to the screen
	 * Return: None
	 * puts a "wait" spinner in the middle of the screen
	 *==============================================================*/
	public void addSpinner()
	{
		//clear out anything currently in the center
		this.clear();
		//re-place all components
		placeComponents();
		//align everything in the center
		this.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
		//add the spinner, in the middle
		this.add(waitImg,DockPanel.CENTER);
		//show the image
		waitImg.setVisible(true);
	}
	
	/*==============================================================
	 * drawSpot - draws a box around a spot
	 * Input: spot name (String) availability (int) average top Y coordinate (int)
	 * average bottom y coordinate (int) previous spot left-x coordinate (int) previous
	 * spot right-x coordinate (int) spot left-x coordinate (int) spot right-x coordinate (int)
	 * next spot left-x coordinate (int) next spot right-x coordinate (int)
	 * Output: outputs a "wait spinner" to the screen
	 * Return: None
	 * puts a "wait" spinner in the middle of the screen
	 *==============================================================*/
	public int[] drawSpot(String sName, int avail, int avgTopY, int avgBotY,
			int prevSpotLX, int prevSpotRX, int spotLX, int spotRX, int nextSpotLX, int nextSpotRX)
	{
		//initialize the variables used in calculations
		int prevSpotMiddleX = 0;
		int spotMiddleX = 0;
		int nextSpotMiddleX = 0;
		int betweenPrevSpot = 0;
		int betweenNextSpot = 0;
		
		//middle x-val of the previous spot. halfway in between that
		//spot's left and right x values
		prevSpotMiddleX = (prevSpotLX + prevSpotRX) / 2;
		//middle x-val of the current spot. halfway in between the
		//spot's left and right x values
		spotMiddleX = (spotLX + spotRX) / 2;
		//middle x-val of the next spot. halfway in between that spot's
		//left and rigth x values
		nextSpotMiddleX = (nextSpotLX + nextSpotRX) / 2;
		//will be the starting x of the current spot - halfway in
		//between the middle of the previous spot, and the middle of
		//the current spot
		betweenPrevSpot = (prevSpotMiddleX + spotMiddleX) / 2;
		//will be the ending x of the current spot - halfway in between
		//the middle of the current spot, and the middle of the
		//next spot.
		betweenNextSpot = (spotMiddleX + nextSpotMiddleX) / 2;
		
		//top left x of the spot, found above
		int boxTopLX = betweenPrevSpot;
		//top left y of the spot is the average
		int boxTopLY = avgTopY;
		//bottom right x of the spot, found above
		int boxBotRX = betweenNextSpot;
		//bottom right y of the spot is the average
		int boxBotRY = avgBotY;
		//height is difference between top & bottom Y values
		int height = boxBotRY - boxTopLY;
		//width is difference between left & right X values
		int width = boxBotRX - boxTopLX;
		
		//for debug, print the spot bounds
		if(dbg){System.out.println("--*--*--");
		System.out.println(boxTopLX);
		System.out.println(boxTopLY);
		System.out.println(boxBotRX + " " + boxTopLX + " " + width);
		System.out.println(boxBotRY + " " + boxTopLY + " " + height);}
		
		//if the spot is available
		if(avail == 1)
			//color the box green
			jsPan.setColor(Color.GREEN);
		//if it is unavailable
		else if(avail == 0)
			//color the box red
			jsPan.setColor(Color.RED);
		//if for some reason, it is neither
		else
			//color the box gray
			jsPan.setColor(Color.GRAY);
		
		//return the values necessary to draw a good box
		int[] rval = new int[5];
		rval[0] = (avail);
		rval[1] = (viewImg.getAbsoluteLeft() + boxTopLX);
		rval[2] = (viewImg.getAbsoluteTop() + boxTopLY);
		rval[3] = width;
		rval[4] = height;
		
		//return the values
		return rval;
	}
	
	/*==============================================================
	 * srvDrawSpots - gets spots in a lot from srv, draws them
	 * Input: Assumes current lot from class
	 * Output: Outputs graphic showing spots to screen
	 * Return: None
	 * Queries the server to get all the spots associated with the
	 * current lot. If these spots have had a row/column attribute
	 * specified, then it will draw them in a grid form, and display
	 * it to the screen
	 *==============================================================*/
	public void srvDrawSpots(String vname)
	{
		final String vw = vname;
		//addSpinner();
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
				if(dbg){System.out.println("trying to show test spot!");}
				//a vector of the results
				Vector x = (Vector) result;
				
				//find max rows, max columns, avg Y (for top & bottom)
				int maxRows = 0;
				int minRows = 10000;
				//go through entire vector
				for(int i=0; i<x.size(); i++)
				{
					//get the string array with the spot info
					String[] arr = (String[]) x.get(i);
					//get the row of the spot
					int y = Integer.parseInt(arr[1]);
					//if it is bigger than any row seen yet
					if(y > maxRows)
						//it is the maximum row (so far, at least)
						maxRows = y;
					//if it is smaller than any row seen yet
					if(y < minRows)
						//it is the minimum row (so far, at least)
						minRows = y;
				}
				
				//an int array, to find the max column for each row
				int[] maxCols = new int[maxRows+1];
				//an int array, to find the minimum column for each row
				int[] firstCols = new int[maxRows+1];
				
				//an int array, to find the avg top y value for each row
				int[] avgYTop = new int[maxRows+1];
				//an int array, to find the avg bottom y value for each row
				int[] avgYBot = new int[maxRows+1];
				//a counter, to count the number in each row
				int[] cnt = new int[maxRows+1];
				
				//go through all rows
				for(int i=0; i<maxRows+1; i++)
				{
					//initialize all array values
					maxCols[i] = 0;
					firstCols[i] = 10000;
					avgYTop[i] = 0;
					avgYBot[i] = 0;
					cnt[i] = 0;
				}
				//go through all results
				for(int i=0; i<x.size(); i++)
				{
					//get the string[] from the result
					String[] arr = (String[]) x.get(i);
					//get the spot row
					int y = Integer.parseInt(arr[1]);
					//get the spot column
					int z = Integer.parseInt(arr[2]);
					//get the spot top Y
					int m = Integer.parseInt(arr[4]);
					//get the spot bottom Y
					int n = Integer.parseInt(arr[6]);
					
					//if the spot's column is larger than the max
					//column for this row, make it the max column
					if(z > maxCols[y])
						maxCols[y] = z;
					//if the spots column is smaller than the max
					//column for this row, make it the min column
					if(z < firstCols[y])
						firstCols[y] = z;
					
					if(dbg){System.out.println("#### " + y + " -- " + maxCols[y] + " ## " + firstCols[y] + " ####");}
					
					//add the top y to the array (to eventually find the avg)
					avgYTop[y] += m;
					//add the bottom y to the array (to eventually find the avg)
					avgYBot[y] += n;
					//increase the counter, in order to find the average
					cnt[y]++;
				}
				
				//find the average top and bottom y for each row
				for(int i=0; i<maxRows+1; i++)
				{
					//if the counter is not zero (to avoid divide by zero)
					if(cnt[i] != 0)
					{
						//divide the total that was found, by the count
						avgYTop[i] = avgYTop[i] / cnt[i];
						avgYBot[i] = avgYBot[i] / cnt[i];
					}
					else
					{
						//if the counter is zero, then the average should be zero
						avgYTop[i] = 0;
						avgYBot[i] = 0;
					}
					
					//System.out.println("Avg. Y Vals: " + avgYTop[i] + " " + avgYBot[i]);
				}
				
				//go through each row, starting at the largest.
				for(int i=maxRows; i>1; i--)
				{
					//if the average bottom Y value is overlapping
					//the row below it
					if(avgYBot[i] != avgYTop[i-1])
					{
						//find a resolution, by making them meet up,
						//halfway through the overlap.
						int temp = (avgYTop[i-1] + avgYBot[i]) / 2;
						avgYTop[i-1] = temp;
						avgYBot[i] = temp;
					}
				}
				
				//clear the drawing panl prior to drawing new items
				jsPan.clear();
				//a vector for green boxes and a vector for red boxes
				//in order for the green boxes to be drawn on top.
				Vector greenBoxes = new Vector();
				Vector redBoxes = new Vector();
				
				//go through all results
				for(int i=0; i<x.size(); i++)
				{
					//get string for current spot
					String[] arr = (String[]) x.get(i);
					//arrays for the previous and next spot
					String[] prvArr = null;
					String[] nxtArr = null;
					//if we are not looking at the first spot
					if(i > 0)
						//previous spot info
						prvArr = (String[]) x.get(i-1);
					//if we are not looking at the last spot
					if(i < (x.size()-1))
						//the next array can be gotten
						nxtArr = (String[]) x.get(i+1);
					
					//the name of the spot
					String a = arr[0];
					//spot physical row
					int b = Integer.parseInt(arr[1]);
					//spot physical column
					int c = Integer.parseInt(arr[2]);
					//spot Top X
					int d = Integer.parseInt(arr[3]);
					//spot Top Y
					int e = Integer.parseInt(arr[4]);
					//spot Bottom X
					int f = Integer.parseInt(arr[5]);
					//spot Bottom Y
					int g = Integer.parseInt(arr[6]);
					//spot availability
					int av = Integer.parseInt(arr[7]);
					
					//previous spot, left x
					int psLX = 0;
					//previous spot, right x
					int psRX = 0;
					//current spot, left x
					int sLX = 0;
					//current spot, right x
					int sRX = 0;
					//next spot, left x
					int nxLX = 0;
					//next spot, right x
					int nxRX = 0;
					
					//if the spot is in the first column of the row
					if( c == firstCols[b] )
					{	
						//set the previous spot to be the top x minus the
						//width of the current spot
						psLX = d - (f-d);
						//set the previous spot right x to be the top  x
						//of the current spot
						psRX = d;
						//set the left x of the current spot to the DB results
						sLX = d;
						//set the right x of the current spot to the DB results
						sRX = f;
						//set the next spot left to the DB results
						nxLX = Integer.parseInt(nxtArr[3]);
						//set the next spot right to the DB results
						nxRX = Integer.parseInt(nxtArr[5]);
						
						//get the values needed in order to draw ths spot, by calling the method
						int[] temp = drawSpot(a,av,avgYTop[b],avgYBot[b],psLX,psRX,sLX,sRX,nxLX,nxRX);
						//if not available, queue it to the red box queue
						if(temp[0] == 0)
							redBoxes.add(temp);
						//if available, queue it to the green box queue
						if(temp[0] == 1)
							greenBoxes.add(temp);
					}
					//if spot is in the max column for its row
					else if( c == maxCols[b])
					{			
						//set the previous spot left x to the DB results
						psLX = Integer.parseInt(prvArr[3]);
						//set the previous spot right x to the DB results
						psRX = Integer.parseInt(prvArr[5]);
						//set the current spot left x to the DB results
						sLX = d;
						//set the current spot right x to the DB results
						sRX = f;
						//set the next spot left x to the current spot right x
						nxLX = f;
						//set the next spot right x to the current spot right x, 
						//plus the width of the current spot
						nxRX = f + (f-d);
						
						//get the values needed in order to draw the spot
						int[] temp = drawSpot(a,av,avgYTop[b],avgYBot[b],psLX,psRX,sLX,sRX,nxLX,nxRX);
						//if it is unavailable queue to red box queue
						if(temp[0] == 0)
							redBoxes.add(temp);
						//if it is available, queue to green box queue
						if(temp[0] == 1)
							greenBoxes.add(temp);
					}
					//if it is just a normal, middle spot
					else
					{
						//set the previous spot left x to the db results
						psLX = Integer.parseInt(prvArr[3]);
						//set the previous spot right x to the db results
						psRX = Integer.parseInt(prvArr[5]);
						//set the current spot left x to the db results
						sLX = d;
						//set the current spot right x to the db results
						sRX = f;
						//set the next spot left x to the db results
						nxLX = Integer.parseInt(nxtArr[3]);
						//set the next spot right x to the db results
						nxRX = Integer.parseInt(nxtArr[5]);
						
						//get the values needed to draw the spot box
						int[] temp = drawSpot(a,av,avgYTop[b],avgYBot[b],psLX,psRX,sLX,sRX,nxLX,nxRX);
						//if it is unavailable, queue it to the red box queue
						if(temp[0] == 0)
							redBoxes.add(temp);
						//if it is available, queue it to the green box queue
						if(temp[0] == 1)
							greenBoxes.add(temp);
					}				
				}
				
				//To draw the unavailable spots
				//set the drawing panel to red
				jsPan.setColor(Color.RED);
				//go through all boxes
				for(int i=0; i<redBoxes.size(); i++)
				{
					//set the stroke width
					jsPan.setStrokeWidth(5);
					//and draw the rectangle
					jsPan.drawRect(((int[])redBoxes.get(i))[1],
							((int[])redBoxes.get(i))[2],
							((int[])redBoxes.get(i))[3],
							((int[])redBoxes.get(i))[4]);
				}
				//paint to show all the boxes
				jsPan.paint();
				
				//to draw all the avaiable spots
				//set the drawing panel to green
				jsPan.setColor(Color.GREEN);
				//go through all boxes
				for(int i=0; i<greenBoxes.size(); i++)
				{
					//set the stroke width
					jsPan.setStrokeWidth(5);
					//and draw a rectangle
					jsPan.drawRect(((int[])greenBoxes.get(i))[1],
							((int[])greenBoxes.get(i))[2],
							((int[])greenBoxes.get(i))[3],
							((int[])greenBoxes.get(i))[4]);
				}
				//paint to show all the boxes
				jsPan.paint();
				//srvLoadImage(vw);
			}
			public void onFailure(Throwable caught)
			{
				System.out.println("Error :: Failed to draw spots " +
						"(uiSpotLocs.srvDrawSpots) :: " + caught.toString());
			}
		};
		//call the asynchronous service to get the list of spots
		//that are associated with the given lot.
		lsA.getSpotInfoForView(vname, cb);
	}
	
	/*==============================================================
	 * srvGetViewImage - updates the image of the view
	 * Input: lot name (String)
	 * Output: updates the view image on the UI
	 * Return: None
	 * Accesses the server, sending in the lot name, and gets the
	 * URL of the image to display in the UI
	 *==============================================================*/
	public void srvGetViewImage(String lotNameForViews)
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
				String[] res = (String[]) result;
				if(res.length != 0)
				{
					//set the label showing the view name
					lblViewName.setText(res[viewCtr % res.length]);
					//load the image of the view
					curView = res[viewCtr % res.length];
					srvLoadImage(curView);
					//srvDrawSpots(res[viewCtr % res.length]);
				}
				//system logging
				if(info){System.out.println("uiSpotLocs - updated views");}
			}
			public void onFailure(Throwable caught)
			{
				//system logging, 
				System.out.println("Error :: Failed to get views " +
						"(uiSpotLocs.srvGetViewListing) :: " + caught.toString());
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results
		lsA.getViews(lotNameForViews, cb);
	}
	
	/*==============================================================
	 * srvLoadImage - loads the image of the view onto the UI
	 * Input: view name (string)
	 * Output: displays the image on the UI
	 * Return: None
	 * Accesses the server, sending in the view name, and gets the 
	 * image URL, displaying it on the UI
	 *==============================================================*/
	public void srvLoadImage(String vName)
	{
		final String viewName = vName;
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
				//set the image to the image that goes
				//with the view.
				viewImg.setUrl((String)result + "?variable=" + System.currentTimeMillis());
				//viewImg.setUrl("C:/xampp/tomcat/webapps/thesisApp/" + (String)result + "?variable=" + System.currentTimeMillis());
				if(!((String)result).equals(""))
				{
					viewImg.setVisible(true);
				}
				//draw spots associated with the view
				srvDrawSpots(viewName);
			}
			public void onFailure(Throwable caught)
			{
				System.out.println("Error :: Failed to load image " +
						"(uiSpotLocs.srvLoadImage) :: " + caught.toString());
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results - add the spot
		if(dbg){System.out.println("-#-#-#-#-> " + vName);}
		lsA.getViewImage(vName,cb);
	}
	
	/*==============================================================
	 * A click listener to listen for clicks on the forward
	 * and backward buttons
	 *==============================================================*/
	private class uiSLClkListener implements ClickListener
	{
		//onClick - waits for a click
		public void onClick(Widget sender) 
		{
			//if it is the forward button
			if(sender.equals(btnForward))
			{
				//increment the view counter
				viewCtr++;
				//refresh the components
				refreshComponents();
				if(dbg){System.out.println("**- viewCtr - " + viewCtr);}
			}
			//if it is the backward button
			if(sender.equals(btnBackward))
			{
				//decrement the view counter
				viewCtr--;
				refreshComponents();
				if(dbg){System.out.println("**- viewCtr - " + viewCtr);}
			}
		}
	}

	public void onError(Widget sender) 
	{
		if(dbg){System.out.println("Image Load Error");}
		
	}
	public void onLoad(Widget sender) 
	{
		if(dbg){System.out.println("Image Load Event");}
		//srvDrawSpots(curView);	
	}
}
