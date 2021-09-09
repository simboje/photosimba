package slike;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageLoaderThread extends Thread {

	int currentFile = -1;
	ImagePanel imagePanel;

	private Map<File, BufferedImage> IMAGES_MAP = new HashMap<File, BufferedImage>();
	private boolean alive;

	public ImageLoaderThread(ImagePanel imagePanel) {
		this.imagePanel = imagePanel;

		alive = true;
	}

	@Override
	public void run() {

		while (alive) {
			if (imagePanel.currentFile != currentFile) {
				loadImageFile(imagePanel.currentFile);
				this.currentFile = imagePanel.currentFile;
				imagePanel.notifyAboutNewImage();
			} else {
				loadImageFile(currentFile - 1);
				loadImageFile(currentFile + 1);
			}

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

		if (!this.IMAGES_MAP.containsKey(imagePanel.getFile_list()[fileIndex]))
			loadImageFile(fileIndex);

		return this.IMAGES_MAP.get(imagePanel.getFile_list()[fileIndex]);
	}

	private void loadImageFile(int index) {

		if (index >= 0 && index < imagePanel.getFile_list().length) {
			if (!this.IMAGES_MAP.containsKey(imagePanel.getFile_list()[index])) {
				try {
					BufferedImage temp_Image = ImageIO.read(imagePanel.getFile_list()[index]);
					this.IMAGES_MAP.put(imagePanel.getFile_list()[index], temp_Image);
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
