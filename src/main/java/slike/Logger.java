package slike;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
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

	public static void saveLogsToFile()
	{
		LocalDateTime ldt = LocalDateTime.now();
		String logFileName = "slike_log#" + ldt.getYear() + "-" + ldt.getMonthValue() + "-" + ldt.getDayOfMonth() + "_" + ldt.getHour() + 
				"-" + ldt.getMinute() + "-" + ldt.getSecond() + ".txt";
		File logFile = new File(logFileName);
		try
		{
			FileWriter fileWriter = new FileWriter(logFile);
			for(Exception exception:logExceptions)
			{
				fileWriter.append(exception.getMessage());
				fileWriter.append("\n");
			}
			for(String message:logMessages)
			{
				fileWriter.append(message);
				fileWriter.append("\n");
			}
			fileWriter.close();
		} catch (IOException e)
		{
			Logger.logException(e);
			if(SHOW_UI)
				ErrorAndLogPanel.updateUI();
		}
		
		
	}
}
