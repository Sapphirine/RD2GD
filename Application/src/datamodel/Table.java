package datamodel;

import java.sql.ResultSet;
import java.util.Set;
import java.util.TreeSet;

import connection.DbConnect;

public class Table implements Comparable<Table>, MappableItem{
	private String name;
	private String type;
	private String comment;
	private String description;
	private int rowCount;
	private Database database;
	private Set<Field> fields;
	private String schema;
	private ResultSet primaryKeyInfo;
	private ResultSet uniqueIndexInfo;
	private ResultSet indexInfo;
	
	{
		fields = new TreeSet<>();
	}
	
	public Table(Database db, String name, String type, String comment)
	{
		this.database = db;
		this.name = name.toLowerCase();
		this.type = type;
		this.comment = "";
		this.description = "";
		this.comment = comment;
	}
	public Table(Database db, String name, String type, String schema, String description)
	{
		this(db, name, type, description);
		this.schema = schema;
	}
	/*public Table(Database db, String name, String type, int rowCount)
	{
		this(db, name, type);
		this.rowCount = rowCount;
	}
	public Table(Database db, String name, String type, int rowCount, String schema)
	{
		this(db, name, type,rowCount);
		this.schema = schema;
	}
	public Table(Database db, String name, String type, int rowCount, ResultSet primaryKeyInfo, 
			ResultSet uniqueIndexInfo, ResultSet indexInfo)
	{
		this(db, name, type,rowCount);
		this.primaryKeyInfo = primaryKeyInfo;
		this.uniqueIndexInfo = uniqueIndexInfo;
		this.indexInfo = indexInfo;
	}*/
	
	public void addField(Field field)
	{
		fields.add(field);
	}
	
	public void setfields(Set<Field> fields)
	{
		this.fields = fields;
	}
	public void setRowCount(int count)
	{
		rowCount = count;
	}
	public void setPrimaryKeyInfo(ResultSet primaryKeyInfo)
	{
		this.primaryKeyInfo = primaryKeyInfo;
	}
	public void setUniqueIndexInfo(ResultSet uniqueIndexInfo)
	{
		this.uniqueIndexInfo = uniqueIndexInfo;
	}
	public void setIndexInfo(ResultSet indexInfo)
	{
		this.indexInfo = indexInfo;
	}
	
	public Set<Field> getFields()
	{
		return fields;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	public void setDescriotion(String description)
	{
		this.description = description;
	}
	
	public String getComment()
	{
		return this.comment;
	}
	public void setComment(String comment)
	{
		this.comment = comment;
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
	
	public ResultSet getPrimeryKeyInfo()
	{
		return primaryKeyInfo;
	}
	public ResultSet getIndexInfo()
	{
		return indexInfo;
	}
	public ResultSet getUniqueIndexInfo()
	{
		return uniqueIndexInfo;
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
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
}