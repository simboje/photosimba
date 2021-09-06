package slike;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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

	public MainPanel(String[] args) {

		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(openButton);
		this.add(fileLabel);
		this.add(testLabel);

		openButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fileChooser.setCurrentDirectory(new File("C:\\Users\\milojevic\\Desktop\\TESTDIRresize"));		// TODO remove default test location
				
				// nothing is selected by default and file chooser craps itself and OK button does no work
				// forcing selected file to be equal to current directory when dialog is open for the first time
				fileChooser.setSelectedFile(fileChooser.getCurrentDirectory().listFiles()[0].getParentFile());
				fileChooser.setCurrentDirectory(new File("C:\\Users\\milojevic\\Desktop\\TESTDIRresize"));		// after setting selected file need to re-set the directory!
				
				int result = fileChooser.showOpenDialog(getParent());
				selectedDir = fileChooser.getSelectedFile();
				if (result == JFileChooser.APPROVE_OPTION) {
					if (selectedDir.isDirectory())
						listOfFiles = selectedDir.listFiles();
					else {
						listOfFiles = selectedDir.getParentFile().listFiles();
					}
				}
				if (listOfFiles != null) {
					testLabel.setText(selectedDir.getAbsolutePath());
				} else {
					testLabel.setText("NULL");
				}
			}
		});

	}

}
