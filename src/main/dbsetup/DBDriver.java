package main.dbsetup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.CSCC43DatabaseProject;

/**
 * Establishes the connection to the database
 * 
 * @author Jeremy Lai
 *
 */
public class DBDriver {

	private static final String sqlClass = "com.mysql.cj.jdbc.Driver";
	private static final String CONNECTION = "jdbc:mysql://localhost/";

	/**
	 * Connects to SQL db
	 * 
	 * @return the connection
	 * @throws ClassNotFoundException 
	 */
	public static Connection dbConnect(String[] cred) throws ClassNotFoundException {
		Class.forName(sqlClass);
		Connection connection = null;
		String user = cred[0];
		String pass = cred[1];
		String connectionName = CONNECTION + cred[2];
		try {

			connection = DriverManager.getConnection(connectionName, user, pass);
			System.out.println("Database Connection Success");
		} catch (SQLException ex) {
			System.err.println("Database Connection Failure");
			Logger.getLogger(CSCC43DatabaseProject.class.getName()).log(Level.SEVERE, null, ex);
		}

		return connection;
	}

	/**
	 * Signals start of disconnect
	 * 
	 * @return the connection
	 * @throws ClassNotFoundException 
	 */
	public void dbDisconnect() {
		System.out.println("Attempting Database disconnection");
	}

	/**
	 *  Legacy code, not needed
	 * @param connection
	 * @return
	 */
	protected static Connection initialize(Connection connection) {
		if (!initializeDatabase(connection)) {
			System.out.println("Error connecting to new DB");
		}
		return connection;
	}

	/**
	 * Initialize the db
	 * legacy code, not needed
	 * @param connection the connection to which db is connected
	 * @return true/false depending on db initialization success
	 */
	private static boolean initializeDatabase(Connection connection) {
		Statement statement = null;

		try {
			statement = connection.createStatement();

			String sql = "CREATE TABLE Login " + "(ID INTEGER PRIMARY KEY NOT NULL," + "username TEXT,"
					+ "password TEXT," + "firstName TEXT," + "lastName TEXT," + "accountType TEXT)";
			statement.executeUpdate(sql);
			sql = "CREATE TABLE SavedQueries " + "(ID INTEGER PRIMARY KEY NOT NULL," + "name TEXT,"
					+ "description TEXT," + "query TEXT UNIQUE)";

			statement.executeUpdate(sql);
			statement.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
