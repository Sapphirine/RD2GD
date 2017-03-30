package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import oracle.jdbc.pool.OracleDataSource;

public class DbConnect
{

		private static Connection connection = null;
		private static DbType dbType = null;
		
		public static void connect(String server,String domain,String user,String password,DbType type, String database)
			throws SQLException
		{
			dbType = type;
			switch(dbType)
			{
				case MYSQL: 
					connection = connectToMySQL(server, user, password,database);
					break;
				case MSSQL:
					connection = connectToMSSQL(server, domain, user, password);
					break;
				case ORACLE:
					connection = connectToOracle(server, domain, user, password);
					break;
				case POSTGRESQL:
					connection = connectToPostgreSQL(server, user, password);
					break;
				case MSACCESS:
					connection = connectToMsAccess(server, user, password);
					break;
				case REDSHIFT:
					connection = connectToRedshift(server, user, password);
					break;
			}
		}

	private static Connection connectToRedshift(String server,String user, String password) throws SQLException 
	{
		if (!server.contains("/"))
		{
			throw new SQLException("For Redshift, database name must be specified in the server field (<host>:<port>/<database>?<options>)");
		}
		try 
		{
			Class.forName("com.amazon.redshift.jdbc4.Driver");
		} 
		catch (ClassNotFoundException e1) 
		{
			throw new SQLException("Cannot find JDBC driver. Make sure the file RedshiftJDBCx-x.x.xx.xxxx.jar is in the path");
		}
		
		String url = "jdbc:redshift://" + server;
		
		try 
		{
			return DriverManager.getConnection(url, user, password);
		} 
		catch (SQLException e1) 
		{
			throw new SQLException("Cannot connect to DB server: " + e1.getMessage());
		}		
	}
	
	private static Connection connectToMsAccess(String server, String user, String password) throws SQLException
	{
		try
		{
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		}
		catch (ClassNotFoundException e) 
		{
			throw new SQLException("Cannot find ucanaccess driver. Make sure the file ucanaccess-3.0.3.1.jar is in the path");
		}
		
		String url = "jdbc:ucanaccess://" + server + ";sysschema=true";
		
		try
		{
			return DriverManager.getConnection(url, user, password);
		}
		catch (SQLException e) 
		{
			throw new SQLException("Cannot connect to DB server: " + e.getMessage());
		}
	}

	private static Connection connectToPostgreSQL(String server, String user, String password) throws SQLException 
	{
		if (!server.contains("/"))
		{
			throw new SQLException("For PostgreSQL, database name must be specified in the server field (<host>/<database>)");
		}
		if (!server.contains(":"))
		{
			server = server.replace("/", ":5432/");
		}
		try 
		{
			Class.forName("org.postgresql.Driver");
		} 
		catch (ClassNotFoundException e1) 
		{
			throw new SQLException("Cannot find JDBC driver. Make sure the file postgresql-x.x-xxxx.jdbcx.jar is in the path");
		}
		
		String url = "jdbc:postgresql://" + server;
		try 
		{
			return DriverManager.getConnection(url, user, password);
		} 
		catch (SQLException e1) 
		{
			throw new SQLException("Cannot connect to DB server: " + e1.getMessage());
		}
	}

	private static Connection connectToMySQL(String server,String user, String password, String database) throws SQLException
	{
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
		} 
		catch (ClassNotFoundException e1) 
		{
			e1.printStackTrace();
			throw new SQLException("Cannot find JDBC driver. Make sure the file mysql-connector-java-x.x.xx-bin.jar is in the path");
		}
		String url = "jdbc:mysql://";
		if(!server.contains(":"))
		{
			url += server + ":3306/?useCursorFetch=true&zeroDateTimeBehavior=convertToNull";
		}
		else
		{
			url += server + "/?useCursorFetch=true&zeroDateTimeBehavior=convertToNull";
		}

		try 
		{
			return DriverManager.getConnection(url, user, password);
		} 
		catch (SQLException e1) 
		{
			e1.printStackTrace();
			throw new SQLException("Cannot connect to DB server: " + e1.getMessage());
		}
	}

	private static Connection connectToMSSQL(String server, String domain, String user, String password) throws SQLException 
	{
		if (user == null || user.length() == 0) // Use Windows integrated security 
		{ 
			try 
			{
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			} 
			catch (ClassNotFoundException e1) 
			{
				throw new SQLException("Cannot find JDBC driver. Make sure the file sqljdbc4.jar is in the path");
			}
			
			String url = "jdbc:sqlserver://" + server + ";integratedSecurity=true";

			try 
			{
				return DriverManager.getConnection(url, user, password);
			} 
			catch (SQLException e1) 
			{
				throw new SQLException("Cannot connect to DB server: " + e1.getMessage());
			}
		} 
		else // Do not use Windows integrated security 
		{ 
			try 
			{
				Class.forName("net.sourceforge.jtds.jdbc.Driver");
			} 
			catch (ClassNotFoundException e1) 
			{
				throw new SQLException("Cannot find JDBC driver. Make sure the file jtds-1.3.0.jar is in the path");
			}

			String url = "jdbc:jtds:sqlserver://" + server + ";ssl=required" + ((domain == null || domain.length() == 0) ? "" : ";domain=" + domain);

			try 
			{
				return DriverManager.getConnection(url, user, password);
			} 
			catch (SQLException e1) 
			{
				throw new SQLException("Cannot connect to DB server: " + e1.getMessage());
			}
		}
	}

	private static Connection connectToOracle(String server, String domain, String user, String password) throws SQLException
	{
		try 
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			throw new SQLException("Class not found exception: " + e.getMessage());
		}
		
		// First try OCI driver:
		String error = null;
		try 
		{
			OracleDataSource ods;
			ods = new OracleDataSource();
			ods.setURL("jdbc:oracle:oci8:@" + server);
			ods.setUser(user);
			ods.setPassword(password);
			return ods.getConnection();
		} 
		catch (UnsatisfiedLinkError e) 
		{
			error = e.getMessage();
		} 
		catch (SQLException e) 
		{
			error = e.getMessage();
		}
		// If fails, try THIN driver:
		if (error != null)
			try 
			{
				String host = "127.0.0.1";
				String sid = server;
				String port = "1521";
				if (server.contains("/")) 
				{
					host = server.split("/")[0];
					if (host.contains(":")) 
					{
						port = host.split(":")[1];
						host = host.split(":")[0];
					}
					sid = server.split("/")[1];
				}
				OracleDataSource ods;
				ods = new OracleDataSource();
				ods.setURL("jdbc:oracle:thin:@" + host + ":" + port + ":" + sid);
				ods.setUser(user);
				ods.setPassword(password);
				return ods.getConnection();
			} 
			catch (SQLException e) 
			{
				throw new SQLException("Cannot connect to DB server:\n- When using OCI: " + error + "\n- When using THIN: " + e.getMessage());
			}
		return null;
	}
	
	public static Connection getConnection()
	{
		return connection;
	}
	public static DbType getDbType()
	{
		return dbType;
	}
	public static void setConnection(Connection connection)
	{
		connection = null;
	}
}
