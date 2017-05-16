package datamodel;

public class ItemToItemMap<T extends MappableItem> 
{
	private T					relationalItem;
	private T					graphItem;
	private boolean				isCompleted;
	private String logic="";
	
	public ItemToItemMap(T relationalItem, T graphItem) 
	{
		this.relationalItem = relationalItem;
		this.graphItem = graphItem;
		isCompleted = false;
	}
	
	public T getRelationalItem() 
	{
		return relationalItem;
	}
	
	public void setRelationalItem(T relationalItem) 
	{
		this.relationalItem = relationalItem;
	}
	
	public T getGraphItem() 
	{
		return graphItem;
	}
	
	public void setGraphItem(T graphItem) 
	{
		this.graphItem = graphItem;
	}
	public boolean isCompleted() 
	{
		return isCompleted;
	}
	
	public void setCompleted(boolean isCompleted) 
	{
		this.isCompleted = isCompleted;
	}
	public String getLogic() 
	{
		return logic;
	}
	
	public void setLogic(String logic) 
	{
		this.logic = logic;
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object other) 
	{
		if (other instanceof ItemToItemMap) {
			return (((ItemToItemMap<T>) other).getRelationalItem().getName().equals(relationalItem.getName()) && 
					((ItemToItemMap<T>) other).graphItem.getName().equals(graphItem.getName()));
		} else
			return false;
	}
	@Override
	public String toString()
	{
		return relationalItem.toString() + "maps to " + graphItem.toString();
	}
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
}
