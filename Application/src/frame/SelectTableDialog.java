package frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import connection.DbConnect;
import cpu.Processor;
import datamodel.ETL;
import datamodel.Field;
import datamodel.Table;
import datamodel.TableMetadata;

public class SelectTableDialog extends JDialog {
	private static final long serialVersionUID = 835342364814963978L;
    private javax.swing.JButton addButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton selectedAllButton;
    private javax.swing.JButton okButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JList<Table> selectList;
    private DefaultListModel<Table> selectListModel;
    private javax.swing.JPanel selectPanel;
    private javax.swing.JScrollPane selectScrollPane;
    private javax.swing.JList<Table> selectedList;
    private DefaultListModel<Table> selectedListModel;
    private javax.swing.JPanel selectedPanel;
    private javax.swing.JScrollPane selectedScrollPanel;
    private AppMainFrame parentFrame = null;
    Connection connection;
    
    public SelectTableDialog(JFrame owner)
    {
    	super(owner, "Add RDBMS Datamodel Tables");
    	parentFrame = ((AppMainFrame)getParent());
    	connection = DbConnect.getConnection();
    	initComponents();
    }
    
    private void initComponents() {
        
    	addButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        selectPanel = new javax.swing.JPanel();
        selectAllButton = new javax.swing.JButton();
        selectScrollPane = new javax.swing.JScrollPane();
        selectList = new javax.swing.JList<>(new DefaultListModel<Table>());
        selectedPanel = new javax.swing.JPanel();
        selectedAllButton = new javax.swing.JButton();
        selectedScrollPanel = new javax.swing.JScrollPane();
        selectedList = new javax.swing.JList<>(new DefaultListModel<Table>());
        removeButton = new javax.swing.JButton();
        selectListModel = (DefaultListModel<Table>)selectList.getModel();
        selectedListModel = (DefaultListModel<Table>)selectedList.getModel();
        
        
        Set<Table> listOfTables = ETL.getAllTables().getTables();
        Set<Table> selectedTables = ETL.getRelationalTables();
        listOfTables.removeAll(selectedTables);
        for(Table table : listOfTables)
        {
        	selectListModel.addElement(table);
        }
        for(Table table : selectedTables)
        {
        	selectedListModel.addElement(table);
        }
        
        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/db_arrow_right.png")));
        addButton.setToolTipText("Add to selected tables");

        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/db_Ok.png")));
        okButton.setToolTipText("Add selected tables to datamodel");
        okButton.addActionListener(
        		new ActionListener()
        		{
        			public void actionPerformed(ActionEvent event)
        			{
        				if(selectedListModel.getSize()==0)
        				{
        					JOptionPane.showMessageDialog(null, "Please select tables to proceed.", "Missing Data", JOptionPane.ERROR_MESSAGE,
									new ImageIcon(getClass().getResource("/icon/db_error.gif")));
        				}
        				else
        				{
        					Table[] selectedTableElements = new Table[selectedListModel.getSize()];
        					selectedListModel.copyInto(selectedTableElements);
        					Semaphore cores = new Semaphore(Processor.getNumCores());
        					Set<Table> selectedTables = new TreeSet<>();
        					for(Table table : selectedTableElements)
        					{
        						new TableMetadata(table, cores);
        						selectedTables.add(table);
        						try
        						{
        							DatabaseMetaData dbmeta = connection.getMetaData();
        							ResultSet resultset = dbmeta.getColumns(connection.getCatalog(), connection.getSchema(),
        									table.getName(), null);
        							ResultSet primaryKeyInfo = dbmeta.getPrimaryKeys(connection.getCatalog(), connection.getSchema(),
        									table.getName());
        							ResultSet uniqueIndexInfo = dbmeta.getIndexInfo(connection.getCatalog(), connection.getSchema(),
        									table.getName(), true, true);
        							ResultSet indexInfo = dbmeta.getIndexInfo(connection.getCatalog(), connection.getSchema(),
        									table.getName(), false, true);
        							table.setPrimaryKeyInfo(primaryKeyInfo);
        							table.setUniqueIndexInfo(uniqueIndexInfo);
        							table.setIndexInfo(indexInfo);
        							
        							while(resultset.next())
        							{
        								Field field = new Field(table.getDatabase(), table, resultset.getString(4),
        										resultset.getString(6),resultset.getInt(7),resultset.getInt(9),
        										resultset.getString(18),resultset.getString(23),resultset.getString(12));
        								table.addField(field);
        							}
        							resultset.close();
        						}
        						catch(SQLException e)
        						{
        							JOptionPane.showMessageDialog(null, e.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE,
        									new ImageIcon(getClass().getResource("/icon/db_error.gif")));
        							e.printStackTrace();
        						}
        					}
        					try
    						{
    							connection.close();
    						}
    						catch(SQLException e)
    						{
    							JOptionPane.showMessageDialog(null, e.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE,
    									new ImageIcon(getClass().getResource("/icon/db_error.gif")));
    							e.printStackTrace();
    						}
        					ETL.setRDBMS(selectedTables);
        					parentFrame.getJMenuBar().getMenu(1).getItem(1).setEnabled(true);
        					parentFrame.getJMenuBar().getMenu(2).getItem(0).setEnabled(true);
        					AppMainFrame.tableMappingPanel.renderModel();
        					setVisible(false);
        				}
        			}
        		}
        );
        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/db_cancel.png")));
        cancelButton.setToolTipText("Cancel");
        cancelButton.addActionListener(
        		new ActionListener()
        		{
        			public void actionPerformed(ActionEvent event)
        			{
        				setVisible(false);
        			}
        		}
        );

        selectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Tables to Select"));

        selectAllButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/db_all.png")));
        selectAllButton.setToolTipText("Select all tables");
        selectAllButton.addActionListener(
        		new ActionListener()
        		{
        			public void actionPerformed(ActionEvent event)
        			{
        				int listSize = selectList.getModel().getSize();
        				selectList.setSelectionInterval(0, listSize - 1);
        			}
        		}
        );
        
        addButton.addActionListener(
        		new ActionListener()
        		{
        			public void actionPerformed(ActionEvent event)
        			{
        				List<Table> selectTables = selectList.getSelectedValuesList();
        				for(Table table : selectTables)
        				{
        					selectedListModel.addElement(table);
        					selectListModel.removeElement(table);
        				}
        				if(selectedListModel.getSize()!=0)
        				{
            				Table[] tables = new Table[selectedListModel.getSize()];
            				selectedListModel.copyInto(tables);
            				Table[] sortedTables = sortElements(tables);
            				selectedListModel.clear();
            				for(Table table : sortedTables)
            				{
            					selectedListModel.addElement(table);
            				}
        				}
        			}
        		}
        );
        selectScrollPane.setViewportView(selectList);

        javax.swing.GroupLayout selectPanelLayout = new javax.swing.GroupLayout(selectPanel);
        selectPanel.setLayout(selectPanelLayout);
        selectPanelLayout.setHorizontalGroup(
            selectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(selectPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(selectAllButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(selectScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        selectPanelLayout.setVerticalGroup(
            selectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(selectPanelLayout.createSequentialGroup()
                .addGroup(selectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(selectScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(selectPanelLayout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(selectAllButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        selectedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Tables Selected"));

        selectedAllButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/db_all.png")));
        selectedAllButton.setToolTipText("Select all tables");
        selectedAllButton.addActionListener(
        		new ActionListener()
        		{
        			public void actionPerformed(ActionEvent event)
        			{
        				int listSize = selectedList.getModel().getSize();
        				selectedList.setSelectionInterval(0, listSize - 1);
        			}
        		}
        );

        selectedScrollPanel.setViewportView(selectedList);

        javax.swing.GroupLayout selectedPanelLayout = new javax.swing.GroupLayout(selectedPanel);
        selectedPanel.setLayout(selectedPanelLayout);
        selectedPanelLayout.setHorizontalGroup(
            selectedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(selectedPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(selectedScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(selectedAllButton)
                .addGap(8, 8, 8))
        );
        selectedPanelLayout.setVerticalGroup(
            selectedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(selectedPanelLayout.createSequentialGroup()
                .addGroup(selectedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(selectedPanelLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(selectedScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(selectedPanelLayout.createSequentialGroup()
                        .addGap(113, 113, 113)
                        .addComponent(selectedAllButton)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        removeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/db_arrow_left.png")));
        removeButton.setToolTipText("Remove from selected tables");
        removeButton.addActionListener(
        	new ActionListener()
        	{
        		public void actionPerformed(ActionEvent event)
        		{
        			List<Table> selectedTables = selectedList.getSelectedValuesList();
        			DefaultListModel<Table> selectedListModel = (DefaultListModel<Table>)selectedList.getModel();
        			DefaultListModel<Table> selectListModel = (DefaultListModel<Table>)selectList.getModel();
        			for(Table table : selectedTables)
        			{
        				selectListModel.addElement(table);
        				selectedListModel.removeElement(table);
        			}
        			if(selectListModel.getSize()!=0)
    				{
        				Table[] tables = new Table[selectListModel.getSize()];
        				selectListModel.copyInto(tables);
        				Table[] sortedTables = sortElements(tables);
        				selectListModel.clear();
        				for(Table table : sortedTables)
        				{
        					selectListModel.addElement(table);
        				}
    				}
        		}
        	}
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(selectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(removeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addComponent(selectedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(324, 324, 324)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(selectPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(selectedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(72, 72, 72)
                        .addComponent(addButton)
                        .addGap(37, 37, 37)
                        .addComponent(removeButton)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(okButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }
    private Table[] sortElements(Table[] tables)
    {
    	Arrays.sort(tables);
    	return tables;
    }
}
