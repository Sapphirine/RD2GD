package datamodel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Mapping<T extends MappableItem> {
	private Map<T,Set<T>> r2g;
	private Set<T> relationalItems;
	private Set<T> graphItems;
	
	public Mapping(Set<T> graphItems)
	{
		this.graphItems = graphItems;
		this.relationalItems = new TreeSet<>();
		this.r2g = new HashMap<>();
	}
	public Mapping(Set<T> relationalItems,Set<T> graphItems)
	{
		this.graphItems = graphItems;
		this.relationalItems = relationalItems;
		this.r2g = new HashMap<>(relationalItems.size());
	}
	public Mapping(Set<T> relationalItems, Set<T> graphItems,Map<T,Set<T>> r2g)
	{
		this.relationalItems = relationalItems;
		this.graphItems = graphItems;
		this.r2g = r2g;
	}
	public Mapping(){}
	
	public void addRelationalItem(T relationalItem)
	{
		this.relationalItems.add(relationalItem);
	}
	public void removeRelationalItem(T relationalItem)
	{
		this.relationalItems.remove(relationalItem);
	}
	
	public void addGraphItem(T graphItem)
	{
		this.graphItems.add(graphItem);
	}
	public void removeGraphItem(T graphItem)
	{
		this.graphItems.remove(graphItem);
	}

	public void addMapItem(T relationalItem, T graphItem)
	{
		if(!r2g.containsKey(relationalItem))
		{
			r2g.put(relationalItem, new TreeSet<T>());
		}
		r2g.get(relationalItem).add(graphItem);
	}
	public void addMapItem(ItemToItemMap<T> itemToItemMap)
	{
		if(!r2g.containsKey(itemToItemMap.getRelationalItem()))
		{
			r2g.put(itemToItemMap.getRelationalItem(),new TreeSet<T>());
		}
		r2g.get(itemToItemMap.getRelationalItem()).add(itemToItemMap.getGraphItem());
	}
	public void removeSourceToTargetMap(T relationalItem, T graphItem)
	{
		this.r2g.get(relationalItem).remove(graphItem);
	}

	public Set<T> getRelationalItems()
	{
		return this.relationalItems;
	}
	public Set<T> getGraphItems()
	{
		return this.graphItems;
	}
	public Set<T> getMapRelationalItems()
	{
		return r2g.keySet();
	}
	
	public Set<T> getGraphItems(T relationalItem)
	{
		if(r2g.containsKey(relationalItem))
		{
			return r2g.get(relationalItem);
		}
		else
		{
			return null;
		}
	}
	public Map<T, Set<T>> getR2G()
	{
		return this.r2g;
	}
	
	public void setRelationalItems(Set<T> items)
	{
		this.relationalItems = items;
	}
	public void setGraphItems(Set<T> items)
	{
		this.graphItems = items;
	}
	public ItemToItemMap<T> getSourceToTargetMap(T sourceItem, T targetItem) {
		Iterator<T> graphItems = r2g.get(sourceItem).iterator();
		while(graphItems.hasNext())
		{
			T graphItem = graphItems.next();
			if(graphItem.equals(targetItem))
			{
				return new ItemToItemMap<>(sourceItem, targetItem);
			}
		}
		return null;
	}
	public void setMapping(Set<T> relationalItems, Set<T> graphItems)
	{
		this.relationalItems = relationalItems;
		this.graphItems = graphItems;
	}
}
