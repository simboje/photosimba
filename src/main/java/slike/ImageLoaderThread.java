package slike;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;

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
					long mili1 = System.currentTimeMillis();
					BufferedImage temp_Image = ImageIO.read(imagePanel.getFile_list()[index]);
					this.IMAGES_MAP.put(imagePanel.getFile_list()[index], temp_Image);

					byte[] fileContent = Files.readAllBytes(imagePanel.getFile_list()[index].toPath());
					ByteArrayInputStream bais2 = new ByteArrayInputStream(fileContent);
					ImageInformation info = readImageInformation(bais2,
							this.IMAGES_MAP.get(imagePanel.getFile_list()[index]));

					imagePanel.setRotateCounter(info.orientation);
					
					imagePanel.setRotateCounter(info.orientation);

					long mili2 = System.currentTimeMillis();
					System.out.println(imagePanel.getFile_list()[index].getName() + " load time ms " + (mili2 - mili1)
							+ " rotation " + info.orientation);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (MetadataException e) {
					e.printStackTrace();
				} catch (ImageProcessingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public static AffineTransform getExifTransformation(ImageInformation info) {

		AffineTransform t = new AffineTransform();

		switch (info.orientation) {
		case 1:
			break;
		case 2: // Flip X
			t.scale(-1.0, 1.0);
			t.translate(-info.width, 0);
			break;
		case 3: // PI rotation
			t.translate(info.width, info.height);
			t.rotate(Math.PI);
			break;
		case 4: // Flip Y
			t.scale(1.0, -1.0);
			t.translate(0, -info.height);
			break;
		case 5: // - PI/2 and Flip X
			t.rotate(-Math.PI / 2);
			t.scale(-1.0, 1.0);
			break;
		case 6: // -PI/2 and -width
			t.translate(info.height, 0);
			t.rotate(Math.PI / 2);
			break;
		case 7: // PI/2 and Flip
			t.scale(-1.0, 1.0);
			t.translate(-info.height, 0);
			t.translate(0, info.width);
			t.rotate(3 * Math.PI / 2);
			break;
		case 8: // PI / 2
			t.translate(0, info.width);
			t.rotate(3 * Math.PI / 2);
			break;
		}

		return t;
	}

	public static ImageInformation readImageInformation(InputStream imageFileStream, BufferedImage image)  throws IOException, MetadataException, ImageProcessingException {
        Metadata metadata = ImageMetadataReader.readMetadata(imageFileStream);
        
        int orientation = 0;
        for (Directory directory:metadata.getDirectories())
        {
        	for(Tag tag:directory.getTags())
        	{
        		if (tag.getDescription().contains("Mirror horizontal"))
        		{
        			
        		} else
    			if (tag.getDescription().contains("Rotate 180"))
        		{
        			
        		} else
    			if (tag.getDescription().contains("Mirror horizontal and rotate 270 CW"))
        		{
        			
        		} else
    			if (tag.getDescription().contains("Rotate 90 CW"))
        		{
        			orientation++;
        		} else
    			if (tag.getDescription().contains("Mirror horizontal and rotate 90 CW"))
        		{
        			
        		} else
    			if (tag.getDescription().contains("Rotate 270 CW"))
        		{
    				orientation--;
        		}                				
        	}
		}

        int width= image.getWidth();
        int height= image.getHeight();

        return new ImageInformation(orientation, width, height);
    }

	// TODO remove as width, height are not used?
	
	// Inner class containing image information
	public static class ImageInformation {
		public final int orientation;
		public final int width;
		public final int height;

		public ImageInformation(int orientation, int width, int height) {
			this.orientation = orientation;
			this.width = width;
			this.height = height;
		}

		public String toString() {
			return String.format("%dx%d,%d", this.width, this.height, this.orientation);
		}
	}

}
