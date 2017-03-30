package datamodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import connection.DbConnect;

public class TableMetadata {
	
	public Table getMetadata(Table table)
	{
		String query = "SELECT count(*) FROM " + table.toString();
		
		try
		(
				Statement statement = DbConnect.getConnection().createStatement();
				ResultSet resultset = statement.executeQuery(query);
		)
		{
			if(resultset.next())
			{
				table.setRowCount(resultset.getInt(1));
			}
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(getClass().getResource("/icon/db_error.gif")));
		}
		return table;
	}
}
