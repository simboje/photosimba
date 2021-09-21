package slike;

import java.awt.image.BufferedImage;

public class ImageData
{

	BufferedImage image;
	int rotation = 0;

	public ImageData(BufferedImage image)
	{
		this.image = image;
	}

	public void setRotation(int rotation)
	{
		this.rotation = rotation;
	}

	public int getRotation()
	{
		return rotation;
	}

	public BufferedImage getImage()
	{
		return image;
	}

	public void setImage(BufferedImage image)
	{
		this.image = image;
	}
}
