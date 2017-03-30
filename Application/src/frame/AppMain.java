package frame;

import java.awt.EventQueue;

import javax.swing.JFrame;

/**
 * @author Jose Alvarado-Guzman
 * @version 1.0 2017-03-24
 * Instantiate the application main frame 
 */
public class AppMain {
	public static void main(String[] args)
	{
		EventQueue.invokeLater(
				new Runnable()
				{
					public void run()
					{
						JFrame appFrame = new AppMainFrame();
						appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						appFrame.setTitle("RD2GD");
						
						appFrame.setVisible(true);
					}
				}
		);
	}
}