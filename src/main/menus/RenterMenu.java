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
								input = this.keyboard.nextLine();
								int resIndex = 0;

								try {
									resIndex = Integer.parseInt(input);
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
											double price = Double.parseDouble(reservations.get(resIndex - 1).get("price"));
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
						}
						exit = false;
						break;
					case 2: // Search for listings
						System.out.print("Do you want a simple search? (yes/no), or type anything else to exit: ");
						input = this.keyboard.next();
						if (input.equalsIgnoreCase("yes")) {
							// Do the simple search and display
						} else if (input.equalsIgnoreCase("no")) {
							// Do the simple search and display
						} else {
							break;
						}
					case 3: // View booking history
						try {
							while (!exit) {
								ArrayList<String> history = queries.getHistoryRenter(this.connection,
										this.credentials.get(0));
								this.printHistory(history);
								System.out.print("Enter the number of a reservation to focus on, or '0' to exit: ");
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
										System.out.print(
												"Enter 'comment' to comment on the host, 'rate' to rate the listing, or '0' to exit: ");
										input = this.keyboard.nextLine();
										if (input.equalsIgnoreCase("comment")) {
											try {
												System.out.println("Type your message, then hit 'Enter' to send.");
												input = this.keyboard.next();
												operations.addComment(this.connection, this.credentials.get(0),
														history.get((resIndex * 5) - 1), input);
											} catch (SQLException e) {

											}
										} else if (input.equalsIgnoreCase("rate")) {
											try {
												System.out.print("Enter a number between 0-5 to rate this listing: ");
												String rating = this.keyboard.next();
												System.out.println("Type your message, then hit 'Enter' to send.");
												input = this.keyboard.next();
												operations.addRating(this.connection, this.credentials.get(0), input,
														rating, history.get((resIndex * 5) + 3)); // offset needed to
																									// get the right
																									// attribute
											} catch (SQLException e) {
												System.err.println("No interaction exists between you and the host: Illegal Operation");
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

	private void printReservations(ArrayList<HashMap<String, String>> reservations) {
		System.out.printf("Bookings: %-10s%-15s%-35s%-20s%-10s%n", "listingID", "type", "address", "city", "country");
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
		System.out.println("=========Renter MENU=========");
		System.out.println("0. Exit.");
		System.out.println("1. View your reservations. (cancel)");
		System.out.println("2. Search for listings. (book and comment)");
		System.out.println("3. View your booking history. (comment on host or rate listing)");
		System.out.print("Choose one of the previous options [0-3]: ");
	}

}
