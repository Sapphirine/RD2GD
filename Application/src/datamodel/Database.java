package datamodel;

import java.util.Set;
import java.util.TreeSet;

public class Database{
	
	public enum Type 
	{
		RDBMS,GDBMS,ALL
	}
	
	private Set<Table> tables;
	private Type type;
	
	public Database(Type type)
	{
		this.type = type;
		this.tables = new TreeSet<>();
	}
	
	public void addTable(Table table)
	{
		tables.add(table);
	}
	
	public void setTables(Set<Table> tables)
	{
		this.tables = tables;
	}
	
	public Set<Table> getTables()
	{
		return tables;
	}
	
	public Type getType()
	{
		return type;
	}
}