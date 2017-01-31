package com.ryanluedders.plmonitor.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/*=====================================================================
 * thesisApp
 * implements - EntryPoint
 * This is the class that is actually used to run the rest of the
 * system. because this class is placed into the browser to run the
 * script. From here, an appFrame is added and other panels are used.
 *=====================================================================*/
public class PLMonitor implements EntryPoint 
{
	//The frame to shuffle panels around
	public AppFrame app;

	/*==============================================================
	 * initComponents
	 * Input: None
	 * Output: None
	 * Return: None
	 * Initializes the appFrame
	 *==============================================================*/
	public void initComponents()
	{
		app = new AppFrame();
	}

	/*==============================================================
	 * onModuleLoad - the Entrypoint method
	 * Input: None
	 * Output: None
	 * Return: None
	 * calls the init function, and adds the appframe to the
	 * browser rootpanel
	 *==============================================================*/
	public void onModuleLoad() 
	{		
		initComponents();
		RootPanel.get().add(app);
	}
}
