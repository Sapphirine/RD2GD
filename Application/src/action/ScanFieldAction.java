package action;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import frame.AppMainFrame;
import frame.SelectFieldDialog;

public class ScanFieldAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2419774357528665050L;
	AppMainFrame parentFrame = null;
	
	public ScanFieldAction(AppMainFrame frame)
	{
		putValue(Action.NAME,"Fields");
		putValue(Action.SMALL_ICON,new ImageIcon(getClass().getResource("/icon/fields.png")));
		putValue(Action.SHORT_DESCRIPTION,"Add RDBMS Fields");
		putValue(Action.MNEMONIC_KEY,KeyEvent.VK_F);
		putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_F,ActionEvent.ALT_MASK));
		putValue("frame", frame);
		parentFrame = frame;
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		SelectFieldDialog selectField = new SelectFieldDialog(parentFrame);
		Point parentLocation = parentFrame.getLocation();
		int parentWidth = parentFrame.getWidth();
		int parentHeight = parentFrame.getHeight();
		int dialogx = parentLocation.x + (parentWidth/20);
		int dialogy = parentLocation.y + (parentHeight/8);
		selectField.setLocation(dialogx, dialogy);
		selectField.setVisible(true);
	}

}
