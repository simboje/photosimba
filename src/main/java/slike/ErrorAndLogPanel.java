package slike;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;

public class ErrorAndLogPanel
{

	private static boolean init = true;
	static JTree tree;
	static JTextArea textArea;
	static JScrollPane scrollPane;
	static JFrame frame;

	public static void updateUI()
	{
		if (init)
		{
			init = false;

			frame = new JFrame();
			frame.setMinimumSize(new Dimension(1000, 600));

			tree = new JTree();
			textArea = new JTextArea();
			scrollPane = new JScrollPane(textArea);
			
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);

			JButton copyButton = new JButton("Copy text to clipboard");
			copyButton.addMouseListener(new MouseAdapter()
			{

				@Override
				public void mousePressed(MouseEvent e)
				{
					StringSelection stringSelection = new StringSelection(textArea.getText());
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(stringSelection, null);
				}

			});

			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.add(copyButton, BorderLayout.PAGE_START);
			frame.add(scrollPane, BorderLayout.CENTER);
		}

		textArea.setText("");

		for (Exception exception : Logger.getLogExceptions())
		{
			for (StackTraceElement element : exception.getStackTrace())
			{
				textArea.append(element.toString() + "\n");
			}
		}

		for (String message : Logger.getLogMessages())
		{
			textArea.append(message);
			textArea.append("\n");
		}

		frame.setVisible(true);
		frame.repaint();

	}

}
