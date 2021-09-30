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

	int currentFile = -1;
	ImagePanel imagePanel;

	private Map<File, ImageData> IMAGES_MAP = new HashMap<File, ImageData>();
	private ArrayList<Integer> fileAddHistoryList = new ArrayList<>();

	private boolean alive;

	GraphicsEnvironment env;
	GraphicsDevice device;
	GraphicsConfiguration config;

	public ImageLoaderThread(ImagePanel imagePanel)
	{
		this.imagePanel = imagePanel;

		env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		device = env.getDefaultScreenDevice();
		config = device.getDefaultConfiguration();

		alive = true;
	}

	@Override
	public void run()
	{

		while (alive)
		{
			if (imagePanel.currentFile != currentFile)
			{
				loadImageFile(imagePanel.currentFile);
				this.currentFile = imagePanel.currentFile;
				imagePanel.notifyAboutNewImage();
			} else
			{
				try
				{
					loadImageFile(currentFile - 1);
					loadImageFile(currentFile + 1);
					loadImageFile(currentFile - 2);
					loadImageFile(currentFile + 2);

					Thread.sleep(5);
				} catch (Exception e)
				{
					Logger.logException(e);
				}
			}
		}

		Logger.logMessage("Shutting down thread...");
	}

	private void clearImageMap() throws Exception
	{
		if (IMAGES_MAP.size() > 10)
		{
			if (fileAddHistoryList.get(0) < imagePanel.file_list.size())
			{
				if (IMAGES_MAP.containsKey(imagePanel.file_list.get(fileAddHistoryList.get(0))))
				{

					IMAGES_MAP.remove(imagePanel.file_list.get(fileAddHistoryList.get(0)));
				}
			}
			fileAddHistoryList.remove(0);

		}
	}

	public ImageData getBufferedImage(int fileIndex)
	{
		this.currentFile = fileIndex;

		if (!this.IMAGES_MAP.containsKey(imagePanel.getFile_list().get(currentFile)))
			loadImageFile(fileIndex);

		return this.IMAGES_MAP.get(imagePanel.getFile_list().get(currentFile));
	}

	private void loadImageFile(int index)
	{

		if (index >= 0 && index < imagePanel.getFile_list().size())
		{
			if (!this.IMAGES_MAP.containsKey(imagePanel.getFile_list().get(index)))
			{
				try
				{
					int rotation = 0;
					long mili1 = System.currentTimeMillis();
					ImageData imageData;
					try (FileInputStream stream = new FileInputStream(imagePanel.getFile_list().get(index)))
					{

						imageData = new ImageData(ImageIO.read(stream));
						this.IMAGES_MAP.put(imagePanel.getFile_list().get(index), imageData);
						fileAddHistoryList.add(index);

					}
					try (FileInputStream stream = new FileInputStream(imagePanel.getFile_list().get(index)))
					{
						rotation = readImageInformation(stream);
						imageData.setRotation(rotation);
					}

					BufferedImage buffy = config.createCompatibleImage(imageData.getImage().getWidth(),
							imageData.getImage().getHeight(), Transparency.TRANSLUCENT);
					Graphics g = buffy.getGraphics();
					g.drawImage(imageData.getImage(), 0, 0, imagePanel);
					imageData.setImage(buffy);
					clearImageMap();
					long mili2 = System.currentTimeMillis();
					Logger.logMessage(imagePanel.getFile_list().get(index).getName() + " load time ms "
							+ (mili2 - mili1) + " , EXIF rotation: " + rotation);

				} catch (Exception e)
				{
					Logger.logException(e);
				}
			}
		}
	}

	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}

	public int readImageInformation(InputStream imageFileStream)
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
		this.IMAGES_MAP.remove(file);

	}

}
