package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CSCC43DatabaseProject {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		CSCC43DatabaseProject pro = new CSCC43DatabaseProject();
		Connection connection = pro.createConnection();
		System.out.println("Welcome to the AirBnB database service! You can exit any time by typing 'exit'");
		Scanner keyboard = new Scanner(System.in);
		String input = keyboard.next();
		while (!input.equalsIgnoreCase("exit")) {
			input = keyboard.next();
			if (input.equals("login")) {
				System.out.println("Welcome!");
			} else {
				System.out.print("Please type in 'login' to select a user: ");
			}
		}
		System.out.println("Thank you for using our service!");
		keyboard.close();
		if (connection != null) {
			connection.close();
		}
	}

	Connection createConnection() throws SQLException{
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con  = DriverManager.getConnection("jdbc:mysql://localhost/air_bnb", "root", "root");
			System.out.println("Database Connection Success");
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(CSCC43DatabaseProject.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SQLException ex) {
			Logger.getLogger(CSCC43DatabaseProject.class.getName()).log(Level.SEVERE, null, ex);
		}
		return con;
	}
}
