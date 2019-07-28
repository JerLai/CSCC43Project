package main.menus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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

								} else if (input.equalsIgnoreCase("mod")) {
									int resIndex = 0;
									System.out.print("Enter the index of the listing to interact with or '0' to exit: ");
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
											Date start = Date
													.valueOf(listings.get(resIndex - 1).get("startDate"));
											Date end = Date.valueOf(listings.get(resIndex - 1).get("endDate"));
											String hostSIN = listings.get(resIndex - 1).get("hostSIN");
											System.out.print(
													"Enter 'remove' to remove this listing, 'adjust' to adjust the price, or anything else to exit: ");
											input = this.keyboard.nextLine();
											if (input.equalsIgnoreCase("remove")) {

												try {
													operations.renterRemoveListing(this.connection, listing, start, end,
															this.credentials.get(0), hostSIN, 30);
													System.out.println("Listing has been removed.");
												} catch (SQLException e) {
													System.err.println(
															"Error has occurred while removing the listing, please try again");
												}
											} else if (input.equalsIgnoreCase("adjust")) {
												System.out.print("Indicate the new price: ");
												String price = this.keyboard.nextLine();
												try {
													operations.hostUpdatePrice(this.connection, listing, hostSIN, start, end, price);
													System.out.println("Listing price has been adjusted.");
												} catch (SQLException e) {
													System.err.println(
															"Error has occurred while adjusting the price, please try again");
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
					case 3: // Host Toolkit
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

}