package action;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import connection.DbConnect;
import frame.AppMainFrame;
import frame.ConnectDialog;

/**
 * Action to perform when the menu item Connect 		
 * @author Jose Alvarado-Guzman
 * @version 1.0 2017-03-24
 */
public class ConnectAction extends AbstractAction {
	
	private static final long serialVersionUID = 1L;
	JDialog connectDialog = null;
	AppMainFrame parentFrame = null;
	
	/**
	 * Construct an action with the specify name, image and description. It also set the Mnemonic and accelerator key.  
	 * @param name
	 * @param icon
	 * @param description
	 */
	
	public ConnectAction(String name, ImageIcon icon, String description, AppMainFrame frame)
	{
		putValue(Action.NAME, name);
		putValue(Action.SMALL_ICON, icon);
		putValue(Action.SHORT_DESCRIPTION, description);
		putValue("frame", frame);
		parentFrame = frame;
		
		if(name.equals("Connect"))
		{
			putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
			putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.ALT_MASK));
		}
		else
		{
			putValue(Action.MNEMONIC_KEY,KeyEvent.VK_D);
			putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_D,ActionEvent.ALT_MASK));
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String action = (String) getValue(Action.NAME);
		if(action.equals("Connect"))
		{
			JFrame parentFrame = (JFrame) getValue("frame");
			if(connectDialog == null)
			{
				connectDialog = new ConnectDialog(parentFrame);
			}
			Point parentLocation = parentFrame.getLocation();
			int parentWidth = parentFrame.getWidth();
			int parentHeight = parentFrame.getHeight();
			int dialogx = parentLocation.x + (parentWidth/20);
			int dialogy = parentLocation.y + (parentHeight/8);
			connectDialog.setBounds(dialogx, dialogy, 400, 250);
			connectDialog.setVisible(true);
		}
		else
		{
			try {
				DbConnect.getConnection().close();
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(parentFrame, "There was an error closing the connection.", "Connection Error", JOptionPane.ERROR_MESSAGE);;
				e1.printStackTrace();
			}
			DbConnect.closeDataSource();
			parentFrame.getJMenuBar().getMenu(0).getItem(0).setEnabled(true);
			parentFrame.getJMenuBar().getMenu(0).getItem(1).setEnabled(false);
			parentFrame.getJMenuBar().getMenu(1).getItem(0).setEnabled(false);
			parentFrame.getJMenuBar().getMenu(1).getItem(1).setEnabled(false);
		}
	}

}