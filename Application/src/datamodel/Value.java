package datamodel;

public class Value {
	private String name;
	private int frequency;
	private Table table;
	private Database database;
	private Field field;
	private double relativeFrequency;
	
	public Value(Database database, Table table, Field field, String name, int value)
	{
		this.database = database;
		this.table = table;
		this.field = field;
		this.relativeFrequency = (double)value/(double)table.getRowCount();
	}
	public String getName()
	{
		return name;
	}
	public int getFrequency()
	{
		return frequency;
	}
	public Table getTable()
	{
		return table;
	}
	public Database getDatabase()
	{
		return database;
	}
	public Field getField()
	{
		return field;
	}
	public double getRelativeFrequency()
	{
		return relativeFrequency;
	}
}
