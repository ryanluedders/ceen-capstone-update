package com.ryanluedders.plmonitor.server;
import com.jscape.inet.smtpssl.*;
import com.jscape.inet.email.*;

/*=====================================================================
 * sendGmail
 * used to send an email from a Gmail account.
 * source: (webpage) http://www.jscape.com/articles/sending_email_smtp_ssl_gmail.html?
 *=====================================================================*/
public class SendGmail 
{
	/*==============================================================
	 * send
	 * Input: Address (String) Subject (String) Message (String)
	 * Output: takes address passed as argument, and sends message
	 * with specified subject to that address.
	 * Return: None
	 * takes address passed as argument, and sends message
	 * with specified subject to that address, using gmail account.
	 *==============================================================*/
	public void send(String toAddress, String subject, String mes) 
	{
		SmtpSsl smtp = null;

	    //Gmail username - parking lot monitoring account
		String username = "parking.lot.monitoring@gmail.com";

		//Gmail password
		String password = "unothesis";

		//email address specified as argument
		String to = toAddress;
		try 
		{      
			// create a new SmtpSsl instance connecting securely via port 465 using implicit SSL
			smtp = new SmtpSsl("smtp.gmail.com",465);

			// establish secure connection
			smtp.connect();

			// login using gmail account details
			smtp.login(username,password);

			// create new email message
			EmailMessage message = new EmailMessage();
			message.setTo(to);
			message.setFrom(username);
			message.setSubject(subject);
			message.setBody(mes);

			// send message
			smtp.send(message);

			// disconnect      
			smtp.disconnect();      
		} 
		catch(Exception e) 
		{
			//capture errors, print to standard out.
			System.out.println("Error :: Failed to send gmail " +
					"(sendGmail.send) :: " + e.toString());
		} 
	}
}