package main;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import operations.operations;
import operations.queries;
import operations.reports;

public class CSCC43DatabaseProject {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		// CSCC43DatabaseProject pro = new CSCC43DatabaseProject();
		// Connection connection = pro.createConnection();
		CommandLineHandler cmd = new CommandLineHandler();
		if (cmd.startSession()) {
			if (cmd.execute()) {
				cmd.endSession();
			}
		}

//		Calendar cal = Calendar.getInstance();
//		cal.set(2019, 0, 3);
//		Date a = new Date(cal.getTime().getTime());
//		cal = Calendar.getInstance();
//		cal.set(2021, 0, 4);
//		Date b = new Date(cal.getTime().getTime());
//		HashMap<String, Integer> res = reports.bookingsDateCity(connection, a, b);
//		res = reports.bookingsDatePostal(connection, "london", a, b);
//		res = reports.numListings(connection,2);
//		HashMap<String, ArrayList<String>> memes = reports.hostRanking(connection, 1);
//		for (String bb: memes.keySet()) {
//			//System.out.println(bb + "  "+ memes.get(bb));
//		}
//		
//		ArrayList<String> am = reports.commercialHosts(connection, "canada");
//		//System.out.println(am);
//		
//		HashMap<String, ArrayList<String>> mas = reports.rentersRankingCity(connection, a, b);
//		//System.out.println(mas);
//		cal = Calendar.getInstance();
//		cal.add(Calendar.YEAR, -1);
//		Date c = new Date(cal.getTime().getTime());
//		// Need to test with new db // res = reports.largestHost(connection, c);
//		
//		HashMap<Integer, HashMap<String, Integer>> red = reports.wordCloud(connection);
//		System.out.println(red);
		// System.out.println(res);

		/*
		 * 
		 * // USAGE OF QUERIES
		 * 
		 * Calendar cal = Calendar.getInstance(); cal.set(2020, 0, 3); Date a = new
		 * Date(cal.getTime().getTime()); cal = Calendar.getInstance(); cal.set(2020, 0,
		 * 4); Date b = new Date(cal.getTime().getTime()); //startFilter(Connection,
		 * boolean, String, boolean, double, double, double, boolean, String, boolean,
		 * Date, Date, boolean, double, double, boolean, String, int)
		 * ArrayList<HashMap<String, String>> data = queries.startFilter(connection,
		 * true, "MMM MMM", true, 100000.0, 69.0, 69.0, true, "london", true, a, b ,
		 * true, 0.0, 70.0, false, "", 1); System.out.println(data);
		 * 
		 * 
		 * 
		 * // USAGE OF THE OPERATIONS
		 * 
		 * Calendar cal = Calendar.getInstance(); cal.set(2020, 0, 6); Date a = new
		 * Date(cal.getTime().getTime()); cal = Calendar.getInstance(); cal.set(2020, 0,
		 * 8); Date b = new Date(cal.getTime().getTime()); String pattern =
		 * "yyyy-MM-dd"; SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		 * String date = formatter.format(a);
		 * 
		 * 
		 * cal.set(2019, 11, 31); Date s1 = new Date(cal.getTime().getTime());
		 * 
		 * cal.set(2020, 0, 1); Date e1 = new Date(cal.getTime().getTime());
		 * 
		 * cal.set(2020, 0, 6); Date s2 = new Date(cal.getTime().getTime());
		 * 
		 * cal.set(2020, 0, 8);
		 * 
		 * Date e2 = new Date(cal.getTime().getTime());
		 * 
		 * //operations.bookListing(connection, "111", "1", a, b);
		 * 
		 * //operations.createCalendar(connection, "1", s1.toString(), e1.toString(),
		 * "50"); //operations.createCalendar(connection, "1", s2.toString(),
		 * e2.toString(), "50");
		 * 
		 * //operations.renterRemoveListing(connection, "1", a, b, "111", "1234", 50);
		 * 
		 * //operations.hostUpdatePrice(connection, "1", "1234", s2, e2, "50.42");
		 * 
		 * //operations.addComment(connection, "55", "111", "asdf");
		 * 
		 * //operations.addRating(connection, "111", "yeet", "5", "1");
		 * 
		 * operations.removeCalendar(connection, "1", a, b);
		 */

//		if (connection != null) {
//			connection.close();
//		}
//		
	}

	Connection createConnection() throws SQLException {
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost/air_bnb", "root", "root");
			System.out.println("Database Connection Success");
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(CSCC43DatabaseProject.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SQLException ex) {
			Logger.getLogger(CSCC43DatabaseProject.class.getName()).log(Level.SEVERE, null, ex);
		}
		return con;
	}
}
