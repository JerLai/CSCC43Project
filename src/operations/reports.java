package operations;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import main.dbsetup.DBAPI;

public class reports {

	private static String query;

	
	/**
	 * Query to provide total number of bookings in a specific date range by city
	 * @param connection
	 * @param start
	 * @param end
	 * @return
	 * @throws SQLException
	 */
	public static HashMap<String, Integer> bookingsDateCity(Connection connection, Date start, Date end) throws SQLException{
		query = "SELECT city, COUNT(*) as COUNT FROM listing, calendar WHERE listing.listingID = calendar.listingID AND startDate>='"+start.toString() + "' AND endDate<='"+end.toString()+"' GROUP BY city;";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		int res = 0;
		String c = "";
		HashMap<String, Integer> entry = new HashMap<String, Integer>();
		while (data.next()) {
			res = data.getInt("COUNT");
			c = data.getString("city");
			entry.put(c, res);	
		}
		return entry;
	}
	/**
	 * Query to provide total number of bookings in a specific date range by ZIP code within a city
	 * @param connection
	 * @param city
	 * @param start
	 * @param end
	 * @return
	 * @throws SQLException
	 */
	public static HashMap<String, Integer> bookingsDatePostal(Connection connection, String city, Date start, Date end) throws SQLException{
		query = "SELECT postalCode, COUNT(*) as COUNT FROM listing, calendar WHERE listing.listingID = calendar.listingID AND city LIKE '"+city+"' AND startDate>='"+start.toString() + "' AND endDate<='"+end.toString()+"' GROUP BY postalCode;";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		int res = 0;
		String c = "";
		HashMap<String, Integer> entry = new HashMap<String, Integer>();
		while (data.next()) {
			res = data.getInt("COUNT");
			c = data.getString("postalCode");
			entry.put(c, res);	
		}
		return entry;
	}

	/**
	 * Query to provide the total number of listings for a country, or country and city, or country and city and postal code
	 * @param connection
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public static HashMap<String, Integer> numListings (Connection connection, int type) throws SQLException{
		HashMap<String, Integer> data = new HashMap<String, Integer>();
		ResultSet result;
		if (type == 0) { // per country
			query = "SELECT country, COUNT(*) as COUNT FROM listing GROUP BY country";
			result = DBAPI.getDataByQuery(connection, query);
			while (result.next()) {
				String c = result.getString("country");
				int r = result.getInt("COUNT");
				data.put(c, r);
			}
		}
		else if (type == 1) {
			query = "SELECT country, city, COUNT(*) as COUNT FROM listing GROUP BY country, city";
			result = DBAPI.getDataByQuery(connection, query);
			while (result.next()) {
				String c = result.getString("country") + ", " + result.getString("city");
				int r = result.getInt("COUNT");
				data.put(c, r);
			}
		}
		else {
			query = "SELECT country, city, postalCode, COUNT(*) as COUNT FROM listing GROUP BY country, city, postalCode";
			result = DBAPI.getDataByQuery(connection, query);
			while (result.next()) {
				String c = result.getString("country") + ", " + result.getString("city") + ", " + result.getString("postalCode");
				int r = result.getInt("COUNT");
				data.put(c, r);
			}
		}
		
		return data;
		
	}

	/**
	 * Query to rank hosts by total number of listings they have per country, and optionally by city
	 * @param connection
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public static  HashMap<String, ArrayList<String>> hostRanking(Connection connection, int type) throws SQLException{
		ArrayList<HashMap<String, Integer>> data = new ArrayList<HashMap<String, Integer>>();
		HashMap<String, HashMap<String, Integer>> res = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, Integer> entry = new HashMap<String, Integer>();
		
		ArrayList<String[]> test = new ArrayList<String[]>();
		String[] dd = new String[3]; 
		if (type == 0) {
			query = "SELECT name, country, COUNT(*) as COUNT FROM listing, users, host WHERE host.SIN = users.SIN AND listing.hostSIN = host.SIN GROUP BY country,name ORDER BY COUNT DESC";
		}
		else if  (type == 1) {
			query = "SELECT name, city, COUNT(*) as COUNT FROM listing, users, host WHERE host.SIN = users.SIN AND listing.hostSIN = host.SIN GROUP BY city,name ORDER BY COUNT DESC";
		}
		ResultSet result = DBAPI.getDataByQuery(connection, query);
		while (result.next()) {
			String c = "";
			if (type == 0) {
				c = result.getString("country");
			}
			else{
				c = result.getString("city");
			}
			String a = result.getString("name");
			int r = result.getInt("COUNT");
			entry.put(a, r);
			dd = new String[3];
			dd[0] = c;
			dd[1] = a;
			dd[2] = Integer.toString(r);
			test.add(dd);
		}
		HashMap<String, ArrayList<String>> ranked = new  HashMap<String, ArrayList<String>>();
		ranked = populateCountry(test);
		return ranked;
	}

	private static HashMap<String, ArrayList<String>> populateCountry(ArrayList<String[]> data){
		HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
		
		for (String[] person: data) {
			//System.out.println(person[0]);
			String country = person[0];
			
			if (result.containsKey(country)) {
				ArrayList<String> list = result.get(country);
				list.add(person[1]);
			}
			else {
				ArrayList<String> entry = new ArrayList<String>();
				entry.add(person[1]);
				result.put(country, entry);
			}
		}
		
		return result;
	}

	/**
	 * Query to identify hosts that have more than 10% of the total number of listings in a country
	 * @param connection
	 * @param country
	 * @return
	 * @throws SQLException
	 */
	public static ArrayList<String> commercialHosts(Connection connection, String country) throws SQLException{
		query = "SELECT users.name as NAME FROM host H, users WHERE (H.SIN=users.SIN) AND (SELECT COUNT(*) *0.1 FROM listing WHERE country='"+country+"')  < (SELECT COUNT(*) FROM listing WHERE country='"+country+"' AND listing.hostSIN=H.SIN)";
		//query = "(SELECT COUNT(*)*1.0 *0.1 as COUNT FROM listing WHERE country='"+country+"')";
		// query = "SELECT U.name as NAME FROM host H, users U WHERE (H.SIN=U.SIN) AND 0 < (SELECT COUNT(*) FROM listing WHERE country='"+country+"' AND listing.hostSIN=U.SIN)"; 
		//query = "SELECT users.name as NAME FROM host H, users WHERE (H.SIN=users.SIN) AND";
		
		System.out.println(query);
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		ArrayList<String> row = new ArrayList<String>();
		while (data.next()) {
			//System.out.println(data.getString("NAME"));
			//System.out.println(data.getString("NAME"));
			row.add(data.getString("NAME"));
			
		}
		
		return row;
	}

	/**
	 * Query to identify any hosts that have more than 10% of the total number of listings in a city
	 * @param connection
	 * @param city
	 * @return
	 * @throws SQLException
	 */
	public static ArrayList<String> commercialHostsCity(Connection connection, String city) throws SQLException{
		query = "SELECT users.name as NAME FROM host H, users WHERE (H.SIN=users.SIN) AND (SELECT COUNT(*) *0.1 FROM listing WHERE city='"+city+"')  < (SELECT COUNT(*) FROM listing WHERE city='"+city+"' AND listing.hostSIN=H.SIN)";
		//query = "(SELECT COUNT(*)*1.0 *0.1 as COUNT FROM listing WHERE country='"+country+"')";
		// query = "SELECT U.name as NAME FROM host H, users U WHERE (H.SIN=U.SIN) AND 0 < (SELECT COUNT(*) FROM listing WHERE country='"+country+"' AND listing.hostSIN=U.SIN)"; 
		//query = "SELECT users.name as NAME FROM host H, users WHERE (H.SIN=users.SIN) AND";
		
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		ArrayList<String> row = new ArrayList<String>();
		while (data.next()) {
			//System.out.println(data.getString("NAME"));
			//System.out.println(data.getString("NAME"));
			row.add(data.getString("NAME"));
			
		}
		
		return row;
	}

	/**
	 * Query to rank the renters by the number of bookings they have made in a time frame
	 * @param connection
	 * @param start
	 * @param end
	 * @return
	 * @throws SQLException
	 */
	public static ArrayList<String> rentersRanking(Connection connection, Date start, Date end) throws SQLException{
		ArrayList<String> result = new ArrayList<String>();
		// query = "SELECT name FROM user, history, listing WHERE user.SIN=history.SIN AND listing.listingID=history.listingID ORDER BY ";
		query = "SELECT name FROM users U, history WHERE U.SIN=history.renterSIN AND startDate>='"+start+"' AND endDate<='"+end+"'ORDER BY (SELECT COUNT(*) FROM history WHERE history.renterSIN=U.SIN) DESC";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		
		while(data.next()) {
			String name = data.getString("name");
			if (!result.contains(name))
				result.add(name);
		}
		
		return result;
	}

	/**
	 * Query to rank the renters by the number of bookings they have made in time frame per city
	 * @param connection
	 * @param start
	 * @param end
	 * @return
	 * @throws SQLException
	 */
	public static HashMap<String, ArrayList<String>> rentersRankingCity(Connection connection, Date start, Date end) throws SQLException{
		HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
		// query = "SELECT name FROM user, history, listing WHERE user.SIN=history.SIN AND listing.listingID=history.listingID ORDER BY ";
		query = "SELECT name, city FROM users U, history, listing WHERE listing.listingID=history.listingID AND U.SIN=history.renterSIN AND startDate>='"+start+"' AND endDate<='"+end+"' GROUP BY U.SIN, city ORDER BY (SELECT COUNT(*) FROM history WHERE history.renterSIN=U.SIN) DESC";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		
		ArrayList<String> entry = new ArrayList<String>();
		while(data.next()) {
			String name = data.getString("name");
			String city = data.getString("city");
			
			if (result.containsKey(city)) {
				entry = result.get(city);
				entry.add(name);
				result.replace(city, entry);
			}
			else {
				entry = new ArrayList<String>();
				entry.add(name);
				result.put(city, entry);
			}
		}
		
		return result;
	}
	
	/**
	 * Query to find the hosts with the largest number of cancellations within a year
	 * @param connection
	 * @param start
	 * @return
	 * @throws SQLException
	 */
	public static HashMap<String, Integer> largestHost(Connection connection, Date start) throws SQLException{
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		query = "SELECT U.name, COUNT(*) AS COUNT FROM users U,cancelled WHERE hostCancel='1' AND U.SIN=cancelled.hostSIN AND (SELECT COUNT(*) FROM users,cancelled WHERE startDate > '"+start+"' AND U.SIN=cancelled.hostSIN)>all(SELECT COUNT(*) FROM users,cancelled WHERE startDate > '"+start+"' AND users.SIN=cancelled.hostSIN GROUP BY U.name)";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		
		while (data.next()) {
			result.put(data.getString("name"), data.getInt("COUNT"));
		}
		
		return result;
	}
	/**
	 * Query to find the renters with the largest number of cancellations within a year
	 * @param connection
	 * @param start
	 * @return
	 * @throws SQLException
	 */
	public static HashMap<String, Integer> largestRenter(Connection connection, Date start) throws SQLException{
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		query = "SELECT U.name, COUNT(*) AS COUNT FROM users U,cancelled WHERE hostCancel='0' AND U.SIN=cancelled.renterSIN AND (SELECT COUNT(*) FROM users,cancelled WHERE startDate > '"+start+"' AND U.SIN=cancelled.renterSIN)>all(SELECT COUNT(*) FROM users,cancelled WHERE startDate > '"+start+"' AND users.SIN=cancelled.renterSIN GROUP BY U.name)";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		
		while (data.next()) {
			result.put(data.getString("name"), data.getInt("COUNT"));
		}
		return result;
	}

	/**
	 * Query to create a word cloud of the most popular noun phrases associated with a listing
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static HashMap<Integer, HashMap<String, Integer>> wordCloud(Connection connection) throws SQLException{
		HashMap<String, Integer> eachListing = new HashMap<String, Integer>();
		HashMap<Integer, HashMap<String, Integer>> result = new HashMap<Integer, HashMap<String, Integer>>();
		
		query = "SELECT message, listingID FROM listingRating GROUP BY message, listingID";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		
		while (data.next()) {
			eachListing = new HashMap<String, Integer>();
			String message = data.getString("message");
			int listingID = data.getInt("listingID");
			String[] phrase = message.split("\\.");
			
			for (String a: phrase) {
				a = a.trim();
				if (result.containsKey(listingID)){
					eachListing = result.get(listingID);
				}
				if (eachListing.containsKey(a)) {
					
					eachListing.replace(a, eachListing.get(a)+1);
				}
				else {
					eachListing.put(a, 1);
				}
				
			}
			result.put(listingID, eachListing);
		}
		
		return result;
	}
	
	
}
