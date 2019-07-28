package main;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import main.dbsetup.DBDriver;
import main.menus.HostMenu;
import main.menus.RenterMenu;
import operations.operations;
import operations.queries;
import operations.reports;

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
						System.out.print(
								"\nType 'host' to start with a host profile, or 'renter' for a renter profile, or anything else to exit: ");
						input = sc.nextLine();
						String [] hostInfo = null;
						if (input.equalsIgnoreCase("renter")) {
							hostInfo = this.userInfo();
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
							hostInfo = this.userInfo();
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

							while (!exit) {
								System.out.print("Enter the number of the renter to select/login to, or '0' to exit: ");
								String renter = sc.nextLine();
								int renterIndex = 0;
								try {
									renterIndex = Integer.parseInt(renter);
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
								System.out.print("Enter the number of the host to select/login to, or 'exit' to exit: ");
								String host = sc.nextLine();
								int hostIndex = 0;
								try {
									hostIndex = Integer.parseInt(host);
									if (hostIndex > allHosts.size() || hostIndex < 0) {
										System.err.println("Invalid index value!");
									} else if (input.equalsIgnoreCase("exit")) {
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
									System.err.printf(
											"Account with SIN: %s does not exist, or already have a host profile!",
											input);
								}

							} else if (type.equalsIgnoreCase("renter")) {
								try {
									operations.deleteProfile(userConnection, input, false);
								} catch (SQLIntegrityConstraintViolationException e) {
									System.err.printf(
											"Account with SIN: %s does not exist, or doesn't have a renter profile!",
											input);
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
									System.err.printf(
											"Account with SIN: %s does not exist, or doesn't have a host profile!",
											input);
								}

							} else if (type.equalsIgnoreCase("renter")) {
								try {
									operations.deleteProfile(userConnection, input, false);
								} catch (SQLException e) {
									System.err.printf(
											"Account with SIN: %s does not exist, or doesn't have a renter profile!",
											input);
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
							} catch (SQLException e) {
								System.err.printf("Account with SIN %s does not exist!", input);
							}
						}
						break;
					case 6:
						String option = "";
						String type = "";
						boolean leave = false;
						do {
							try {
								Date start = null;
								Date end = null;
								String city = null;
								String country = null;
								reports();
								option = sc.nextLine();
								switch (option) {
								case "1": // per city
									System.out.print("Enter a start date (YYYY-MM-DD): ");
									start = Date.valueOf(sc.nextLine());
									System.out.print("Enter an end date (YYYY-MM-DD): ");
									end = Date.valueOf(sc.nextLine());
									HashMap<String, Integer> totalNumBkCity = reports
											.bookingsDateCity(this.userConnection, start, end);
									System.out.println("Report on Number of Bookings in a Date Range, Grouped by City");
									System.out.printf("%-20s|%-15s%n", "City", "Bookings");
									this.printHashMapStringInt(totalNumBkCity);
									break;
								case "2": // per zip code
									System.out.print("Enter a city: ");
									city = sc.nextLine();
									System.out.print("Enter a start date (YYYY-MM-DD): ");
									start = Date.valueOf(sc.nextLine());
									System.out.print("Enter an end date (YYYY-MM-DD): ");
									end = Date.valueOf(sc.nextLine());
									HashMap<String, Integer> totalNumBkPostal = reports
											.bookingsDatePostal(this.userConnection, city, start, end);
									System.out.println("Report on Number of Bookings in a Date Range, Grouped by Postal Code per City");
									System.out.printf("%-20s|%-15s%n", "Postal Code", "Bookings");
									this.printHashMapStringInt(totalNumBkPostal);
									break;
								case "3": // total number listings for 1, 2, 3
									System.out.print(
											"Type 0 for country, 1 for country and city, 2 for country, city and postal code: ");
									type = sc.nextLine();
									HashMap<String, Integer> totalListings = null;
									System.out.print("Report on Number of Listings, Grouped by ");
									if (type.equalsIgnoreCase("0")) {
										System.out.print("Country\n");
										System.out.printf("%-50s|%-10s%n", "Country", "Bookings");
										totalListings = reports.numListings(this.userConnection,
												0);
									} else if (type.equalsIgnoreCase("1")) {
										System.out.print("Country/City\n");
										System.out.printf("%-50s|%-10s%n", "Country/City", "Bookings");
										totalListings = reports.numListings(this.userConnection,
												1);
									} else if (type.equalsIgnoreCase("2")) {
										System.out.print("Country/City/Postal Code\n");
										System.out.printf("%-50s|%-10s%n", "Country/City/Postal Code", "Bookings");
										totalListings = reports.numListings(this.userConnection,
												2);
									}
									this.printHashMapStringInt(totalListings);
									break;
								case "4": // host ranking
									System.out.print("Type 0 for country, 1 for city: ");
									type = sc.nextLine();
									HashMap<String, ArrayList<String>> hostRanks = null;
									System.out.print("Report on host rankings by total number listings owned per ");
									if (type.equalsIgnoreCase("0")) {
										System.out.print("Country\n");
										System.out.printf("%-20s|%-20s%n", "Country", "Listings");
										hostRanks = reports
												.hostRanking(this.userConnection, 0);
									} else if (type.equalsIgnoreCase("1")) {
										System.out.print("City\n");
										System.out.printf("%-20s|%-20s%n", "City", "Listings");
										hostRanks = reports
												.hostRanking(this.userConnection, 1);
									}
									this.printHashMapStringArrayListString(hostRanks);
									break;
								case "5": // commercial hosts in country
									System.out.print("Enter the country to find potential commerical hosts for: ");
									country = sc.nextLine();
									ArrayList<String> maybeCommerce = reports.commercialHosts(this.userConnection,
											country);
									System.out.println("Report on Hosts that own more than 10% of all listings in a country");
									System.out.printf("%-20s%n", "Host");
									this.printArrayListString(maybeCommerce);
									break;
								case "6": // commercial hosts in city
									System.out.print("Enter the city to find potential commerical hosts for: ");
									city = sc.nextLine();
									ArrayList<String> maybeCommerceCity = reports
											.commercialHostsCity(this.userConnection, city);
									System.out.println("Report on Hosts that own more than 10% of all listings in a city");
									System.out.printf("%-20s%n", "Host");
									this.printArrayListString(maybeCommerceCity);
									break;
								case "7": // renters ranking in time frame
									System.out.print("Enter a start date (YYYY-MM-DD): ");
									start = Date.valueOf(sc.nextLine());
									System.out.print("Enter an end date (YYYY-MM-DD): ");
									end = Date.valueOf(sc.nextLine());
									ArrayList<String> renterRanks = reports.rentersRanking(this.userConnection, start,
											end);
									System.out.printf("Report on renter ranks on number of bookings made between %s and %s%n", start.toString(), end.toString());
									System.out.printf("%-20s%n", "Host");
									this.printArrayListString(renterRanks);
									break;
								case "8": // renters ranking city
									System.out.print("Enter a start date (YYYY-MM-DD): ");
									start = Date.valueOf(sc.nextLine());
									System.out.print("Enter an end date (YYYY-MM-DD): ");
									end = Date.valueOf(sc.nextLine());
									HashMap<String, ArrayList<String>> renterRanksCity = reports
											.rentersRankingCity(this.userConnection, start, end);
									System.out.printf("Report on renter ranks on number of bookings made per city who have made at least 2 bookings\n");
									System.out.printf("%-20s|%-20s|%-20s", "City", "Name", "Bookings");
									this.printHashMapStringArrayListString(renterRanksCity);
									break;
								case "9": // largest num cancellations host
									System.out.print("Enter a start date (YYYY-MM-DD): ");
									start = Date.valueOf(sc.nextLine());
									HashMap<String, Integer> hostCancels = reports.largestHost(this.userConnection,
											start);
									System.out.println("Report on Largest Number of Cancellations for a Host in a year from a start date");
									System.out.printf("%-20s|%-15s%n", "Host", "Cancellations");
									this.printHashMapStringInt(hostCancels);
									break;
								case "10": // largest num cancellations renter
									System.out.print("Enter a start date (YYYY-MM-DD): ");
									start = Date.valueOf(sc.nextLine());
									HashMap<String, Integer> renterCancels = reports.largestRenter(this.userConnection,
											start);
									System.out.println("Report on Largest Number of Cancellations for a Renter in a year from a start date");
									System.out.printf("%-20s|%-15s%n", "Renter", "Cancellations");
									this.printHashMapStringInt(renterCancels);
									break;
								case "11": // Word Cloud
									System.out.println("Here is the breakdown of the word clouds for the entries:");
									HashMap<Integer, HashMap<String, Integer>> wordCloudRes = reports
											.wordCloud(this.userConnection);
									this.printWordCloud(wordCloudRes);
									break;
								case "exit":
									leave = true;
									break;
								default:
									leave = true;
									break;
								}
							} catch (SQLException e) {
								System.err.println("An unexpected database error has occurred. Please try again.");
								break;
							}

						} while (!leave);
						leave = false;
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

	private void printHashMapStringInt(HashMap<String, Integer> results) {
		System.out.println("------------------------------------------------------------");
		for (String strKey : results.keySet()) {
			System.out.printf("%-50s|%-10d%n", strKey, results.get(strKey));
		}
		System.out.println("------------------------------------------------------------");
	}

	private void printHashMapStringArrayListString(HashMap<String, ArrayList<String>> results) {
		System.out.println("------------------------------------------------------------");
		for (String strKey : results.keySet()) {
			System.out.printf("%-20s", strKey);
			for (int i = 0; i < results.get(strKey).size(); i++) {
				System.out.printf("|%-20s%n", results.get(strKey).get(i));
			}
			System.out.println("------------------------------------------------------------");
		}
	}

	private void printArrayListString(ArrayList<String> results) {
		for(int i = 0; i < results.size(); i++) {
			System.out.printf("%-20s%n", results.get(i));
		}
	}

	private void printWordCloud(HashMap<Integer, HashMap<String, Integer>> results) {
		System.out.printf("%-10s|%-30s|%-5s", "ListingID", "Message", "Occurrences");
		System.out.println("------------------------------------------------------------");
		for (Integer listingID : results.keySet()) {
			for (String nounPhrase : results.get(listingID).keySet()) {
				System.out.printf("%-10d|%-30s|%-5d%n", listingID, nounPhrase, results.get(listingID).get(nounPhrase));
			}
			System.out.println("------------------------------------------------------------");
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
		System.out.printf("Renters: %-9s%-15s%-35s%-20s%-10s%n", "SIN", "Name", "Address", "Occupation", "DoB");
		System.out
				.println("-------------------------------------------------------------------------------------------");
		for (int i = 0; i < results.size(); i++) {
			System.out.printf("%-9d%-9s%-15s%-35s%-20s%-10s%n", i + 1, (results.get(i)).get(0), (results.get(i)).get(1),
					(results.get(i)).get(2), (results.get(i)).get(3), (results.get(i)).get(4));
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
		System.out.print("Enter your date of birth (YYYY-MM-DD): ");
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

	// Print menu options
	private static void reports() {
		System.out.println("=========MyBnB Report Statistics=========");
		System.out.println("0. Exit.");
		System.out.println("1. Total Number of Bookings in a Date Range per City");
		System.out.println("2. Total Number of Bookings in a Date Range in a City per ZIP code");
		System.out.println(
				"3. Total Number of Listings for a (Country/Country and City/Country and City and Postal Code)");
		System.out.println("4. Host Rankings by Number of Listings per Country (optionally City)");
		System.out.println("5. Potential Commerical Hosts by Country");
		System.out.println("6. Potential Commerical Hosts by City");
		System.out.println("7. Renter Rankings by Number of Bookings made within a time period");
		System.out.println("8. Renter Rankings by Number of Bookings made in a year per City (minimum of 2 bookings)");
		System.out.println("9. Hosts with the largest number of Cancellations in a year");
		System.out.println("10. Renters with the largest number of Cancellations in a year");
		System.out.println("11. Word cloud statistics for listing comments per message and listing");
		System.out.print("Choose one of the previous options [0-11]: ");
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
