package frame;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import action.AddGraphPropertyAction;
import action.AddRelationalLabelAction;
import action.ArrowCompleteAction;
import action.ConnectAction;
import action.FieldStatisticsAction;
import action.MigrateDataAction;
import action.ScanFieldAction;
import action.ScanTableAction;
import datamodel.ETL;
import datamodel.Field;
import datamodel.ResizeListener;
import datamodel.Table;
import screen.ScreenResolution;

/**
 * @author Jose Alvarado-Guzman
 * @version 1.0 2017-03-24
 * RDGD application main frame
 */
public class AppMainFrame extends JFrame 
{ 

	private static final long 					serialVersionUID = 1756608707039163492L;
	public static MappingPanel<Table>			tableMappingPanel;
	public static MappingPanel<Field>			fieldMappingPanel;
	private JScrollPane 						tableMappingScrollPane;
	private JScrollPane							fieldMappingScrollPane;
	private DetailsPanel						detailsPanel;
	private JSplitPane							tableFieldSplitPane;
	
	public AppMainFrame()
	{		
		tableMappingPanel = new MappingPanel<>(this);
		fieldMappingPanel = new MappingPanel<>(this);
		
		int appWidth = ScreenResolution.getWidth();
		int appHeight = ScreenResolution.getHeight();
		setSize(appWidth, appHeight);
		setIconImage(new ImageIcon(getClass().getResource("/icon/app_logo.gif")).getImage());
		
		setJMenuBar(constructMenu());
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		tableMappingPanel.addResizeListener(new ResizeHandler());
		tableMappingScrollPane = new JScrollPane(tableMappingPanel);
		tableMappingScrollPane.setBorder(new TitledBorder("Relational 2 Graph Entity Mapping"));
		tableMappingScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		tableMappingScrollPane.setAutoscrolls(true);
		tableMappingScrollPane.setOpaque(true);
		tableMappingScrollPane.setBackground(Color.WHITE);
		
		tableMappingPanel.setSlaveMappingPanel(fieldMappingPanel);
		fieldMappingPanel.addResizeListener(new ResizeHandler());
		fieldMappingScrollPane = new JScrollPane(fieldMappingPanel);
		fieldMappingScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		fieldMappingScrollPane.setAutoscrolls(true);
		fieldMappingScrollPane.setVisible(false);
		fieldMappingScrollPane.setBorder(new TitledBorder("Relational 2 Graph Field Mapping"));
		
		tableFieldSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableMappingScrollPane, fieldMappingScrollPane);
		tableFieldSplitPane.setDividerLocation(600);
		tableFieldSplitPane.setDividerSize(0);
		
		detailsPanel = new DetailsPanel();
		detailsPanel.setBorder(new TitledBorder("Details"));
		detailsPanel.setPreferredSize(new Dimension((int)(ScreenResolution.getWidth() * .30), 500));
		detailsPanel.setMinimumSize(new Dimension(0, 0));
		tableMappingPanel.setDetailsListener(detailsPanel);
		fieldMappingPanel.setDetailsListener(detailsPanel);
		JSplitPane leftRightSplinePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableFieldSplitPane, detailsPanel);
		leftRightSplinePane.setResizeWeight(0.70);
		getContentPane().add(leftRightSplinePane);
		
		tableMappingPanel.setMapping(ETL.getMappingTables());
		//add(tableMappingScrollPane);
		
	}
	
	private JMenuBar constructMenu()
	{
		JMenuBar menubar = new JMenuBar();
		JMenu connection = new JMenu("Connection"); 
		JMenu datamodel = new JMenu("Datamodel");
		JMenu stats = new JMenu("Statistics");
		JMenu migrate = new JMenu("Migrate");
		JMenu arrow = new JMenu("Arrow");
		
		connection.setMnemonic('C');
		datamodel.setMnemonic('D');
		stats.setMnemonic('S');
		migrate.setMnemonic('M');
		arrow.setMnemonic('A');
		
		menubar.add(connection);
		menubar.add(datamodel);
		menubar.add(stats);
		menubar.add(arrow);
		menubar.add(migrate);
		
		connection.add(new ConnectAction("Connect", 
				new ImageIcon(getClass().getResource("/icon/db_connect.gif")),
				"Connect to RDBMS",this));
		JMenuItem disconnectItem = connection.add(new ConnectAction("Disconnect", 
				new ImageIcon(getClass().getResource("/icon/db_disconnect.gif")),
				"Disconnect from RDBMS",this));
		
		JMenuItem table = datamodel.add(new ScanTableAction(this));
		JMenuItem field = datamodel.add(new ScanFieldAction(this));
		JMenuItem constantLabel = datamodel.add(new AddRelationalLabelAction(this));
		JMenuItem nodeProperty = datamodel.add(new AddGraphPropertyAction(this,"node"));
		JMenuItem edgeProperty = datamodel.add(new AddGraphPropertyAction(this,"edge"));
		JMenuItem fieldStats = stats.add(new FieldStatisticsAction());
		JMenuItem migrateItem = migrate.add(new MigrateDataAction());
		JMenuItem completeArrow = arrow.add(new ArrowCompleteAction(this, true,
				new ImageIcon(getClass().getResource("/icon/complete.png"))));
		JMenuItem incompleteArrow = arrow.add(new ArrowCompleteAction(this, false,
				new ImageIcon(getClass().getResource("/icon/incomplete.png"))));
		
		disconnectItem.setEnabled(false);
		table.setEnabled(false);
		field.setEnabled(false);
		constantLabel.setEnabled(false);
		nodeProperty.setEnabled(false);
		edgeProperty.setEnabled(false);
		fieldStats.setEnabled(false);
		completeArrow.setEnabled(false);
		incompleteArrow.setEnabled(false);
		migrateItem.setEnabled(false);
		
		return menubar;
	}
	
	private class ResizeHandler implements ResizeListener
	{
		public void notifyResized(int height, boolean minimized, boolean maximized)
		{
			if (fieldMappingScrollPane.isVisible() == maximized)
			{
				fieldMappingScrollPane.setVisible(!maximized);
			}
			
			if (!maximized)
			{
				tableMappingScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			}
			else
			{
				tableMappingScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
				getJMenuBar().getMenu(1).getItem(2).setEnabled(false);
				getJMenuBar().getMenu(1).getItem(3).setEnabled(false);
				getJMenuBar().getMenu(1).getItem(4).setEnabled(false);
			}

			if (!minimized)
				fieldMappingScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			else
				fieldMappingScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

			tableFieldSplitPane.setDividerLocation(height);
			//System.out.println("Table scrollPanel:" + tableMappingScrollPane.getVerticalScrollBarPolicy());
			//System.out.println("Field scrollPanel:" + fieldMappingScrollPane.getVerticalScrollBarPolicy());
		};
	}
}