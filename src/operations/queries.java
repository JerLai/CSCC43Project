package operations;

import java.sql.Date;
import java.util.ArrayList;

public class queries {

	private static String query;
	
	
	 // TODO: Distance filtering
	
	public static ArrayList<Object> startFilter(boolean postal, boolean distance, boolean address, boolean temporal, boolean price,
			boolean amenities, int rankPrice, String postalValue, 
			double km, double lat, double lng,
			String searchAddress,
			Date start, Date end,
			double priceL, double priceH,
			String searchAmenities){
		ArrayList<Object> data = new ArrayList<Object>();
		
		query = "SELECT * FROM listing amenities";
		String priceQuery = "";
		if (rankPrice == 1) {
			priceQuery = "ORDER BY price ASC";
		}
		else if (rankPrice == 2){
			priceQuery = "ORDER BY price DESC";
		}
		
		// price Ranking 0 none, 1 asc, 2 desc
		
		/*
		 ArrayList<Object> data = query.execute();
		 
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
	
	private static ArrayList<Object> intersection(ArrayList<Object> a, ArrayList<Object> b){
		ArrayList<Object> set = new ArrayList<Object>();
		
		for (Object p: a) {
			for (Object q: b) {
				
				/*if (p.listingID = q.listingID){ 
				  	set.add(a);
				 }
				 
				 
				 */
			}
		}
		return set;
	}
	
	public static ArrayList<Object> allListings() {
		ArrayList<Object> result = new ArrayList<Object>();
		
		String query = "Select * FROM listing";
		
		
		
		return result;
	}
	
	public static ArrayList<Object> viewTimes(String SIN, String listingID){
		ArrayList<Object> result = new ArrayList<Object>();
		
		String query = "Select startDate, endDate FROM calendar WHERE listingID="+listingID+";";
		return result;
		
	}
	
	private static ArrayList<Object> searchPostal(ArrayList<Object> data,String postal, String priceFilter){
		
		ArrayList<Object> result = new ArrayList<Object>();
		
		String query = "SELECT * FROM listing,calendar,amenities WHERE postalCode="+postal+" "+priceFilter+";";
		/*
			result = query.execute();
		 	result = intersection(data,result);
		 */
		
		
		return result;
	}
	
	private static ArrayList<Object> allAmenities(String query, ArrayList<Object> data, String priceFilter){
		ArrayList<Object> result = new ArrayList<Object>();
		
		query = "SELECT * FROM listing,calendar,amenities WHERE dining LIKE "+query+ " OR safetyFeatures LIKE "+query+ " OR facilities LIKE "+query+ 
				" OR guessAccess LIKE "+query+ " OR logistics LIKE "+query+ " OR notIncluded LIKE "+query+ " OR bedAndBath LIKE "+query+ "outdoor LIKE "+
				query+ " OR basic LIKE "+query+ " " + priceFilter+";";
		
		/*
		 result = query.execute();
		 result = intersection(data, result);
		 */
		return result;
	}
	
	private static ArrayList<Object> priceRange(double low, double high, ArrayList<Object> data, String priceFilter){
		ArrayList<Object> result = new ArrayList<Object>();
		query = "SELECT * FROM listing,calendar,amenities WHERE price < "+high + " AND price >"+ low+" " + priceFilter+";";
		
		/*
		 result = query.exeute();
		 result = intersection(data, result);
		  
		 */
		
		
		return result;
	}
	
	
	private static ArrayList<Object> closeListings(double setLat, double Lng, double distance, ArrayList<Object> data, String priceFilter) {
		ArrayList<Object> result = new ArrayList<Object>();
		query = "SELECT * FROM listing,calendar,amenities "+ priceFilter + ";";
		
		/*
		 ArrayList<Object> data = query.execute();
		 
		 for (Object a: data){
		 	double eachLat = a.latitude;
		 	double eachLong = a.longitude;
		 
		 	double difference = calculateDistance(setLat, setLng, eachLat, eachLong);
		 	
		 	if (distance >= difference){
		 		result.add(a);
		 	}
		 	
		 	
		 }
		 result = intersection(data, result)
		 */
		return result;	
	}
	
	private static ArrayList<Object> searchTime(Date start, Date end, ArrayList<Object> data, String priceFilter){
		ArrayList<Object> result = new ArrayList<Object>();
		String startStr = start.toString();
		String endStr = end.toString();
		
		query = "SELECT * FROM listing,calendar,amenities WHERE startDate<="+ startStr + " AND endDate>= " + endStr + " " + priceFilter +";";
		
		/*
		 result = query.execute();
		 result = intersection(data, result);
		 
		 */
		
		return result;
	}
	
	private static ArrayList<Object> specificAddress(String address, ArrayList<Object> data, String priceFilter){
		ArrayList<Object> result = new ArrayList<Object>();
		query = "SELECT * FROM listing,calendar,amenities WHERE city LIKE '" + address+"' OR country LIKE '"+address+"' " + priceFilter + ";";
		/*
		
		result = query.execute();
		
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
	
	
}
