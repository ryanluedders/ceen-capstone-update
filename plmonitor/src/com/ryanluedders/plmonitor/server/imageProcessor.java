package com.ryanluedders.plmonitor.server;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import javax.swing.JFrame;

import com.jhlabs.image.ExposureFilter;
import com.jhlabs.image.QuantizeFilter;
import com.jhlabs.image.ThresholdFilter;

public class imageProcessor 
{
	//booleans to enable output of debug and system information
	public static boolean dbg = false;
	public static boolean info = false;
	
	/*==============================================================
	 * averageColorRGB
	 * Input: an image (BufferedImage)
	 * Output: None
	 * Return: the average color (Color)
	 *==============================================================*/
	public static Color averageColorRGB(BufferedImage img)
	{
		//a value to return.
		Color avgColor = new Color(img.getRGB(0, 0));

		//get the components of the rval
		int avgRed = avgColor.getRed();
		int avgGreen = avgColor.getGreen();
		int avgBlue = avgColor.getBlue();

		//for use in loops
		int xSize;
		int ySize;

		//get the image size
		xSize = img.getWidth();
		ySize = img.getHeight();

		//count the number of pixels analyzed
		int pixelCount = 1;

		//go through entire image
		for(int i=0; i<xSize; i++)
		{
			for(int j=0; j<ySize; j++)
			{
				//get the color of the current pixel
				Color tmpColor = new Color(img.getRGB(i,j));
				//keep track of the averages of the components
				avgRed = ((tmpColor.getRed() + pixelCount * avgRed) / (pixelCount + 1));
				avgGreen = ((tmpColor.getGreen() + pixelCount * avgGreen) / (pixelCount + 1));
				avgBlue = ((tmpColor.getBlue() + pixelCount * avgBlue) / (pixelCount + 1));

				//keep track of pixels analyzed.
				pixelCount++;
			}
		}
		//combine them again into the color
		avgColor = new Color(avgRed,avgGreen,avgBlue);

		//return the average
		return avgColor;

	}
	
	/*==============================================================
	 * averageColorThirds
	 * Input: an Image
	 * Output: None
	 * Return: An array with the average color of the top, middle
	 * 		and bottom of the image
	 * This takes the passed image, divides it into thirds, and
	 * calculates the average color of each of the thirds, returning
	 * that color in an array.
	 *==============================================================*/
	public static Color[] averageColorThirds(BufferedImage img)
	{
		//start with 0,0 coordinate
		int imgLtX = 0;
		int imgBotY = 0;
	
		//the last third's starting point
		int midY = 2 * (img.getHeight() / 3);
		//the middle third's starting point
		int lwrY = (img.getHeight() / 3);
		
		//the height is the same as 1/3 of total image height
		int smallImgHt = lwrY;
		//compare the whole width
		int smallImgW = img.getWidth();
		
		//get three new images that are the thirds of the current image
		BufferedImage topThird = img.getSubimage(imgLtX, midY, smallImgW, smallImgHt);
		BufferedImage midThird = img.getSubimage(imgLtX, lwrY, smallImgW, smallImgHt);
		BufferedImage botThird = img.getSubimage(imgLtX, imgBotY, smallImgW, smallImgHt);
		
		//an array to return the colors in
		Color[] rval = new Color[3];
		//get the average color of each third
		rval[0] = averageColorRGB(topThird);
		rval[1] = averageColorRGB(midThird);
		rval[2] = averageColorRGB(botThird);

		//return the array
		return rval;	
	}
	
	/*==============================================================
	 * averageColorPercentThirds
	 * Input: an Image, a percentage of the image to analyze
	 * Output: None
	 * Return: An array with the average color of the thirds
	 * This image calculates the average color of the top, middle
	 * and bottom of an image, excluding a certain percent of the
	 * original image, and returns the colors in an array.
	 *==============================================================*/
	public static Color[] averageColorPercentThirds(BufferedImage img, double percent)
	{
		int botX = (img.getHeight() / 2) - (int)((img.getHeight() * percent)/2);
		int botY = (img.getWidth() / 2) - (int)((img.getWidth() * percent)/2);
		int newHeight = (int)(img.getHeight() * percent);
		int newWidth = (int)(img.getWidth() * percent);
		return averageColorThirds(img.getSubimage(botX, botY, newWidth, newHeight));
	}
	
	/*==============================================================
	 * colorArrayToString
	 * Input: an Array of Colors (Color[])
	 * Output: None
	 * Return: A String with each color on a new line
	 * Returns a string that will print each of the colors included
	 * in the array, on their own line.
	 *==============================================================*/
	public static String colorArrayToString(Color[] x)
	{
		String rval = "";
		//go through entire color array
		for(int i=0; i<x.length; i++)
		{
			//add the color to the return string
			rval += x[i].toString() + "\n";
		}
		//return the string
		return rval;
	}

	//GOOD
	/*==============================================================
	 * getAvgFlatVariance
	 * Input: an image (BufferedImage)
	 * Output: None
	 * Return: the variance in the colors (double)
	 *==============================================================*/
	public static double getAvgFlatVariance(BufferedImage img)
	{
		//a counter to keep track of difference between pixels
		double totalDifferenceCount = 0;
		//count the number of comparisons
		double moveCount = 0;

		//go through the entire image width-wise
		for(int i=1; i<img.getWidth(); i++)
		{
			for(int j=1; j<img.getHeight(); j++)
			{
				//if there is a difference between this pixel
				//and the one next to it
				if(img.getRGB(i,j) != img.getRGB((i-1),j))
				{
					//that qualifies as a difference
					totalDifferenceCount += 1;
				}
				//keep track of the moves
				moveCount++;
			}
		}

		//go through the image height-wise
		for(int i=1; i<img.getHeight(); i++)
		{
			for(int j=1; j<img.getWidth(); j++)
			{
				//if the pixel is different than the one next to it
				if(img.getRGB(j,i) != img.getRGB(j,(i-1)))
				{
					//keep track of difference
					totalDifferenceCount += 1;					
				}
				//keep track of moves.
				moveCount++;
			}
		}

		//return the ratio of the number of differences
		//to the number of moves.
		return (totalDifferenceCount / moveCount);
	}
	
	/*==============================================================
	 * getAvgHorizontalVariance - gets average variance (horizontally)
	 * Input: an image (BufferedImage)
	 * Output: None
	 * Return: the variance in the colors (horizontally) (double)
	 *==============================================================*/
	public static double getAvgHorizontalVariance(BufferedImage img)
	{
		//a counter to keep track of difference between pixels
		double totalDifferenceCount = 0;
		//count the number of comparisons
		double moveCount = 0;

		//go through the entire image width-wise
		for(int i=1; i<img.getHeight(); i++)
		{
			for(int j=1; j<img.getWidth(); j++)
			{
				//if there is a difference between this pixel
				//and the one next to it
				if(img.getRGB(j,i) != img.getRGB((j-1),i))
				{
					//that qualifies as a difference
					totalDifferenceCount += 1;
				}
				//keep track of the moves
				moveCount++;
			}
		}
		//return an average difference based on number of moves
		return (totalDifferenceCount / moveCount);
	}
	
	/*==============================================================
	 * getAvgVerticalVariance - gets average variance (vertically)
	 * Input: an image (BufferedImage)
	 * Output: None
	 * Return: the variance in the colors (vertically) (double)
	 *==============================================================*/
	public static double getAvgVerticalVariance(BufferedImage img)
	{
		//a counter to keep track of difference between pixels
		double totalDifferenceCount = 0;
		//count the number of comparisons
		double moveCount = 0;

		//go through the image height-wise
		for(int i=1; i<img.getWidth(); i++)
		{
			for(int j=1; j<img.getHeight(); j++)
			{
				//if the pixel is different than the one next to it
				if(img.getRGB(i,j) != img.getRGB(i,(j-1)))
				{
					//keep track of difference
					totalDifferenceCount += 1;					
				}
				//keep track of moves.
				moveCount++;
			}
		}
		//return an average difference based on number of moves
		return (totalDifferenceCount / moveCount);
	}

	/*==============================================================
	 * isCar
	 * Input: A picture of a spot (BufferedImage)
	 * Output: None
	 * Return: is a car (boolean)
	 *==============================================================*/
	public static boolean isCar(BufferedImage spot)
	{
		//a value to return
		boolean rval = true;		
		double thresh = 0.10;
		
		//an image to work with while deciding.
		BufferedImage workingImage = workWithImage(spot);
		//displayImage(workingImage);

		//judge based on the variance whether it is a car or not.
		if(getAvgFlatVariance(workingImage) < thresh)
		{
			rval = false;
		}
		else
		{
			rval = true;
		}
		return rval;
	}
	
	/*==============================================================
	 * isCarByEdges - uses edge detection method to determine if it's a car
	 * Input: A picture of a spot (bufferedimage) threshold (integer)
	 * percent of the spot (double)
	 * Output: None
	 * Return: is a car (boolean)
	 *==============================================================*/
	public static boolean isCarByEdges(BufferedImage spot, double thresh, double percent)
	{
		//a value to return
		boolean rval = true;		
		//RescaleFilter rf = new RescaleFilter();
		//rf.setScale(100 / spot.getWidth());
		//spot = rf.filter(spot, spot);
		
		//an image to work with while deciding.
		BufferedImage workingImage = workWithImage(spot);
		//displayImage(workingImage);
		
		if(dbg){System.out.println("new threshold: " + percent + " " + (thresh * percent));}
		
		//get the number of white pixels in the image
		int straightWhite = countWhiteSpots(workingImage);
		//get the spot size
		int spotSize = workingImage.getHeight() * workingImage.getWidth();
		//a size to normalize the spot size to
		//int comparisonSize = (60 * 45);
		//"curve" the straight count of white pixels, by the spot size
		//double curvedWhite = ((double)comparisonSize / (double)spotSize) * (double)straightWhite;
		double curvedWhite = (double) straightWhite/ (double)(workingImage.getHeight() * workingImage.getWidth());
		
		//judge based on the variance whether it is a car or not.
		if(curvedWhite < thresh)
		{
			rval = false;
		}
		else
		{
			rval = true;
		}
		return rval;
	}
	
	/*==============================================================
	 * getAnalysis
	 * Input: A picture of a spot (BufferedImage)
	 * Output: None
	 * Return: A String with analysis results
	 * this method takes in an image of a spot, and runs it through
	 * the various tests specified throughout this processing class
	 * and returns their results all in one string for display to
	 * the user
	 *==============================================================*/
	public static String getAnalysis(BufferedImage spot)
	{
		String rval = "";
		
		//an image to work with while deciding.
		BufferedImage workingImage = workWithImage(spot);
		
		NumberFormat nf = DecimalFormat.getNumberInstance();
		nf.setMaximumFractionDigits(5);

		//print the average color to console
		rval = averageColorRGB(workingImage).toString() + "\n";
		//print the variance to console
		rval += nf.format(getAvgFlatVariance(workingImage)) + "\n";
		//print the horizontal variance
		rval += nf.format(getAvgHorizontalVariance(workingImage)) + "\n";
		//print the vertical variance
		rval += nf.format(getAvgVerticalVariance(workingImage)) + "\n";
		//get the amount of white
		rval += nf.format(countWhiteSpots(workingImage)) + "\n";
		//get the amount of white, in the middle 60%
		rval += nf.format(countWhiteSpotsLimitArea(workingImage,0.3)) + "\n";
		//get the ranges
		rval += Arrays.toString(getImageRanges(spot,5));
		
		rval += "\n--##--";
		
		int straightWhite = countWhiteSpots(workingImage);
		int spotSize = workingImage.getHeight() * workingImage.getWidth();
		int comparisonSize = (60 * 45);
		double curvedWhite = (double)straightWhite / (double)(workingImage.getHeight() * workingImage.getWidth());
		
		if(dbg){System.out.println(straightWhite + " -> " + spotSize + " -> " + comparisonSize + " -> " + curvedWhite);}
		
		//print the average color to console
		rval += averageColorRGB(workingImage).toString() + "\n";
		//print the variance to console
		rval += nf.format(getAvgFlatVariance(workingImage)) + "\n";
		//print the horizontal variance
		rval += nf.format(getAvgHorizontalVariance(workingImage)) + "\n";
		//print the vertical variance
		rval += nf.format(getAvgVerticalVariance(workingImage)) + "\n";
		//get the amount of white
		rval += nf.format(curvedWhite) + "\n";

		return rval;
	}

	/*==============================================================
	 * reduceToBlackAndWhite
	 * Input: An image (BufferedImage)
	 * Output: None
	 * Return: A black & white image (BufferedImage)
	 *==============================================================*/
	public static BufferedImage reduceToBlackAndWhite(BufferedImage spot)
	{
		//an image to return
		BufferedImage rval = null;
		//start with the image that is passed
		rval = spot;

		//run the image through a threshold filter
		ThresholdFilter threshFilt = new ThresholdFilter();	
		//set the threshold
		threshFilt.setLowerThreshold(75);
		//apply the threshold filter
		rval = threshFilt.filter(rval, rval);

		//go through the image after performing threshold filter
		for(int j=0; j<rval.getHeight(); j++)
		{
			for(int i=0; i<rval.getWidth(); i++)
			{
				//to remove shadows - if the color is black, change to white
				if(new Color(rval.getRGB(i,j)).equals(Color.BLACK))
				{
					rval.setRGB(i, j, Color.white.getRGB());
				}
			}
		}

		//return the transformed image.
		return rval;
	}

	/*==============================================================
	 * workWithImage
	 * Input: An image (BufferedImage)
	 * Output: None
	 * Return: A modified Image (BufferedImage)
	 *==============================================================*/
	public static BufferedImage workWithImage(BufferedImage startImg)
	{
		//an image to return
		BufferedImage rval = null;
		//start with the image that is sent in.
		rval = startImg;
		
		//reduce the number of colors
		//QuantizeFilter q = new QuantizeFilter();
		//q.setNumColors(32);
		//rval = q.filter(rval, rval);
		
		//ExposureFilter ex = new ExposureFilter();
		//ex.setExposure(2.5f);
		//rval = ex.filter(rval, rval);
		
		//perform any processing that will be helpful
		//rval = reduceToBlackAndWhite(rval);
		
		//new line. runs canny method.
		rval = cannyEdgeDetect(rval);

		//return the processed image
		return rval;
	}
	
	/*==============================================================
	 * countWhiteSpots
	 * Input: A picture of a processed spot (BufferedImage)
	 * Output: None
	 * Return: A number representing how many lines there are
	 * This method takes an image that has already been run through
	 * a line detector, and goes through the image, counting how
	 * extensive the lines are in the image, returning how many
	 * pixes of lines there actually are in an image.
	 *==============================================================*/
	public static int countWhiteSpots(BufferedImage img)
	{
		int ctr = 0;
		//go through the image after performing line detection
		for(int j=1; j<img.getHeight(); j++)
		{
			for(int i=1; i<img.getWidth(); i++)
			{
				//if the pixel is white, then it is a line
				if(new Color(img.getRGB(i,j)).equals(Color.WHITE))
				{
					ctr++;
				}
			}
		}
		return ctr;
	}
	
	/*==============================================================
	 * countWhiteSpotsLimitArea
	 * Input: A picture of a processed spot (BufferedImage), a
	 * percentage of the image ot use in processing (double)
	 * Output: None
	 * Return: A number representing how many lines there are
	 * This method takes an image that has already been run through
	 * a line detector, and goes through the image, counting how
	 * extensive the lines are in the image, returning how many
	 * pixes of lines there actually are in an image, focusing on
	 * a certain percent of the image as specified.
	 *==============================================================*/
	public static int countWhiteSpotsLimitArea(BufferedImage img, double percent)
	{
		int originalWidth = img.getWidth();
		int originalHeight = img.getHeight();
		
		int newWidth = (int) (originalWidth * percent);
		int newHeight = (int) (originalHeight * percent);
		
		int newX = (int) ((originalWidth * percent) / 2);
		int newY = (int) ((originalHeight * percent) / 2);
		
		return countWhiteSpots(img.getSubimage(newX,newY,newWidth,newHeight));
	}
	
	/*==============================================================
	 * getImageRanges
	 * Input: A picture of a non-processed image
	 * Output: None
	 * Return: The proportion of pixels in a given number of ranges
	 * This method uses a sort of histogram approach, going through
	 * the entire images, and counting the occurence of a given pixel
	 * color, keeping track, and ending up with a histogram showing
	 * frequency of colors. It then divides it into ranges to be
	 * more easily analyzed
	 *==============================================================*/
	public static int[] getImageRanges(BufferedImage img, int ranges)
	{
		//this simluates a histogram, pixels are valued at colors
		//between 0-255, so it will simply count the frequency of
		//each color, using an array of integers
		int[] hist = new int[256];
		
		//A quantize filter to reduce the number of colors
		QuantizeFilter qf = new QuantizeFilter();
		//set it to six colors, currently.
		qf.setNumColors(6);
		//create a new "working" image
		BufferedImage newImg = null;
		//make the new image using the filter
		newImg = qf.filter(img, newImg);
		
		//go through the entire histogram, set all counts to zero
		for(int i=0; i<hist.length; i++)
		{
			hist[i] = 0;
		}
		
		//go through entire image, height & width-wise
		for(int i=0; i<newImg.getWidth(); i++)
		{
			for(int j=0; j<newImg.getHeight(); j++)
			{
				//get the color
				Color x = new Color(newImg.getRGB(i,j));
				//get the average RGB value
				int mx = (x.getBlue() + x.getGreen() + x.getRed()) / 3;
				//increment count for that color
				hist[mx]++;
			}
		}
		
		//the number of ranges
		int nR = ranges;
		//the array to return the count of the ranges
		int[] range = new int[ranges];
		
		//for the given number of ranges
		for(int cur = 0; cur<nR; cur++)
		{
			//the count of each range
			range[cur] = 0;
			//count all values in a subset (range) of the histogram
			for(int i=(255/nR)*cur; i<(((255/nR)*cur)+(255/nR)); i++)
			{
				//increment range count by individual (included) pixel count
				range[cur] += hist[i];
			}
		}
		//return the array
		return range;
	}
	
	/*==============================================================
	 * cannyEdgeDetect - runs the canny method of edge detection
	 * Input: A picture of a non-processed image
	 * Output: None
	 * Return: An image showing edges
	 * Runs the image through the canny method of edge detection to
	 * find edges, to be used with with other methods
	 *==============================================================*/
	public static BufferedImage cannyEdgeDetect(BufferedImage startImg)
	{
		//make a new canny edge detecting filter
		CannyEdgeDetector x = new CannyEdgeDetector();
		//set the source to the passed in image
		x.setSourceImage(startImg);
		//set the low threshold to a float value
		x.setLowThreshold(4f);
		//set the high threshold to a float value
		//x.setHighThreshold(15f);
		x.setHighThreshold(16f);
		//set the kernel radius to a float value
		x.setGaussianKernelRadius(0.5f);
		//set the wdth to an integer value
		x.setGaussianKernelWidth(3);
		//process the image
		x.process();
		//get the filtered image
		BufferedImage rval = x.getEdgesImage();

		//return it.
		return rval;
	}
	
	/*==============================================================
	 * displayImage - displays image in a frame
	 * Input: An image (BufferedImage)
	 * Output: displays image to user in a frame
	 * Return: None
	 * For testing purposes, mostly. Displays an image to the user
	 * in a frame.
	 *==============================================================*/
	public static void displayImage(BufferedImage img)
	{
		//new frame
		JFrame fr = new JFrame();
		//imagepanel to put on frame
		ImagePanel pan = new ImagePanel(img);
		//set the size
		pan.setSize(256,256);
		//add the panel to the frame
		fr.getContentPane().add(pan);
		fr.pack();
		//set the size of the frame
		fr.setSize(256,256);
		//show the frame
		fr.setVisible(true);
	}
	
	/*==============================================================
	 * getProcessedImage - all-in-one image processing
	 * Input: An image (BufferedImage)
	 * Output: None
	 * Return: A processed Image (BufferedImage)
	 * Simplify a lot of calls (potentially) with just one.
	 *==============================================================*/
	public static BufferedImage getProcessedImage(BufferedImage img)
	{
		return workWithImage(img);
	}
}
