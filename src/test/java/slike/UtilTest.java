package slike;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.mockito.Mock;

import com.sun.jna.platform.FileUtils;
import com.sun.jna.platform.win32.W32FileUtils;

import static org.mockito.Mockito.*;	// need it for mock()

public class UtilTest
{
	// File delete tests - START
	@Mock
	FileUtils fileUtils = mock(W32FileUtils.class);

	@Test
	public void testSendFileToRecycleBinWorks() throws IOException
	{
		File file = new File("dummy.txt");
		file.createNewFile();
		Util.fileUtils = new W32FileUtils();
		assertTrue(file.exists());
		assertTrue(Util.sendFileToRecycleBin(file));
		assertTrue(!file.exists());
	}

	@Test
	public void testSendFileToRecycleBinNoTrash() throws IOException
	{
		File file = new File("dummy_no_trash.txt");
		file.createNewFile();
		assertTrue(file.exists());
		Util.fileUtils = fileUtils;
		when(fileUtils.hasTrash()).thenReturn(false);
		assertFalse(Util.sendFileToRecycleBin(file));
		assertEquals("No Trash available. Failed to delete " + file, Logger.getLogMessages().get(0));
		assertTrue(file.exists());
		file.delete();
		assertTrue(!file.exists());
		
	}
	
	@Mock
	FileUtils fileUtilsExc = new W32FileUtils() {

		@Override
		public void moveToTrash(File... files) throws IOException
		{
			throw new IOException("IOexc");
		}
		
	};
	
	@Test
	public void testSendFileToRecycleBinException() throws IOException
	{
		Logger.SHOW_UI = false;
		File file = new File("dummy_e.txt");
		file.createNewFile();
		assertTrue(file.exists());

		Util.fileUtils = fileUtilsExc;
		assertFalse(Util.sendFileToRecycleBin(file));
		assertEquals("IOexc", Logger.getLogExceptions().get(0).getMessage());
		assertTrue(file.exists());
		file.delete();
		assertTrue(!file.exists());
	}
	// File delete tests - START
	
	// Fix Cyrillic Path tests - START
	@Test
	public void testPathResolve() throws IOException
	{
		File fileOfInterest = new File("dir1/testŠĐŽČĆšđžčć/mainfile.txt");
		fileOfInterest.getParentFile().mkdirs();
		fileOfInterest.createNewFile();
		
		File distractionFile = new File("dir1/tesstĐŽČĆšđžčć/mainfile.txt");
		distractionFile.getParentFile().mkdirs();
		distractionFile.createNewFile();
		
		File relativeFile = new File("dir1/testSDZCCsdzcc/mainfile.txt");
		File foundFile = Util.findCyrillicPath(relativeFile.getAbsolutePath());
		assertEquals(fileOfInterest.getAbsoluteFile(), foundFile);
		
		fileOfInterest.delete();
		distractionFile.delete();
		distractionFile.getParentFile().delete();
		fileOfInterest.getParentFile().delete();
		fileOfInterest.getParentFile().getParentFile().delete();
		
	}
	// Fix Cyrillic Path tests - END
}
