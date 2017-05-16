package action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import connection.DbConnect;
import cpu.Processor;
import datamodel.DataFile;
import datamodel.ETL;
import datamodel.Field;
import datamodel.ItemToItemMap;
import datamodel.Mapping;
import datamodel.PopulateDataFile;
import datamodel.Table;

public class MigrateDataAction extends AbstractAction 
{

	private static final long 	serialVersionUID 	= -2721502994690791025L;
	private File dataFilesPath;
	private Semaphore semaphore;
	private CountDownLatch countDown;
	private Set<DataFile> dataFiles;
	private String neo4jImport;
	
	public MigrateDataAction()
	{
		putValue(Action.NAME,"Migrate Data");
		putValue(Action.SHORT_DESCRIPTION,"Migrate the data to graph database");
		putValue(Action.MNEMONIC_KEY,KeyEvent.VK_M);
		putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_M,ActionEvent.ALT_MASK));
		putValue(Action.SMALL_ICON,new ImageIcon(getClass().getResource("/icon/migrate.png")));
		semaphore = new Semaphore(Processor.getNumCores());
		dataFiles = new TreeSet<>();
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Directory to Save the Data Migration Files");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter
		( 
			new FileFilter()
			{
				@Override
				public boolean accept(File f) 
				{
					return f.isDirectory();
				}

				@Override
				public String getDescription() 
				{
					return "Directories only";
				}

			}
		);
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
		{ 
			dataFilesPath =  chooser.getSelectedFile();
			neo4jImport = "neo4j-import --into ".concat(DbConnect.getDatabase()).concat(".db ")
					.concat("--ignore-empty-strings").concat(" --skip-bad-relationships");
			createFiles();
			graphImport();
		 }
	}
	
	private void createFiles()
	{
		Map<ItemToItemMap<Table>, Mapping<Field>> r2g = ETL.getMappingFields();
		Set<ItemToItemMap<Table>> itemToItemMap = r2g.keySet();
		countDown = new CountDownLatch(itemToItemMap.size());
		for(ItemToItemMap<Table> itemToitem: itemToItemMap)
		{
			Table graphEntity = itemToitem.getGraphItem(); 
			Mapping<Field> mapping = r2g.get(itemToitem);
			Map<Field, Set<Field>> fieldMapping = mapping.getR2G();
			if(graphEntity.getName().equals("node"))
			{
				for(Field relational : fieldMapping.keySet())
				{
					if(relational.getType().equals("UserConstant"))
					{
						createFile("node", relational.getName(),itemToitem);
					}
				}
			}
			else
			{
				for(Field relational : fieldMapping.keySet())
				{
					if(relational.getType().equals("UserConstant"))
					{
						createFile("edge",relational.getName(),itemToitem);
					}
				}
			}
		}
	}
	
	private void createFile(String type, String label, ItemToItemMap<Table> itemToItem)
	{
		File file = new File(dataFilesPath, type.concat("_").concat(label.concat(".csv")));
		try 
		{
			file.createNewFile();
			DataFile dataFile = new DataFile(file, type, label, itemToItem);
			dataFiles.add(dataFile);
			new PopulateDataFile(dataFile, semaphore, countDown);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	private void executeNeo4jImportTest() 
	{ 
		try
		{	
			File file = new File(dataFilesPath,"neo4j_import.sh");
			file.createNewFile();
			PrintWriter writer = new PrintWriter(file);
			writer.println("#!/bin/bash");
			writer.write(neo4jImport);
			writer.flush();
			writer.close();//neo4jImport.split(" ")
			Process process = new ProcessBuilder("chmod","u+x",Paths.get(file.toURI()).toString())
					.redirectErrorStream(true)
					.start();
			Process importData = new ProcessBuilder(Paths.get(file.toURI()).toString())
					.redirectErrorStream(true)
					.start();
			//.directory(new File("/usr/local/Cellar/neo4j/3.1.4/bin"))
			//Process process = Runtime.getRuntime().exec(neo4jImport);
			StringBuilder out = new StringBuilder();
		    BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    String line = null, previous = null;
			while ((line = br.readLine()) != null)
			{
				if (!line.equals(previous)) 
				{
					previous = line;
					out.append(line).append('\n');
					System.out.println(line);
			    }
			}
			if (process.waitFor() == 0) 
			{
				System.out.println("Success!");
			}
			else
			{
				JOptionPane.showMessageDialog(null, out.toString(), "Neo4j Error", JOptionPane.ERROR_MESSAGE,
						new ImageIcon(getClass().getResource("/icon/db_error.gif")));
			}
		}
		catch(IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
	    
	}
	private void graphImport()
	{
		new Thread
		(
			new Runnable()
			{
				@Override
				public void run()
				{
					long count = countDown.getCount();
					while(count > 0)
					{
						try 
						{
							System.out.println("Waiting for files to be crated");
							Thread.sleep(1000);
							count = countDown.getCount();
						} catch (InterruptedException e) 
						{
							e.printStackTrace();
						}
					}
					for(DataFile datafile : dataFiles)
					{
						if(datafile.getType().equals("node"))
						{
							neo4jImport += " --nodes:";
						}
						else
						{
							neo4jImport += " --relationships:";
						}
						neo4jImport += datafile.getLabel().concat(" ").concat(datafile.getFileFullPath());
					}
					executeNeo4jImportTest();
				}
			}).start();
	}
}
