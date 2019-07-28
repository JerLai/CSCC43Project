package main.menus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import operations.hostToolKit;
import operations.operations;
import operations.queries;

public class HostMenu extends Menu {

	public HostMenu(ArrayList<String> credentials, Connection connection) {
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
					case 1: // View listings
						try {
							// Don't let them leave unless they explicitly type exit
							while (!exit) {
								// Always repull the listings incase they actually do remove one, so we should
								// update the list
								ArrayList<HashMap<String, String>> listings = queries.hostListings(this.connection,
										this.credentials.get(0));
								this.printListings(listings);
								System.out.print(
										"Enter 'add' to add a new listing, 'mod' to interact with your listings, or anything else to exit: ");
								input = this.keyboard.nextLine();
								// Was going to do a switch and case, but considering the mix of number and
								// string input, if statements
								if (input.equalsIgnoreCase("add")) {
									// stuff to add new listing
									System.out.print("Indicate the type of accomdation: ");
									String types = this.keyboard.nextLine();
									System.out.print("Indicate the longitude: ");
									String longitude = this.keyboard.nextLine();
									System.out.print("Indicate the latitude: ");
									String latitude = this.keyboard.nextLine();
									System.out.print("Indicate the city: ");
									String city = this.keyboard.nextLine();
									System.out.print("Indicate the country: ");
									String country = this.keyboard.nextLine();
									System.out.print("Indicate the address: ");
									String address = this.keyboard.nextLine();
									System.out.print("Indicate the postal code: ");
									String postalCode = this.keyboard.nextLine();
									try {
										operations.createListing(this.connection, this.credentials.get(0), types,
												longitude, latitude, city, country, address, postalCode);
									} catch (SQLException e) {
										System.err.println(
												"An unexpected error has occurred while adding a listing. Make sure the listing is unique");
									}
								} else if (input.equalsIgnoreCase("mod")) {
									int resIndex = 0;
									System.out
											.print("Enter the index of the listing to interact with or '0' to exit: ");
									input = this.keyboard.nextLine();
									try {
										resIndex = Integer.parseInt(input);
										// Error handling
										if (resIndex > listings.size() || resIndex < 0) {
											System.err.println("Invalid index value!");
										} else if (resIndex == 0) {
											exit = true;
										} else {
											// data values needed regardless of option
											String listing = listings.get(resIndex - 1).get("listingID");
											String hostSIN = listings.get(resIndex - 1).get("hostSIN"); // this.credentials.get(0);
											String renterSIN = listings.get(resIndex - 1).get("renterSIN");
											System.out.print(
													"Enter 'remove' to remove this listing, 'adjust' to adjust the price, 'check' to view availabilities, or anything else to exit: ");
											input = this.keyboard.nextLine();
											if (input.equalsIgnoreCase("remove")) {

												try {
													operations.deleteListing(this.connection, listing);
													System.out.println("Listing has been removed.");
												} catch (SQLException e) {
													System.err.println(
															"Error has occurred while removing the listing, please try again");
												}
											} else if (input.equalsIgnoreCase("adjust")) {
												System.out.print("Indicate the new price: ");
												String price = this.keyboard.nextLine();
												System.out.print(
														"Indicate the start date for the new price (YYY-MM-DD): ");
												input = this.keyboard.nextLine();
												Date start = Date.valueOf(input);
												System.out
														.print("Indicate the end date for the new price (YYY-MM-DD): ");
												input = this.keyboard.nextLine();
												Date end = Date.valueOf(input);
												try {
													operations.hostUpdatePrice(this.connection, listing, hostSIN, start,
															end, price);
													System.out.println("Listing price has been adjusted.");
												} catch (SQLException e) {
													System.err.println(
															"Error has occurred while adjusting the price, please try again");
												}

											} else if (input.equalsIgnoreCase("check")) {
												System.out.print(
														"Enter 'remove' to remove an availability, 'update' to alter availability range, anything else to exit: ");
												input = this.keyboard.nextLine();
												if (input.equalsIgnoreCase("remove")) {
													System.out.print(
															"Enter the start date of the availability to remove (YYY-MM-DD): ");
													input = this.keyboard.nextLine();
													Date startDate = Date.valueOf(input);
													System.out.print(
															"Enter the end date of the availability to remove (YYY-MM-DD): ");
													input = this.keyboard.nextLine();
													Date endDate = Date.valueOf(input);
													try {
														operations.removeCalendar(this.connection, listing, startDate,
																endDate);
													} catch (SQLException e) {
														System.err.println(
																"An unexpected error has occurred while removing an availability. Please try again.");
													}
												} else if (input.equalsIgnoreCase("update")) {

												} else {
													exit = true;
												}
											} else {
												exit = true;
											}
										}
									} catch (NumberFormatException e) {
										System.err.println("Invalid format value!");
									}
								} else {
									exit = true;
								}
							}

						} catch (SQLException e) {
							System.err.println("An unexpected error has occurred, returning to main menu");
						}
						exit = false;
						break;
					case 2: // View rental history
						try {
							while (!exit) {
								ArrayList<String> history = queries.getHistoryHost(this.connection,
										this.credentials.get(0));
								this.printHistory(history);
								System.out.print("Enter the number of a record to focus on, or '0' to exit: ");
								input = this.keyboard.nextLine();
								int resIndex = 0;
								try {
									resIndex = Integer.parseInt(input);
									int historySize = history.size() / 5;
									if (resIndex > historySize || resIndex < 0) {
										System.err.println("Invalid index value!");
									} else if (resIndex == 0) {
										exit = true;
									} else {
										System.out.print("Enter 'comment' to comment on the renter, or '0' to exit: ");
										input = this.keyboard.nextLine();
										if (input.equalsIgnoreCase("comment")) {
											try {
												System.out.println("Type your message, then hit 'Enter' to send.");
												input = this.keyboard.next();
												operations.addComment(this.connection, this.credentials.get(0),
														history.get((resIndex * 5) - 1), input);
											} catch (SQLException e) {
												System.err.println(
														"No interaction exists between you and the renter: Illegal Operation");
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
					case 3: // View all current reservations
						while (!exit) {
							// Always repull the reservations incase they actually do remove one, so we
							// should
							// update the list
							ArrayList<HashMap<String, String>> reservations = queries
									.reservationsToHost(this.connection, this.credentials.get(0));
							this.printReservations(reservations);
							System.out.print("Enter the index of a reservation to interact with, or '0' to exit: ");
							input = this.keyboard.nextLine();
							try {
								int bookedIndex = 0;
								bookedIndex = Integer.parseInt(input);
								if (bookedIndex < 0 || bookedIndex > reservations.size()) {
									System.err.println("Not a valid index!");
								} else if (bookedIndex == 0) {
									exit = true;
								} else {
									System.out.print("Enter 'remove' to remove this listing, anything else to exit: ");
									input = this.keyboard.nextLine();
									if (input.equalsIgnoreCase("remove")) {
										try {
											Date resStart = Date
													.valueOf(reservations.get(bookedIndex - 1).get("startDate"));
											Date resEnd = Date
													.valueOf(reservations.get(bookedIndex - 1).get("endDate"));
											String renter = reservations.get(bookedIndex - 1).get("renterSIN");
											operations.hostRemoveListing(this.connection,
													reservations.get(bookedIndex - 1).get("listingID"), resStart,
													resEnd, renter, this.credentials.get(0));
											System.out.println("Reservation has been removed.");
										} catch (SQLException e) {
											System.err.println(
													"An unexpected error has occurred when removing this reservation. Please try again");
										}

									}
								}
							} catch (NumberFormatException e) {
								System.err.println("Invalid Number Format!");
							}
						}
						exit = false;
						break;
					case 4: // Host Toolkit
						do {
							this.toolKitOptions();
							input = this.keyboard.nextLine();
							int option = -1;
							try {
								option = Integer.parseInt(input);
								String city = "";
								switch (option) {
								case 1:
									System.out.print("Which city would you like to search in: ");
									city = this.keyboard.nextLine();
									try {
										String amenity = hostToolKit.recommendAmenity(this.connection, city);
										System.out.printf(
												"The most lacking amenity in %s is: %s. Use this to help you make your listings stand out!",
												input, amenity);
									} catch (SQLException e) {
										System.err.println(
												"An unexpected error has occurred while finding the lacking amenity.");
									}
									break;
								case 2:
									System.out.print("Which city would you like to search in: ");
									city = this.keyboard.nextLine();
									System.out.print(
											"Enter 'low' to find the lowest pricing, 'high' to find the highest: ");
									String type = this.keyboard.nextLine();
									try {
										boolean decision = type.equalsIgnoreCase("low");
										String price = hostToolKit.endPrice(this.connection, city, decision);
										if (decision) {
											System.out.printf(
													"The lowest listing in %s is: %s. Use this to help you make your listings stand out!",
													city, price);
										} else {
											System.out.printf(
													"The highest listing in %s is: %s. Use this to help you make your listings stand out!",
													city, price);
										}

									} catch (SQLException e) {
										System.err.println(
												"An unexpected error has occurred while finding the highest/lowest price in the city.");
									}
									break;
								default:
									break;
								}
							} catch (NumberFormatException e) {
								input = "-1";
							}
						} while (input.compareTo("0") != 0);
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
			System.err.println("A problem has occurred while rendering the Host Menu. Please try again.");
			return false;
		}
	}

	private void printListings(ArrayList<HashMap<String, String>> listings) {
		System.out.printf("Listings: %-10s%-15s%-35s%-20s%-10s%n", "listingID", "type", "address", "city", "country");
		System.out
				.println("-------------------------------------------------------------------------------------------");
		for (int i = 0; i < listings.size(); i++) {
			System.out.printf("%-10d%-10s%-15s%-35s%-20s%-10s%n", i + 1, (listings.get(i)).get("listingID"),
					(listings.get(i)).get("type"), (listings.get(i)).get("address"), (listings.get(i)).get("city"),
					(listings.get(i)).get("country"));
		}
		System.out
				.println("-------------------------------------------------------------------------------------------");
	}

	private void printReservations(ArrayList<HashMap<String, String>> reservations) {
		System.out.printf("Listings: %-10s%-15s%-35s%-20s%-10s%n", "listingID", "type", "address", "city", "country");
		System.out
				.println("-------------------------------------------------------------------------------------------");
		for (int i = 0; i < reservations.size(); i++) {
			System.out.printf("%-10d%-10s%-15s%-35s%-20s%-10s%n", i + 1, (reservations.get(i)).get("listingID"),
					(reservations.get(i)).get("type"), (reservations.get(i)).get("address"),
					(reservations.get(i)).get("city"), (reservations.get(i)).get("country"));
		}
		System.out
				.println("-------------------------------------------------------------------------------------------");
	}

	private void printHistory(ArrayList<String> history) {
		System.out.printf("History: %-10s%-15s%-35s%-20s%-10s%n", "hostSIN", "renterSIN", "listingID", "startDate",
				"endDate");
		System.out
				.println("-------------------------------------------------------------------------------------------");
		int j = 0;
		for (int i = 1; i <= history.size() / 5; i++) {
			if (j < history.size()) {
				System.out.printf("%-10d%-10s%-15s%-35s%-20s%-10s%n", i + 1, history.get(j), history.get(j + 1),
						history.get(j + 2), history.get(j + 3), history.get(j + 4));
				j += 5;
			}
		}

		System.out
				.println("-------------------------------------------------------------------------------------------");
	}

	@Override
	public void printMenu() {
		System.out.println("=========Host MENU=========");
		System.out.println("0. Exit.");
		System.out.println("1. View your listings. (Add and remove, adjust price)");
		System.out.println("2. View all current reservations. (Remove)");
		System.out.println("3. View rental history (comment on renter)");
		System.out.println("4. Host Toolkit.");
		System.out.print("Choose one of the previous options [0-4]: ");
	}

	public void toolKitOptions() {
		System.out.println("=========Toolkit=========");
		System.out.println("Use these options to help make your listings stand out from others!");
		System.out.println("0. Exit.");
		System.out.println("1. Find what amenities are lacking in a city of your choosing!");
		System.out.println("2. Find the highest/lowest price for a listing in a city of your choosing!");
		System.out.print("Choose one of the previous options [0-2]: ");
	}
}
