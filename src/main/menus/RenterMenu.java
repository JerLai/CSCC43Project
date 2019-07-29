package main.menus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import operations.operations;
import operations.queries;

public class RenterMenu extends Menu {

	public RenterMenu(ArrayList<String> credentials, Connection connection) {
		super(credentials, connection);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean execute() {
		if (this.credentials != null && this.connection != null && this.keyboard != null) {
			String input = "";
			int choice = -1;
			boolean exit = false;
			do {
				this.printMenu();
				input = this.keyboard.nextLine();
				try {
					choice = Integer.parseInt(input);

					switch (choice) {
					case 1: // View reservations
						try {
							// Don't let them leave unless they explicitly type exit
							while (!exit) {
								// Always repull the bookings incase they actually do cancel one, so we should
								// update the list
								ArrayList<HashMap<String, String>> reservations = queries
										.renterBookings(this.connection, this.credentials.get(0));
								this.printReservations(reservations);

								System.out.print("Enter the number of a reservation to focus on, or '0' to exit: ");
								String num = this.keyboard.nextLine();
								int resIndex = 0;

								try {
									resIndex = Integer.parseInt(num);
									// Error handling
									if (resIndex > reservations.size() || resIndex < 0) {
										System.err.println("Invalid index value!");
									} else if (resIndex == 0) {
										exit = true;
									} else {
										//
										System.out.print(
												"Enter 'cancel' to cancel this reservation, or anything else to exit: ");
										input = this.keyboard.nextLine();
										if (input.equalsIgnoreCase("cancel")) {
											String listing = reservations.get(resIndex - 1).get("listingID");
											Date start = Date.valueOf(reservations.get(resIndex - 1).get("startDate"));
											Date end = Date.valueOf(reservations.get(resIndex - 1).get("endDate"));
											String hostSIN = reservations.get(resIndex - 1).get("hostSIN");
											double price = Double
													.parseDouble(reservations.get(resIndex - 1).get("price"));
											try {
												operations.renterRemoveListing(this.connection, listing, start, end,
														this.credentials.get(0), hostSIN, price);
											} catch (SQLException e) {
												System.err.println(
														"Error has occurred while removing reservation, please try again");
											}
										} else {
											exit = true;
										}
									}
								} catch (NumberFormatException e) {
									System.err.println("Invalid format value!");
								}
							}

						} catch (SQLException e) {
							System.err.println("An unexpected error has occurred, returning to main menu");
							System.err.println(e.getMessage());
							e.printStackTrace();
						}
						exit = false;
						break;
					case 2: // Search for listings
						boolean postal = false, distance = false, address = false, temporal = false, price = false,
								amenities = false;
						String postalValue = null, searchAddress = null, searchAmenities = null;
						double km = 0, lat = 0, lng = 0, priceL = 0, priceH = 0;
						Date start = null, end = null;
						int rankPrice = 1;
						System.out.print("Do you want a simple search? (yes/no), or type 'exit' to exit: ");
						input = this.keyboard.next();
						ArrayList<HashMap<String, String>> results = null;
						/*
						 * public static ArrayList<HashMap<String, String>> startFilter(Connection
						 * connection, boolean postal, String postalValue, boolean distance, double km,
						 * double lat, double lng, boolean address, String searchAddress, boolean
						 * temporal, Date start, Date end, boolean price, double priceL, double priceH,
						 * boolean amenities, String searchAmenities, int rankPrice)
						 */
						if (input.equalsIgnoreCase("no") || input.equalsIgnoreCase("yes")) {
							if (input.equalsIgnoreCase("no")) {
								System.out.print("Do you want to search for a specific postal code (yes/no): ");
								input = this.keyboard.nextLine();
								postal = input.equalsIgnoreCase("yes");
								if (postal) {
									System.out.print("Enter the 6 digit postal code you desire: ");
									postalValue = this.keyboard.nextLine();
								}
								System.out.print("Do you want to limit the search radius (yes/no): ");
								input = this.keyboard.nextLine();
								distance = input.equalsIgnoreCase("yes");
								if (distance) {
									System.out.print("Enter the maximum distance in km you desire: ");
									km = this.keyboard.nextDouble();
									System.out.print("Enter the latitude you desire: ");
									lat = this.keyboard.nextDouble();
									System.out.print("Enter the longitude you desire: ");
									lng = this.keyboard.nextDouble();
								}
								System.out.print("Do you want to search for a specific address (yes/no): ");
								input = this.keyboard.nextLine();
								address = input.equalsIgnoreCase("yes");
								if (address) {
									System.out.print("Enter the address you desire: ");
									searchAddress = this.keyboard.nextLine();
								}
								System.out.print("Do you want to specify the date range for availability (yes/no): ");
								input = this.keyboard.nextLine();
								temporal = input.equalsIgnoreCase("yes");
								if (temporal) {
									System.out.print("Enter your intended start date (YYYY-MM-DD): ");
									start = Date.valueOf(this.keyboard.nextLine());
									System.out.print("Enter your intended end date (YYYY-MM-DD): ");
									end = Date.valueOf(this.keyboard.nextLine());
								}
								System.out.print("Do you want to limit the price range (yes/no): ");
								input = this.keyboard.nextLine();
								price = input.equalsIgnoreCase("yes");
								if (price) {
									System.out.print("Enter the lower price limit: ");
									priceL = this.keyboard.nextDouble();
									System.out.print("Enter the upper price limit: ");
									priceH = this.keyboard.nextDouble();
								}
								System.out.print("Do you want to have a specific amenity (limit 1) (yes/no): ");
								input = this.keyboard.nextLine();
								amenities = input.equalsIgnoreCase("yes");
								if (amenities) {
									System.out.print("Enter the amenity you desire: ");
									searchAmenities = this.keyboard.nextLine();
								}
								System.out.print(
										"Do you want to sort your searches in 'ascending', 'descending' order, type anything else for no preference: ");
								input = this.keyboard.nextLine();
								if (input.equalsIgnoreCase("ascending")) {
									rankPrice = 1;
								} else if (input.equalsIgnoreCase("descending")) {
									rankPrice = 2;
								}
							}
							try {
								boolean doneSearch = false;
								while (!doneSearch) {
									results = queries.startFilter(this.connection, postal, postalValue, distance, km,
											lat, lng, address, searchAddress, temporal, start, end, price, priceL,
											priceH, amenities, searchAmenities, rankPrice);
									this.printQueries(results);
									System.out.print("Enter the number of a listing to focus on, or '0' to exit: ");
									String searchChoice = this.keyboard.nextLine();
									try {
										int selection = Integer.parseInt(searchChoice);
										System.out.println(selection);
										if (selection > results.size() || selection < 0) {
											System.err.println("Invalid index!");
										} else if (selection == 0) {
											doneSearch = true;
										} else {
											System.out.print(
													"Enter 'book' to book this listing, 'comment' to comment on the host, 'rate' to rate the listing, anything else to exit: ");
											input = this.keyboard.nextLine();
											if (input.equalsIgnoreCase("book")) {
												ArrayList<HashMap<String, String>> calendar = queries.showCalendar(
														this.connection, results.get(selection - 1).get("listingID"),
														results.get(selection - 1).get("hostSIN"));
												this.printCalendarForListing(calendar);
												System.out.print(
														"Enter the start date for the particular availability you want (YYYY-MM-DD): ");
												Date desiredS = Date.valueOf(this.keyboard.nextLine());
												System.out.print(
														"Enter the end date for the particular availability you want (YYYY-MM-DD): ");
												Date desiredE = Date.valueOf(this.keyboard.nextLine());
												operations.bookListing(this.connection, this.credentials.get(0),
														results.get(selection - 1).get("listingID"), desiredS,
														desiredE);
												System.out.println("Booking has been made.");
											} else if (input.equalsIgnoreCase("comment")) {
												System.out.println(
														"Type the comment you want to make below, then hit enter.");
												String message = this.keyboard.next();
												operations.addComment(this.connection,
														results.get(selection - 1).get("hostSIN"),
														this.credentials.get(0), message);
											} else if (input.equalsIgnoreCase("rate")) {
												System.out.println("Enter a rating from 0-5: ");
												double rating = this.keyboard.nextDouble();
												System.out.println(
														"Type the comment you want to make below, then hit enter.");
												String comment = this.keyboard.next();
												operations.addRating(this.connection, this.credentials.get(0), comment,
														Double.toString(rating),
														results.get(selection - 1).get("listingID"));
											}
										}
									} catch (NumberFormatException e) {
										System.err.println("Invalid number format!");
									}
								}
							} catch (SQLException e) {
								System.err.println(
										"An unexpected error has occurred while processing your request. Please try again");
								e.getMessage();
								e.printStackTrace();
							}
						} else if (input.equalsIgnoreCase("exit")) {
							break;
						}

						break;
					case 3: // View booking history
						try {
							while (!exit) {
								ArrayList<HashMap<String, String>> history = queries.getHistoryRenter(this.connection,
										this.credentials.get(0));
								this.printHistory(history);
								System.out.print("Enter the number of a reservation to focus on, or '0' to exit: ");
								String num2 = this.keyboard.nextLine();
								int resIndex = 0;
								try {
									resIndex = Integer.parseInt(num2);
									if (resIndex > history.size() || resIndex < 0) {
										System.err.println("Invalid index value!");
									} else if (resIndex == 0) {
										exit = true;
									} else {
										System.out.print(
												"Enter 'comment' to comment on the host, 'rate' to rate the listing, or '0' to exit: ");
										input = this.keyboard.nextLine();
										if (input.equalsIgnoreCase("comment")) {
											try {
												System.out.println("Type your message, then hit 'Enter' to send.");
												input = this.keyboard.next();
												operations.addComment(this.connection,
														history.get(resIndex - 1).get("hostSIN"),
														this.credentials.get(0), input);
											} catch (SQLException e) {

											}
										} else if (input.equalsIgnoreCase("rate")) {
											try {
												System.out.print("Enter a number between 0-5 to rate this listing: ");
												String rating = this.keyboard.next();
												System.out.println("Type your message, then hit 'Enter' to send.");
												input = this.keyboard.next();
												operations.addRating(this.connection, this.credentials.get(0), input,
														rating, history.get(resIndex - 1).get("listingID")); // offset
																												// needed
																												// to
												// get the right
												// attribute
											} catch (SQLException e) {
												System.err.println(
														"No interaction exists between you and the host: Illegal Operation");
											}
										} else if (input.equalsIgnoreCase("0")) {
											exit = true;
										}
									}
								} catch (NumberFormatException e) {
									System.err.println("Invalid format value!");
								}
							}

						} catch (SQLException e) {
							System.err.println("An unexpected error has occurred, returning to main menu");
						}
						exit = false;
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
			System.err.println("A problem has occurred while rendering the Renter Menu. Please try again.");
			return false;
		}
	}

	private void printCalendarForListing(ArrayList<HashMap<String, String>> calendar) {
		System.out.printf("Calendar: %-10s%-10s%-11s%-11s%-5s%n", "listingID", "hostSIN", "startDate", "endDate",
				"price");
		System.out
				.println("-------------------------------------------------------------------------------------------");
		for (int i = 0; i < calendar.size(); i++) {
			System.out.printf("%-10d%-10s%-10s%-11s%-11s%-5s%n", i + 1, (calendar.get(i)).get("listingID"),
					(calendar.get(i)).get("hostSIN"), (calendar.get(i)).get("startDate"),
					(calendar.get(i)).get("endDate"), (calendar.get(i)).get("price"));
		}
		System.out
				.println("-------------------------------------------------------------------------------------------");
	}

	private void printReservations(ArrayList<HashMap<String, String>> reservations) {
		System.out.printf("Bookings: %-10s%-40s%-35s%-20s%-10s%-10s%-10s%n", "listingID", "type", "address", "city",
				"country", "startDate", "endDate");
		System.out
				.println("-------------------------------------------------------------------------------------------");
		for (int i = 0; i < reservations.size(); i++) {
			System.out.printf("%-10d%-10s%-40s%-35s%-20s%-10s%-10s%-10s%n", i + 1,
					(reservations.get(i)).get("listingID"), (reservations.get(i)).get("type"),
					(reservations.get(i)).get("address"), (reservations.get(i)).get("city"),
					(reservations.get(i)).get("country"), (reservations.get(i)).get("startDate"),
					(reservations.get(i)).get("endDate"));
		}
		System.out
				.println("-------------------------------------------------------------------------------------------");
	}

	private void printHistory(ArrayList<HashMap<String, String>> history) {
		System.out.printf("History: %-10s%-15s%-35s%-20s%-10s%n", "hostSIN", "renterSIN", "listingID", "startDate",
				"endDate");
		System.out
				.println("-------------------------------------------------------------------------------------------");
		for (int i = 0; i <= history.size(); i++) {
			System.out.printf("%-10d%-10s%-15s%-35s%-20s%-10s%n", i, history.get(i).get("hostSIN"),
					history.get(i).get("renterSIN"), history.get(i).get("listingID"), history.get(i).get("startDate"),
					history.get(i).get("endDate"));

		}

		System.out
				.println("-------------------------------------------------------------------------------------------");
	}

	private void printQueries(ArrayList<HashMap<String, String>> results) {
		System.out.printf(
				"Results: %-10s%-40s%-10s%-5s%-5s%-25s%-25s%-35s%-10s%-40s%-40s%-40s%-40s%-40s%-40s%-40s%-40s%-40s%n",
				"listingID", "hostSIN", "type", "longitude", "latitude", "city", "country", "address", "postalCode",
				"dining", "safetyFeatures", "facilities", "guestAccess", "logistics", "notIncluded", "bedAndBath",
				"outdoor", "basic");
		System.out.println(
				"--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		for (int i = 0; i < results.size(); i++) {
			System.out.printf(
					"%-10d%-10s%-40s%-10s%-5s%-5s%-25s%-25s%-35s%-10s%-40s%-40s%-40s%-40s%-40s%-40s%-40s%-40s%-40s%n",
					i + 1, (results.get(i)).get("listingID"), (results.get(i)).get("hostSIN"),
					(results.get(i)).get("type"), (results.get(i)).get("longitude"), (results.get(i)).get("latitude"),
					(results.get(i)).get("city"), (results.get(i)).get("country"), (results.get(i)).get("address"),
					(results.get(i)).get("postalCode"), (results.get(i)).get("dining"),
					(results.get(i)).get("safetyFeatures"), (results.get(i)).get("facilities"),
					(results.get(i)).get("guessAccess"), (results.get(i)).get("logistics"),
					(results.get(i)).get("notIncluded"), (results.get(i)).get("bedAndBath"),
					(results.get(i)).get("outdoor"), (results.get(i)).get("basic"));
		}
		System.out.println(
				"--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
	}

	@Override
	public void printMenu() {
		System.out.println("=========Renter MENU=========");
		System.out.println("0. Exit.");
		System.out.println("1. View your reservations. (cancel)");
		System.out.println("2. Search for listings. (book and comment)");
		System.out.println("3. View your booking history. (comment on host or rate listing)");
		System.out.print("Choose one of the previous options [0-3]: ");
	}

}
