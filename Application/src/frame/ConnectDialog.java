package frame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
//import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import gridback.GBC;
import connection.DbConnect;
import connection.DbType;
import datamodel.Database;
import datamodel.ETL;
import datamodel.Table;

/**
 * Dialog for creating a connection.
 * @author Jose Alvarado-Guzman
 * @version 1.0 
 */
public class ConnectDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private JComboBox<DbType>	dbtypecombo = new JComboBox<>();
	private JTextField			server = new JTextField(15);
	private JTextField			user = new JTextField(15);
	private JPasswordField		password = new JPasswordField(15);
	private JTextField			database = new JTextField(15);
	private JButton ok = new JButton(new ImageIcon(getClass().getResource("/icon/db_Ok.png")));
	private JButton cancel = new JButton(new ImageIcon(getClass().getResource("/icon/db_cancel.png")));
	private JButton test = new JButton(new ImageIcon(getClass().getResource("/icon/db_test.png")));
	private JPanel panel = new JPanel();
	private JPanel buttomPanel = new JPanel();
	private AppMainFrame parentFrame = null;
	
	public ConnectDialog(JFrame owner)
	{
		super(owner, "RDBMS Connection",true);
		this.setResizable(false);
		
		ok.setToolTipText("Create Connection");
		cancel.setToolTipText("Cancel");
		test.setToolTipText("Test Connection");
		
		parentFrame = ((AppMainFrame)getParent());
		panel.setLayout(new GridBagLayout());
		buttomPanel.setLayout(new FlowLayout());
		
		for(DbType dbtype : DbType.values())
		{
			dbtypecombo.addItem(dbtype);
		}
		
		cancel.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						DbConnect.closeDataSource();
						server.setText("");
						user.setText("");
						password.setText("");
						database.setText("");
						setVisible(false);
					}
				}
		);
		
		ok.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						if(!ConnectDialog.this.isValidFrame())
						{
							JOptionPane.showMessageDialog(null, "All fields are require.", "Missing Data", JOptionPane.ERROR_MESSAGE,
									new ImageIcon(getClass().getResource("/icon/db_error.gif")));
						}
						else
						{
							if(DbConnect.getDataSource() == null)
							{
								DbConnect.setDataSource(server.getText(),null,user.getText(),new String(password.getPassword()),
											dbtypecombo.getItemAt(dbtypecombo.getSelectedIndex()),database.getText());
							}
							parentFrame.getJMenuBar().getMenu(0).getItem(0).setEnabled(false);
							parentFrame.getJMenuBar().getMenu(0).getItem(1).setEnabled(true);
							parentFrame.getJMenuBar().getMenu(1).getItem(0).setEnabled(true);
							try {
								Database allTables = ETL.getAllTables();
								Connection connection = DbConnect.getConnection();
								DatabaseMetaData dbMetaData = connection.getMetaData();
								ResultSet tableMetaData = dbMetaData.getTables(connection.getCatalog(), null, null, null);
								while(tableMetaData.next())
								{
									String schemaName = tableMetaData.getString(2);
									String tableType = tableMetaData.getString(4);
									String tableName = tableMetaData.getString(3);
									String tableDescription = tableMetaData.getString(5);
									allTables.addTable(new Table(allTables, tableName, tableType, schemaName, tableDescription));
								}
								tableMetaData.close();
								connection.close();
							} catch (SQLException e) {
								JOptionPane.showMessageDialog(null, e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE,
										new ImageIcon(getClass().getResource("/icon/db_error.gif")));
								e.printStackTrace();
							}
							setVisible(false);
						}
					}
				}
		);
		
		test.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						if(!ConnectDialog.this.isValidFrame())
						{
							JOptionPane.showMessageDialog(null, "All fields are require.", "Missing Data", JOptionPane.ERROR_MESSAGE,
									new ImageIcon(getClass().getResource("/icon/db_error.gif")));
						}
						else
						{
							DbConnect.setDataSource(server.getText(),null,user.getText(),new String(password.getPassword()),
									dbtypecombo.getItemAt(dbtypecombo.getSelectedIndex()),database.getText());
							JOptionPane.showMessageDialog(null, "Successfully connected to " + database.getText() + " on " 
									+ server.getText() 
									, "Successful Connection", JOptionPane.ERROR_MESSAGE,
									new ImageIcon(getClass().getResource("/icon/db_success.gif")));
						}
					}
				}
		);
		
		panel.add(new JLabel("RDBMS:"),new GBC(0,0).setInsets(0,5,10,0).setAnchor(GBC.WEST));
		panel.add(dbtypecombo, new GBC(1,0).setWeight(100,0).setAnchor(GBC.WEST));
		panel.add(new JLabel("Server (<ip or host>:<port>):"), new GBC(0,1).setInsets(0,5,10,0).setAnchor(GBC.WEST));
		panel.add(server, new GBC(1,1).setWeight(100,0).setAnchor(GBC.WEST));
		panel.add(new JLabel("Database:"), new GBC(0,2).setInsets(0,5,10,0).setAnchor(GBC.WEST));
		panel.add(database, new GBC(1,2).setAnchor(GBC.WEST));
		panel.add(new JLabel("User (<domain>/<user>):"), new GBC(0,3).setInsets(0,5,10,0).setAnchor(GBC.WEST));
		panel.add(user, new GBC(1,3).setAnchor(GBC.WEST));
		panel.add(new JLabel("Password:"), new GBC(0,4).setInsets(0,5,10,0).setAnchor(GBC.WEST));
		panel.add(password, new GBC(1,4).setAnchor(GBC.WEST));
		add(panel,BorderLayout.CENTER);
		buttomPanel.add(test);
		buttomPanel.add(ok);
		buttomPanel.add(cancel);
		add(buttomPanel,BorderLayout.SOUTH);
	}
	
	private  boolean isValidFrame()
	{
		boolean valid = false;
		if(
				!server.getText().trim().equals("") &&
				!database.getText().trim().equals("") &&
				!user.getText().trim().equals("") &&
				!(new String(password.getPassword()).equals(""))
		)
		{
			valid = true;
		}
		return valid;
	}
}