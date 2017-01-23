import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFrame;

public class ImageFeederConsole
{
	//boolean values to enable info and debug to be sent to standard out
	public static boolean info = false;
	public static boolean dbg = false;
	
	//the main method, run when this is called from the console, taking
	//a text file as input, which specifies all of the images and timings
	public static void main(String args[])
	{
		//input file
		File inputFile = null;
		//input file reader
		FileReader inF = null;
		//buffered file reader
		BufferedReader bufR = null;

		//A vector of view infos to be read in
		Vector<ViewInfo> views = new Vector<ViewInfo>();
		
		//get input file as argument
		if(args.length <= 0)
		{
			System.out.println("Error :: No Input File Argument");
			//exit the program
			System.exit(1);
		}
		else
		{
			//use the command line argument as the filename
			inputFile = new File(args[0]);
		}

		//try and open the file for reading
		try
		{
			inF = new FileReader(inputFile);
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Error :: File Not Found");
			System.exit(1);
		}

		//open the bufferedreader on the file to read lines
		bufR = new BufferedReader(inF);

		//var to keep track of read
		String read = "";
		try
		{
			//the file should start with "startsite"
			if((read=bufR.readLine()).compareToIgnoreCase("startsite") == 0)
			{
				//while it has not hit "endsite"
				while((read=bufR.readLine()).compareToIgnoreCase("endsite") != 0)
				{
					//it should then see "startview"
					if(read.compareToIgnoreCase("startview") == 0)
					{
						//create a new viewinfo object
						ViewInfo vInfo = new ViewInfo();
						//create a new imageandduration vector
						Vector<ImageAndDuration> ins = new Vector<ImageAndDuration>();
						
						//get a line
						String rd = bufR.readLine();
						//the first line should be "view_name: ...."
						if(rd.startsWith("view_name:"))
						{
							//get rid of the "view_name:..." leaving the actual view name
							rd = rd.replaceAll("view_name: ", "");
							//get rid of any "
							vInfo.setViewName(rd.replace("\"", ""));
						}
						else
						{
							//have to follow the input rules!!!
							System.out.println("Error :: Bad Input File");
							System.exit(1);
						}
						
						//get another line
						rd = bufR.readLine();
						//next line should be "output_path: ..."
						if(rd.startsWith("output_path:"))
						{
							//get rid of the "output_path: ..." leaving the actual output path
							rd = rd.replaceAll("output_path: ","");
							//get rid of any "
							vInfo.setViewOut(rd.replace("\"", ""));
						}
						else
						{
							//Again, follow the rules!
							System.out.println("Error :: Bad Input File");
							System.exit(1);
						}
						
						//get another line
						rd = bufR.readLine();
						//next line should be "output_filename: ..."
						if(rd.startsWith("output_filename:"))
						{
							//get rid of "output_filename ..." leaving the actual output filename
							rd = rd.replaceAll("output_filename: ","");
							//get rid of any "
							vInfo.setViewOut(vInfo.getViewOut() + rd.replace("\"", ""));
						}
						else
						{
							//always, with the rules.
							System.out.println("Error :: Bad Input File");
							System.exit(1);
						}
						
						//another line !?
						rd = bufR.readLine();
						//should start with "input_path: ..."
						if(rd.startsWith("input_path:"))
						{
							//get rid of "input_path: ..." leaving only the input path
							rd = rd.replaceAll("input_path: ","");
							//get rid of any "
							vInfo.setInPath(rd.replace("\"", ""));
						}
						else
						{
							//too many rules...? deal with it.
							System.out.println("Error :: Bad Input File");
							System.exit(1);
						}	
						
						//go through while it has not hit "endview"
						while((read=bufR.readLine()).compareToIgnoreCase("endview") != 0)
						{
							//guess what "input_file: " signifies there will be an input file
							//directly following!
							if(read.startsWith("input_file:"))
							{
								//get rid of the "input_file:" leaving the actual filename
								read = read.replaceAll("input_file: ", "");
								//tokenize the string to get the duration out of brackets
								StringTokenizer tok = new StringTokenizer(read,"[]");
								//if there is more or less data, you are breaking the rules
								if(tok.countTokens() < 2)
								{
									//so you would have to quit, and fix it!
									System.out.println("Error :: Bad Input File");
									System.exit(1);
								}
								//new image and duration object
								ImageAndDuration x = new ImageAndDuration();
								//store the filename & duration in said object.
								x.filename = tok.nextToken();
								x.duration = Long.parseLong(tok.nextToken());
								//add to vector of ImageAndDuration's
								ins.add(x);
							}
							//if it breaks, you broke the input file rules
							else
							{
								System.out.println("Error :: Bad Input File");
								System.exit(1);
							}
						}
						//for the view info, set the source file vector
						vInfo.setSrcImages(ins);
						//add the view info to the system vector
						views.add(vInfo);
					}
				}
			}
		}
		//if it makes it here, it was not read correctly, unfortunately
		catch(Exception e)
		{
			//try following the rules.
			System.out.println("Error :: couldn't read from file correctly");
			System.exit(1);
		}
		
		if(false){System.out.println(views.toString());}
		//start a timer for every view
		for(int i=0; i<views.size(); i++)
		{
			//and you're good!
			DynamicImageTimer x = new DynamicImageTimer(views.elementAt(i));
		}
		Scanner sc = new Scanner(System.in);
	}
}

/* EXAMPLE TEXT FILE
startsite

startview
view_name: "First Set"
output_path: "c:\imgFeederTest\out\"
output_filename: "firstset.jpg"
input_path: "c:\imgFeederTest\src\"
input_file: firstset001.jpg [15]
input_file: firstset002.jpg [5]
endview

startview
view_name: "Second F'ing Set"
output_path: "c:\imgFeederTest\out\"
output_filename: "secondset.jpg"
input_path: "c:\imgFeederTest\src\"
input_file: secondset001.jpg[4]
input_file: secondset002.jpg[10]
input_file: secondset003.jpg[4]
input_file: secondset004.jpg[10]
endview

endsite
*/
