package operations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import main.dbsetup.DBAPI;

public class hostToolKit {

	static String query;
	
	public static String avergagePrice(Connection connection, String city) throws SQLException {
		query = "SELECT AVG(price) AS PRICE from listing, calendar WHERE listing.listingID = calendar.listingID AND city='"+city+"'";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		double price = 0;
		while (data.next()) {
			price = data.getDouble("PRICE");
		}
		
		return Double.toString(price);
	}
	
	public static String endPrice(Connection connection, String city, boolean type) throws SQLException {
		query = "SELECT price from listing, calendar WHERE listing.listingID = calendar.listingID AND city='"+city+"' ORDER BY price";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		double price = 0;
		boolean set = false;
		double low = 0;
		double high = 0;
		while (data.next()) {
			price = data.getDouble("PRICE");
			if (!set)
				low=price;
			high = price;
		}
		
		if (type) //low
			return Double.toString(low);
		else
			return Double.toString(high);
	}
	
	public static String recommendAmenity(Connection connection, String city) throws SQLException {
		query = "SELECT * FROM listing,amenities WHERE listing.listingID=amenities.listingID";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		
		ArrayList<String> dining = new ArrayList<String>();
		ArrayList<String> safety = new ArrayList<String>();
		ArrayList<String> facilities = new ArrayList<String>();
		ArrayList<String> guessAccess = new ArrayList<String>();
		ArrayList<String> logistics = new ArrayList<String>();
		ArrayList<String> bedBath = new ArrayList<String>();
		ArrayList<String> outdoor = new ArrayList<String>();
		ArrayList<String> basic = new ArrayList<String>();
		
		while (data.next()) {
			String s = data.getString("safetyFeatures");
			if (!s.equals(null) || !s.equals(""))
				safety.add(s);
			s = data.getString("dining");
			if (!s.equals(null) || !s.equals(""))
				dining.add(s);
			s = data.getString("facilities");
			if (!s.equals(null) || !s.equals(""))
				facilities.add(s);
			s = data.getString("guestAccess");
			if (!s.equals(null) || !s.equals(""))
				guessAccess.add(s);
			s = data.getString("logistics");
			if (!s.equals(null) || !s.equals(""))
				logistics.add(s);
			s = data.getString("bedAndBath");
			if (!s.equals(null) || !s.equals(""))
				bedBath.add(s);
			s = data.getString("outdoor");
			if (!s.equals(null) || !s.equals(""))
				outdoor.add(s);
			s = data.getString("basic");
			if (!s.equals(null) || !s.equals(""))
				basic.add(s);
		}
		
		ArrayList[] res = {dining, safety, facilities, guessAccess, logistics, bedBath, outdoor, basic};
		String [] catagories = {"Dining", "Safety Features", "Facilities", "Guest Access", "Logistics", "Bed and Bath", "Outdoors", "Basic"}; 
		int lowest = 0;
		int location = -1;
		for (int i = 0; i< res.length - 1; i++) {
			if (lowest < res[i].size()) {
				lowest = res[i].size();
				location = i;
			}
		}
		if (location == -1) {
			location = 7; // default to basic
		}
		String result = catagories[location];
		
		return result;
	}
	
	
}
