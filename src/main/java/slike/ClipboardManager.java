package slike;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;

public class ClipboardManager implements ClipboardOwner, Transferable
{

	Image image;
	java.util.List<File> listOfFiles;

	public void copyImage()
	{
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		c.setContents(this, this);
	}

	public void copyFile()
	{
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		c.setContents(this, this);
	}

	public void lostOwnership(Clipboard clip, Transferable trans)
	{
		System.out.println("Lost Clipboard Ownership");
	}

	public ClipboardManager(Image img)
	{
		this.image = img;
	}

	public ClipboardManager(java.util.List<File> listOfFiles)
	{
		this.listOfFiles = listOfFiles;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
	{
		if (flavor.equals(DataFlavor.imageFlavor) && image != null)
		{
			return image;
		} else if (flavor.equals(DataFlavor.javaFileListFlavor) && listOfFiles != null)
		{
			return listOfFiles;
		} else
		{
			return null;
		}
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		DataFlavor[] flavors = new DataFlavor[2];
		flavors[0] = DataFlavor.imageFlavor;
		flavors[1] = DataFlavor.javaFileListFlavor;
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		DataFlavor[] flavors = getTransferDataFlavors();
		for (int i = 0; i < flavors.length; i++)
		{
			if (flavor.equals(flavors[i]))
			{
				return true;
			}
		}

		return false;
	}
}