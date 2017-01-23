package com.luedders.server;

import java.sql.Date;

public class Event 
{
	int eventID;
	String eventName;
	int lotAffected;
	Date startDate;
	int startTime;
	Date endDate;
	int endTime;
	
	public Event()
	{
		setEventID(-1);
	}
	public Event(int eID, String eName, int lAf, Date sDate, int sTime, Date eDate, int eTime)
	{
		setEventID(eID);
		setEventName(eName);
		setLotAffected(lAf);
		setStartDate(sDate);
		setStartTime(sTime);
		setEndDate(eDate);
		setEndTime(eTime);
	}
	public void setEventID(int eID)
	{
		eventID = eID;
	}
	public int getEventID()
	{
		return eventID;
	}
	public void setEventName(String eName)
	{
		eventName = eName;
	}
	public String getEventName()
	{
		return eventName;
	}
	public void setLotAffected(int lAf)
	{
		lotAffected = lAf;
	}
	public int getLotAffected()
	{
		return lotAffected;
	}
	public void setStartDate(Date sDate)
	{
		startDate = sDate;
	}
	public Date getStartDate()
	{
		return startDate;
	}
	public void setStartTime(int sTime)
	{
		startTime = sTime;
	}
	public int getStartTime()
	{
		return startTime;
	}
	public void setEndDate(Date eDate)
	{
		endDate = eDate;
	}
	public Date getEndDate()
	{
		return endDate;
	}
	public void setEndTime(int eTime)
	{
		endTime = eTime;
	}
	public int getEndTime()
	{
		return endTime;
	}
	
	public String toString()
	{
		return getEventID() + " " + getEventName() + " " +
		getLotAffected() + " STARTS:" + getStartDate().toString() + " " +
		getStartTime() + " ENDS: " + getEndDate().toString() + " " + 
		getEndTime();
	}
	
	public String toDB()
	{
		return "INSERT INTO Events (EventID, EventName, LotAffected, StartDate," +
				"StartTime, EndDate, EndTime) \n" +
				"VALUES (" + getEventID() + ",'" + getEventName().toString() +
				"'," + getLotAffected() + ",'" + getStartDate() + "'," + getStartTime() +
				",'" + getEndDate() + "'," + getEndTime() + ")";
	}
	
}
