import java.util.Vector;

//A class to keep track of all image information for a view
public class ViewInfo 
{
	//a vector of source images and durations
	Vector<ImageAndDuration> srcImages = new Vector<ImageAndDuration>();
	//the name of the view
	String viewName;
	//the path to the output file
	String viewOut;
	//the directory of the input files
	String inPath;
	
	//default constructor sets everything to "zero" values
	public ViewInfo()
	{
		//set view name, view out, path to blank
		viewName = "";
		viewOut = "";
		inPath = "";
	}
	
	//set source images, by passing in a vector of image,duration combos
	public void setSrcImages(Vector<ImageAndDuration> x)
	{
		//set the source image vector
		srcImages = x;
	}
	
	//set the view name by passing in a string of a view name
	public void setViewName(String vname)
	{
		//set the view name
		viewName = vname;
	}
	
	//set the image output file to a pathname
	public void setViewOut(String vout)
	{
		//set the output file
		viewOut = vout;
	}
	
	//set the path to the directory of the input files
	public void setInPath(String inpath)
	{
		//set the incoming path
		inPath = inpath;
	}
	
	//get the source images by returning a vector with
	//the source filenames/durations
	public Vector<ImageAndDuration> getSrcImages()
	{
		//return the vector
		return srcImages;
	}
	
	//get the view name
	public String getViewName()
	{
		//return the view name
		return viewName;
	}
	
	//get the output file
	public String getViewOut()
	{
		//get the output file
		return viewOut;
	}
	
	//get the input directory
	public String getInPath()
	{
		//get the input directory
		return inPath;
	}
	
	//print all the view information on one string
	public String toString()
	{
		String rval = "";
		//add the view name
		rval += "View Name: " + viewName + "\n";
		//output path
		rval += "Out Path: " + viewOut + "\n";
		//input directory
		rval += "In Path: " + inPath + "\n";
		//and source images
		rval += "**Src Images**\n";
		rval += srcImages.toString() + "\n";
		return rval;
	}
	
}
