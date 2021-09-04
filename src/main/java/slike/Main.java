package slike;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {

		JFrame frame = new JFrame("SLIKE");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		MainPanel panel = new MainPanel(args);
		frame.add(panel, BorderLayout.CENTER);
		frame.pack();

		panel.setFocusable(true);
		panel.requestFocusInWindow();

		frame.setVisible(true);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);

	}

}
