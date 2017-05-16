package worker;

import java.util.concurrent.Semaphore;

import javax.swing.SwingWorker;

import cpu.Processor;
import datamodel.Table;
import datamodel.TableMetadata;

public class TableWorker extends SwingWorker<Integer,Void>{
	private Table[] tablesToProcess;
	Semaphore cores;
	
	public TableWorker(Table[] tables)
	{
		tablesToProcess = tables;
		cores = new Semaphore(Processor.getNumCores());
	}
	
	@Override
	protected Integer doInBackground()
	{
		for(Table table : tablesToProcess)
		{
			new TableMetadata(table,cores).getMetadata();//selectedTables.add(table);
		}
		return 1;
	}
}
