package slike;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageLoaderThread extends Thread {
	
	File file_to_load;
	int currentFile, numberOfFiles;
	File[] listFiles;
	
	private Map<File, BufferedImage> IMAGES_MAP = new HashMap<File, BufferedImage>();
	private boolean alive;
	
	static final Object lock = new Object();

	public ImageLoaderThread(File file) {
		
	}

	public ImageLoaderThread(File[] listOfFiles, int currentFile) {
		this.file_to_load = listOfFiles[currentFile];
		this.listFiles = listOfFiles;
		this.currentFile = currentFile;
		numberOfFiles = listOfFiles.length;
		
		alive = true;
	}

	@Override
	public void run() {
		
		while(alive)
		{
			if( !this.IMAGES_MAP.containsKey(listFiles[currentFile]))
			{
				loadImageFile(currentFile);
			}
				
			if(currentFile - 1 >= 0 && ! this.IMAGES_MAP.containsKey(listFiles[currentFile - 1]))
			{
				System.out.println("A2");
				loadImageFile(currentFile-1);
			}
				
			if(currentFile + 1 < listFiles.length && ! this.IMAGES_MAP.containsKey(listFiles[currentFile + 1]))
			{
				System.out.println("A3");
				loadImageFile(currentFile+1);
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Shutting down thread...");

	}
	
	public BufferedImage getBufferedImage(int fileIndex)
	{
		this.currentFile = fileIndex;
		if(this.IMAGES_MAP.containsKey(listFiles[fileIndex]))
			return this.IMAGES_MAP.get(listFiles[fileIndex]);
		else {
			loadImageFile(fileIndex);
			return this.IMAGES_MAP.get(listFiles[fileIndex]);
		}
	}
	
	private void loadImageFile(int index) {
		
		if(index>=0 && index < listFiles.length)
		{
			if (!this.IMAGES_MAP.containsKey(listFiles[index])) {
				try {
					BufferedImage temp_Image = ImageIO.read(listFiles[index]);
					synchronized (lock) {
						this.IMAGES_MAP.put(listFiles[index], temp_Image);
					}
					System.out.println("Added " + listFiles[index].toString() + " index " + index);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Map already has file " + listFiles[currentFile] + " index " + this.currentFile);
			}
		}

	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

}
