package main;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Scanner;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;

import main.dbsetup.DBAPI;
import main.dbsetup.DBDriver;
import main.menus.HostMenu;
import main.menus.RenterMenu;
import operations.operations;
import operations.queries;

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
		boolean success = false;
		if (sc == null) {
			sc = new Scanner(System.in);
		}
		if (sqlMngr == null) {
			sqlMngr = new DBDriver();
		}
		try {
			System.out.println("Enter the database and the connection credentials you were given proceed further.");
			userConnection = DBDriver.dbConnect(this.getCredentials());
			if (userConnection != null) {
				success = true;
			}
		} catch (Exception e) {
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
					case 1: // Create User Account + Host/Renter
						String hostInfo[] = this.userInfo();
						System.out.print(
								"\nType 'host' to start with a host profile, or 'renter' for a renter profile, or anything else to exit: ");
						input = sc.nextLine();
						if (input.equalsIgnoreCase("renter")) {
							System.out.print("Enter your Credit Card number: ");
							String creditCard = sc.next();
							try {
								operations.createProfile(userConnection, hostInfo[0], hostInfo[1], hostInfo[2],
										hostInfo[3], hostInfo[4], creditCard);
								System.out.println("Renter Account Created!");
							} catch (SQLIntegrityConstraintViolationException e) {
								System.err.println("User Account with that SIN already exists!" + e.getMessage());
							}

						} else if (input.equalsIgnoreCase("host")) {
							try {
								operations.createProfile(userConnection, hostInfo[0], hostInfo[1], hostInfo[2],
										hostInfo[3], hostInfo[4]);
								System.out.println("Host Account Created!");
							} catch (SQLIntegrityConstraintViolationException e) {
								System.err.println("User Account with that SIN already exists!" + e.getMessage());
							}
						}

						break;
					case 2: // Select Host/Renter Profile
						System.out.print(
								"Type 'host' to view all host profiles, 'renter' to view all renter profiles, anything else to exit: ");
						input = sc.nextLine();
						boolean exit = false;
						if (input.equalsIgnoreCase("renter")) {
							// Get all the renter profiles from the database, then display and allow user to
							// select via index
							ArrayList<ArrayList<String>> allRenters = queries.getRenter(userConnection);
							this.printRenterResults(allRenters);
							input = sc.nextLine();

							while (!exit) {
								System.out.print("Enter the number of the renter to select/login to, or '0' to exit: ");
								input = sc.nextLine();
								int renterIndex = 0;
								try {
									renterIndex = Integer.parseInt(input);
									if (renterIndex > allRenters.size() || renterIndex < 0) {
										System.err.println("Invalid index value!");
									} else if (renterIndex == 0) {
										exit = true;
									} else {
										// RUN THE RENTER MENU
										RenterMenu renterMenu = new RenterMenu(allRenters.get(renterIndex - 1),
												this.userConnection);
										if (renterMenu.execute()) {
											renterMenu.terminate();
											exit = true;
										}
									}
								} catch (NumberFormatException e) {
									System.err.println("Invalid format value!");
								}
							}

						} else if (input.equalsIgnoreCase("host")) {
							// Get all the host profiles from the database, then display and allow user to
							// select via index
							ArrayList<ArrayList<String>> allHosts = queries.getHosts(userConnection);
							this.printHostResults(allHosts);
							while (!exit) {
								System.out.print("Enter the number of the host to select/login to, or '0' to exit: ");
								input = sc.nextLine();
								int hostIndex = 0;
								try {
									hostIndex = Integer.parseInt(input);
									if (hostIndex > allHosts.size() || hostIndex < 0) {
										System.err.println("Invalid index value!");
									} else if (hostIndex == 0) {
										exit = true;
									} else {
										// RUN THE HOST MENU
										HostMenu hostMenu = new HostMenu(allHosts.get(hostIndex - 1),
												this.userConnection);
										if (hostMenu.execute()) {
											hostMenu.terminate();
											exit = true;
										}
									}
								} catch (NumberFormatException e) {
									System.err.println("Invalid format value!");
								}
							}

						}
						break;
					case 3: // Add a Host or Renter Profile
						System.out
								.print("Enter the SIN number for the account to add a profile to, or 'exit' to exit: ");
						input = sc.nextLine();
						if (!input.equalsIgnoreCase("exit")) {
							System.out.print("Do you wish to add a 'host' profile, or 'renter' profile: ");
							String type = sc.nextLine();
							if (type.equalsIgnoreCase("host")) {
								try {
									operations.makeHost(userConnection, input);
								} catch (SQLIntegrityConstraintViolationException e) {
									System.err.printf("Account with SIN: %s does not exist, or already have a host profile!", input);
								}
			
							} else if (type.equalsIgnoreCase("renter")) {
								try {
									operations.deleteProfile(userConnection, input, false);
								} catch (SQLIntegrityConstraintViolationException e) {
									System.err.printf("Account with SIN: %s does not exist, or doesn't have a renter profile!", input);
								}
							}
						}
						break;
					case 4: // Delete a host or Renter Profile
						System.out.print(
								"Enter the SIN number for the account to delete a profile from, or 'exit' to exit: ");
						input = sc.nextLine();
						if (!input.equalsIgnoreCase("exit")) {
							System.out.print("Do you wish to delete the 'host' profile, or 'renter' profile: ");
							String type = sc.nextLine();
							if (type.equalsIgnoreCase("host")) {
								try {
									operations.deleteProfile(userConnection, input, true);
								} catch (SQLException e) {
									System.err.printf("Account with SIN: %s does not exist, or doesn't have a host profile!", input);
								}
			
							} else if (type.equalsIgnoreCase("renter")) {
								try {
									operations.deleteProfile(userConnection, input, false);
								} catch (SQLException e) {
									System.err.printf("Account with SIN: %s does not exist, or doesn't have a renter profile!", input);
								}
							}
						}
						break;
					case 5:// Delete a User account and associated profiles
						System.out.println(
								"WARNING: This action removes a user account and all associated profiles to it!");
						System.out.print(
								"Enter the SIN number for the account to delete the User account for, or 'exit' to exit: ");
						input = sc.nextLine();
						if (!input.equalsIgnoreCase("exit")) {
							try {
								operations.deleteUser(userConnection, input);
								System.out.println("Account has been deleted.");
							} catch (SQLException e){
								System.err.printf("Account with SIN %s does not exist!", input);
							}
						}
						break;
					case 6:
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

	private void printHostResults(ArrayList<ArrayList<String>> results) {
		System.out.printf("Hosts: %-9s%-15s%-35s%-20s%-10s%n", "SIN", "Name", "Address", "Occupation", "DoB");
		System.out
				.println("-------------------------------------------------------------------------------------------");
		for (int i = 0; i < results.size(); i++) {
			System.out.printf("%-7d%-9s%-15s%-35s%-20s%-10s%n", i + 1, (results.get(i)).get(0), (results.get(i)).get(1),
					(results.get(i)).get(2), (results.get(i)).get(3), (results.get(i)).get(4));
		}
		System.out
				.println("-------------------------------------------------------------------------------------------");
	}

	private void printRenterResults(ArrayList<ArrayList<String>> results) {
		System.out.printf("Renters: %-9s%-15s%-35s%-20s%-10s%-16%n", "SIN", "Name", "Address", "Occupation", "DoB",
				"Credit Card");
		System.out
				.println("-------------------------------------------------------------------------------------------");
		for (int i = 0; i < results.size(); i++) {
			System.out.printf("%-9d%-9s%-15s%-35s%-20s%-10s%n", i + 1, (results.get(i)).get(0), (results.get(i)).get(1),
					(results.get(i)).get(2), (results.get(i)).get(3), (results.get(i)).get(4), (results.get(i)).get(5));
		}
		System.out
				.println("-------------------------------------------------------------------------------------------");
	}

	private String[] userInfo() {
		String[] info = new String[5];
		System.out.print("Enter your SIN: ");
		info[0] = sc.nextLine();
		System.out.print("Enter your name: ");
		info[1] = sc.nextLine();
		System.out.print("Enter your address: ");
		info[2] = sc.nextLine();
		System.out.print("Enter your occupation: ");
		info[3] = sc.nextLine();
		System.out.print("Enter your date of birth (DD/MM/YYYY): ");
		info[4] = sc.nextLine();
		return info;
	}

	// Print menu options
	private static void menu() {
		System.out.println("=========User MENU=========");
		System.out.println("0. Exit.");
		System.out.println("1. Create an Account + Host/Renter Profile.");
		System.out.println("2. Select/Login a Host/Renter Profile.");
		System.out.println("3. Add a Host/Renter Profile.");
		System.out.println("4. Delete a Host/Renter Profile.");
		System.out.println("5. Delete a User Account (and associated profiles).");
		System.out.println("6. View AirBnB report statistics.");
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

}
