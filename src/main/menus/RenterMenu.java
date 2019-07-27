package main.menus;

import java.sql.Connection;
import java.util.ArrayList;

public class RenterMenu extends Menu{

	public RenterMenu(ArrayList<String> credentials, Connection connection) {
		super(credentials, connection);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean execute() {
		if (this.credentials != null && this.connection != null && this.keyboard != null) {
			String input = "";
			int choice = -1;

			do {
				try {
					choice = Integer.parseInt(input);

					switch(choice) {
					case 1: //View reservations
					case 2: // Search for listings
					case 3: // Make a reservation
					case 4: // Cancel a reservation
					case 5: // Rate a listing
					case 6: // Rate a host
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

	@Override
	public void printMenu() {
		System.out.println("=========Renter MENU=========");
		System.out.println("0. Exit.");
		System.out.println("1. View your reservations.");
		System.out.println("2. Search for listings (and make a reservation).");
		System.out.println("3. Make a reservation.");
		System.out.println("4. Cancel a reservation.");
		System.out.println("5. Rate a listing.");
		System.out.println("6. Rate a host.");
		System.out.print("Choose one of the previous options [0-6]: ");
	}

}
