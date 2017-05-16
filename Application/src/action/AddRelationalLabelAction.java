package action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import datamodel.ETL;
import datamodel.Field;
import datamodel.Table;
import datamodel.Value;
import frame.AppMainFrame;
import geo.LabeledRectangle;

public class AddRelationalLabelAction extends AbstractAction 
{
		
	private static final long serialVersionUID = 7866643653860108347L;
	private AppMainFrame parentFrame;
	
	public AddRelationalLabelAction(AppMainFrame parentFrame)
	{	
			putValue(Action.NAME,"Add Constant Label Field");
			putValue(Action.SHORT_DESCRIPTION,"Add constant label field");
			putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
			putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_L,ActionEvent.ALT_MASK));
			putValue(Action.SMALL_ICON,new ImageIcon(getClass().getResource("/icon/property.png")));
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object label = JOptionPane.showInputDialog(null,"Please provide the label name","Add Constant Label Field",
				JOptionPane.PLAIN_MESSAGE,new ImageIcon(getClass().getResource("/icon/global_network.png")),null,"");
		
		if(label != null)
		{
			String labelMunged = ((String)label).replace(' ', '_');
			List<LabeledRectangle<Table>> visibleRectangles = parentFrame.tableMappingPanel.getVisibleSourceComponents();
			Table table = visibleRectangles.get(0).getItem();
			Field constantField = new Field(table.getDatabase(), table, labelMunged, "UserConstant", "User define constant value");
			constantField.addValue(new Value(table.getDatabase(), table, constantField, labelMunged, 0));
			table.addField(constantField);
			parentFrame.fieldMappingPanel.renderModel();
		}
	}

}
