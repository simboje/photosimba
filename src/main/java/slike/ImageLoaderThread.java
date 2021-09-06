package slike;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageLoaderThread extends Thread {
	
	File file_to_load;
	int currentFile, numberOfFiles;
	File[] listFiles;
	
	boolean spawnmorethreads;
	
	static final Object lock = new Object();

	public ImageLoaderThread(File file) {
		
	}

	public ImageLoaderThread(File[] listOfFiles, int currentFile, boolean spawnmorethreads) {
		this.file_to_load = listOfFiles[currentFile];
		this.listFiles = listOfFiles;
		this.currentFile = currentFile;
		numberOfFiles = listOfFiles.length;
		this.spawnmorethreads = spawnmorethreads;
	}

	@Override
	public void run() {
		synchronized (lock) {
			
			try {
				// file index is already checked when A or D is catched on panel
				if(!MainPanel.IMAGES_MAP.containsKey(file_to_load))
				{
					MainPanel.IMAGES_MAP.put(file_to_load, ImageIO.read(file_to_load));
					System.out.println("Added " + file_to_load.toString() + " index " + this.currentFile);

				}
				else {
					System.out.println("Hi from thread, map already has file " + file_to_load  + " index " + this.currentFile);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		if(currentFile>0 && spawnmorethreads)
		{
			ImageLoaderThread threadGoBack = new ImageLoaderThread(this.listFiles, currentFile-1, false);
			threadGoBack.run();
		}
		if(currentFile - 1>0 && spawnmorethreads)
		{
			ImageLoaderThread threadGoBack = new ImageLoaderThread(this.listFiles, currentFile-2, false);
			threadGoBack.run();
		}
		if(currentFile<numberOfFiles-1 && spawnmorethreads)
		{
			ImageLoaderThread threadGoForward = new ImageLoaderThread(this.listFiles, currentFile+1, false);
			threadGoForward.run();
		}
		if(currentFile<numberOfFiles-2 && spawnmorethreads)
		{
			ImageLoaderThread threadGoForward = new ImageLoaderThread(this.listFiles, currentFile+2, false);
			threadGoForward.run();
		}	
	}

}
