package datamodel;

import java.util.ArrayList;
import java.util.List;

public class Database{
	
	public enum Type 
	{
		RDBMS,GDBMS,ALL
	}
	
	public Database(Type type)
	{
		this.type = type;
	}
	
	private List<Table> tables = new ArrayList<>();
	private Type type;
	
	public void addTable(Table table)
	{
		tables.add(table);
	}
	
	public void setTables(List<Table> tables)
	{
		this.tables = tables;
	}
	
	public List<Table> getTables()
	{
		return tables;
	}
	
	public Type getType()
	{
		return type;
	}
}