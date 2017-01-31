package com.ryanluedders.plmonitor.server;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Timer;

/*=====================================================================
 * statTender - This is a class that can be created to take stats about
 * the system with a given interval between when the stats are taken.
 * It works by creating a timer for that interval, and an action to
 * perform when the timer runs out. That action will be whatever
 * action is necessary to take stats.
 *=====================================================================*/
public class StatTender 
{
	public static boolean dbg = false;
	public static boolean info = false;
	
	//an action to perform when the timer hits its
	//specified interval. this is used to take statistics of
	//the system on a regular interval
	Action updateStats = new AbstractAction()
	{

		//the action to take
		public void actionPerformed(ActionEvent e) 
		{
			System.out.println("Taking Stats");
			//get the lots from the server
			String[] lots = ServerMethods.statGetLots();
			for(int i=0; i<lots.length; i++)
			{
				int openSpots = 0;
				int takenSpots = 0;
				openSpots = ServerMethods.statGetOpenSpots(lots[i]);
				takenSpots = ServerMethods.statGetSpotsForLot(lots[i]).length - openSpots;
				
				ServerMethods.addStat(System.currentTimeMillis(),
						lots[i], openSpots, takenSpots);
			}
			if(info){System.out.println("Updating Stats");}
		}
	};
	
	//A timer to keep the action happening over an interval
	Timer statTimer;
	
	/*==============================================================
	 * Default Constructor 
	 * Input: interval-milliseconds (int)
	 * Output: None
	 * Return: None
	 * The constructor takes an interval and constructs the timer
	 * for that interval - it doesn't start it, however.
	 *==============================================================*/
	public StatTender(int interval)
	{
		//Construct the timer, for the given interval
		statTimer = new Timer(interval, updateStats);
	}
	
	/*==============================================================
	 * Start
	 * Input: None
	 * Output: Starts the timer
	 * Return: None
	 * This starts the timer
	 *==============================================================*/
	public void start()
	{
		//Start the timer
		statTimer.start();
	}
}
