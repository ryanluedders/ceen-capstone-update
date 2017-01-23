package com.luedders.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;

/*=====================================================================
 * srvAccessor
 * This is a base class for all of the panels that my system uses
 * that includes all of the basic components and functions that are 
 * found common through the various panels
 *=====================================================================*/
public class srvAccessor extends DockPanel
{
	//The label with the current system info - site name
	//will be in upper left corner
	public Label lblSysInfo = new Label();
	//The label with the current time
	//will be in upper right corner
	public Label lblCurTime = new Label();
	
	/*==============================================================
	 * Default Constructor
	 * Input: None
	 * Output: None
	 * Return: None
	 * Constructs a dockPanel, and calls the server to get the current
	 * system time and current system info
	 *==============================================================*/
	public srvAccessor()
	{
		//call the parent constructor
		super();
		//get the current system time from the server
		srvSysTime();
		//get the current system info from the server
		srvSysInfo();
	}
	
	/*==============================================================
	 * setSiteName
	 * Input: site name (String)
	 * Output: None
	 * Return: None
	 * Takes a string that is the site name, and sets the label
	 * to tell the user the information
	 *==============================================================*/
	public void setSiteName(String sitename)
	{
		lblSysInfo.setText(sitename);
	}
	
	/*==============================================================
	 * setCurTime
	 * Input: current time (String)
	 * Output: None
	 * Return: None
	 * Takes a string that is the current time, and sets the 
	 * appropriate label to tell the user.
	 *==============================================================*/
	public void setCurTime(String curTime)
	{
		lblCurTime.setText(curTime);
	}
	
	/*==============================================================
	 * srvSysInfo
	 * Input: None
	 * Output: None
	 * Return: None
	 * Creates an asynchronous service, and calls the server to get
	 * the system info. Then after receiving the information, it
	 * calls the appropriate function to set the information, unless there
	 * is an error, then it prints the error to the system.
	 *==============================================================*/
	public void srvSysInfo()
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
				//set the site name label to show the site name
				setSiteName(result.toString());
			}
			public void onFailure(Throwable caught)
			{
				//otherwise use the label to tell the server call
				//was unsuccessful
				System.out.println("Error :: (srvAccessor.srvSysInfo :: " + 
						caught.toString());
				setSiteName("Failed to Get Site Name");
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results
		lsA.getSiteName(cb);
	}
	
	/*==============================================================
	 * srvSysTime
	 * Input: None
	 * Output: None
	 * Return: None
	 * Creates an asynchronous service, and calls the server to get
	 * the system info. Then after receiving the information, it
	 * calls the appropriate function to set the information, unless there
	 * is an error, then it prints the error to the system.
	 *==============================================================*/
	public void srvSysTime()
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
				//Set the current time label to show the current time
				setCurTime(result.toString());
			}
			public void onFailure(Throwable caught)
			{
				//otherwise use the label to show the server call
				//was unsuccessful
				System.out.println("Error :: (srvAccessor.srvSysTime :: " + 
						caught.toString());
			}
		};
		//call the asynchronous service, providing the callback to
		//handle the results
		lsA.getSysTime(cb);
	}
}