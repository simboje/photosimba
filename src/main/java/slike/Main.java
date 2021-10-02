package slike;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class Main
{

	public static void main(String[] args)
	{

		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e)
		{
			Logger.logException(e);
		}

		JFrame frame = new JFrame("SLIKE");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		ImageIcon icon = new ImageIcon("program_icon.png");

		JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.WHITE);

		JLabel fileIndexLabel = new JLabel("0/0");
		JLabel fileNameLabel = new JLabel("No file is loaded.");

		ImagePanel imagePanel = new ImagePanel(args, fileIndexLabel, fileNameLabel);
		imagePanel.setBackground(Color.WHITE);
		imagePanel.setFocusable(true);
		imagePanel.requestFocusInWindow();

		OpenButton openButton = new OpenButton("Open file or directory", imagePanel);

		topPanel.add(openButton, FlowLayout.LEFT);
		topPanel.add(fileIndexLabel, FlowLayout.CENTER);
		topPanel.add(fileNameLabel, FlowLayout.RIGHT);

		frame.add(topPanel, BorderLayout.PAGE_START);
		frame.add(imagePanel, BorderLayout.CENTER);

		frame.setVisible(true);
		frame.setIconImage(icon.getImage());
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);

		frame.addWindowListener(new java.awt.event.WindowAdapter()
		{
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent)
			{
				imagePanel.shutdownThread();
				Logger.saveLogsToFile();
			}
		});
	}
}
