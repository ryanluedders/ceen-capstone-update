package com.ryanluedders.plmonitor.server;
import java.sql.ResultSet;
import java.sql.SQLException;

/*=====================================================================
 * sendSMS
 * A class that is used to convert from a mobile number to an
 * email->SMS address, by using the provider name, and then send
 * that user an "email" to their phone in the form of a text message.
 *=====================================================================*/
public class SendSms 
{	
	//boolean values to enable/disable debug and info standard output
	public static boolean dbg = false;
	public static boolean info = false;
	
	/*==============================================================
	 * sendSMSMessage
	 * Input: Mobile Number (String) Mobile Provider (String)
	 * Subject (String) Message (String)
	 * Output: sends an SMS
	 * Return: None
	 * This method takes a mobile number and provider, as well as a
	 * message and subject. and looks up the correct provider email->
	 * SMS address, and sends a message to that mobile number.
	 *==============================================================*/
	public static void sendSMSMessage(String mobileNo, String provider, String Subj, String mes)
	{
		//look up the mobileno -> email conversion
		String dbQuery = "SELECT providerSuffix FROM providers WHERE " +
			"ProviderName = '" + provider + "'";
		ResultSet x = DBMethods.queryDB(dbQuery);
		
		try
		{
			//make sure there is at least one result
			if(x.first())
			{
				//if there is, add the provider suffix to the
				//mobile number to get the email->SMS address
				mobileNo += x.getString(1);
			}
		}
		catch(SQLException e)
		{
			System.out.println("Error :: failed to get provider suffix " +
					"(sendSMS.sendSMS) :: " + e.toString());
		}
		
		if(info){System.out.println(mobileNo);}
		//send to email address...
		SendGmail gmail = new SendGmail();
		//send the message.
		gmail.send(mobileNo,Subj,mes);
	}	
}
