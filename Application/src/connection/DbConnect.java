package connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.commons.dbcp2.BasicDataSource;

public class DbConnect
{
		private static BasicDataSource dataSource;
		private static DbType dbType;
		private static String user;
		private static String domain;
		private static String database;
		private static String server;
		
		public static void setDataSource(String serverName,String domainName,String userName,String password,DbType type, 
				String databaseName)
		{
			dataSource = new BasicDataSource();
			dbType = type;
			user = userName;
			domain = domainName;
			database = databaseName;
			server = serverName;
			dataSource.setDriverClassName(getDriver());
			dataSource.setUsername(user);
			dataSource.setPassword(password);
			dataSource.setUrl(getURL());
		}
	
	public static Connection getConnection()
	{
		Connection connection = null;
		//return connection;
		try
		{
			connection = dataSource.getConnection();
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(DbConnect.class.getClass().getResource("/icon/db_error.gif")));
		}
		return connection;
	}
	
	public static DbType getDbType()
	{
		return dbType;
	}
	
	private static String getURL()
	{
		String url = null;
		switch(dbType)
		{
			case MYSQL:
				url = "jdbc:mysql://";
				if(!server.contains(":"))
				{
					url += server + ":3306/" + database + "?useCursorFetch=true&zeroDateTimeBehavior=convertToNull";
				}
				else
				{
					url += server + "/" + database + "?useCursorFetch=true&zeroDateTimeBehavior=convertToNull";
				}
				break;
			case MSACCESS:
				url = "jdbc:ucanaccess://" + server + ";sysschema=true";
				break;
			case MSSQL:
				if (user == null || user.length() == 0)
				{
					url = "jdbc:sqlserver://" + server + ";integratedSecurity=true";
				}
				else
				{
					url = "jdbc:jtds:sqlserver://" + server + ";ssl=required" + ((domain == null || domain.length() == 0) ? "" : ";domain=" + domain);
				}
				break;
			case ORACLE:
				url = "jdbc:oracle:oci8:@" + server + "/" + database;
				break;
			case POSTGRESQL:
				url = "jdbc:postgresql://";
				if (!server.contains(":"))
				{
					server = server + ":5432/";
				}
				url = "jdbc:postgresql://" + server + "/" + database;
				break;
			case REDSHIFT:
				url = "jdbc:redshift://" + server + "/" + database;
				break;
			default:
				break;
		}
		return url;
	}
	private static String getDriver()
	{
		String driver = null;
		switch(dbType)
		{
			case MYSQL:
				driver = "com.mysql.jdbc.Driver";
				break;
			case MSACCESS:
				driver = "net.ucanaccess.jdbc.UcanaccessDriver";
				break;
			case MSSQL:
				if (user == null || user.length() == 0)
				{
					driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
				}
				else
				{
					driver = "net.sourceforge.jtds.jdbc.Driver";
				}
				break;
			case ORACLE:
				driver = "oracle.jdbc.driver.OracleDriver";
				break;
			case POSTGRESQL:
				driver = "org.postgresql.Driver";
				break;
			case REDSHIFT:
				driver = "com.amazon.redshift.jdbc4.Driver";
				break;
		}
		return driver;
	}
	
	public static BasicDataSource getDataSource()
	{
		return dataSource;
	}
	public static String getDatabase()
	{
		return database;
	}
	
	public static void closeDataSource()
	{
		dataSource = null;
	}
}
