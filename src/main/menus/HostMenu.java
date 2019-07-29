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
									String listingChoice = this.keyboard.nextLine();
									try {
										resIndex = Integer.parseInt(listingChoice);
										// Error handling
										if (resIndex > listings.size() || resIndex < 0) {
											System.err.println("Invalid index value!");
										} else if (resIndex == 0) {
											exit = true;
										} else {
											// data values needed regardless of option
											String listing = listings.get(resIndex - 1).get("listingID");
											String hostSIN = listings.get(resIndex - 1).get("hostSIN"); // this.credentials.get(0);
											System.out.print(
													"Enter 'remove' to remove this listing, 'amenity' to add amenities, 'check' to view availabilities, or anything else to exit: ");
											input = this.keyboard.nextLine();
											if (input.equalsIgnoreCase("remove")) {

												try {
													operations.deleteListing(this.connection, listing);
													System.out.println("Listing has been removed.");
												} catch (SQLException e) {
													System.err.println(
															"Error has occurred while removing the listing, please try again");
												}
											} else if (input.equalsIgnoreCase("amenity")) {
												/*
												 * public static void addAmenity(Connection connection, String dining,
												 * String safety, String facilities, String guest, String logistics,
												 * String notIncluded, String bed, String outdoor, String basic, String
												 * listingID) throws SQLException {
												 */
												String dining = null, safety = null, facilities = null, guest = null,
														logistics = null, notIncluded = null, bed = null,
														outdoor = null, basic = null;
												System.out.print("Do you want to add a dining feature (yes/no): ");
												input = this.keyboard.nextLine();
												if (input.equalsIgnoreCase("yes")) {
													System.out.print("Enter your addition: ");
													dining = this.keyboard.nextLine();
												}
												System.out.print("Do you want to add a safety feature (yes/no): ");
												input = this.keyboard.nextLine();
												if (input.equalsIgnoreCase("yes")) {
													System.out.print("Enter your addition: ");
													safety = this.keyboard.nextLine();
												}
												System.out.print("Do you want to add a facility feature (yes/no): ");
												input = this.keyboard.nextLine();
												if (input.equalsIgnoreCase("yes")) {
													System.out.print("Enter your addition: ");
													facilities = this.keyboard.nextLine();
												}
												System.out
														.print("Do you want to add a guest entrance option (yes/no): ");
												input = this.keyboard.nextLine();
												if (input.equalsIgnoreCase("yes")) {
													System.out.print("Enter your addition: ");
													guest = this.keyboard.nextLine();
												}
												System.out.print("Do you want to add a logistics option (yes/no): ");
												input = this.keyboard.nextLine();
												if (input.equalsIgnoreCase("yes")) {
													System.out.print("Enter your addition: ");
													logistics = this.keyboard.nextLine();
												}
												System.out.print(
														"Do you want to add a feature that isn't included (yes/no): ");
												input = this.keyboard.nextLine();
												if (input.equalsIgnoreCase("yes")) {
													System.out.print("Enter your addition: ");
													notIncluded = this.keyboard.nextLine();
												}
												System.out.print("Do you want to add a bed option (yes/no): ");
												input = this.keyboard.nextLine();
												if (input.equalsIgnoreCase("yes")) {
													System.out.print("Enter your addition: ");
													bed = this.keyboard.nextLine();
												}
												System.out.print("Do you want to add an outdoor feature (yes/no): ");
												input = this.keyboard.nextLine();
												if (input.equalsIgnoreCase("yes")) {
													System.out.print("Enter your addition: ");
													outdoor = this.keyboard.nextLine();
												}
												System.out.print("Do you want to add a basic feature (yes/no): ");
												input = this.keyboard.nextLine();
												if (input.equalsIgnoreCase("yes")) {
													System.out.print("Enter your addition: ");
													basic = this.keyboard.nextLine();
												}
												try {
													operations.addAmenity(this.connection, dining, safety, facilities,
															guest, logistics, notIncluded, bed, outdoor, basic,
															listing);
													System.out.println("Amenities for this listing has been updated.");
												} catch (SQLException e) {
													System.err.println(
															"Error has occurred while adding amenities to the listing");
													e.printStackTrace();
												}
											} else if (input.equalsIgnoreCase("check")) {
												try {
													ArrayList<HashMap<String, String>> calendar = queries.showCalendar(
															this.connection, listing, this.credentials.get(0));
													this.printCalendarForListing(calendar);
													System.out.print(
															"Enter 'add' to add a new availability, or 'mod' to modify existing ones, anything else to exit: ");
													input = this.keyboard.nextLine();
													if (input.equalsIgnoreCase("add")) {
														System.out.println(
																"WARNING: The system will not merge overlapping or consecutive availabilities.");
														System.out.print(
																"Enter the start date of the availability (YYYY-MM-DD): ");
														String addStart = this.keyboard.nextLine();
														System.out.print(
																"Enter the end date of the availability (YYYY-MM-DD): ");
														String addEnd = this.keyboard.nextLine();
														System.out.print("Enter the price of the availability: ");
														String addPrice = this.keyboard.nextLine();
														try {
															operations.createCalendar(this.connection, listing,
																	addStart, addEnd, addPrice);
														} catch (SQLException e) {
															System.err.println(
																	"An unexpected error has occurred while adding an availability");
															System.err.println(e.getMessage());
															e.printStackTrace();
														}
													} else if (input.equalsIgnoreCase("mod")) {
														System.out.print(
																"Enter the index of the availability to interact with, or '0' to exit: ");
														String indexOpt = this.keyboard.nextLine();
														int availIndex = -1;
														try {
															availIndex = Integer.parseInt(indexOpt);
															if (availIndex > calendar.size() || availIndex < 0) {
																System.err.println("Invalid index!");
															} else if (availIndex == 0) {
																exit = true;
															} else {
																System.out.print(
																		"Enter 'remove' to remove the availability, 'update' to alter availability range, 'adjust' to adjust the price, anything else to exit: ");
																input = this.keyboard.nextLine();
																Date startDate = Date.valueOf(
																		calendar.get(availIndex - 1).get("startDate"));
																Date endDate = Date.valueOf(
																		calendar.get(availIndex - 1).get("endDate"));
																if (input.equalsIgnoreCase("remove")) {
																	try {
																		operations.removeCalendar(this.connection,
																				listing, startDate, endDate);
																		System.out.println(
																				"Availability has been removed.");
																	} catch (SQLException e) {
																		System.err.println(
																				"An unexpected error has occurred while removing an availability. Please try again.");
																	}
																} else if (input.equalsIgnoreCase("update")) {
																	System.out.print(
																			"Enter the new start date to update to (YYYY-MM-DD): ");
																	input = this.keyboard.nextLine();
																	Date newStart = Date.valueOf(input);
																	System.out.print(
																			"Enter the new end date to update to (YYYY-MM-DD): ");
																	input = this.keyboard.nextLine();
																	Date newEnd = Date.valueOf(input);
																	try {
																		operations.updateTime(this.connection, listing,
																				startDate, endDate, newStart, newEnd);
																	} catch (SQLException e) {
																		System.err.println(
																				"An unexpected error has occurred while updating the availability. Please try again.");
																		System.err.println(e.getMessage());
																		e.printStackTrace();
																	}
																} else if (input.equalsIgnoreCase("adjust")) {
																	System.out.print("Indicate the new price: ");
																	String price = this.keyboard.nextLine();
																	try {
																		operations.hostUpdatePrice(this.connection, listing, hostSIN, startDate,
																				endDate, price);
																		System.out.println("Listing price has been adjusted.");
																	} catch (SQLException e) {
																		System.err.println(
																				"Error has occurred while adjusting the price, please try again");
																		System.err.println(e.getMessage());
																		e.printStackTrace();
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
													System.err.println(
															"Error has occurred while retrieving the calendar, please try again");
													e.printStackTrace();
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
								ArrayList<HashMap<String, String>> history = queries.getHistoryHost(this.connection,
										this.credentials.get(0));
								this.printHistory(history);
								System.out.print("Enter the number of a record to focus on, or '0' to exit: ");
								String record = this.keyboard.nextLine();
								int resIndex = 0;
								try {
									resIndex = Integer.parseInt(record);
									if (resIndex > history.size() || resIndex < 0) {
										System.err.println("Invalid index value!");
									} else if (resIndex == 0) {
										exit = true;
									} else {
										System.out.print("Enter 'comment' to comment on the renter, or '0' to exit: ");
										input = this.keyboard.nextLine();
										if (input.equalsIgnoreCase("comment")) {
											try {
												System.out.println("Type your message, then hit 'Enter' to send.");
												input = this.keyboard.nextLine();
												operations.addComment(this.connection,
														history.get(resIndex - 1).get("renterSIN"),
														this.credentials.get(0), input);
											} catch (SQLException e) {
												System.err.println(
														"No interaction exists between you and the renter: Illegal Operation");
												System.err.println(e.getMessage());
												e.printStackTrace();
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
						try {
							while (!exit) {

								// Always repull the reservations incase they actually do remove one, so we
								// should
								// update the list
								ArrayList<HashMap<String, String>> reservations = queries
										.reservationsToHost(this.connection, this.credentials.get(0));
								this.printReservations(reservations);
								System.out.print("Enter the index of a reservation to interact with, or '0' to exit: ");
								String reserve = this.keyboard.nextLine();
								try {
									int bookedIndex = 0;
									bookedIndex = Integer.parseInt(reserve);
									if (bookedIndex < 0 || bookedIndex > reservations.size()) {
										System.err.println("Not a valid index!");
									} else if (bookedIndex == 0) {
										exit = true;
									} else {
										System.out.print(
												"Enter 'remove' to remove this listing, anything else to exit: ");
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
						} catch (SQLException e) {
							System.err.println(
									"An unexpected error has occurred when retrieving reservations. Returning to main menu.");
						}

						exit = false;
						break;
					case 4: // Host Toolkit
						do {
							this.toolKitOptions();
							String toolOp = this.keyboard.nextLine();
							int option = -1;
							try {
								option = Integer.parseInt(toolOp);
								String city = "";
								switch (option) {
								case 1:
									System.out.print("Which city would you like to search in: ");
									city = this.keyboard.nextLine();
									try {
										String amenity = hostToolKit.recommendAmenity(this.connection, city);
										System.out.printf(
												"The most lacking amenity in %s is: %s. Use this to help you make your listings stand out!%n",
												city, amenity);
									} catch (SQLException e) {
										System.err.println(
												"An unexpected error has occurred while finding the lacking amenity.");
										e.printStackTrace();
									}
									break;
								case 2:
									System.out.print("Which city would you like to search in: ");
									city = this.keyboard.nextLine();
									System.out.print(
											"Enter 'low' to find the lowest pricing, 'high' to find the highest: ");
									String type = this.keyboard.nextLine();
									try {
										boolean decision = type.equalsIgnoreCase("high");
										String price = hostToolKit.endPrice(this.connection, city, decision);
										if (!decision) {
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
										e.printStackTrace();
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
		} else

		{
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
		System.out.printf("Listings: %-10s%-15s%-11s%-11s%n", "listingID", "renterSIN", "endDate", "startDate");
		System.out
				.println("-------------------------------------------------------------------------------------------");
		for (int i = 0; i < reservations.size(); i++) {
			System.out.printf("%-10d%-10s%-15s%-11s%-11s%n", i + 1, (reservations.get(i)).get("listingID"),
					(reservations.get(i)).get("renterSIN"), (reservations.get(i)).get("endDate"),
					(reservations.get(i)).get("startDate"));
		}
		System.out
				.println("-------------------------------------------------------------------------------------------");
	}

	private void printHistory(ArrayList<HashMap<String, String>> history) {
		System.out.printf("History: %-10s%-15s%-35s%-20s%-10s%n", "hostSIN", "renterSIN", "listingID", "startDate",
				"endDate");
		System.out
				.println("-------------------------------------------------------------------------------------------");
		for (int i = 0; i < history.size(); i++) {
			System.out.printf("%-10d%-10s%-15s%-35s%-20s%-10s%n", i + 1, history.get(i).get("hostSIN"),
					history.get(i).get("renterSIN"), history.get(i).get("listingID"), history.get(i).get("startDate"),
					history.get(i).get("endDate"));
		}

		System.out
				.println("-------------------------------------------------------------------------------------------");
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

	@Override
	public void printMenu() {
		System.out.println("=========Host MENU=========");
		System.out.println("0. Exit.");
		System.out.println("1. View your listings. (Add and remove, adjust price)");
		System.out.println("2. View rental history (comment on renter)");
		System.out.println("3. View all current reservations. (Remove)");
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
