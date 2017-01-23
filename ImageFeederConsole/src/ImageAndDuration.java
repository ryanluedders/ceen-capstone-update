//A simple class to hold an image filename and duration 
//that it should be shown
public class ImageAndDuration
{
	//variables made public to be accessed by whoever.
	//filename of the image
	public String filename;
	//duration the file should be shown
	public long duration;
	
	//the default constructor sets both filename
	//and duration to default "zero" values
	public ImageAndDuration()
	{
		//set filename blank
		filename = "";
		//set duration to zero
		duration = 0;
	}
	
	//print the combination by printing filename + [duration]
	public String toString()
	{
		return filename + " [" + Long.toString(duration) +"]";
	}
}