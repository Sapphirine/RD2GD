package frame;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import action.ConnectAction;
import action.ScanFieldAction;
import action.ScanTableAction;
import datamodel.ETL;
import screen.ScreenResolution;

/**
 * @author Jose Alvarado-Guzman
 * @version 1.0 2017-03-24
 * RDGD application main frame
 */
public class AppMainFrame extends JFrame { 

	private static final long serialVersionUID = 1L;
	private ETL etl;

	public AppMainFrame()
	{			
		int appWidth = ScreenResolution.getWidth();
		int appHeight = ScreenResolution.getHeight();
		setSize(appWidth, appHeight);
		setIconImage(new ImageIcon(getClass().getResource("/icon/app_logo.gif")).getImage());
		
		setJMenuBar(constructMenu());
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		 etl = new ETL();
	}
	
	public ETL getETL()
	{
		return etl;
	}
	
	private JMenuBar constructMenu()
	{
		JMenuBar menubar = new JMenuBar();
		JMenu connection = new JMenu("Connection"); 
		JMenu datamodel = new JMenu("Datamodel");
		
		connection.setMnemonic('C');
		datamodel.setMnemonic('D');
		
		menubar.add(connection);
		menubar.add(datamodel);
		
		connection.add(new ConnectAction("Connect", 
				new ImageIcon(getClass().getResource("/icon/db_connect.gif")),
				"Connect to RDBMS",this));
		JMenuItem disconnectItem = connection.add(new ConnectAction("Disconnect", 
				new ImageIcon(getClass().getResource("/icon/db_disconnect.gif")),
				"Disconnect from RDBMS",this));
		
		JMenuItem table = datamodel.add(new ScanTableAction(this));
		JMenuItem field = datamodel.add(new ScanFieldAction(this));
		
		disconnectItem.setEnabled(false);
		table.setEnabled(false);
		field.setEnabled(false);
		
		return menubar;
	}
}