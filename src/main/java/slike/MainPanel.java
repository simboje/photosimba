package slike;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {

	JButton openButton = new JButton("Open file or directory");
	JLabel fileLabel = new JLabel("File name");
	JLabel testLabel = new JLabel("Test info");

	File selectedDir;
	File[] file_list;
	ImageFilenameFilter imageFilenameFilter;
	BufferedImage displayImage;

	private boolean init = true;
	private AffineTransform coordTransform = new AffineTransform();

	int currentFile = 0;

	private Point dragStartScreen;
	private Point dragEndScreen;

	private int zoomLevel = 0;
	private int minZoomLevel = -20;
	private int maxZoomLevel = 10;
	private double zoomMultiplicationFactor = 1.2;

	ImageLoaderThread imageLoaderThread;
	private int rotateCounter = 0;

	public MainPanel(String[] args) {

		imageFilenameFilter = new ImageFilenameFilter();

		if (args.length == 1) {
			File selectedFile = new File(args[0]);
			selectedDir = selectedFile.getParentFile();
			file_list = selectedDir.listFiles(imageFilenameFilter);
			currentFile = findFileIndex(file_list, selectedFile);
			imageLoaderThread = new ImageLoaderThread(file_list, currentFile);
			// start loading images
			imageLoaderThread.start();

			if (file_list != null) {
				testLabel.setText(selectedDir.getAbsolutePath());
				long mili1 = System.currentTimeMillis();
				displayImage = getDisplayImage(currentFile); // show first image
				// repaint seems to kick the GUI in the right spot and speeds up time for image
				// to appear on GUI
				repaint();
				long mili2 = System.currentTimeMillis();
				// TODO investigate delay when image is shown for the first time
				System.out.println("### DISPLAY IMAGE LOAD TIME IN ms " + (mili2 - mili1) + " repaint");
			} else {
				testLabel.setText("NULL");
			}
		}

		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(openButton);
		this.add(fileLabel);
		this.add(testLabel);

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				init = true;
				repaint();
			}
		});

		openButton.setFocusable(false);
		openButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				// TODO nothing is selected by default and file chooser craps itself and OK button
				// does not work

				int result = fileChooser.showOpenDialog(getParent());
				selectedDir = fileChooser.getSelectedFile();
				if (result == JFileChooser.APPROVE_OPTION) {
					if (selectedDir.isDirectory())
						file_list = selectedDir.listFiles(imageFilenameFilter);
					else {
						file_list = selectedDir.getParentFile().listFiles(imageFilenameFilter);
					}
				}

				if (imageLoaderThread != null) {
					// shutdown thread for new directory
					imageLoaderThread.setAlive(false);
					currentFile = 0;
				}

				imageLoaderThread = new ImageLoaderThread(file_list, 0);
				// start loading images
				imageLoaderThread.start();

				if (file_list != null) {
					testLabel.setText(selectedDir.getAbsolutePath());
					long mili1 = System.currentTimeMillis();
					displayImage = getDisplayImage(currentFile); // show first image
					long mili2 = System.currentTimeMillis();
					// repaint seems to kick the GUI in the right spot and speeds up time for image
					// to appear on GUI
					repaint();
					System.out.println("### DISPLAY IMAGE LOAD TIME IN ms " + (mili2 - mili1) + " repaint");
				} else {
					testLabel.setText("NULL");
				}
			}
		});

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					dragStartScreen = e.getPoint();
					dragEndScreen = null;
				} else if (e.getButton() == MouseEvent.BUTTON2) { // scroll button click - reset image to initial size
					init = true;
					repaint();
				}

			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				pan(e);
			}
		});
		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				zoom(e);
			}
		});

		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 65 || e.getKeyCode() == 37) { // go left
					if (currentFile > 0) {
						currentFile--;
						long mili1 = System.currentTimeMillis();
						displayImage = getDisplayImage(currentFile);
						rotateCounter = 0;
						repaint();
						long mili2 = System.currentTimeMillis();
						System.out.println("### DISPLAY IMAGE LOAD TIME IN ms " + (mili2 - mili1) + " rep");
					}
				} else if (e.getKeyCode() == 68 || e.getKeyCode() == 39) { // go right
					if (currentFile < file_list.length - 1) {
						currentFile++;
						long mili1 = System.currentTimeMillis();
						displayImage = getDisplayImage(currentFile);
						rotateCounter = 0;
						
						//repaint();
						
						// https://pavelfatin.com/low-latency-painting-in-awt-and-swing/
						custompaint();
						
						// well...even the built in photos struggle here
						// image load is super fast but with low quality
						// it quickly loads full quality but this transition is very noticeable 
						
						long mili2 = System.currentTimeMillis();
						System.out.println("### DISPLAY IMAGE LOAD TIME IN ms " + (mili2 - mili1) + " rep");
					}
				} else if (e.getKeyCode() == 87 || e.getKeyCode() == 38) { // w or UP - rotate counter clockwise
					coordTransform.quadrantRotate(-1, displayImage.getWidth() / 2, displayImage.getHeight() / 2);
					rotateCounter--;
					repaint();
				} else if (e.getKeyCode() == 83 || e.getKeyCode() == 40) { // s or DOWN -rotate clockwise
					coordTransform.quadrantRotate(1, displayImage.getWidth() / 2, displayImage.getHeight() / 2);
					rotateCounter++;
					repaint();
				} else if (e.getKeyCode() == 67 && e.isControlDown() && e.isShiftDown()) {
					// c = 67, copy file to clipboard
					List<File> listOfFiles = new ArrayList<File>();
					listOfFiles.add(file_list[currentFile]);

					ClipboardManager ci = new ClipboardManager(listOfFiles);
					ci.copyFile();

				} else if (e.getKeyCode() == 67 && e.isControlDown()) {
					// c = 67, copy image to clipboard
					ClipboardManager ci = new ClipboardManager(displayImage);
					ci.copyImage();
				}

			}
		});
	}

	protected void custompaint() {
		paintImmediately(0, 0, this.getWidth(), this.getHeight());
		/*
		Graphics g = getGraphics();
		this.paintComponent(g);
		if (displayImage != null) {
			Graphics2D g2 = (Graphics2D) g;

			if (init) {
				AffineTransform at = calculateScale();
				at.quadrantRotate(rotateCounter, displayImage.getWidth() / 2, displayImage.getHeight() / 2);
				g2.setTransform(at);
				init = false;
				coordTransform = g2.getTransform();
			} else {
				g2.setTransform(coordTransform);
			}

			g2.drawImage(displayImage, 0, 0, this);

			g2.dispose();
		}
		*/
	}

	private int findFileIndex(File[] flist, File selectedFile) {
		for (int i = 0; i < flist.length; ++i) {
			if (flist[i].equals(selectedFile))
				return i;
		}
		return 0;
	}

	private BufferedImage getDisplayImage(int currentFile) {
		init = true;
		return imageLoaderThread.getBufferedImage(currentFile);
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		if (displayImage != null) {
			Graphics2D g2 = (Graphics2D) g;

			if (init) {
				AffineTransform at = calculateScale();
				at.quadrantRotate(rotateCounter, displayImage.getWidth() / 2, displayImage.getHeight() / 2);
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

	private AffineTransform calculateScale() {
		AffineTransform at = new AffineTransform();
		int x = 0;
		int y = 0;
		float xscale = (float) this.getWidth() / displayImage.getWidth();
		float yscale = (float) this.getHeight() / displayImage.getHeight();
		if (xscale > yscale) {
			// in this case image is 'taller' than the frame
			// need to scale image on y axis in order to fit in the frame from inside
			at.scale(yscale, yscale);
			// af applies (multiplies) scale to x so need to pre-empt this and divide with
			// yscale
			// final x value in at will then have expected value
			x = (int) ((this.getWidth() - displayImage.getWidth() * yscale) / yscale / 2);
		}

		else {
			// in this case image is 'wider' than the frame
			// need to scale image on y axis
			at.scale(xscale, xscale);
			y = (int) ((this.getHeight() - displayImage.getHeight() * xscale) / xscale / 2); // scale fix
		}
		at.translate(x, y);

		// initial testing looks good, image is scaled properly, centered on x or y axis
		// and fills in frame properly
		return at;
	}

	private void pan(MouseEvent e) {
		try {
			dragEndScreen = e.getPoint();
			Point2D.Float dragStart = transformPoint(dragStartScreen);
			Point2D.Float dragEnd = transformPoint(dragEndScreen);
			double dx = dragEnd.getX() - dragStart.getX();
			double dy = dragEnd.getY() - dragStart.getY();
			coordTransform.translate(dx, dy);
			dragStartScreen = dragEndScreen;
			dragEndScreen = null;
			repaint();
		} catch (NoninvertibleTransformException ex) {
			ex.printStackTrace();
		}
	}

	private void zoom(MouseWheelEvent e) {
		try {
			int wheelRotation = e.getWheelRotation();
			Point p = e.getPoint();
			if (wheelRotation > 0) {
				if (zoomLevel < maxZoomLevel) {
					zoomLevel++;
					Point2D p1 = transformPoint(p);
					coordTransform.scale(1 / zoomMultiplicationFactor, 1 / zoomMultiplicationFactor);
					Point2D p2 = transformPoint(p);
					coordTransform.translate(p2.getX() - p1.getX(), p2.getY() - p1.getY());
					repaint();
				}
			} else {
				if (zoomLevel > minZoomLevel) {
					zoomLevel--;
					Point2D p1 = transformPoint(p);
					coordTransform.scale(zoomMultiplicationFactor, zoomMultiplicationFactor);
					Point2D p2 = transformPoint(p);
					coordTransform.translate(p2.getX() - p1.getX(), p2.getY() - p1.getY());
					repaint();
				}
			}
		} catch (NoninvertibleTransformException ex) {
			ex.printStackTrace();
		}
	}

	private Point2D.Float transformPoint(Point p1) throws NoninvertibleTransformException {
		AffineTransform inverse = coordTransform.createInverse();
		Point2D.Float p2 = new Point2D.Float();
		inverse.transform(p1, p2);
		return p2;
	}

	public void shutdownThread() {
		if (imageLoaderThread != null)
			imageLoaderThread.setAlive(false);

	}
}
