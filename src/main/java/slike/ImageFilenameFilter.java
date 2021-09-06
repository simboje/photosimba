package slike;

import java.io.File;
import java.io.FilenameFilter;

public class ImageFilenameFilter implements FilenameFilter {
	
	// TODO
	// Check https://github.com/haraldk/TwelveMonkeys
	// and support more image formats

	@Override
	public boolean accept(File dir, String name) {
		String lowercaseExtension = name.toLowerCase();
		if (lowercaseExtension.endsWith(".jpg") || lowercaseExtension.endsWith(".jpeg")
				|| lowercaseExtension.endsWith(".png") || lowercaseExtension.endsWith(".bmp")
				|| lowercaseExtension.endsWith(".gif"))
			return true;
		else {
			return false;
		}
	}

}
