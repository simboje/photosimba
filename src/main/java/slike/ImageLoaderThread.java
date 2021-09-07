package slike;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageLoaderThread extends Thread {

	File file_to_load;
	int currentFile;
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

		alive = true;
	}

	@Override
	public void run() {

		while (alive) {
			loadImageFile(currentFile);
			loadImageFile(currentFile - 1);
			loadImageFile(currentFile + 1);

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Shutting down thread...");

	}

	public BufferedImage getBufferedImage(int fileIndex) {
		this.currentFile = fileIndex;
		if (this.IMAGES_MAP.containsKey(listFiles[fileIndex]))
			return this.IMAGES_MAP.get(listFiles[fileIndex]);
		else {
			loadImageFile(fileIndex);
			return this.IMAGES_MAP.get(listFiles[fileIndex]);
		}
	}

	private void loadImageFile(int index) {

		if (index >= 0 && index < listFiles.length) {
			if (!this.IMAGES_MAP.containsKey(listFiles[index])) {
				try {
					BufferedImage temp_Image = ImageIO.read(listFiles[index]);
					synchronized (lock) {
						this.IMAGES_MAP.put(listFiles[index], temp_Image);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

}
