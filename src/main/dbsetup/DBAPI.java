package main.dbsetup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that offers the basic database modification tools such as UPDATE,
 * DELETE, INSERT, ALTER for adding, removing, updating columns rows or tables
 * from a database
 * 
 * @author Jeremy Lai
 *
 */
public class DBAPI {
	/**
	 * Removes a row from table
	 * 
	 * @param connection to database
	 * @param table      String name of the table
	 * @param condition  String of condition met to remove ie "ID=5"
	 */
	public static boolean removeData(Connection connection, String table, String condition) throws SQLException {
		String sql = "DELETE FROM '" + table + "' WHERE " + condition + ";";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		return preparedStatement.execute();
	}

	/**
	 * adds a column into table
	 * 
	 * @param connection to database
	 * @param table      String name of the table
	 * @param name       String name of the column
	 * @param type       String type of the column such as
	 *                   INTEGER/TEXT/REAL/BLOB/NUMERIC PRIMARY KEY NOT NULL etc
	 */
	public static void addColumn(Connection connection, String table, String name, String type) throws SQLException {
		for (String column : getTableColumnData(connection, table)) {
			if (column.contains(name)) {
				return;
			}
		}
		String sql = "ALTER TABLE '" + table + "' ADD COLUMN " + name + " " + type + ";";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.execute();
		preparedStatement.close();
	}

	/**
	 * removes a table from the database
	 * 
	 * @param connection to database
	 * @param table      String name of the table
	 */
	public static void deleteTable(Connection connection, String table) throws SQLException {
		String sql = "DROP TABLE IF EXISTS '" + table + "';";
		Statement Statement = connection.createStatement();
		Statement.executeUpdate(sql);
		Statement.close();
	}

	/**
	 * adds a table to the database
	 * 
	 * @param connection to database
	 * @param table      String name of the table
	 */
	public static void createTable(Connection connection, String table, String columnData) throws SQLException {
		if (!columnData.contains("PRIMARY KEY"))
			columnData = "ID INTEGER PRIMARY KEY NOT NULL" + columnData;
		String sql = "CREATE TABLE IF NOT EXISTS '" + table + "'(" + columnData + ");";
		Statement Statement = connection.createStatement();
		Statement.executeUpdate(sql);
		Statement.close();
	}

	/**
	 * renames a table to the database
	 * 
	 * @param connection to database
	 * @param table      String name of the table
	 * @param newName    String name of the table which it will now take on
	 */
	public static void renameTable(Connection connection, String table, String newName) throws SQLException {
		String sql = "ALTER TABLE '" + table + "' RENAME TO '" + newName + "';";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.execute();
		preparedStatement.close();
	}

	/**
	 * removes a column from a table
	 * 
	 * @param connection to database
	 * @param table      String name of the table
	 * @param name       String name of the column
	 */
	public static void deleteColumn(Connection connection, String table, String name) throws SQLException {
		List<String> columns = DBAPI.getTableColumnData(connection, table);
		if (!columns.isEmpty()) {
			String columnData = "";
			for (String columnName : columns) {
				if (!columnName.contains(name)) {
					columnData += ", " + columnName;
				}
			}
			columnData = columnData.substring(2);
			renameTable(connection, table, table + "_TEMP");
			createTable(connection, table, columnData);
			deleteTable(connection, table + "_TEMP");
		}
	}

	/**
	 * edits the values of a row of a table
	 * 
	 * @param connection    to database
	 * @param table         String name of the table
	 * @param columnToValue String series of "[column name]=[overwriting value],
	 *                      ..."
	 * @param condition     String condition isolating which rows to edit ie "ID=5"
	 */
	public static void updateData(Connection connection, String table, String columnToValue, String condition)
			throws SQLException {
		String sql = "UPDATE '" + table + "' SET " + columnToValue + " WHERE " + condition + ";";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.executeUpdate();
		preparedStatement.close();
	}

	/**
	 * adds a blank row to a table given no information
	 * 
	 * @param connection to database
	 * @param table      String name of the table
	 */
	public static void insertData(Connection connection, String destination) throws SQLException {
		String sql = "INSERT INTO '" + destination + "' DEFAULT VALUES;";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.execute();
		preparedStatement.close();
	}

	/**
	 * adds a row of data to a table
	 * 
	 * @param connection  to database
	 * @param destination String name of the table
	 * @param attributes  String csv of corresponding columns for values
	 * @param values      String csv of corresponding entries for column in
	 *                    attributes
	 */
	public static void insertData(Connection connection, String destination, String attributes, String values)
			throws SQLException {
		String sql = "INSERT INTO '" + destination + "'(" + attributes + ")" + " VALUES(" + values + ");";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.execute();
		preparedStatement.close();
	}

	/**
	 * inserts a new user to our database
	 * 
	 * @param connection  to database
	 * @param username    String username to login with for the user
	 * @param password    String password prehashed of the user
	 * @param firstName   String first name of the user
	 * @param lastName    String last name of the user
	 * @param accountType String from set of {"Admin", "Receptionist", "Service
	 *                    Provider", "Settlement Worker"}
	 */
	public static void insertUser(Connection connection, String username, String password, String firstName,
			String lastName, String accountType) throws SQLException {
		// check the user is not already made
		ResultSet results = getData(connection, "ID, firstName, lastName, accountType", "Login",
				"username = '" + username + "'");
		if (!results.next()) {
			String sql = "INSERT INTO Login(username, firstName, lastName, accountType) VALUES(?,?,?,?);";
			try {
				// insert the basic information of the user excluding the password
				PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setString(1, username);
				preparedStatement.setString(2, firstName);
				preparedStatement.setString(3, lastName);
				preparedStatement.setString(4, accountType);
				int id = 0;
				// retrieve primary key to find the row entry
				if (preparedStatement.executeUpdate() > 0) {
					ResultSet uniqueKey = preparedStatement.getGeneratedKeys();
					if (uniqueKey.next()) {
						id = uniqueKey.getInt(1);
						// hash and insert the password to aforementioned row
						accountPasswordHelper(connection, id, password);
					}
				}
				preparedStatement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		results.close();
	}

	/**
	 * hashes and inserts the password to user entry
	 * 
	 * @param connection to database
	 * @param id         int of the user entry Primary Key
	 * @param password   String to be hashed
	 */
	private static void accountPasswordHelper(Connection connection, int id, String password) {
		String sql = "Update Login set password = ? where ID = ?;";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, passwords.passwordHash(password));
			preparedStatement.setInt(2, id);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks the password to match the registered user
	 * 
	 * @param connection to database
	 * @param username   String of the user's username
	 * @param password   to check with the entry under the unique username
	 * @return boolean true if match, false otherwise
	 */
	protected static boolean checkPassword(Connection connection, String username, String password)
			throws SQLException {
		String sql = "SELECT password FROM Login WHERE Username = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, username);
		ResultSet results = preparedStatement.executeQuery();
		boolean match = false;
		if (results.isClosed())
			return false;
		else {
			match = results.getString("password").equals(passwords.passwordHash(password));
		}
		results.close();
		return match;

	}

	/**
	 * returns a resultset containing the data of a query structure
	 * 
	 * @param connection to database
	 * @param select     String column names csv
	 * @param from       String name of the table
	 * @param condition  String csv of columnName*operands*value
	 * @return resultsSet containing the results but must be closed when done using
	 */
	public static ResultSet getData(Connection connection, String select, String from) throws SQLException {
		String sql = "SELECT " + select + " FROM '" + from + "'";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		ResultSet results = preparedStatement.executeQuery();
		return results;
	}

	/**
	 * returns a resultset containing the data of a query structure
	 * 
	 * @param connection to database
	 * @param select     String column names csv
	 * @param from       String name of the table
	 * @param condition  String csv of columnName*operands*value
	 * @return resultsSet containing the results but must be closed when done using
	 */
	public static ResultSet getData(Connection connection, String select, String from, String condition)
			throws SQLException {
		String sql = "SELECT " + select + " FROM '" + from + "' WHERE " + condition;
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		ResultSet results = preparedStatement.executeQuery();
		return results;
	}

	/**
	 * returns the user item if valid login credentials are provided
	 * 
	 * @param connection to database
	 * @param username   String user's username
	 * @param password   String password for the user
	 * @return User type matching the user's actual account type
	 */
	public static User login(Connection connection, String username, String password) throws SQLException {
		UserFactory AccountCreator = new UserFactory();
		User Account = null;
		if (checkPassword(connection, username, password)) {
			ResultSet results = getData(connection, "ID, firstName, lastName, accountType", "Login",
					"username = '" + username + "'");
			int ID = Integer.parseInt(results.getString("ID"));
			String firstName = results.getString("firstName");
			String lastName = results.getString("lastName");
			String accountType = results.getString("accountType");
			Account = AccountCreator.getUser(username, firstName, lastName, ID, accountType);
			results.close();
		}
		return Account;
	}

	/**
	 * returns the type of a column of a table
	 * 
	 * @param connection to database
	 * @param table      String name of the table
	 * @param columnName String of the selected column
	 * @return String name of the column type
	 */
	public static String getTableColumnType(Connection connection, String table, String columnName)
			throws SQLException {
		String sql = "pragma table_info('" + table + "');";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		ResultSet results = preparedStatement.executeQuery();
		String columnData = null;
		while (results.next()) {
			if (results.getString(2).equals(columnName)) {
				columnData = results.getString(3);
				break;
			}
		}
		results.close();
		return columnData;
	}

	/**
	 * returns true if the column selected cannot be set null
	 * 
	 * @param connection to database
	 * @param table      String name of the table
	 * @param columnName String name of the selected column
	 * @return boolean true if the column cannot be set null, false otherwise
	 */
	public static boolean getTableColumnMandatory(Connection connection, String table, String columnName)
			throws SQLException {
		String sql = "pragma table_info('" + table + "');";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		ResultSet results = preparedStatement.executeQuery();
		boolean isMandatory = false;
		while (results.next()) {
			if (results.getString(2).equals(columnName)) {
				isMandatory = results.getString(4).equals("1");
				break;
			}
		}
		results.close();
		return isMandatory;
	}

	/**
	 * returns a list of String of necessary data to replicate a a table
	 * 
	 * @param connection to database
	 * @param table      String name of the table
	 * @return List<String> of column meta data
	 */
	public static List<String> getTableColumnData(Connection connection, String table) throws SQLException {
		ArrayList<String> columns = new ArrayList<String>();
		String sql = "pragma table_info('" + table + "');";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		ResultSet results = preparedStatement.executeQuery();
		String columnData;
		while (results.next()) {
			columnData = results.getString(2) + " " + results.getString(3);
			if (results.getString(6).equals("1"))
				columnData += " PRIMARY KEY";
			if (results.getString(4).equals("1"))
				columnData += " NOT NULL";
			columns.add(columnData);
		}
		results.close();
		return columns;
	}
}
