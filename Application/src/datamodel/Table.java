package datamodel;

import java.util.Set;
import java.util.TreeSet;

import connection.DbConnect;

public class Table implements Comparable<Table>{
	private String name;
	private String type;
	private int rowCount;
	private Database database;
	private Set<Field> fields;
	private String schema;
	
	{
		fields = new TreeSet<>();
	}
	
	public Table(Database db, String name, String type)
	{
		this.database = db;
		this.name = name;
		this.type = type;
		
	}
	public Table(Database db, String name, String type, String schema)
	{
		this(db, name, type);
		this.schema = schema;
	}
	public Table(Database db, String name, String type, int rowCount)
	{
		this(db, name, type);
		this.rowCount = rowCount;
	}
	public Table(Database db, String name, String type, int rowCount, String schema)
	{
		this(db, name, type, rowCount);
		this.schema = schema;
	}
	
	public void addField(Field field)
	{
		fields.add(field);
	}
	
	public void setfields(TreeSet<Field> fields)
	{
		this.fields = fields;
	}
	public void setRowCount(int count)
	{
		rowCount = count;
	}
	
	public Set<Field> getFields()
	{
		return fields;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getType()
	{
		return type;
	}
	
	public int getRowCount()
	{
		return rowCount;
	}
	
	public Database getDatabase(){
		return database;
	}
	
	public String getSchema()
	{
		return schema;
	}
	
	public void removeField(Field field)
	{
		fields.remove(field);
	}
	
	@Override
	public String toString()
	{
		String name = this.name;
		if(schema!=null)
		{
			switch(DbConnect.getDbType())
			{
				case MSACCESS:
					name = "[" + name + "]";
					break;
				case MSSQL:
					name = "[" + schema + "]" + ".[" + name + "];";
					break;
				default:
					name = schema.concat(".").concat(name);
			} 
		}
		return name;
	}
	@Override
	public int compareTo(Table t)
	{
		return toString().compareTo(t.toString());
	}
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Table && ((Table)o).toString().equals(this.toString()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}