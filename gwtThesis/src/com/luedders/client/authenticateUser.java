package com.luedders.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/*=====================================================================
 * authenticate user extends DialogBox, because it is designed as a box
 * that can be shown from within the application, to prompt the user for
 * a username and password.
 *=====================================================================*/
public class authenticateUser extends DialogBox
{
	//the panel to contain all other panels
	VerticalPanel contentPan = new VerticalPanel();
	
	//label, textbox to ask for & enter username
	HorizontalPanel panUName = new HorizontalPanel();
	Label lblEnterUName = new Label("Username: ");
	TextBox txtEnterUName = new TextBox();
	
	//label, textbox to ask for & enter password
	HorizontalPanel panPWord = new HorizontalPanel();
	Label lblEnterPWord = new Label("Password: ");
	PasswordTextBox txtEnterPWord = new PasswordTextBox();
	
	//panel holding the submit & cancel buttons
	HorizontalPanel panButtons = new HorizontalPanel();
	public Button btnSubmit = new Button();
	public Button btnCancel = new Button();
	
	/*==============================================================
	 * Default Constructor
	 * Input: None
	 * Output: None
	 * Return: None
	 * Constructs the dialog box, initializes all components, and
	 * places all components, by calling class methods.
	 *==============================================================*/
	public authenticateUser()
	{
		//create a new dialog box, that will not hide when clicking
		//outside of it
		super(false,false);
		//initialize all components, setting text values
		initComponents();
		//place components within various panels.
		placeComponents();
	}
	
	/*==============================================================
	 * initComponents - initialize all the components
	 * Input: None
	 * Output: initializes all UI components
	 * Return: None
	 * Sets the style of the popup box, and sets the text of the
	 * labels and the buttons
	 *==============================================================*/
	public void initComponents()
	{
		//set the css style of the panel
		this.setStylePrimaryName("dlgGetName");
		//set the title of the panel
		this.setText("Enter Username/Password");
		
		//set the size of the textbox
		txtEnterUName.setMaxLength(20);
		//set the text of the buttons
		btnSubmit.setText("Submit");
		btnCancel.setText("Cancel");
	}
	
	/*==============================================================
	 * initComponents - clears the current values, then hides
	 * Input: None
	 * Output: clears textbox (username, password) values
	 * Return: None
	 * clears the textbox values, and then hides the panel
	 *==============================================================*/
	public void hide()
	{
		//clear textbox values
		txtEnterUName.setText("");
		txtEnterPWord.setText("");
		//hides panel
		super.hide();
	}
	
	/*==============================================================
	 * placeComponents - places components in the main panel
	 * Input: None
	 * Output: places components
	 * Return: None
	 * Uses panels to arrange and place all necessary components
	 *==============================================================*/
	public void placeComponents()
	{
		//username panel, set to take up whole width of panel
		panUName.setWidth("100%");
		//add the label
		panUName.add(lblEnterUName);
		//add the textbox
		panUName.add(txtEnterUName);
		//split the panel 35/65 for the label/textbox
		panUName.setCellWidth(lblEnterUName, "35%");
		panUName.setCellWidth(txtEnterUName, "65%");
		
		//password panel, set to take up whole width of panel
		panPWord.setWidth("100%");
		//add the label
		panPWord.add(lblEnterPWord);
		//add the textbox
		panPWord.add(txtEnterPWord);
		//split the panel 35/65 for the label/textbox
		panPWord.setCellWidth(lblEnterPWord, "35%");
		panPWord.setCellWidth(txtEnterPWord, "65%");
		
		//button panel, set to take up whole width of panel
		panButtons.setWidth("100%");
		//button to submit the value, should be listened to
		//by the parent panel
		panButtons.add(btnSubmit);
		//button to cancel value, also listened to by parent panel
		panButtons.add(btnCancel);
		
		//to the main panel, add the username, password, and button
		//panels
		contentPan.add(panUName);
		contentPan.add(panPWord);
		contentPan.add(panButtons);
		
		//add the content panel to the popup widget.
		this.setWidget(contentPan);
	}
}
