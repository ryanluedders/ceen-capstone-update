package com.ryanluedders.plmonitor.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;

import javax.swing.text.NumberFormatter;

/*=====================================================================
 * exportMobilePage
 * A class used to export a page that is compatible with browser on
 * mobile phones. this page will show all of the lots at the site, and
 * the percentage full that they are
 *=====================================================================*/
public class ExportMobilePage 
{
	//calling function is notificationTender (on action)
	final static String HTML_PATH = "c:/xampp/tomcat/webApps/thesisApp/mobile.html";
	
	//call this fuction to create the mobile page, it takes no arguments and
	//returns nothing, but it queries the database and outputs and html file
	//to the directory structure
	public static void createMobilePage()
	{
		//new string, to be written in the near future
		String towrite = "";
		//an array to hold the lots
		String[] lotz = ServerMethods.statGetLots();
		//an array to hold the number open
		int[] numOpenz = new int[lotz.length];
		//an array to hold the total number
		int[] numTot = new int[lotz.length];
		//go through and get the number open & total number
		for(int i=0; i<lotz.length; i++)
		{
			numOpenz[i] = ServerMethods.statGetOpenSpots(lotz[i]);
			numTot[i] = ServerMethods.statGetSpotsForLot(lotz[i]).length;
		}
		//add the standard welcome message
		towrite = "============<br>" +
		          "Welcome To<br>" +
		          "Parking Lot<br>" +
		          "Monitoring<br>" +
		          "============<br>" +
		          "Current Avail:<br>";
		
		//format the number as a percentage
		NumberFormat nf = NumberFormat.getPercentInstance();
		//with no decimal point
		nf.setMaximumFractionDigits(0);
		
		//output each lot & its availability to the mobile page
		for(int i=0; i<lotz.length; i++)
		{
			towrite += "[" + lotz[i] + " " + nf.format(((double)numOpenz[i] / (double)numTot[i]))+ " ]<br>";
		}
		
		//write the mobile page
		writeHTMLFile(towrite);
	}
	
	//this method takes care of writing the file to the directory
	//structure.
	public static void writeHTMLFile(String fileContents)
	{
		//the file to write out to
		File outFile = new File(HTML_PATH);
		
		try
		{
			//if it doesn't exist, make a new one
			if(!outFile.exists())
				outFile.createNewFile();

			//write it
			FileWriter fw = new FileWriter(outFile);
			fw.write(fileContents);
			
			//close it!
			fw.close();
		}
		catch(IOException e)
		{
			System.out.println("Error :: Couldn't write mobile html file");
		}
	}
}
