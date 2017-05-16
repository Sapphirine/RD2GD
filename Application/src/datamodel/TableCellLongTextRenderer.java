package datamodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;

public class TableCellLongTextRenderer extends DefaultTableCellRenderer
{

	private static final long serialVersionUID = 7920163334647774178L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
		final JTextArea jtext = new JTextArea();
		jtext.setText((String)value);
		jtext.setWrapStyleWord(true);
		jtext.setLineWrap(true);   
		jtext.setFont(table.getFont());
		jtext.setSize(table.getColumn(table.getColumnName(column)).getWidth(), (int)(jtext.getPreferredSize().getHeight() * 1.7));
		jtext.setMargin(new Insets(2,5,2,5));
      	table.setShowGrid(true);
      	table.setGridColor(Color.DARK_GRAY);
		return jtext;
	}
}