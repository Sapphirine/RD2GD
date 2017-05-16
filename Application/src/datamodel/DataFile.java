package datamodel;

import java.io.File;
import java.nio.file.Paths;

public class DataFile implements Comparable<DataFile>
{
	private File file;
	private String type;
	private String label;
	private ItemToItemMap<Table> itemToItem;
	
	public DataFile(File file, String type, String label, ItemToItemMap<Table> itemToItem)
	{
		this.file = file;
		this.type = type;
		this.label = label;
		this.itemToItem = itemToItem;
	}
	public File getFile()
	{
		return file;
	}
	public String getType()
	{
		return type;
	}
	public String getLabel()
	{
		return label;
	}
	public String getFileName()
	{
		return Paths.get(file.toURI()).getFileName().toString();
	}
	public ItemToItemMap<Table> getItemToItemMap()
	{
		return this.itemToItem;
	}
	public String getFileFullPath()
	{
		return Paths.get(file.toURI()).normalize().toString();
	}
	@Override
	public int compareTo(DataFile df)
	{
		return toString().compareTo(df.getType());
	}
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof DataFile && ((DataFile)o).getFileFullPath().equals(this.getFileFullPath()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
