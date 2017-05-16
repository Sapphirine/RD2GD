package datamodel;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import connection.DbConnect;

public class PopulateDataFile extends Thread 
{
	private DataFile 				file;
	private Map<Field, Set<Field>> 	fieldMapping;
	private Semaphore 				cores;
	private List<String> 			header;
	private String 					query;
	private CountDownLatch			countDown;	
	
	public PopulateDataFile(DataFile file, Semaphore semaphore, CountDownLatch countDown)
	{
		this.file = file;
		this.cores = semaphore;
		this.countDown = countDown;
		this.fieldMapping = ETL.getMappingFields().get(file.getItemToItemMap()).getR2G();
		header = new ArrayList<>();
		this.setQuery();
		this.start();
	}
	@Override
	public void run()
	{
		cores.acquireUninterruptibly();
		System.out.println("Working with file:" + file.getFileName() + " of type:" + file.getType());
		for(Field relationalField : fieldMapping.keySet())
		{
			for(Field graphEntity: fieldMapping.get(relationalField))
			{
				switch(graphEntity.getName())
				{
					case "id":
						header.add(relationalField.getName().concat(":ID"));
						break;
					case "node_id1":
						header.add(relationalField.getName().concat(":START_ID"));
						break;
					case "node_id2":
						header.add(relationalField.getName().concat(":END_ID"));
						break;
					case "label":
						break;
					default:
						header.add(graphEntity.getName());
				}
				try
				(
					PrintWriter writer = new PrintWriter(file.getFile());
					Connection conn = DbConnect.getConnection();
					Statement statement = conn.createStatement();
					ResultSet result = statement.executeQuery(query);
				)
				{
					writer.println(constructHeader());
					int cols = result.getMetaData().getColumnCount();
					while(result.next())
					{
						for(int i=1; i <= cols; i++)
						{
							writer.printf("%2$s%1$s%2$s",result.getObject(i).toString(),"\"");
							if(i < cols)
							{
								writer.write(",");
							}
						}
						writer.println("");
					}
					writer.flush();
					countDown.countDown();
				}
				catch (FileNotFoundException | SQLException e ) 
				{
					JOptionPane.showMessageDialog(null, e.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE,
							new ImageIcon(getClass().getResource("/icon/db_error.gif")));
					e.printStackTrace();
				}
			};
		}
		cores.release();
	}
	private String constructHeader()
	{
		String line = "";
		for(int i=0; i < header.size(); i++)
		{
			line += header.get(i);
			if(i < header.size() - 1)
			{
				line += ",";
			}
		}
		return line;
	}
	private void setQuery()
	{
		query = "SELECT ";
		String table = file.getItemToItemMap().getRelationalItem().getName();
		Set<Field> relationalFields = fieldMapping.keySet();
		int numberOfFields = relationalFields.size();
		int i = 0;
		for(Field relationalField : relationalFields)
		{
			i += 1;
			if(!relationalField.getType().equals("UserConstant"))
			{
				query += relationalField.getName();
				if(i < numberOfFields)
				{
					query += ", ";
				}
			}
		}
		query += " from ".concat(table);
	}
}
