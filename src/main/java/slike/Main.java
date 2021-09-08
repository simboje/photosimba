package slike;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class Main {

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JFrame frame = new JFrame("SLIKE");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.WHITE);

		JLabel testLabel = new JLabel("test");

		ImagePanel imagePanel = new ImagePanel(args, testLabel);
		imagePanel.setBackground(Color.WHITE);
		imagePanel.setFocusable(true);
		imagePanel.requestFocusInWindow();

		OpenButton openButton = new OpenButton("Open file or directory", imagePanel);

		topPanel.add(openButton, FlowLayout.LEFT);
		topPanel.add(testLabel, FlowLayout.CENTER);
		frame.add(topPanel, BorderLayout.PAGE_START);
		frame.add(imagePanel, BorderLayout.CENTER);

		frame.setVisible(true);
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				imagePanel.shutdownThread();
			}
		});
	}
}
