package operations;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import main.dbsetup.DBAPI;

public class queries {

	private static String query;
	private static String all = "listing LEFT JOIN calendar ON listing.listingID=calendar.listingID LEFT JOIN amenities on listing.listingID = amenities.listingID";
	
	 // TODO: Distance filtering
	
	public static ArrayList<HashMap<String, String>> startFilter(Connection connection, boolean postal, String postalValue, 
			boolean distance, double km, double lat, double lng,
			boolean address, String searchAddress,
			boolean temporal, Date start, Date end,
			boolean price, double priceL, double priceH,
			boolean amenities, String searchAmenities,
			int rankPrice) throws SQLException{
		
		query = "SELECT * FROM " + all+";";
		
	
		
		// price Ranking 0 none, 1 asc, 2 desc
	
		 ArrayList<HashMap<String, String>> data = processResult(DBAPI.getDataByQuery(connection, query));
			
		  if (postal){
		  	data = searchPostal(connection, data, postalValue);
		  }
		  
		  if (distance){
		  	data = closeListings(connection, lat, lng, km, data);
		  }
		  if (address){
		  	data = specificAddress(connection, searchAddress, data);
		  }
		  if (temporal){
		  	data = searchTime(connection, start, end, data);
		  }
		  if (price){
		  	data = priceRange(connection, priceL, priceH, data);
		  	if (rankPrice == 0) {
		  		rankPrice = 1;
		  	}
		  }
		  if(amenities){
		  	data = allAmenities(connection, searchAmenities, data);
		  }
		  if (rankPrice > 0) {
				//System.out.println(rankPrice);
			if (rankPrice == 1) {
				Comparator<HashMap<String, String>> c = (h1, h2) -> { return h1.get("price").compareTo(h2.get("price"));};
				data.sort(c);
			} 
			else if (rankPrice == 2) {
			
				Comparator<HashMap<String, String>> c = (h1, h2) -> { return h2.get("price").compareTo(h1.get("price"));};
				data.sort(c);
				
			}
		  }
		
		return data;
		
	}
	
	public static ArrayList<ArrayList<String>> getUsers(Connection connection) throws SQLException {
		ArrayList<String> entry = new ArrayList<String>();
		ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
		query = "SELECT * FROM users";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		while (data.next()) {
			entry = new ArrayList<String>();
			String name = data.getString("name");
			String address = data.getString("address");
			String occupation = data.getString("occupation");
			Date dob = data.getDate("DoB");
			int SIN = data.getInt("SIN");
			
			entry.add(Integer.toString(SIN));
			entry.add(name);
			entry.add(address);
			entry.add(occupation);
			entry.add(dob.toString());
			
			results.add(entry);
		}
		
		return results;
	}
	
	public static ArrayList<ArrayList<String>> getHosts(Connection connection) throws SQLException {
		ArrayList<String> entry = new ArrayList<String>();
		ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
		query = "SELECT * FROM users,host WHERE users.SIN=host.SIN";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		while (data.next()) {
			entry = new ArrayList<String>();
			String name = data.getString("name");
			String address = data.getString("address");
			String occupation = data.getString("occupation");
			Date dob = data.getDate("DoB");
			int SIN = data.getInt("SIN");
			
			entry.add(Integer.toString(SIN));
			entry.add(name);
			entry.add(address);
			entry.add(occupation);
			entry.add(dob.toString());
			
			results.add(entry);
		}
		
		return results;
	}
	
	public static ArrayList<ArrayList<String>> getRenter(Connection connection) throws SQLException {
		ArrayList<String> entry = new ArrayList<String>();
		ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
		query = "SELECT * FROM users,renter WHERE users.SIN=renter.SIN";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		while (data.next()) {
			entry = new ArrayList<String>();
			String name = data.getString("name");
			String address = data.getString("address");
			String occupation = data.getString("occupation");
			Date dob = data.getDate("DoB");
			int SIN = data.getInt("SIN");
			int credit = data.getInt("creditCard");
			
			entry.add(Integer.toString(SIN));
			entry.add(name);
			entry.add(address);
			entry.add(occupation);
			entry.add(dob.toString());
			entry.add(Integer.toString(credit));
			
			results.add(entry);
		}
		
		return results;
	}
	
	public static ArrayList<String> getHistoryRenter(Connection connection, String SIN) throws SQLException { // useful to get SIN for comments
		ArrayList<String> result = new ArrayList<String>();
		query = "SELECT * FROM history WHERE renterSIN='"+SIN+"'";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		while (data.next()) {
			int host = data.getInt("hostSIN");
			int renter = data.getInt("renterSIN");
			int listing = data.getInt("listingID");
			Date start = data.getDate("startDate");
			Date end = data.getDate("endDate");
			result.add(Integer.toString(host));
			result.add(Integer.toString(renter));
			result.add(Integer.toString(listing));
			result.add(start.toString());
			result.add(end.toString());
		}
		
		return result;
	}
	
	public static ArrayList<String> getHistoryHost(Connection connection, String SIN) throws SQLException { // useful to get SIN for comments
		ArrayList<String> result = new ArrayList<String>();
		query = "SELECT * FROM history WHERE hostSIN='"+SIN+"'";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		while (data.next()) {
			int host = data.getInt("hostSIN");
			int renter = data.getInt("renterSIN");
			int listing = data.getInt("listingID");
			Date start = data.getDate("startDate");
			Date end = data.getDate("endDate");
			result.add(Integer.toString(host));
			result.add(Integer.toString(renter));
			result.add(Integer.toString(listing));
			result.add(start.toString());
			result.add(end.toString());
		}
		
		return result;
	}
	
	public static ArrayList<HashMap<String, String>> processResult(ResultSet r) throws SQLException{
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> entry = new HashMap<String, String>();
		
		
		while(r.next()){
			//Retrieve by column name
			entry = new HashMap<String, String>();
			
			entry.put("listingID", Integer.toString(r.getInt("listingID")));
			entry.put("type", r.getString("type"));
			entry.put("longitude", Double.toString(r.getDouble("longitude")));
			entry.put("latitude", Double.toString(r.getDouble("latitude")));
			entry.put("city", r.getString("city"));
			entry.put("postalCode", r.getString("postalCode"));
			try {
			entry.put("startDate", r.getDate("startDate").toString());
			entry.put("endDate", r.getDate("endDate").toString());
			entry.put("price", Double.toString(r.getDouble("price")));
			}
			catch (Exception e) {
				entry.put("startDate", "");
				entry.put("endDate", "");
				entry.put("price", "");
			}
			try {
			entry.put("dining", r.getString("dining"));
			entry.put("safetyFeatures", r.getString("safetyFeatures"));
			entry.put("facilities", r.getString("facilities"));
			entry.put("logistics", r.getString("logistics"));
			entry.put("notIncluded", r.getString("notIncluded"));
			entry.put("bedAndBath", r.getString("bedAndBath"));
			entry.put("outdoor", r.getString("outdoor"));
			entry.put("basic", r.getString("basic"));
			}
			catch(Exception e) {
				entry.put("dining", "");
				entry.put("safetyFeatures", "");
				entry.put("facilities", "");
				entry.put("logistics", "");
				entry.put("notIncluded", "");
				entry.put("bedAndBath", "");
				entry.put("outdoor", "");
				entry.put("basic", "");
			}
			result.add(entry);

		}
		
		return result;
	} 
	
	private static ArrayList<HashMap<String, String>> intersection(ArrayList<HashMap<String, String>> a, ArrayList<HashMap<String, String>> b){
		ArrayList<HashMap<String, String>> set = new ArrayList<HashMap<String, String>>();
		//System.out.println(a);
		for (HashMap<String, String> p: a) {
			for (HashMap<String, String> q: b) {
				//System.out.println(p.get("listingID") + "  " +q.get("listingID"));
				if (p.get("listingID").equals(q.get("listingID")) && p.get("startDate").contentEquals(q.get("startDate")) && p.get("endDate").equals(q.get("endDate"))){ 
				  	set.add(p);
				  	break;
				}
			}
		}
		return set;
	}
		
	
	
	/////////
	
	private static ArrayList<HashMap<String, String>> searchPostal(Connection connection, ArrayList<HashMap<String, String>> data,String postal) throws SQLException{
		
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		
		String query = "SELECT * FROM "+all+" WHERE postalCode='"+postal+"';";
		result = processResult(DBAPI.getDataByQuery(connection, query));
		//System.out.println(data);
		result = intersection(data,result);
		
		
		
		return result;
	}
	
	private static ArrayList<HashMap<String, String>> allAmenities(Connection connection, String query, ArrayList<HashMap<String, String>> data) throws SQLException{
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		
		query = "SELECT * FROM "+all+" WHERE dining LIKE '"+query+ "' OR safetyFeatures LIKE '"+query+ "' OR facilities LIKE '"+query+ 
				"' OR guestAccess LIKE '"+query+ "' OR logistics LIKE '"+query+ "' OR notIncluded LIKE '"+query+ "' OR bedAndBath LIKE '"+query+ "' OR outdoor LIKE '"+
				query+ "' OR basic LIKE '"+query+";";
		//System.out.println(query);
		
		 result = processResult(DBAPI.getDataByQuery(connection, query));
		 result = intersection(data, result);
		 
		return result;
	}
	
	private static ArrayList<HashMap<String, String>> priceRange(Connection connection, double low, double high, ArrayList<HashMap<String, String>> data) throws SQLException{
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		query = "SELECT * FROM "+all+" WHERE price < "+high + " AND price >"+ low+";";
		
		 result = processResult(DBAPI.getDataByQuery(connection, query));
		 result = intersection(data, result);
		  		
		return result;
	}
	
	
	private static ArrayList<HashMap<String, String>> closeListings(Connection connection, double setLat, double setLng, double distance, ArrayList<HashMap<String, String>> data) throws SQLException {
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		query = "SELECT * FROM "+all + ";";
		
		
		 ArrayList<HashMap<String, String>> inners = processResult(DBAPI.getDataByQuery(connection, query));
		 
		 for (HashMap<String, String> a: inners){
		 	double eachLat = Double.parseDouble(a.get("latitude"));
		 	double eachLong = Double.parseDouble(a.get("longitude"));
		 	
		 	double difference = calculateDistance(setLat, setLng, eachLat, eachLong);
		 	if (distance >= difference){
		 		
		 		result.add(a);
		 	}
		 	
		 	
		 }
		 result = intersection(data, result);
		 
		return result;	
	}
	
	private static ArrayList<HashMap<String, String>> searchTime(Connection connection, Date start, Date end, ArrayList<HashMap<String, String>> data) throws SQLException{
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		String startStr = start.toString();
		String endStr = end.toString();
		
		query = "SELECT * FROM "+all+" WHERE startDate<='"+ startStr + "' AND endDate>= '" + endStr + "';";
		
		
		 result = processResult(DBAPI.getDataByQuery(connection, query));
		 result = intersection(data, result);
		
		return result;
	}
	
	private static ArrayList<HashMap<String, String>> specificAddress(Connection connection, String address, ArrayList<HashMap<String, String>> data) throws SQLException{
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		query = "SELECT * FROM "+all+" WHERE address LIKE '"+ address +"'";
		//System.out.println(query);
		result = processResult(DBAPI.getDataByQuery(connection, query));
		result = intersection(data, result);		
		
		return result;
	}
	
	
	
	private static double calculateDistance(double setLat, double setLng, double eachLat, double eachLng) {
		double latDistance = Math.toRadians(setLat - eachLat);
		double lngDistance = Math.toRadians(setLng - eachLng);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) 
				+ Math.cos(Math.toRadians(setLat)) * Math.cos(Math.toRadians(eachLat))
	            * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
		double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double distance = 6371 * b * 1000;
		
		double height = 0.0;
		distance = Math.pow(distance, 2) + Math.pow(height, 2);
		distance = Math.sqrt(distance);
		return (distance/1000);
	}
	
	public static ArrayList<HashMap<String, String>> hostListings(Connection connection, String SIN) throws SQLException {
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> entry = new HashMap<String, String>();
		String query = "Select * FROM listing WHERE hostSIN='"+SIN+"'";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		
		while (data.next()) {
			entry = new HashMap<String, String>();
			entry.put("listingID", Integer.toString(data.getInt("listingID")));
			entry.put("hostSIN", Integer.toString(data.getInt("hostSIN")));
			entry.put("type", data.getString("type"));
			entry.put("longitude",  Double.toString(data.getDouble("longitude")));
			entry.put("latitude", Double.toString(data.getDouble("latitude")));
			entry.put("city", data.getString("city"));
			entry.put("country", data.getString("country"));
			entry.put("postalCode", data.getString("postalCode"));
			entry.put("address", data.getString("address"));
			
			result.add(entry);
		}
		
		return result;
	}
	

	public static ArrayList<HashMap<String, String>> renterBookings(Connection connection, String SIN) throws SQLException {
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> entry = new HashMap<String, String>();
		String query = "Select * FROM listing, reservations WHERE renterSIN='"+SIN+"' AND reservations.listingID=listing.listingID";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		
		while (data.next()) {
			entry = new HashMap<String, String>();
			entry.put("listingID", Integer.toString(data.getInt("listingID")));
			entry.put("hostSIN", Integer.toString(data.getInt("hostSIN")));
			entry.put("type", data.getString("type"));
			entry.put("longitude",  Double.toString(data.getDouble("longitude")));
			entry.put("latitude", Double.toString(data.getDouble("latitude")));
			entry.put("city", data.getString("citresy"));
			entry.put("price", Double.toString(data.getDouble("price")));
			entry.put("address", data.getString("address"));
			entry.put("country", data.getString("country"));
			entry.put("postalCode", data.getString("postalCode"));
			entry.put("startDate", data.getDate("startDate").toString());
			entry.put("endDate", data.getDate("endDate").toString());
			
			result.add(entry);
		}
		
		return result;
	}
	
	public static ArrayList<HashMap<String, String>> viewTimes(String SIN, String listingID){
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		
		String query = "Select startDate, endDate FROM calendar WHERE listingID="+listingID+";";
		return result;
		
		
	}
	
	public static ArrayList<HashMap<String, String>> reservationsToHost(Connection connection, String SIN) throws SQLException{
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> entry = new HashMap<String, String>();
		query = "SELECT * FROM reservations, listing, users WHERE users.SIN = listing.hostSIN AND listing.listingID = reservations.listingID";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		while (data.next()) {
			entry = new HashMap<String, String>();
			entry.put("listingID", Integer.toString(data.getInt("listingID")));
			entry.put("renterSIN", Integer.toString(data.getInt("renterSIN")));
			entry.put("endDate", data.getDate("endDate").toString());
			entry.put("startDate", data.getDate("startDate").toString());
			
			result.add(entry);
		}
		
		return result;
	}
	
	
}
