package main.dbsetup;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
	 * adds a row of data to a table based on a pre-written query
	 * 
	 * @param connection  to database
	 * @param query the query to execute
	 */
	public static void sendQuery(Connection connection, String query)
			throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.execute();
		preparedStatement.close();
	}

	/**
	 * returns a ResultSet containing the data of a pre-written query
	 * @param connection to database
	 * @param query the query to execute
	 * @return the data generated from the query
	 * @throws SQLException
	 */
	public static ResultSet getDataByQuery(Connection connection, String query) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(query);
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
	 * returns the schema for the database
	 * @param connection
	 * @return
	 */
	public static ArrayList<String> getSchema(Connection connection) {
		ArrayList<String> output = new ArrayList<String>();
		try {
			DatabaseMetaData meta = connection.getMetaData();
			ResultSet schemas = meta.getTables(null,null,"%",null);
			//ResultSet catalogs = meta.getCatalogs();
			while (schemas.next()) {
				output.add(schemas.getString("TABLE_NAME"));
			}
			schemas.close();
		} catch (SQLException e) {
			System.err.println("Retrieval of Schema Info failed!");
			e.printStackTrace();
			output.clear();
		}
		return output;
	}

    // Controls the execution of functionality: "4. Print table schema."
	public static ArrayList<String> colSchema(Connection connection, String tableName) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			DatabaseMetaData meta = connection.getMetaData();
			ResultSet rs = meta.getColumns(null, null, tableName, null);
			while(rs.next()) {
				result.add(rs.getString(4));
				result.add(rs.getString(6));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Retrieval of Table Info failed!");
			e.printStackTrace();
			result.clear();
		}
		return result;
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
