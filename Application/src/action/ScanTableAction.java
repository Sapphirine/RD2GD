package action;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import frame.AppMainFrame;
import frame.SelectTableDialog;

public class ScanTableAction extends AbstractAction {
	private static final long serialVersionUID = -8366772338562192201L;
	AppMainFrame parentFrame = null;
	
	public ScanTableAction(AppMainFrame frame)
	{
		putValue(Action.NAME,"Tables");
		putValue(Action.SMALL_ICON,new ImageIcon(getClass().getResource("/icon/tables.gif")));
		putValue(Action.SHORT_DESCRIPTION,"Select RDBMS Tables");
		putValue(Action.MNEMONIC_KEY,KeyEvent.VK_T);
		putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_T,ActionEvent.ALT_MASK));
		putValue("frame", frame);
		parentFrame = frame;
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		SelectTableDialog selectTable = new SelectTableDialog(parentFrame);
		Point parentLocation = parentFrame.getLocation();
		int parentWidth = parentFrame.getWidth();
		int parentHeight = parentFrame.getHeight();
		int dialogx = parentLocation.x + (parentWidth/20);
		int dialogy = parentLocation.y + (parentHeight/8);
		selectTable.setLocation(dialogx, dialogy);
		selectTable.setVisible(true);
	}
}
