package slike;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;

@SuppressWarnings("serial")
public class OpenButton extends JButton
{

	private File lastVisitedLocation;

	public OpenButton(String string, ImagePanel imagePanel)
	{

		super(string);

		this.setFocusable(false);
		this.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mousePressed(MouseEvent e)
			{
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				// TODO nothing is selected by default and file chooser craps itself and OK
				// button does not work
				ImageFilenameFilter imageFilenameFilter = new ImageFilenameFilter();
				if (lastVisitedLocation != null)
				{
					fileChooser.setCurrentDirectory(lastVisitedLocation);
				}

				File[] file_list;
				int result = fileChooser.showOpenDialog(getParent());
				File selectedFileOrDir = fileChooser.getSelectedFile();
				if (result == JFileChooser.APPROVE_OPTION)
				{
					if (selectedFileOrDir.isDirectory())
					{
						file_list = selectedFileOrDir.listFiles(imageFilenameFilter);
						lastVisitedLocation = selectedFileOrDir.getParentFile();
					}

					else
					{
						file_list = selectedFileOrDir.getParentFile().listFiles(imageFilenameFilter);
						lastVisitedLocation = selectedFileOrDir.getParentFile().getParentFile();
					}

					imagePanel.loadFiles(file_list);
				}
			}
		});

	}
}
