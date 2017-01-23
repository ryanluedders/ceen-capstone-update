package com.luedders.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/*=====================================================================
 * dlgGetName
 * extends- DialogBox
 * This is a custom dialog box that is shown whenever the program
 * needs user input as far as what to add. So, whenever a spot or view,
 * or lot is added to the database, this dialog box is used to get the
 * name from the user. It has a label, with instructions, a text box
 * where the user can input data, an OK button and a Cancel Button
 *=====================================================================*/
public class dlgGetName extends DialogBox
{
	//Label instructing the user what to do
	public Label lblGetName = new Label("Enter new name:");
	//Okay button for user to accept value
	public Button popLotNameOK = new Button();
	//Cancel button for user to cancel process
	public Button popLotNameCan = new Button();
	//Textbox in which the user can enter a value
	public TextBox txtGetLotName = new TextBox();

	//A Vertical & Horizontal Panel for helping organize components
	VerticalPanel dbVPanel = new VerticalPanel();
	HorizontalPanel dbHPanel = new HorizontalPanel();

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
	public dlgGetName(boolean a, boolean b, String txt)
	{
		//call the original constructor
		super(a,b);
		
		//set button text to "OK" and "Cancel"
		popLotNameOK.setText("OK");
		popLotNameCan.setText("Cancel");

		//Add the buttons to a horizontal panel
		dbHPanel.add(popLotNameOK);
		dbHPanel.add(popLotNameCan);
		//Set the text of the label
		this.setText(txt);
		
		//Add the textbox and button panel to a vertical panel
		dbVPanel.add(txtGetLotName);
		dbVPanel.add(dbHPanel);
		
		//Set the CSS style of the dialog box
		this.setStyleName("dlgGetName");
		
		//Set the widget contained within the dialogbox
		this.setWidget(dbVPanel);
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
		//clear the contents of the textbox
		txtGetLotName.setText("");
		//set the DB to visible
		this.setVisible(true);
		//show it using the normal show() function
		super.show();
		//center the dialog box
		this.center();
	}
}
