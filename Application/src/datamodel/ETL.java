package datamodel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ETL{
	
	private static Database gdbms;
	private static Database rdbms;
	private static Database allTable;
	private static Mapping<Table> mappingTables;
	private static Map<ItemToItemMap<Table>,Mapping<Field>> mappingFields;
	
	public enum FileFormat
	{
		Binary, Json, GzipJson
	}
	
	static
	{
		gdbms = new Database(Database.Type.GDBMS);
		rdbms = new Database(Database.Type.RDBMS);
		allTable = new Database(Database.Type.ALL);
		setGraphDefaultModel();
		mappingFields = new HashMap<>();
	}
	
	private static void setGraphDefaultModel()
	{
		Set<Table> gdb = new TreeSet<>();
		Table node = new Table(gdbms, "node", "vertex","A vertex (plural vertices) or node is the fundamental unit of which graphs are formed.");
		Table edge = new Table(gdbms, "edge", "edge","An edge is a pair of nodes that specify a relationship between them."); 
		gdb.add(node);
		gdb.add(edge);
		mappingTables = new Mapping<Table>(gdb);
		Set<Field> nodeProperties = new TreeSet<>();
		Set<Field> edgeProperties = new TreeSet<>();
		nodeProperties.add(new Field(gdbms,node,"ID","vertex property","Unique identifier"));
		nodeProperties.add(new Field(gdbms,node,"Label","vertex property","Node category"));
		edgeProperties.add(new Field(gdbms,edge,"ID","edge property","Unique identifier"));
		edgeProperties.add(new Field(gdbms,edge,"Node_ID1","edge property","Unique Identifier of the first node on the edge"));
		edgeProperties.add(new Field(gdbms,edge,"Node_ID2","edge property","Unique Identifier of the second node on the edge"));
		edgeProperties.add(new Field(gdbms,edge,"Label","edge property","Edge category"));
		node.setfields(nodeProperties);
		edge.setfields(edgeProperties);
		gdbms.setTables(gdb);
	}
	
	public static void setRDBMS(Set<Table> tables)
	{
		mappingTables.setRelationalItems(tables);
	}
	
	public static void setAllTables(Set<Table> tables)
	{
		allTable.setTables(tables);
	}
	
	public static void addRdbmsTable(Table table)
	{
		mappingTables.addRelationalItem(table);
	}
	public static void addGraphProperty(String name, String type)
	{
		Set<Table> graphItems = gdbms.getTables();
		Table graphItem = null;
		String description = null;
		for(Table item : graphItems)
		{
			//System.out.println(item.getName());
			if(item.getName().equals(type))
			{
				graphItem = item;
				if(type.equals("node"))
				{
					description = "node property";
				}
				else
				{
					description = "edge property";
				}
				Field property = new Field(gdbms, graphItem, name,"graph property",description);
				graphItem.addField(property);
			}
		}
	}
	public static void addAllTable(Table table)
	{
		allTable.addTable(table);
	}
	
	public static Set<Table> getRelationalTables()
	{
		return mappingTables.getRelationalItems();
	}
	
	public static Map<ItemToItemMap<Table>,Mapping<Field>> getMappingFields()
	{
		return mappingFields;
	}
	
	/*public static Database getRDBMS()
	{
		return rdbms;
	}
	public static Database getGDBMS()
	{
		return gdbms;
	}*/
	public static Database getAllTables()
	{
		return allTable;
	}
	public static Mapping<Table> getMappingTables()
	{
		return mappingTables;
	}
	public static void addMappingFields(ItemToItemMap<Table> itemtoItemMap, Set<Field> relationalItems, Set<Field> graphItems)
	{
		mappingFields.put(itemtoItemMap, new Mapping<Field>(relationalItems, graphItems));
	}
	
	public static Mapping<Field> getMappingFields(Table relationalItem, Table graphItem)
	{
		ItemToItemMap<Table> itemToItem = new ItemToItemMap<Table>(relationalItem, graphItem);

		if(!mappingFields.containsKey(itemToItem))
		{
			mappingFields.put(itemToItem, new Mapping<Field>(((Table)relationalItem).getFields(),((Table)graphItem).getFields()));
		}
		return mappingFields.get(itemToItem);
	}
	
	public static void addSourceToTargetMap(MappableItem relationalItem, MappableItem graphItem)
	{
		if(relationalItem instanceof Table)
		{
			mappingTables.addMapItem((Table)relationalItem, (Table)graphItem);
		}
		else
		{
			Table relationalTable = ((Field)relationalItem).getTable();
			Table graphTable = ((Field)graphItem).getTable();
			mappingFields.get(new ItemToItemMap<Table>(relationalTable, graphTable)).addMapItem((Field)relationalItem, (Field)graphItem);
		}
	}
}