package slike;

import java.util.ArrayList;

public class Logger
{
	
	private static ArrayList<String> logMessages = new ArrayList<>();
	private static ArrayList<Exception> logExceptions = new ArrayList<>();
	public static boolean SHOW_UI = true;

	public static void logMessage(String message)
	{
		logMessages.add(message);
	}
	
	public static void logException(Exception e)
	{
		logExceptions.add(e);
		if(SHOW_UI)
			ErrorAndLogPanel.updateUI();
	}
	
	public static ArrayList<String> getLogMessages()
	{
		return logMessages;
	}
	
	public static ArrayList<Exception> getLogExceptions()
	{
		return logExceptions;
	}
}
