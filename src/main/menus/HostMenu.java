package main.menus;

import java.sql.Connection;
import java.util.ArrayList;

public class HostMenu extends Menu{

	public HostMenu(ArrayList<String> credentials, Connection connection) {
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
					case 1: // View listings
					case 2: // Add a listing
					case 3: // Remove a listing
					case 4: // cancel a reservation
					case 5: // Adjust price of listing
					case 6: // Rate a renter
					case 7: // Host toolkit: uses hostToolKit.java
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

	@Override
	public void printMenu() {
		System.out.println("=========Host MENU=========");
		System.out.println("0. Exit.");
		System.out.println("1. View your listings.");
		System.out.println("2. Add a listing.");
		System.out.println("3. Remove a listing.");
		System.out.println("4. Cancel a reservation.");
		System.out.println("5. Adjust the price of a listing.");
		System.out.println("6. Rate a renter.");
		System.out.println("7. Host Toolkit.");
		System.out.print("Choose one of the previous options [0-7]: ");
	}

}
