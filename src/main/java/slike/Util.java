package slike;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.jna.platform.FileUtils;

public class Util
{
	public static File fixCyrillicPath(String filePath) // as a rule args[0]
	{
		// https://stackoverflow.com/questions/7660651/passing-command-line-unicode-argument-to-java-code
		// seems that my PC has a lucky locale/encoding setting
		Path path = Paths.get(filePath);

		File rootFile = path.getRoot().toFile();
		float globalMax = 0;

		for (int k = 0; k < path.getNameCount(); ++k)
		{
			String miniPath = rootFile.getPath() + File.separator + path.getName(k);
			File miniFile = new File(miniPath);
			if (miniFile.exists())
			{
				rootFile = miniFile;
			} else
			{
				Path name = path.getName(k);
				for (File file : rootFile.listFiles())
				{
					if (file.getName().length() == name.getFileName().toString().length())
					{
						// as soon as the file has same length give it a little bit of similarity +0.1.
						// For example file "Ć" will have zero same characters as our args "C", but with
						// this bump it will be the best candidate and greater than zero which enables
						// us to return at least some file. It may work or not if we have "Č", "Ć" files
						// in same directory.
						float countSameLetters = 0.1f;
						for (int i = 0; i < file.getName().length(); i++)
						{
							if (file.getName().charAt(i) == name.getFileName().toString().charAt(i))
							{
								countSameLetters++;
							}
						}

						float localMax = countSameLetters / file.getName().length();
						if (localMax > globalMax)
						{
							globalMax = localMax;
							rootFile = file;
						}
					}
				}
			}
		}

		return rootFile;
	}
	
	public static void sendFileToRecycleBin(File fileToDelete)
	{
		FileUtils fileUtils = FileUtils.getInstance();

		if (fileUtils.hasTrash())
		{
			try
			{
				fileUtils.moveToTrash(fileToDelete);
				Logger.logMessage("A have trash! Deleted " + fileToDelete);
			} catch (IOException ioe)
			{
				Logger.logException(ioe);
			}
		} else
		{
			Logger.logMessage("No Trash available. Failed to delete " + fileToDelete);
		}
	}

}
