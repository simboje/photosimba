package slike;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainPanel extends JPanel {

	JButton openButton = new JButton("Open file or directory");
	JLabel fileLabel = new JLabel("File name");
	JLabel testLabel = new JLabel("Test info");

	File selectedDir;
	File[] listOfFiles;
	BufferedImage[] listOfImages;
	ImageFilenameFilter imageFilenameFilter;

	public MainPanel(String[] args) {

		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(openButton);
		this.add(fileLabel);
		this.add(testLabel);
		
		imageFilenameFilter = new ImageFilenameFilter();

		openButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				// TODO remove default test location
				fileChooser.setCurrentDirectory(new File("C:\\Users\\milojevic\\Desktop\\TESTDIRresize"));

				// nothing is selected by default and file chooser craps itself and OK button
				// does no work
				// forcing selected file to be equal to current directory when dialog is open
				// for the first time
				fileChooser.setSelectedFile(fileChooser.getCurrentDirectory().listFiles()[0].getParentFile());
				fileChooser.setCurrentDirectory(new File("C:\\Users\\milojevic\\Desktop\\TESTDIRresize"));
				// after setting selected file need to re-set the directory!

				int result = fileChooser.showOpenDialog(getParent());
				selectedDir = fileChooser.getSelectedFile();
				if (result == JFileChooser.APPROVE_OPTION) {
					if (selectedDir.isDirectory())
						listOfFiles = selectedDir.listFiles(imageFilenameFilter);
					else {
						listOfFiles = selectedDir.getParentFile().listFiles(imageFilenameFilter);
					}
				}
				if (listOfFiles != null) {
					testLabel.setText(selectedDir.getAbsolutePath());
					openAllImages();
				} else {
					testLabel.setText("NULL");
				}
			}
		});

	}

	private void openAllImages() {

		listOfImages = new BufferedImage[listOfFiles.length];
		for (int i = 0; i < listOfFiles.length; ++i) {
			try {
				long mili1 = System.currentTimeMillis();
				listOfImages[i] = ImageIO.read(listOfFiles[i]);
				long mili2 = System.currentTimeMillis();
				System.out.println("File " + listOfFiles[i] + " read time " + (mili2 - mili1));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
