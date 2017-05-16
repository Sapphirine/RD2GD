package action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.concurrent.Semaphore;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import cpu.Processor;
import datamodel.ETL;
import datamodel.Field;
import datamodel.FieldMetadata;
import datamodel.Table;

public class FieldStatisticsAction extends AbstractAction {

	private static final long serialVersionUID = 2796401963183883192L;
	
	public FieldStatisticsAction()
	{
		putValue(Action.NAME,"Generate Field Statistics");
		putValue(Action.SHORT_DESCRIPTION,"Generate field descriptive statistics");
		putValue(Action.MNEMONIC_KEY,KeyEvent.VK_S);
		putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.ALT_MASK));
		putValue(Action.SMALL_ICON,new ImageIcon(getClass().getResource("/icon/table_sum.png")));
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Set<Table> tables = ETL.getRelationalTables();
		for(Table table : tables)
		{
			for(Field field : table.getFields())
			{
				new FieldMetadata(field, new Semaphore(Processor.getNumCores()));
			}
		}
	}

}
