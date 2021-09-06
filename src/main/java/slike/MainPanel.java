package slike;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Label;

import javax.swing.JPanel;

public class MainPanel extends JPanel {

	Button openButton = new Button("Open file or directory");
	Label fileLabel = new Label("File name");
	Label testLabel = new Label("Test info");

	public MainPanel(String[] args) {

		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(openButton);
		this.add(fileLabel);
		this.add(testLabel);

	}

}
