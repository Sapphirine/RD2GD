package datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

public class ListDataModel<T extends Comparable<T>> extends AbstractListModel<T> {
	private static final long serialVersionUID = -4505286816325000941L;
	private List<T> elements;
	private int size;
	
	public ListDataModel(List<T> elements)
	{
		Collections.sort(elements);
		this.elements = elements;
	}
	public ListDataModel()
	{
		this.elements = new ArrayList<>();
	}
	
	@Override
	public int getSize()
	{
		return size;
	}
	
	@Override
	public T getElementAt(int index)
	{
		return elements.get(index);
	}
	
	public List<T> getElementList()
	{
		Collections.sort(elements);
		return elements;
	}
	
	public void addElement(T element)
	{
		elements.add(element);
	}
	
	public void addElementAt(T element, int index)
	{
		elements.add(index, element);
	}
	
	public void addElements(List<T> elements)
	{
		this.elements.addAll(elements);
	}
	
	public void remoteElementAt(int index)
	{
		elements.remove(index);
	}
	
	public void removeElements(List<T> elements)
	{
		this.elements.removeAll(elements);
	}
	
	public void setElements(List<T> elements)
	{
		this.elements = elements;
	}
}
