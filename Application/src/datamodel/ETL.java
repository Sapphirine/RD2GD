package datamodel;

import java.util.List;

public class ETL{
	
	private static Database gdbms = new Database(Database.Type.GDBMS);
	private Database rdbms = new Database(Database.Type.RDBMS);
	private static Database allTable = new Database(Database.Type.ALL);
	
	public enum FileFormat
	{
		Binary, Json, GzipJson
	}
	
	public ETL()
	{
		setGDBMS();
	}
	
	private void setGDBMS()
	{
		gdbms.addTable(new Table(gdbms, "node", "node", 0));
		gdbms.addTable(new Table(gdbms, "edges", "edge", 0));
	}
	
	public void setRDBMS(List<Table> tables)
	{
		rdbms.setTables(tables);
	}
	
	public void setAllTables(List<Table> tables)
	{
		allTable.setTables(tables);
	}
	public void addRdbmsTable(Table table)
	{
		rdbms.addTable(table);
	}
	public void addAllTable(Table table)
	{
		allTable.addTable(table);
	}
	
	public Database getRDBMS()
	{
		return rdbms;
	}
	public Database getGDBMS()
	{
		return gdbms;
	}
	public Database getAllTables()
	{
		return allTable;
	}
}