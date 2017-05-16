package datamodel;

public class Value implements Comparable<Value>{
	private String 		name;
	private int 		frequency;
	private Table 		table;
	private Database 	database;
	private Field 		field;
	private double 		relativeFrequency;
	
	public Value(Database database, Table table, Field field, String name, int value)
	{
		this.database = database;
		this.table = table;
		this.field = field;
		this.name = name;
		this.frequency = value;
		this.relativeFrequency = ((double)value/(double)table.getRowCount())*100;
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
	@Override
	public String toString()
	{
		return table.toString() + '.' + field.toString() + '.' + name;
	}
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Value && ((Value)o).toString().equals(this.toString()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	@Override
	public int compareTo(Value value)
	{
		return this.getFrequency() - value.getFrequency();
	}
}
