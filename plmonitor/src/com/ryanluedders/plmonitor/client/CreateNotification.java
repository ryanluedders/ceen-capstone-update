package com.ryanluedders.plmonitor.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.datepicker.client.DatePicker;

/*=====================================================================
 * createNotification extends DialogBox, it will be a box that can
 * be shown (as a popup, of sorts) from any panel in the program, but
 * it will most likely only be shown from the site overview panel. It
 * will pop up and have all the fields required to set up a new SMS
 * (or email) notification, and will listen to its own buttons and
 * handle all of its own queries.
 *=====================================================================*/
public class CreateNotification extends DialogBox implements ClickListener, PopupListener, ChangeListener
{
	//boolean values to determine whether debug or program
	//info logging outputs are enabled.
	public static boolean dbg = false;
	public static boolean info = false;
	
	//the content panel of this popup/dialogBox
	VerticalPanel contentPan = new VerticalPanel();

	//horizontally, the mobile number label, textbox, and example format
	HorizontalPanel mobNumPan = new HorizontalPanel();
	Label lblMobNum = new Label("Mobile No. ");
	TextBox txtMobNum = new TextBox();
	Label lblMobEx = new Label("ex. 4028675309");

	//horizontally, the provider label, and list to choose from
	HorizontalPanel providerPan = new HorizontalPanel();
	Label lblMobProv = new Label("Provider      ");
	ListBox lstProvider = new ListBox();

	//horizontally, the lot name label, and list. The threshold for
	//the number of spots, and a textbox to enter it.
	HorizontalPanel lotNamePan = new HorizontalPanel();
	Label lblLotName = new Label("Lot Name   ");
	ListBox lstLotNames = new ListBox();
	Label lblNumSpots = new Label("No. of Spots (0 if any number)");
	TextBox txtNumSpots = new TextBox();

	//horizontally, the date, hour/min, and am/pm to notify fields
	HorizontalPanel timeToNotifyPan = new HorizontalPanel();
	Label lblTime = new Label("Time To Notify");
	TextBox txtTimeToNotify = new TextBox();
	TextBox txtHourMinToNotify = new TextBox();
	ListBox lstAmPmToNotify = new ListBox();

	//horizontally, the times to recur label & textbox & example, as
	//well as the interval label & textbox
	HorizontalPanel timeToRecurPan = new HorizontalPanel();
	Label lblTimeToRecur = new Label("Times to Recur");
	TextBox txtTimesToRecur = new TextBox();
	Label lblExTimes = new Label("(0-10)");
	Label lblInterval = new Label("Interval (Minutes)");
	TextBox txtInterval = new TextBox();

	//horizontally, buttons to enter the notification, cancel the
	//notification, and delete all notifications for a mobile number
	HorizontalPanel buttonPan = new HorizontalPanel();
	Button btnEnterNew = new Button();
	Button btnCancel = new Button();
	Button btnDeleteForMobile = new Button();

	//A date picking popup panel, that will pop up with the textbox
	//to choose the date is clicked on, and then when the date is changed
	//on the date popup, will place a new date into said textbox.
	DatePicker datePick = new DatePicker();

	//variables to keep track of when the chosen date actually changes.
	int curMonth = 0;
	int curYear = 0;
	int curDay = 0;

	/*==============================================================
	 * Default Constructor
	 * Input: None
	 * Output: None
	 * Return: None
	 * Constructs the dialog box that will not close when someone 
	 * clicks outside of it, initializes all the components, and
	 * then places them, and sets it visible.
	 *==============================================================*/
	public CreateNotification()
	{
		//create the dialog box
		super(false,false);
		//center it
		this.center();
		//init & place components
		initComponents();
		placeComponents();
		//give the dialog a title
		this.setText("Create A Notification");
		this.setVisible(true);
	}
	
	/*==============================================================
	 * initComponents() - Initialize all the components
	 * Input: None
	 * Output: Initializes the components
	 * Return: None
	 * Will set sizes and contents of labels, textboxes, lists,
	 * etc, that will be placed onto the create notification 
	 * dialog box.
	 *==============================================================*/
	public void initComponents()
	{		
		//set the length of the mobile number textbox
		txtMobNum.setMaxLength(10);
		txtMobNum.setWidth("25ex");

		//set the length of the number of spots to require
		txtNumSpots.setMaxLength(2);
		txtNumSpots.setWidth("12ex");

		//a textbox to collect a date the user wants to be notified
		txtTimeToNotify.setStylePrimaryName("gwtThesis-greyBackground");
		txtTimeToNotify.setWidth("20ex");
		txtTimeToNotify.addClickListener(this);

		//a textbox to collect the time on said to to be notified
		txtHourMinToNotify.setStylePrimaryName("gwtThesis-greyBackground");
		txtHourMinToNotify.setWidth("15ex");
		//time should be entered with a colon in the middle
		txtHourMinToNotify.setText("00:00");

		//a list to choose either AM or PM for the hours
		lstAmPmToNotify.setStylePrimaryName("gwtThesis-greyBackground");
		lstAmPmToNotify.addItem("AM");
		lstAmPmToNotify.addItem("PM");

		//a textbox to allow the user to specify the number of times
		//the notification is supposed to recur
		txtTimesToRecur.setMaxLength(2);
		txtTimesToRecur.setWidth("12ex");

		//a textbox to allow the user to specify the interval at which
		//the notification will occur
		txtInterval.setMaxLength(2);
		txtInterval.setWidth("12ex");

		//set the text of the buttons
		btnEnterNew.setText("Enter New Notification");
		btnCancel.setText("Cancel Notification");
		btnDeleteForMobile.setText("Delete for Mobile Number");

		//set the dialog box to be it's own click listener, so it can
		//perform the appropriate actions upon button clicks
		btnEnterNew.addClickListener(this);
		btnCancel.addClickListener(this);
		btnDeleteForMobile.addClickListener(this);

		//set the style of the dialog box
		this.setStylePrimaryName("dlgGetName");

		//set the style of the date picker
		datePick.setStylePrimaryName("gwtThesis-calendarPicker");
		//make this dialog box listen for actions on the popup (datepicker)
		datePick.addPopupListener(this);
		datePick.showTodayButton(false);
		datePick.addChangeListener(this);
	}
	
	/*==============================================================
	 * placeComponents() - uses a series of panels and alignments to
	 * place the components in the appropriate places.
	 * Input: None
	 * Output: places the components
	 * Return: None
	 * uses panels and alignments to set the location of the various
	 * widgets on the dialog box
	 *==============================================================*/
	public void placeComponents()
	{
		//a panel to hold the label, textbox, and example for
		//the user to enter their mobile number
		mobNumPan.add(lblMobNum);
		//set the style of the textbox
		txtMobNum.setStylePrimaryName("gwtThesis-greyBackground");
		mobNumPan.add(txtMobNum);
		mobNumPan.add(lblMobEx);
		mobNumPan.setWidth("100%");
		//split the components 20/30/50 for lbl, textbox, and ex lbl
		mobNumPan.setCellWidth(lblMobNum,"20%");
		mobNumPan.setCellWidth(txtMobNum,"30%");
		mobNumPan.setCellWidth(lblMobEx, "50%");

		//a panel to hold the label, and listbox for providers
		providerPan.add(lblMobProv);
		//set the style of the listbox
		lstProvider.setStylePrimaryName("gwtThesis-greyBackground");
		providerPan.add(lstProvider);
		providerPan.setWidth("100%");
		//split the components 20/80 for the label, listbox
		providerPan.setCellWidth(lblMobProv, "20%");
		providerPan.setCellWidth(lstProvider, "80%");

		//a panel to hold the lot name panel, and listbox, as well
		//as the minimum available number available for a notification
		//to occur.
		lotNamePan.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		lotNamePan.add(lblLotName);
		//set style of provider listbox
		lstLotNames.setStylePrimaryName("gwtThesis-greyBackground");
		lotNamePan.add(lstLotNames);
		lotNamePan.add(lblNumSpots);
		//set style of textbox
		txtNumSpots.setStylePrimaryName("gwtThesis-greyBackground");
		lotNamePan.add(txtNumSpots);
		lotNamePan.setWidth("100%");
		//split the components 20/30/35/15, horizontally.
		lotNamePan.setCellWidth(lblLotName, "20%");
		lotNamePan.setCellWidth(lstLotNames, "30%");
		lotNamePan.setCellWidth(lblNumSpots, "35%");
		lotNamePan.setCellWidth(txtNumSpots, "15%");

		//a panel to hold the time label, date, and hour/min, and am/pm
		//specifications for the users to be notified of.
		timeToNotifyPan.add(lblTime);
		timeToNotifyPan.add(txtTimeToNotify);
		timeToNotifyPan.add(txtHourMinToNotify);
		timeToNotifyPan.add(lstAmPmToNotify);
		timeToNotifyPan.setWidth("100%");
		timeToNotifyPan.setCellWidth(lblTime, "20%");
		timeToNotifyPan.setCellWidth(txtTimeToNotify, "25");
		timeToNotifyPan.setCellWidth(txtHourMinToNotify, "20%");
		timeToNotifyPan.setCellWidth(lstAmPmToNotify, "35%");

		//a panel to hold the label, textbox, example for number of 
		//times to recur, and the label, textbox for # of times to recur
		timeToRecurPan.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		timeToRecurPan.add(lblTimeToRecur);
		txtTimesToRecur.setStylePrimaryName("gwtThesis-greyBackground");
		timeToRecurPan.add(txtTimesToRecur);
		timeToRecurPan.add(lblExTimes);
		timeToRecurPan.add(lblInterval);
		txtInterval.setStylePrimaryName("gwtThesis-greyBackground");
		timeToRecurPan.add(txtInterval);
		timeToRecurPan.setWidth("100%");
		//split the panel 20/15/15/35/15 percents
		timeToRecurPan.setCellWidth(lblTimeToRecur, "20%");
		timeToRecurPan.setCellWidth(txtTimesToRecur,"15%");
		timeToRecurPan.setCellWidth(lblExTimes, "15%");
		timeToRecurPan.setCellWidth(lblInterval, "35%");
		timeToRecurPan.setCellWidth(txtInterval, "15%");

		//set the styles of the buttons to the css style
		btnEnterNew.setStylePrimaryName("gwtThesis-borderedButton");
		btnCancel.setStylePrimaryName("gwtThesis-borderedButton");
		btnDeleteForMobile.setStylePrimaryName("gwtThesis-borderedButton");
		//a panel to hold the buttons to enter, cancel & delete
		buttonPan.add(btnEnterNew);
		buttonPan.add(btnCancel);
		buttonPan.add(btnDeleteForMobile);
		buttonPan.setWidth("100%");
		//space the buttons evenly across the panel
		buttonPan.setCellWidth(btnEnterNew, "33%");
		buttonPan.setCellWidth(btnCancel, "33%");
		buttonPan.setCellWidth(btnDeleteForMobile, "33%");

		//add all the panels to the content panel
		contentPan.add(mobNumPan);
		contentPan.add(providerPan);
		contentPan.add(lotNamePan);
		contentPan.add(timeToNotifyPan);
		contentPan.add(timeToRecurPan);
		contentPan.add(buttonPan);

		//set the content panel to show up in the dialog box
		this.setWidget(contentPan);

	}

	/*==============================================================
	 * setVisible - "improves" the existing setVisible
	 * Input: visible (boolean)
	 * Output: changes visibilty of box
	 * Return: None
	 * clears values on the popup box, gets providers, gets lots
	 * and sets the dialog box visible
	 *==============================================================*/
	public void setVisible(boolean vis)
	{	
		//clear values on the dialog box
		clearValues();
		//get service providers from the DB
		srvGetProviders();
		//get lots from the DB
		srvGetLots();
		//set visible or not visible
		super.setVisible(vis);
	}

	/*==============================================================
	 * clearValues - clears values on box
	 * Input: none
	 * Output: clears user modifiable values.
	 * Return: None
	 * clear selection on lists, clear text in textboxes.
	 *==============================================================*/
	public void clearValues()
	{
		//clear mobile number textbox
		txtMobNum.setText("");
		//no provider or lot name selection
		lstProvider.setSelectedIndex(0);
		lstLotNames.setSelectedIndex(0);
		//clear the number of spots
		txtNumSpots.setText("");
		//clear the time (date) to notify
		txtTimeToNotify.setText("");
		//clear the hour:minute to notify
		txtHourMinToNotify.setText("00:00");
		//no selection on AM/PM
		lstAmPmToNotify.setSelectedIndex(0);
		//clear the # of times to recur
		txtTimesToRecur.setText("");
		//clear the minute interval
		txtInterval.setText("");
	}

	/*==============================================================
	 * onClick - handles any click events for this dialog box
	 * Input: sending Widget (Widget)
	 * Output: performs actions requested by buttons on dialog box
	 * Return: None
	 * perform button actions
	 *==============================================================*/
	public void onClick(Widget src) 
	{
		//if the user clicks the "enter new notification" button
		if(src.equals(btnEnterNew))
		{
			//get the date they picked from the datepanel popup
			Date x = datePick.selectedDate();
			//get the hours from the hour:minute textbox
			x.setHours(Integer.parseInt(txtHourMinToNotify.getText().replaceAll(":", "")) / 100);
			if(lstAmPmToNotify.getItemText(lstAmPmToNotify.getSelectedIndex()).compareTo("PM") == 0)
			{
				//if it is PM, add 12 hours to it
				x.setHours(x.getHours() + 12);
			}
			//get the minutes from the hour:minute textbox
			x.setMinutes(Integer.parseInt(txtHourMinToNotify.getText().replaceAll(":", "")) % 100);
			
			//to enable a default value
			int numSpots = 0;
			int interval = 1;
			
			//if the user entered a value, don't use the default
			if(txtNumSpots.getText().compareTo("") != 0)
				numSpots = Integer.parseInt(txtNumSpots.getText());
			if(txtInterval.getText().compareTo("") != 0)
				interval = Integer.parseInt(txtInterval.getText());
			
			//call the server to add the new notification
			srvAddNotification(txtMobNum.getText(),lstProvider.getItemText(lstProvider.getSelectedIndex()),
					lstLotNames.getItemText(lstLotNames.getSelectedIndex()), numSpots,
					x.getTime(),Integer.parseInt(txtTimesToRecur.getText()), interval);
			//hide the create notification box
			setVisible(false);
		}
		//if the user clicks the cancel button
		if(src.equals(btnCancel))
		{
			//hide the create notification box
			setVisible(false);
		}
		//if the user clicks the button to delete all notifications
		//set up for a mobile number
		if(src.equals(btnDeleteForMobile))
		{
			//call the server to delete all notifications for the
			//entered mobile number.
			srvDelNotification(txtMobNum.getText());
			//clear the values on the create notification box
			clearValues();
			//and hide the box
			setVisible(false);
		}
		//if they click on the date portion of the time to notify
		if(src.equals(txtTimeToNotify))
		{
			//set the date picker popup to pop up right in the textbox
			datePick.setPopupPosition((txtTimeToNotify.getAbsoluteLeft() + 1), (txtTimeToNotify.getAbsoluteTop() - 1));
			datePick.setVisible(true);
			//show the date picker
			datePick.show();
			//set the current month, day, year to keep track of when the user has
			//picked a date
			curMonth = datePick.selectedDate().getMonth();
			curYear = datePick.selectedDate().getYear();
			curDay = datePick.selectedDate().getDay();
		}

	}

	/*==============================================================
	 * onPopupClosed - listens for popup boxes closing
	 * Input: Panel (PopupPanel)
	 * Output: performs an action when the popup is closed
	 * Return: None
	 * Listens for popup closing actions from popups.
	 *==============================================================*/
	public void onPopupClosed(PopupPanel src, boolean arg1) 
	{
		//if it is the date picking popup
		if(src.equals(datePick))
		{
			if(dbg){System.out.println(datePick.selectedDate());}
			//when the popup is closed, set the contents of the textbox
			//to show the day, month, and year that is selected.
			txtTimeToNotify.setText(toTxt(datePick.selectedDate()));
		}

	}

	/*==============================================================
	 * onChange - listens for changes in Widgets
	 * Input: source widget (Widget)
	 * Output: performs an action on change
	 * Return: None
	 * Listens for changes, and performs an action on change of a
	 * widget
	 *==============================================================*/
	public void onChange(Widget src) 
	{
		//if the change comes from the date picker
		if(src.equals(datePick))
		{
			//if they change either the date or year, they are
			//probably not done yet
			if(curMonth != datePick.selectedDate().getMonth() ||
					curYear != datePick.selectedDate().getYear())
			{
				//so keep track of it
				curMonth = datePick.selectedDate().getMonth();
				curYear = datePick.selectedDate().getYear();
			}
			//else, they choose a date number
			else
			{
				//and hide the date picker widget
				datePick.setVisible(false);
				datePick.hide();
			}

		}
	}

	/*==============================================================
	 * txTxt - takes a date object, returns a string
	 * Input: A date (Date)
	 * Output: none
	 * Return: A String showing the day, month & year of the date
	 * Takes the date object, and returns a string, showing the day,
	 * month and year of the Date object.
	 *==============================================================*/
	public String toTxt(Date x)
	{
		String rval = "";

		//the case of the month
		switch(x.getMonth())
		{
		//set the appropriate string
		case 0: rval+= "Jan "; break;
		case 1: rval+= "Feb "; break;
		case 2: rval+= "Mar "; break;
		case 3: rval+= "Apr "; break;
		case 4: rval+= "May "; break;
		case 5: rval+= "Jun "; break;
		case 6: rval+= "Jul "; break;
		case 7: rval+= "Aug "; break;
		case 8: rval+= "Sep "; break;
		case 9: rval+= "Oct "; break;
		case 10: rval+= "Nov "; break;
		case 11: rval+= "Dec "; break;
		}

		//set the date
		rval += (Integer.toString(x.getDate()) + " ");
		rval += (Integer.toString(x.getYear() + 1900));

		return rval;
	}

	/*==============================================================
	 * srvAddNotification - adds a new sms notification to the system
	 * Input: mobile number (String) provider (String) lot name (String)
	 * notification level (int) time to notify (long) times to recur (int)
	 * interval at which to recur (long)
	 * Output: tells the system to send an SMS
	 * Return: none
	 * Adds a new notification to the dateabase, so the system can
	 * notify the user of the requested information
	 *==============================================================*/
	public void srvAddNotification(String mobNum, String prov, String lname, int lvl, 
			long time, int recurTimes, long recurInterval)
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
				//success message
				if(dbg){System.out.println("Added SMS Notification");}
			}
			public void onFailure(Throwable caught)
			{
				System.out.println("Error :: Failed to Add New SMS " +
						"Notification (createNotification.srvAddNotification)");
			}
		};
		//call the asynchronous service to get all of the lots
		//that are associated with the current site
		lsA.addNotification(mobNum, prov, lname, lvl, time, recurTimes, recurInterval, cb);
	}
	
	/*==============================================================
	 * srvDelNotification - deletes notifications to a specified mobile #
	 * Input: mobile number (String)
	 * Output: removes notification requests from the system.
	 * Return: none
	 * Takes a mobile number form the UI, and accesses the database to
	 * remove any notifications that are setup for that mobile number.
	 *==============================================================*/
	public void srvDelNotification(String mobNum)
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
				//success notification
				if(dbg){System.out.println("Deleted SMS Notifications");}
			}
			public void onFailure(Throwable caught)
			{
				System.out.println("Error :: Could not Delete Notifications " +
						"(createNotification.srvDelNotification)");
			}
		};
		//call the asynchronous service to get all of the lots
		//that are associated with the current site
		lsA.deleteNotsForMobile(mobNum, cb);
	}
	
	/*==============================================================
	 * srvGetProviders - gets supported mobile providers
	 * Input: none
	 * Output: populates the provider list from the DB
	 * Return: none
	 * Accesses the server to get a listing of the providers
	 * that are available to be setup to send notifications to
	 *==============================================================*/

	public void srvGetProviders()
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
				//clear the current list
				lstProvider.clear();
				//go through the result string array
				for(int i=0; i<res.length; i++)
				{
					//add each provider to the list
					lstProvider.addItem(res[i]);
				}
			}
			public void onFailure(Throwable caught)
			{
				System.out.println("Error :: Could not retrieve" +
						"sms providers (createNotification.srvGetProviders)");
			}
		};
		//call the asynchronous service to get all of the lots
		//that are associated with the current site
		lsA.getProviders(cb);
	}
	
	/*==============================================================
	 * srvGetLots - gets list of available lots
	 * Input: none
	 * Output: populates the lot list from the db
	 * Return: none
	 * Accesses the server to get the list of lots available to
	 * get notifications for , and puts them into a drop down list
	 *==============================================================*/
	public void srvGetLots()
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
				//clear the current list
				lstLotNames.clear();
				//ad an "Any" choice, that is used to get a notification
				//for the most open lot.
				lstLotNames.addItem("Any");
				for(int i=0; i<res.length; i++)
				{
					//add them all to the list
					lstLotNames.addItem(res[i]);
				}
			}
			public void onFailure(Throwable caught)
			{
				System.out.println("Error :: Could not get lots" +
						" (createNotification.srvGetLots)");
			}
		};
		//call the asynchronous service to get all of the lots
		//that are associated with the current site
		lsA.getLots(cb);
	}
}
