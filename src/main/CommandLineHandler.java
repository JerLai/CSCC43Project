package main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import main.dbsetup.DBAPI;
import main.dbsetup.DBDriver;
import operations.operations;

/**
 * handles Command Line Handler to handle running this java program outside an
 * editor taken from the sample CommandLine.java provided at
 * http://www.cs.toronto.edu/~koudas/courses/csc43/dynamic/CommandLine.java
 * 
 * @author Jeremy Lai
 *
 */
public class CommandLineHandler {

	// 'sqlMngr' is the object which interacts directly with MySQL, or just rather
	// sets up the connection
	private DBDriver sqlMngr = null;
	// 'sc' is needed in order to scan the inputs provided by the user
	private Scanner sc = null;
	private Connection userConnection = null;
	// Public functions - CommandLine State Functions

	/**
	 * Starts the connection to the database given specific connection credentials
	 * 
	 * @return if the connection was a success
	 */
	public boolean startSession() {
		boolean success = true;
		if (sc == null) {
			sc = new Scanner(System.in);
		}
		if (sqlMngr == null) {
			sqlMngr = new DBDriver();
		}
		try {
			System.out.println("Enter the database and the connection credentials you were given proceed further.");
			userConnection = DBDriver.dbConnect(this.getCredentials());
		} catch (ClassNotFoundException e) {
			success = false;
			System.err.println("Establishing connection triggered an exception!");
			e.printStackTrace();
			sc = null;
			sqlMngr = null;
		}
		return success;
	}

	/**
	 * Destroys all related components that connect to the database
	 */
	public void endSession() {
		if (sqlMngr != null)
			sqlMngr.dbDisconnect();
		try {
			userConnection.close();
		} catch (SQLException e) {
			System.err.println("Exception occurred while disconnecting!");
			e.printStackTrace();
		} finally {
			userConnection = null;
		}
		if (sc != null) {
			sc.close();
		}
		sqlMngr = null;
		sc = null;
	}

	/**
	 * Menu handler for the user once connected to the database, executes in an
	 * infinite loop until user decides to exit
	 * 
	 * @return whether or not the user has decided to exit
	 * @throws SQLException
	 */
	public boolean execute() throws SQLException {
		if (sc != null && sqlMngr != null) {
			System.out.println("");
			System.out.println("***************************");
			System.out.println("******ACCESS GRANTED*******");
			System.out.println("***************************");
			System.out.println("");

			String input = "";
			int choice = -1;
			do {
				menu(); // Print Menu
				input = sc.nextLine();
				try {
					choice = Integer.parseInt(input);
					switch (choice) { // Activate the desired functionality
					case 1: // Create Host Profile
						String hostInfo[] = this.userInfo();
						operations.createProfile(userConnection, hostInfo[0], hostInfo[1], hostInfo[2], hostInfo[3],
								hostInfo[4]);
						break;
					case 2: // Create Renter Profile
						String userInfo[] = this.userInfo();
						System.out.print("\nEnter your Credit Card number: ");
						String creditCard = sc.next();
						operations.createProfile(userConnection, userInfo[0], userInfo[1], userInfo[2], userInfo[3],
								userInfo[4], creditCard);
						break;
					case 3: // Select Host Profile
						System.out.print("Enter 0 to return, or a SIN for a host profile: ");
						String hostSIN = sc.next();
						if (Integer.parseInt(hostSIN) == 0) {
							break;
						} else {
							hostMenu();
							// TODO: host menu options
							break;
						}
					case 4: // Select Renter Profile
						System.out.print("Enter 0 to return, or a SIN for a renter profile: ");
						String renterSIN = sc.next();
						if (Integer.parseInt(renterSIN) == 0) {
							break;
						} else {
							renterMenu();
							// TODO: renter menu options
							break;
						}
					case 5: // Print database schema
						this.printSchema();
						break;
					case 6: // Print a table from the database
						this.printColSchema();
						break;
					case 7:
						System.out.println("Report options coming soon");
						break;
					default:
						break;
					}
				} catch (NumberFormatException e) {
					input = "-1";
				}
			} while (input.compareTo("0") != 0);

			return true;
		} else {
			System.out.println("");
			System.out.println("Connection could not been established! Bye!");
			System.out.println("");
			return false;
		}
	}

	private String[] userInfo() {
		String[] info = new String[5];
		System.out.print("Enter your SIN: ");
		info[0] = sc.next();
		System.out.print("\nEnter your name: ");
		info[1] = sc.next();
		System.out.print("\nEnter your address: ");
		info[2] = sc.next();
		System.out.print("\nEnter your occupation: ");
		info[3] = sc.next();
		System.out.print("\nEnter your date of birth (DD/MM/YYYY): ");
		info[4] = sc.next();
		return info;
	}

	// Print menu options
	private static void menu() {
		System.out.println("=========User MENU=========");
		System.out.println("0. Exit.");
		System.out.println("1. Create a Host Profile.");
		System.out.println("2. Create a Renter Profile.");
		System.out.println("3. Select a Host Profile.");
		System.out.println("4. Select a Renter Profile.");
		System.out.println("5. Print schema.");
		System.out.println("6. Print table schema.");
		System.out.println("7. View AirBnB report statistics.");
		System.out.print("Choose one of the previous options [0-7]: ");
	}

	private static void renterMenu() {
		System.out.println("=========Renter MENU=========");
		System.out.println("0. Exit.");
		System.out.println("1. View Reservations.");
		System.out.println("2. Search for listings.");
		System.out.println("3. Make a reservation.");
		System.out.println("4. Cancel a reservation.");
		System.out.println("5. Rate a listing.");
		System.out.println("6. Rate a host.");
		System.out.print("Choose one of the previous options [0-6]: ");
	}

	private static void hostMenu() {
		System.out.println("=========Host MENU=========");
		System.out.println("0. Exit.");
		System.out.println("1. View Listings.");
		System.out.println("2. Add a listing.");
		System.out.println("3. Remove a listing.");
		System.out.println("4. Cancel a reservation.");
		System.out.println("5. Adjust the price of a listing.");
		System.out.println("6. Rate a renter.");
		System.out.print("Choose one of the previous options [0-6]: ");
	}

	// Called during the initialization of an instance of the current class
	// in order to retrieve from the user the credentials with which our program
	// is going to establish a connection with MySQL
	private String[] getCredentials() {
		String[] cred = new String[3];
		System.out.print("Username: ");
		cred[0] = sc.nextLine();
		System.out.print("Password: ");
		cred[1] = sc.nextLine();
		System.out.print("Database: ");
		cred[2] = sc.nextLine();
		return cred;
	}

	// Function that handles the feature: "3. Print schema."
	private void printSchema() {
		ArrayList<String> schema = DBAPI.getSchema(this.userConnection);

		System.out.println("");
		System.out.println("------------");
		System.out.println("Total number of tables: " + schema.size());
		for (int i = 0; i < schema.size(); i++) {
			System.out.println("Table: " + schema.get(i));
		}
		System.out.println("------------");
		System.out.println("");
	}

	// Function that handles the feature: "4. Print table schema."
	private void printColSchema() {
		System.out.print("Table Name: ");
		String tableName = sc.nextLine();
		ArrayList<String> result = DBAPI.colSchema(userConnection, tableName);
		System.out.println("");
		System.out.println("------------");
		System.out.println("Total number of fields: " + result.size() / 2);
		for (int i = 0; i < result.size(); i += 2) {
			System.out.println("-");
			System.out.println("Field Name: " + result.get(i));
			System.out.println("Field Type: " + result.get(i + 1));
		}
		System.out.println("------------");
		System.out.println("");
	}

//	// Function that handles the feature: "2. Select a record."
//	private void selectOperator() {
//		String query = "";
//		System.out.print("Issue the Select Query: ");
//		query = sc.nextLine();
//		query.trim();
//		if (query.substring(0, 6).compareToIgnoreCase("select") == 0)
//			sqlMngr.selectOp(query);
//		else
//			System.err.println("No select statement provided!");
//	}
//
//	// Function that handles the feature: "1. Insert a record."
//	private void insertOperator() {
//		int rowsAff = 0;
//		int counter = 0;
//		String query = "";
//		System.out.print("Table: ");
//		String table = sc.nextLine();
//		System.out.print("Comma Separated Columns: ");
//		String cols = sc.nextLine();
//		System.out.print("Comma Separated Values: ");
//		String[] vals = sc.nextLine().split(",");
//		// transform the user input into a valid SQL insert statement
//		query = "INSERT INTO " + table + " (" + cols + ") VALUES(";
//		for (counter = 0; counter < vals.length - 1; counter++) {
//			query = query.concat("'" + vals[counter] + "',");
//		}
//		query = query.concat("'" + vals[counter] + "');");
//		System.out.println(query);
//		rowsAff = sqlMngr.insertOp(query);
//		System.out.println("");
//		System.out.println("Rows affected: " + rowsAff);
//		System.out.println("");
//	}
}
