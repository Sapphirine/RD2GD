package action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import datamodel.Table;
import frame.AppMainFrame;
import geo.Arrow;

public class ArrowCompleteAction extends AbstractAction 
{

	private static final long serialVersionUID = 4293050211343156724L;
	private boolean complete;
	private AppMainFrame parentFrame;
	
	public ArrowCompleteAction(AppMainFrame parentFrame, boolean complete, ImageIcon icon)
	{
		this.complete = complete;
		this.parentFrame = parentFrame;
		
		if(this.complete)
		{
			putValue(Action.NAME,"Mark As Complete");
			putValue(Action.SHORT_DESCRIPTION,"Mark arrow as complete");
			putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
			putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_G,ActionEvent.ALT_MASK));
		}
		else
		{
			putValue(Action.NAME,"Mark As Incomplete");
			putValue(Action.SHORT_DESCRIPTION,"Mark arrow as incomplete");
			putValue(Action.MNEMONIC_KEY,KeyEvent.VK_I);
			putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.ALT_MASK));
		}
		putValue(Action.SMALL_ICON,icon);
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(complete)
		{
			parentFrame.tableMappingPanel.markCompleted();
		}
		else
		{
			parentFrame.tableMappingPanel.unmarkCompleted();
		}
		if(parentFrame.tableMappingPanel.isMappingComplete())
		{
			parentFrame.getJMenuBar().getMenu(4).getItem(0).setEnabled(true);
		}
		else
		{
			parentFrame.getJMenuBar().getMenu(4).getItem(0).setEnabled(false);
		}
	}	
}
