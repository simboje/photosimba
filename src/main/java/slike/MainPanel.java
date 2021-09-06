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
	
    private Point dragStartScreen;
    private Point dragEndScreen;
    
    private int zoomLevel = 0;
    private int minZoomLevel = -20;
    private int maxZoomLevel = 10;
    private double zoomMultiplicationFactor = 1.2;

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

			if (init) {
				AffineTransform at = new AffineTransform();
				at.translate(x, y);
				at.scale(.2, .2);
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
}
