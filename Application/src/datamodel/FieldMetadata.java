package datamodel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import connection.DbConnect;

public class FieldMetadata extends Thread{
	Field field;
	Semaphore cores;
	
	public FieldMetadata(Field field, Semaphore cores)
	{
		this.field = field;
		this.cores = cores;
		setName(field.toString());
		this.start();
	}
	
	@Override
	public void run()
	{
		System.out.println("Processing field:" + getName());
		cores.acquireUninterruptibly();
		getMetadata();
		cores.release();
		System.out.println("Finished processing filed:" + getName());
	}
	public void getMetadata()
	{
		String query = "SELECT " + field.getName() + ", count(*) freq FROM " + field.getTable().toString() + 
				" group by " + field.getName();
		try
		(
				Connection connection = DbConnect.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query);
		)
		{
			//System.out.println("Results for field:" + field.getName());
			while(resultSet.next())
			{
				//System.out.println(resultSet.getString(1) + ": " + resultSet.getInt(2));
				Value value = new Value(field.getDatabase(), field.getTable(), field, resultSet.getString(1), 
						resultSet.getInt(2));
				field.addValue(value);
			}
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(getClass().getResource("/icon/db_error.gif")));
		}
		
	}
}
