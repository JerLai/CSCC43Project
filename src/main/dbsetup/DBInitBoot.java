package main.dbsetup;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Makes/Initializes database on boot
 * may or may not be used
 * @author Jeremy Lai
 *
 */
public class DBInitBoot {

	public static void initialize() {
		Connection connection = DBDriver.dbConnect();
		try {
			initBootDB(connection);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				System.out.println("Connection closing failed. Aborting operation");
			}
		}
	}

	private static void initBootDB(Connection connection) {
		try {
			DBDriver.initialize(connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
