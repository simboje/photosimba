package slike;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
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
	BufferedImage displayImage;

	private boolean init = true;
	private AffineTransform coordTransform = new AffineTransform();

	int currentFile = 0;

	public MainPanel(String[] args) {

		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(openButton);
		this.add(fileLabel);
		this.add(testLabel);

		imageFilenameFilter = new ImageFilenameFilter();

		openButton.setFocusable(false);
		openButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				// TODO remove default test location
				fileChooser.setCurrentDirectory(new File("C:\\Users\\milojevic\\Desktop\\TESTDIRresize"));

				// nothing is selected by default and file chooser craps itself and OK button
				// does no work
				// forcing selected file to be equal to current directory when dialog is open
				// for the first time
				// still very bad 'solution' but it will do for testing
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
					displayImage = listOfImages[0]; // show first image
					// repaint seems to kick the GUI in the right spot and speeds up time for image
					// to appear on GUI
					repaint();
				} else {
					testLabel.setText("NULL");
				}
			}
		});

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == 'a') {
					if (currentFile > 0) {
						currentFile--;
						displayImage = listOfImages[currentFile];
						repaint();
					}
				}
				if (e.getKeyChar() == 'd') {
					if (currentFile < listOfFiles.length - 1) {
						currentFile++;
						displayImage = listOfImages[currentFile];
						repaint();
					}
				}
			}
		});
	}

	// TODO testing purposes!
	// need to move image load to background thread later
	private void openAllImages() {

		listOfImages = new BufferedImage[listOfFiles.length];
		for (int i = 0; i < listOfFiles.length; ++i) {
			try {
				long mili1 = System.currentTimeMillis();
				listOfImages[i] = ImageIO.read(listOfFiles[i]);
				long mili2 = System.currentTimeMillis();
				System.out.println("File " + listOfFiles[i] + " load time " + (mili2 - mili1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		if (displayImage != null) {
			Graphics2D g2 = (Graphics2D) g;
			int x = (int) (this.size().getWidth() - (displayImage.getWidth() * .2)) / 2;
			int y = (int) (this.size().getHeight() - (displayImage.getHeight() * .2)) / 2;

			AffineTransform at = new AffineTransform();
			at.translate(x, y);
			at.scale(.2, .2);
			if (init) {
				g2.setTransform(at);
				init = false;
				coordTransform = g2.getTransform();
			} else {
				g2.setTransform(coordTransform);
			}

			g2.drawImage(displayImage, 0, 0, this);

			g2.dispose();
		}
	}
}
