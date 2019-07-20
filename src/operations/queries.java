package operations;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class queries {

	private static String query;
	private static String all = "listing LEFT JOIN calendar ON listing.listingID=calendar.listingID INNER JOIN amenities on listing.listingID = amenities.listingID";
	
	 // TODO: Distance filtering
	
	public static ArrayList<HashMap<String, String>> startFilter(Connection connection, boolean postal, boolean distance, boolean address, boolean temporal, boolean price,
			boolean amenities, int rankPrice, String postalValue, 
			double km, double lat, double lng,
			String searchAddress,
			Date start, Date end,
			double priceL, double priceH,
			String searchAmenities){
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		
		query = "SELECT * FROM "+all+";";
		String priceQuery = "";
		if (rankPrice == 1) {
			priceQuery = "ORDER BY price ASC";
		}
		else if (rankPrice == 2){
			priceQuery = "ORDER BY price DESC";
		}
		
		// price Ranking 0 none, 1 asc, 2 desc
		
		/*
		 ArrayList<HashMap<String, String>> data = query.execute();
		 
		  if (postal){
		  	data = searchPostal(data, postalValue, rankPrice);
		  }
		  if (distance){
		  	data = closeListings(lat, lng, km, data, rankPrice);
		  }
		  if (address){
		  	data = specificAddress(searchAddress, data, rankPrice);
		  }
		  if (temporal){
		  	data = searchTime(start, end, data, rankPrice);
		  }
		  if (price){
		  	data = priceRange(priceL, priceH, data, rankPrice);
		  }
		  if(amenities){
		  	data = allAmenities(searchAmenities, data, rankPrice);
		  }
		 
	
		  
		 */
		
		return data;
		
	}
	
	public static ArrayList<HashMap<String, String>> processResult(ResultSet r) throws SQLException{
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> entry = new HashMap<String, String>();
		
		
		while(r.next()){
			//Retrieve by column name
			entry.put("listingID", Integer.toString(r.getInt("listingID")));
			entry.put("type", r.getString("type"));
			entry.put("longitude", Double.toString(r.getDouble("longitude")));
			entry.put("latitude", Double.toString(r.getDouble("latitude")));
			entry.put("city", r.getString("city"));
			entry.put("postalCode", r.getString("postalCode"));
			entry.put("startDate", r.getDate("startDate").toString());
			entry.put("endDate", r.getDate("endDate").toString());
			entry.put("price", Double.toString(r.getDouble("price")));
			
			entry.put("dining", r.getString("dining"));
			entry.put("safetyFeatures", r.getString("safetyFeatures"));
			entry.put("facilities", r.getString("facilities"));
			entry.put("logistics", r.getString("logistics"));
			entry.put("notIncluded", r.getString("notIncluded"));
			entry.put("bedAndBath", r.getString("bedAndBath"));
			entry.put("outdoor", r.getString("outdoor"));
			entry.put("basic", r.getString("basic"));
			
			result.add(entry);

		}
		
		return result;
	} 
	
	private static ArrayList<HashMap<String, String>> intersection(ArrayList<HashMap<String, String>> a, ArrayList<HashMap<String, String>> b){
		ArrayList<HashMap<String, String>> set = new ArrayList<HashMap<String, String>>();
		
		for (HashMap<String, String> p: a) {
			for (HashMap<String, String> q: b) {
				
				if (p.get("listingID") == q.get("listingID") && p.get("startDate") == q.get("startDate") && p.get("endDate") == q.get("endDate")){ 
				  	set.add(p);
				 
				}
			}
		}
		return set;
	}
		
	
	
	/////////
	
	private static ArrayList<HashMap<String, String>> searchPostal(ArrayList<HashMap<String, String>> data,String postal, String priceFilter){
		
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		
		String query = "SELECT * FROM "+all+" WHERE postalCode="+postal+" "+priceFilter+";";
		/*
			result = processResult(query.execute());
		 	result = intersection(data,result);
		 */
		
		
		return result;
	}
	
	private static ArrayList<HashMap<String, String>> allAmenities(String query, ArrayList<HashMap<String, String>> data, String priceFilter){
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		
		query = "SELECT * FROM "+all+" WHERE dining LIKE "+query+ " OR safetyFeatures LIKE "+query+ " OR facilities LIKE "+query+ 
				" OR guessAccess LIKE "+query+ " OR logistics LIKE "+query+ " OR notIncluded LIKE "+query+ " OR bedAndBath LIKE "+query+ "outdoor LIKE "+
				query+ " OR basic LIKE "+query+ " " + priceFilter+";";
		
		/*
		 result = processResult(query.execute());
		 result = intersection(data, result);
		 */
		return result;
	}
	
	private static ArrayList<HashMap<String, String>> priceRange(double low, double high, ArrayList<HashMap<String, String>> data, String priceFilter){
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		query = "SELECT * FROM "+all+" WHERE price < "+high + " AND price >"+ low+" " + priceFilter+";";
		
		/*
		 result = processResult(query.execute());
		 result = intersection(data, result);
		  
		 */
		
		
		return result;
	}
	
	
	private static ArrayList<HashMap<String, String>> closeListings(double setLat, double Lng, double distance, ArrayList<HashMap<String, String>> data, String priceFilter) {
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		query = "SELECT * FROM "+all+" "+ priceFilter + ";";
		
		/*
		 ArrayList<HashMap<String, String>> inners = processResult(query.execute());
		 
		 for (HashMap a: inners){
		 	double eachLat = a.get(latitude);
		 	double eachLong = a.get(longitude);
		 	
		 	double difference = calculateDistance(setLat, setLng, eachLat, eachLong);
		 	
		 	if (distance >= difference){
		 		result.add(a);
		 	}
		 	
		 	
		 }
		 result = intersection(data, result)
		 */
		return result;	
	}
	
	private static ArrayList<HashMap<String, String>> searchTime(Date start, Date end, ArrayList<HashMap<String, String>> data, String priceFilter){
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		String startStr = start.toString();
		String endStr = end.toString();
		
		query = "SELECT * FROM "+all+" WHERE startDate<="+ startStr + " AND endDate>= " + endStr + " " + priceFilter +";";
		
		/*
		 result = processResult(query.execute());
		 result = intersection(data, result);
		 
		 */
		
		return result;
	}
	
	private static ArrayList<HashMap<String, String>> specificAddress(String address, ArrayList<HashMap<String, String>> data, String priceFilter){
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		query = "SELECT * FROM "+all+" WHERE city LIKE '" + address+"' OR country LIKE '"+address+"' " + priceFilter + ";";
		/*
		
		result = processResult(query.execute());
		
		result = intersection(data, result);
		*/
		
		
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
	
	public static ArrayList<HashMap<String, String>> allListings() {
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		
		String query = "Select * FROM listing";
		
		
		
		return result;
	}
	
	public static ArrayList<HashMap<String, String>> viewTimes(String SIN, String listingID){
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		
		String query = "Select startDate, endDate FROM calendar WHERE listingID="+listingID+";";
		return result;
		
	}
	
	
}
