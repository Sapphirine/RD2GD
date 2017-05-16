package action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import datamodel.ETL;
import frame.AppMainFrame;

public class AddGraphPropertyAction extends AbstractAction 
{
	private static final long serialVersionUID = 2997366366682916219L;
	private String propertyType;
	private AppMainFrame parentFrame;

	public AddGraphPropertyAction(AppMainFrame parentFrame,String propertyType)
	{
		this.propertyType = propertyType;
		this.parentFrame = parentFrame;
		
		if(this.propertyType.equals("node"))
		{
			putValue(Action.NAME,"Add Node Property");
			putValue(Action.SHORT_DESCRIPTION,"Add properties to nodes");
			putValue(Action.MNEMONIC_KEY,KeyEvent.VK_N);
			putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.ALT_MASK));
			putValue(Action.SMALL_ICON,new ImageIcon(getClass().getResource("/icon/node_insert.png")));
		}
		else
		{
			putValue(Action.NAME,"Add Edge Property");
			putValue(Action.SHORT_DESCRIPTION,"Add properties to edges");
			putValue(Action.MNEMONIC_KEY,KeyEvent.VK_E);
			putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.ALT_MASK));
			putValue(Action.SMALL_ICON,new ImageIcon(getClass().getResource("/icon/edge_design.png")));
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		JLabel label = new JLabel("Please provide the property Name");
		JTextField textField = new JTextField(50);
		Component[] components = new Component[2];
		components[0] = label;
		components[1] = textField;
		Object propertyName = JOptionPane.showInputDialog(null,"Please provide the property name","Add Graph Property",
				JOptionPane.PLAIN_MESSAGE,new ImageIcon(getClass().getResource("/icon/global_network.png")),null,"");
		if(propertyName != null)
		{
			String propertyMunged = ((String)propertyName).replace(' ','_');
			ETL.addGraphProperty(propertyMunged, propertyType);
			parentFrame.fieldMappingPanel.renderModel();
		}
	}

}
