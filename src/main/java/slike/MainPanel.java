package slike;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
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
	File[] listOfFiles;
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
				
				if (imageLoaderThread!=null) {
					// shutdown thread for new directory
					imageLoaderThread.setAlive(false);
					currentFile = 0;
					
				}
				
				imageLoaderThread = new ImageLoaderThread(listOfFiles, 0);
				// start loading images
				imageLoaderThread.start();
				
				if (listOfFiles != null) {
					testLabel.setText(selectedDir.getAbsolutePath());
    				long mili1 = System.currentTimeMillis();                	
					displayImage = getDisplayImage(currentFile); // show first image
                	long mili2 = System.currentTimeMillis();
    				System.out.println("### DISPLAY IMAGE LOAD TIME IN ms " + (mili2-mili1));
					// repaint seems to kick the GUI in the right spot and speeds up time for image
					// to appear on GUI
					repaint();
				} else {
					testLabel.setText("NULL");
				}
			}
		});
		
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStartScreen = e.getPoint();
                dragEndScreen = null;
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
				if (e.getKeyChar() == 'a') {
					if (currentFile > 0) {
						currentFile--;
						long mili1 = System.currentTimeMillis();
						displayImage = getDisplayImage(currentFile);
						repaint();
	                	long mili2 = System.currentTimeMillis();
	    				System.out.println("### DISPLAY IMAGE LOAD TIME IN ms " + (mili2-mili1));
					}
				}
				if (e.getKeyChar() == 'd') {
					if (currentFile < listOfFiles.length - 1) {
						currentFile++;
						long mili1 = System.currentTimeMillis();
						displayImage = getDisplayImage(currentFile);
						repaint();
	                	long mili2 = System.currentTimeMillis();
	    				System.out.println("### DISPLAY IMAGE LOAD TIME IN ms " + (mili2-mili1));
					}
				}
			}
		});
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
				//at.setToRotation(90);
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
		if(imageLoaderThread!=null)
			imageLoaderThread.setAlive(false);
		
	}
}
