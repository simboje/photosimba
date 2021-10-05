package slike;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;

public class ImageLoaderThread extends Thread
{
	private static int threadCounter = 0;
	private static Map<File, ImageData> IMAGES_MAP = new HashMap<File, ImageData>();
	private static ArrayList<Integer> fileAddHistoryList = new ArrayList<>();

	static GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	static GraphicsDevice device = env.getDefaultScreenDevice();
	static GraphicsConfiguration config = device.getDefaultConfiguration();

	static Object lockObject = new Object();

	@Override
	public void run()
	{
		try
		{
			loadImageFile(ImagePanel.currentFile - 1);
			loadImageFile(ImagePanel.currentFile + 1);
			loadImageFile(ImagePanel.currentFile - 2);
			loadImageFile(ImagePanel.currentFile + 2);
		} catch (Exception e)
		{
			Logger.logException(e);
		}
		threadCounter++;
		Logger.logMessage("Shutting down thread id#"+threadCounter);
	}

	private static void clearImageMap() throws Exception
	{
		if (IMAGES_MAP.size() > 10)
		{
			if (fileAddHistoryList.get(0) < ImagePanel.file_list.size())
			{
				if (IMAGES_MAP.containsKey(ImagePanel.file_list.get(fileAddHistoryList.get(0))))
				{

					IMAGES_MAP.remove(ImagePanel.file_list.get(fileAddHistoryList.get(0)));
				}
			}
			fileAddHistoryList.remove(0);

		}
	}

	public static ImageData loadImageFile(int index)
	{
		ImageData imageData = null;
		synchronized (lockObject) // stop duplicate loading and wasting time
		{
			if (index >= 0 && index < ImagePanel.file_list.size())
			{
				if (!IMAGES_MAP.containsKey(ImagePanel.file_list.get(index)))
				{
					try
					{
						int rotation = 0;
						long mili1 = System.currentTimeMillis();

						try (FileInputStream stream = new FileInputStream(ImagePanel.file_list.get(index)))
						{

							imageData = new ImageData(ImageIO.read(stream));
							IMAGES_MAP.put(ImagePanel.file_list.get(index), imageData);
							fileAddHistoryList.add(index);

						}
						try (FileInputStream stream = new FileInputStream(ImagePanel.file_list.get(index)))
						{
							rotation = readImageInformation(stream);
							imageData.setRotation(rotation);
						}

						BufferedImage buffy = config.createCompatibleImage(imageData.getImage().getWidth(),
								imageData.getImage().getHeight(), Transparency.TRANSLUCENT);
						Graphics g = buffy.getGraphics();
						g.drawImage(imageData.getImage(), 0, 0, null);
						imageData.setImage(buffy);
						clearImageMap();
						long mili2 = System.currentTimeMillis();
						Logger.logMessage(ImagePanel.file_list.get(index).getName() + " load time ms " + (mili2 - mili1)
								+ " , EXIF rotation: " + rotation);

					} catch (Exception e)
					{
						e.printStackTrace();
						Logger.logException(e);
					}
				} else
				{
					return IMAGES_MAP.get(ImagePanel.file_list.get(index));
				}
			}
		}

		return imageData;
	}

	public static int readImageInformation(InputStream imageFileStream)
			throws IOException, MetadataException, ImageProcessingException
	{
		Metadata metadata = ImageMetadataReader.readMetadata(imageFileStream);

		int orientation = 0;
		for (Directory directory : metadata.getDirectories())
		{
			for (Tag tag : directory.getTags())
			{
				if (tag.getDescription().contains("Mirror horizontal"))
				{
					// check in this case will be encountered in practice
				} else if (tag.getDescription().contains("Rotate 180"))
				{
					orientation += 2;
				} else if (tag.getDescription().contains("Mirror horizontal and rotate 270 CW"))
				{
					// check in this case will be encountered in practice
				} else if (tag.getDescription().contains("Rotate 90 CW"))
				{
					orientation++;
				} else if (tag.getDescription().contains("Mirror horizontal and rotate 90 CW"))
				{
					// check in this case will be encountered in practice
				} else if (tag.getDescription().contains("Rotate 270 CW"))
				{
					orientation--;
				}
			}
		}

		return orientation;
	}

	public void removeImage(File file)
	{
		// part of file delete procedure
		IMAGES_MAP.remove(file);

	}

}
