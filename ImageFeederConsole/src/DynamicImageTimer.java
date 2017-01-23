import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/*=====================================================================
 * DynamicImageTimer - A timer to change the image for a view
 * This is a class that incorporates a timer and a list of filenames
 * and durations to coordinate the changing of the image that is
 * supplied to the parking monitor system.
 *=====================================================================*/
public class DynamicImageTimer
{	
	//boolean values to enable info and dbg standard out
	public static boolean info = true;
	public static boolean dbg = false;

	//a viewInfo object to hold object for the view associated
	//with this timer. There should be a different timer for every view
	public ViewInfo vI = new ViewInfo();
	//timer to schedule all things related to this view
	public Timer renewTimer = new Timer();
	
	public Runtime rt = Runtime.getRuntime();

	//A timer to change the image, simulating a "live image feed"
	//of sorts
	public DynamicImageTimer(ViewInfo x)
	{
		//set the view info object
		vI = x;
		//total time keeps track of the time it will take
		//to go through all source images in a view, so the timer
		//can be run again, to keep going through all source images.
		long totaltime = 0;
		if(dbg){System.out.println(vI);}
		//go through all source images to get the duration it will
		//take for all of them to cycle through
		for(int i=0; i<vI.getSrcImages().size(); i++)
		{
			totaltime += vI.getSrcImages().elementAt(i).duration * 1000;
		}
		//make the new timer task to refresh the "view" timer, which cycles
		//through the entire view, scheduling all the sources to be shown
		refreshTimer rfT = new refreshTimer(vI);
		//schedule the "view" timer to run periodically using the total
		//duration determined above.
		renewTimer.schedule(rfT, 0, totaltime);
	}
	
	//this timer runs through the entire view, scheduling each source
	//image to be shown for the specified period of time.
	public class refreshTimer extends TimerTask
	{
		//a new view info object
		ViewInfo viewInf = new ViewInfo();
		
		//default constructor, given all the view info
		public refreshTimer(ViewInfo vInfo)
		{
			if(dbg){System.out.println("Refresh Timer Created");}
			//keep the view info accurate in this object
			viewInf = vInfo;
		}
		
		//this is the function that will run on the scheduled basis
		public void run() 
		{
			if(dbg){System.out.println("Refresh Timer Running");}
			//keep track of time relative to the beginning of showing
			//the view
			long runningTime = 0;
			//go through each source image
			for(int i=0; i<viewInf.getSrcImages().size(); i++)
			{
				//create a new timer to show the individual source image or "frame" in the feed
				switchImg sched = new switchImg(viewInf.inPath + viewInf.getSrcImages().elementAt(i).filename,viewInf.viewOut);
				//schedule above timer, using runningTime to keep the durations accurate
				renewTimer.schedule(sched, new Date(runningTime + System.currentTimeMillis()));
				//increment running time to keep the previously scheduled image visible
				//for the correct amount of time.
				runningTime += (1000 * viewInf.getSrcImages().elementAt(i).duration);
			}
		}
	}
	
	//this is the task that handles the switching of the source
	//images to the output images
	public class switchImg extends TimerTask
	{
		//source filename
		public String sFileName = "";
		//view filename (output)
		public String vFileName = "";
		
		//default constrctor is given a source file and an output file
		public switchImg(String srcFile, String viewFile)
		{
			if(dbg){System.out.println("switch image timer created");}
			//keep this object's filenames accurate
			sFileName = srcFile;
			vFileName = viewFile;
		}
		//this is the method that will be run each time the timer is
		//scheduled to run
		public void run() 
		{
			if(dbg){System.out.println("Switch Image Timer Running");}
			//get the file to be fed into the system
			File x = new File(sFileName);
			//create the new file for the view
			File y = new File(vFileName);
			//if the file currently exists, get rid of it
			if(y.exists())
				y.delete();
			try 
			{
				//create a new file, and copy the frame into it
				y.createNewFile();
				copyFile(x,y);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			catch(Exception e)
			{
				System.out.println("error copying");
			}
			//Print for system logging
			if(info){System.out.println(vFileName + " now showing src: " + sFileName);}
		}
	}

	/*==============================================================
	 * copyFile
	 * Input: two files (File)
	 * Output: copies first file to the second
	 * Return: none
	 * This method takes an input file and copies it to the output
	 * file.
	 *==============================================================*/
	public void copyFile(File in, File out) throws Exception 
	{
		String cmd = "cmd /c copy /y \"" + in.getCanonicalPath() + "\" \"" + out.getCanonicalPath() + "\"";
		//System.out.println(cmd);
		rt.exec(cmd);
		/*
		//input streams
		FileInputStream fis  = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		byte[] buf = new byte[1024];
		int i = 0;
		//copy them
		while((i=fis.read(buf))!=-1) {
			fos.write(buf, 0, i);
		}
		//close the streams
		fis.close();
		fos.close();
		*/
	}
}
