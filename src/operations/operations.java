package operations;

import java.sql.Date;

public class operations {

	public static String query;
	
	public static void createProfile(String SIN, String name, String address, String occupation, Date dob) {
		// This one is for hosts
		query = "INSERT INTO users(SIN, name, address, occupation, DoB) VALUES(" + SIN+","+ name +","+ address+","+ occupation+","+ dob+");";
		
		// host
		query = "INSERT INTO host(SIN) VALUES("+SIN+");";
				
	}
	
	
	public static void createProfile(String SIN, String name, String address, String occupation, Date dob, int creditCard){
	// This is for renter
		query = "INSERT INTO users(SIN, name, address, occupation, DoB) VALUES(" + SIN+","+ name +","+ address+","+ occupation+","+ dob+");";
		
		//renter
		query = "INSERT INTO renter(SIN, creditCard) VALUES("+SIN+","+creditCard+");";
	}
	
	public static void deleteProfile(String SIN, boolean type) {
		query = "DELETE FROM users WHERE SIN="+SIN+";";
		
		if (type) { // host
			query = "DELETE FROM host WHERE SIN="+SIN+";";
		
		}
		else { // renter
			query = "DELETE FROM renter WHERE SIN="+SIN+";";
			
			
		}
	}
	
	public static void bookListing(String SIN, String listingID, Date start, Date end) {
		
		
		/*
		 if (start.compareTo(end) > 0){
		 	// the start is after the end date somehow
		 	//give an error
		 	 * return;
		 }
		 */
		query = "SELECT startDate, endDate FROM calendar WHERE listingID="+listingID+";";
		
		/*
		 ArrayList<Object> result = query.execute()// or something like this
		 for (Object a: result) {
			Date setStart = a.startDate;
			Date setEnd = a.endDate;
			String price = a.price;
			
			if (setStart.compareTo(start) < 0 && setEnd.compareTo(end) > 0){
				// split the time into 2
				query = "INSERT INTO reservations(listingID, renterSIN, endDate, startDate) VALUES("+listingID+","+SIN+","+start+","+end+");";
				
				
				query = "UPDATE calendar SET endDate=start WHERE startDate="+setDate+ "AND listingID="+listingID+";";
				
				query = INSERT INTO calendar(listingID, startDate, endDate, price) VALUES("+listingID+","+end+","+setEnd+","+price+");";
					
			}
			else if (setStart.compareTo(start) == 0 && setEnd.compareTo(end) == 0){
				query = "INSERT INTO reservations(listingID, renterSIN, endDate, startDate) VALUES("+listingID+","+SIN+","+start+","+end+");";
				
				query = "DELETE FROM calendar WHERE listingID="+listingID+"AND startDate="+setStart+";";
			}
			else if (setStart.compareTo(start) <= 0 && setEnd.compareTo(end) >= 0){
				// session starts at the beginning
				query = "INSERT INTO reservations(listingID, renterSIN, endDate, startDate) VALUES("+listingID+","+SIN+","+start+","+end+");";
				
				
				query = "UPDATE calendar SET endDate=end,startDate=start WHERE startDate="+setDate+ "AND listingID="+listingID+";";
				
			}
			else{
			// date is not within range of this entry
			}
			
			
		}*/
		
	}
	
	public static void hostRemoveListing(int listingID, Date start, int SIN) {
		query = "DELETE FROM reservations WHERE listingID="+listingID+",starDate="+start+",renterSIN="+SIN+";";
		
		query = "DELETE FROM calendar WHERE listingID="+listingID+",renterSIN="+SIN+",startDate="+start+";";
	}
	
	public static void renterRemoveListing(int listingID, Date start, Date end, int SIN) {
		query = "DELETE FROM reservations WHERE listingID="+listingID+",starDate="+start+",renterSIN="+SIN+";";
		
		query = "SELECT * FROM calendar WHERE listingID="+listingID+";";
		
		/*
		 
		  ArrayList<Object> result = query.execute()
		  for (Object a: result){
		  	Date setStart = a.startDate;
		  	Date setEnd = a.endDate;
		  	
		  	Calendar calendar = Calendar.getInstance();
		  	calendar.setTime(setStart);
		  	calendar.add(Calendar.Date, -1);
		  	Date yesterday = calendar.getTime();
		  	calendar.setTime();
		  	calendar.add(Calendar.Date, 1);
		  	Date tomorrow = calendar.getTime();
		  	
		  	if (yesterday.compareTo(start) == 0){
		  		query = "UPDATE calendar SET startDate = setStart WHERE startDate="+start+",endDate="+end+"listingID="+listingID+",renterSIN="+SIN+";";
		  		
		  		query = "DELETE FROM calendar WHERE listingID="+listingID+",renterSIN="+SIN+",endDate="+yesterday+";";
		  		
		  	}
		  	else if (tomorrow.compareTo(end) == 0){
		  		query = "UPDATE calendar SET endDate = setEnd WHERE endDate="+end+"listingID="+listingID+",renterSIN="+SIN+";";
		  		
		  		query = "DELETE FROM calendar WHERE listingID="+listingID+",renterSIN="+SIN+",startDate="+tomorrow+";";
		  		
		  	}
		  }
		 
		 */
	}
	
	public static void hostUpdatePrice(String listingID, String SIN, Date start, Date end, String price) {
		query = "UPDATE calendar SET price="+price+" WHERE (SELECT price FROM calendar listing WHERE hostSIN="+SIN+", startDate="+start+", endDate="+end+");";
	
		
	}

}
