package frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.JButton;

import datamodel.ETL;
import datamodel.Field;
import datamodel.Table;

public class SelectFieldDialog extends JDialog {
	private static final long serialVersionUID = 8709203067932722272L;
	
    private JButton addButton;
    private JButton cancelButton;
    private JButton selectedAllButton;
    private JButton okButton;
    private JButton removeButton;
    private JButton selectAllButton;
    private JList<Field> selectList;
    private DefaultListModel<Field> selectListModel;
    private JPanel selectPanel;
    private JScrollPane selectScrollPane;
    private JList<Field> selectedList;
    private DefaultListModel<Field> selectedListModel;
    private JPanel selectedPanel;
    private JScrollPane selectedScrollPanel;
    
    public SelectFieldDialog(JFrame owner)
    {
    	super(owner, "Add RDBMS Datamodel Fields");
    	initComponents();
    }
    
    public void initComponents()
    {
    	addButton = new JButton();
        okButton = new JButton();
        cancelButton = new JButton();
        selectPanel = new JPanel();
        selectAllButton = new JButton();
        selectScrollPane = new JScrollPane();
        selectList = new JList<>(new DefaultListModel<Field>());
        selectedPanel = new JPanel();
        selectedAllButton = new JButton();
        selectedScrollPanel = new JScrollPane();
        selectedList = new JList<>(new DefaultListModel<Field>());
        removeButton = new JButton();
        selectListModel = (DefaultListModel<Field>)selectList.getModel();
        selectedListModel = (DefaultListModel<Field>)selectedList.getModel();
        
        Set<Table> selectedTables = ETL.getRelationalTables();
        for(Table table : selectedTables)
        {
        	for(Field field : table.getFields())
        	{
        		selectListModel.addElement(field);
        	}
        }
        
        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/db_arrow_right.png")));
        addButton.setToolTipText("Add to selected fields");
        okButton.addActionListener(
        		new ActionListener()
        		{
        			public void actionPerformed(ActionEvent event)
        			{
        				if(selectedListModel.getSize()==0)
        				{
        					JOptionPane.showMessageDialog(null, "Please select fields to proceed.", "Missing Data", JOptionPane.ERROR_MESSAGE,
									new ImageIcon(getClass().getResource("/icon/db_error.gif")));
        				}
        				else
        				{
        					Enumeration<Field> fieldsToRemove = selectListModel.elements();
        					while(fieldsToRemove.hasMoreElements())
        					{
        						Field field = fieldsToRemove.nextElement();
        						field.getTable().removeField(field);
        					}
            				setVisible(false);
        				}
        			}
        		}
        );

        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/db_Ok.png")));
        okButton.setToolTipText("Add selected fields to datamodel");

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
        
        selectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Fields to Select"));
        
        selectAllButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/db_all.png")));
        selectAllButton.setToolTipText("Select all fields");
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
        				List<Field> selectFields = selectList.getSelectedValuesList();
        				for(Field field : selectFields)
        				{
        					selectedListModel.addElement(field);
        					selectListModel.removeElement(field);
        				}
        				if(selectedListModel.getSize()!=0)
        				{
            				Field[] fields = new Field[selectedListModel.getSize()];
            				selectedListModel.copyInto(fields);
            				Field[] sortedFields = sortElements(fields);
            				selectedListModel.clear();
            				for(Field field : sortedFields)
            				{
            					selectedListModel.addElement(field);
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
                .addComponent(selectScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        selectedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Fields Selected"));

        selectedAllButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/db_all.png")));
        selectedAllButton.setToolTipText("Select all fields");
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
                .addContainerGap()
                .addComponent(selectedScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
        removeButton.setToolTipText("Remove from selected fields");
        removeButton.addActionListener(
        	new ActionListener()
        	{
        		public void actionPerformed(ActionEvent event)
        		{
        			List<Field> selectedFields = selectedList.getSelectedValuesList();
        			DefaultListModel<Field> selectedListModel = (DefaultListModel<Field>)selectedList.getModel();
        			DefaultListModel<Field> selectListModel = (DefaultListModel<Field>)selectList.getModel();
        			for(Field field : selectedFields)
        			{
        				selectListModel.addElement(field);
        				selectedListModel.removeElement(field);
        			}
        			if(selectListModel.getSize()!=0)
    				{
        				Field[] fields = new Field[selectListModel.getSize()];
        				selectListModel.copyInto(fields);
        				Field[] sortedFields = sortElements(fields);
        				selectListModel.clear();
        				for(Field field : sortedFields)
        				{
        					selectListModel.addElement(field);
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
                .addContainerGap()
                .addComponent(selectPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(removeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(selectedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(469, 469, 469)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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

    private Field[] sortElements(Field[] tables)
    {
    	Arrays.sort(tables);
    	return tables;
    }
}
