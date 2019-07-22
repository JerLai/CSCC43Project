package main;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import operations.operations;

public class CSCC43DatabaseProject {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		CSCC43DatabaseProject pro = new CSCC43DatabaseProject();
		Connection connection = pro.createConnection();
		
		
		System.out.println("Welcome to the AirBnB database service! You can exit any time by typing 'exit'");
		Scanner keyboard = new Scanner(System.in);
		String input = "";
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
		
		
		/*
		 	Calendar cal = Calendar.getInstance();
		cal.set(2020, 0, 6);
		Date a = new Date(cal.getTime().getTime());
		cal = Calendar.getInstance();
		cal.set(2020, 0, 8);
		Date b = new Date(cal.getTime().getTime());
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		String date = formatter.format(a);
		
		
		cal.set(2019, 11, 31);
		Date s1 = new Date(cal.getTime().getTime());
		
		cal.set(2020, 0, 1);
		Date e1 = new Date(cal.getTime().getTime());
		
		cal.set(2020, 0, 6);
		Date s2 = new Date(cal.getTime().getTime());
		
		cal.set(2020, 0, 8);
		Date e2 = new Date(cal.getTime().getTime());
		
		//operations.bookListing(connection, "111", "1", a, b);
		
		//operations.createCalendar(connection, "1", s1.toString(), e1.toString(), "50");
		//operations.createCalendar(connection, "1", s2.toString(), e2.toString(), "50");
		
		//operations.renterRemoveListing(connection, "1", a, b, "111", "1234", 50);
		
		//operations.hostUpdatePrice(connection, "1", "1234", s2, e2, "50.42");
		
		//operations.addComment(connection, "55", "111", "asdf");
		
		//operations.addRating(connection, "111", "yeet", "5", "1");
		
		operations.removeCalendar(connection, "1", a, b);
		 * */
		
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
