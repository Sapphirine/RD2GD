package datamodel;

import java.util.Set;
import java.util.TreeSet;

public class Field implements Comparable<Field>, MappableItem{
	private Table table;
	private Database database;
	private String name;
	private String description;
	private String type;
	private Integer size;
	private Integer decimal;
	private String nullable;
	private String autoIncrement;
	private Set<Value> values;
	

	{
		values = new TreeSet<>();
	}
	public Field(Database database, Table table, String name, String type, Integer size,Integer decimal,String nullable, 
			String autoIncrement, String description)
	{
		this(database, table, name, type, description);
		this.size = size;
		this.decimal = decimal;
		this.nullable = nullable;
		this.autoIncrement = autoIncrement;
	}
	public Field(Database database, Table table, String name, String type, String description)
	{
		this.database = database;
		this.table = table;
		this.name = name.toLowerCase();
		this.type = type;
		this.description = description;
		this.size = new Integer(0);
		this.decimal = new Integer(0);
		this.nullable = "";
	}
	
	public Database getDatabase()
	{
		return database;
	}
	public Table getTable()
	{
		return table;
	}
	public String getName()
	{
		return name;
	}
	public String getType()
	{
		return type;
	}
	public Integer getSize()
	{
		return size;
	}
	public Integer getDecimal()
	{
		return decimal;
	}
	public String getAutoIncrement()
	{
		return autoIncrement;
	}
	public String getNullable()
	{
		return nullable;
	}
	public String getDescription()
	{
		return description;
	}
	public Set<Value> getValues()
	{
		return values;
	}
	
	public void addValue(Value value)
	{
		values.add(value);
	}
	public String getFullType()
	{
		String fullType = getType();
		if(getSize() > 0)
		{
			fullType += "(" + getSize();
			if(getDecimal() > 0)
			{
				fullType += "," + getDecimal();
			}
			fullType += ")";
		}
		return fullType;
	}
	@Override
	public String toString()
	{
		return table.toString().concat(".").concat(name);
	}
	@Override
	public boolean equals(Object field)
	{
		if(field instanceof Field && this.toString().equals(((Field)field).toString()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	@Override
	public int compareTo(Field field)
	{
		return this.toString().compareTo(field.toString());
	}
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
}