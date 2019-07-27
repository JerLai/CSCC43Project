package main.menus;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class Menu {
	protected static ArrayList<String> credentials;
	protected static Connection connection;
	protected static Scanner keyboard;

	public Menu (ArrayList<String> credentials, Connection connection) {
		this.credentials = credentials;
		this.connection = connection;
		this.keyboard = new Scanner(System.in);
	}

	public abstract boolean execute();
	public abstract void printMenu();
	public void terminate() {
		this.credentials = null;
		this.connection = null;
		this.keyboard = null;
	};
}
