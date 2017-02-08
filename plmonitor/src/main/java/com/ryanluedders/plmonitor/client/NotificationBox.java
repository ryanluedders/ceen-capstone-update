package com.ryanluedders.plmonitor.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

/*=====================================================================
 * notificationBox
 * extends- DialogBox
 * This is a custom dialog box that is shown whenever the system needs
 * to alert the user of something. It is just a general purpose box
 * that can be constructed to show a simple string.
 *=====================================================================*/
public class NotificationBox extends DialogBox implements ClickListener
{
	//Okay button for user to accept value
	public Button popLotNameOK = new Button();


	/*==============================================================
	 * Default Constructor
	 * Input: Boolean autohide, Boolean modal
	 * Output: None
	 * Return: None
	 * This is the default constructor. The values passed in are to
	 * enable autohide, which will hide the dialogbox when the user
	 * clicks outside of it, and modal will set whether to ignore
	 * keyboard and mouse events outside the dialogBox This particular
	 * constructor sets the text of the buttons and the labels, as well
	 * as setting the CSS style for the background of the box, so it
	 * has a solid blue background, per the CSS file.
	 *==============================================================*/
	public NotificationBox(boolean a, boolean b, String txt)
	{
		//call the original constructor
		//super(a,b);
		super(true,b);
		
		//set button text to "OK" and "Cancel"
		//popLotNameOK.setText("OK");
		popLotNameOK.addClickListener(this);

		//Set the text of the label
		this.setText(txt);
		
		//Set the CSS style of the dialog box
		this.setStyleName("dlgGetName");
		
		//Set the widget contained within the dialogbox
		//this.setWidget(popLotNameOK);
		
		//center the DB on the screen
		this.center();
		
		//keep it not shown until it is desired
		this.setVisible(false);
	}
	
	/*==============================================================
	 * show()
	 * Input: None
	 * Output: None
	 * Return: None
	 * Modify the default show() function slightly, by clearing the
	 * contents of the textbox, setting the DB to be visible, showing
	 * it like normal, and then centering it.
	 *==============================================================*/
	public void show()
	{
		//set the DB to visible
		this.setVisible(true);
		//show it using the normal show() function
		super.show();
		//center the dialog box
		this.center();
	}

	/*==============================================================
	 * onClick()
	 * Input: Sender (Widget)
	 * Output: None
	 * Return: None
	 * Right now, just hides the dialog box when OK is clicked.
	 *==============================================================*/
	public void onClick(Widget sender) 
	{
		if(sender.equals(popLotNameOK))
		{
			this.hide();
		}
		
	}
}
