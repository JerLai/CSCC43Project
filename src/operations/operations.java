package operations;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import main.dbsetup.DBAPI;

public class operations {

	public static String query;

	//	public static void sendQuery(Connection connection, String query)

	public static void createProfile(Connection connection, String SIN, String name, String address, String occupation, String dob) throws SQLException {
		// This one is for hosts

		query = "INSERT INTO users(SIN, name, address, occupation, DoB) VALUES('" + SIN+"', '"+ name +"', '"+ address+"', '"+ occupation+"', '"+ dob+"');";
		System.out.println(query);
		DBAPI.sendQuery(connection, query);

		// host
		query = "INSERT INTO host(SIN) VALUES("+SIN+");";
		DBAPI.sendQuery(connection, query);

	}


	public static void createProfile(Connection connection, String SIN, String name, String address, String occupation, String dob, String creditCard) throws SQLException{
		// This is for renter
		query = "INSERT INTO users(SIN, name, address, occupation, DoB) VALUES('" + SIN+"','"+ name +"','"+ address+"','"+ occupation+"','"+ dob+"');";
		DBAPI.sendQuery(connection, query);

		//renter
		query = "INSERT INTO renter(SIN, creditCard) VALUES('"+SIN+"','"+creditCard+"');";
		DBAPI.sendQuery(connection, query);
	}

	public static void makeHost(Connection connection, String SIN) throws SQLException {
		query = "INSERT INTO host(SIN) VALUES('"+SIN+"')";
		DBAPI.sendQuery(connection, query);
	}
	
	public static void makeRenter(Connection connection, String SIN, String credit) throws SQLException {
		query = "INSERT INTO renter(SIN, creditCard) VALUES('"+SIN+"','"+credit+"');";
		DBAPI.sendQuery(connection, query);
	}
	
	public static void deleteProfile(Connection connection, String SIN, boolean type) throws SQLException {


		if (type) { // host
			query = "DELETE FROM host WHERE SIN="+SIN+";";
			DBAPI.sendQuery(connection, query);

		}
		else { // renter
			query = "DELETE FROM renter WHERE SIN="+SIN+";";
			DBAPI.sendQuery(connection, query);
		}

	}
	
	public static void deleteUser(Connection connection, String SIN) throws SQLException {
		query = "DELETE FROM users WHERE SIN="+SIN+";";
		DBAPI.sendQuery(connection, query);
	}


	private static ArrayList<HashMap> processResult(ResultSet r) throws SQLException{
		ArrayList<HashMap> result = new ArrayList<HashMap>();
		HashMap<String, String> entry = new HashMap<String, String>();


		while(r.next()){
			//Retrieve by column name
			entry.put("listingID", Integer.toString(r.getInt("listingID")));
			entry.put("startDate", r.getDate("startDate").toString());
			entry.put("endDate", r.getDate("endDate").toString());
			entry.put("price", Double.toString(r.getDouble("price")));


			result.add(entry);

		}	
		return result;
	} 



	public static void createListing(Connection connection, String SIN, String types, String longitude, String latitude, String city, String country, String address, String postalCode) throws SQLException {

		query = "INSERT INTO listing(hostSIN, type, longitude, latitude, city, country, address, postalCode) VALUES('"+ SIN + "', '" + types+"', '" + longitude + "', '" + latitude + "', '" + city + "', '" + country + "', '" + address + "', '" + postalCode + "');";
		DBAPI.sendQuery(connection, query);

	}
	
	private static boolean checkDates(Connection connection, String listingID, Date start, Date end) throws SQLException {
	
		query = "SELECT startDate, endDate FROM calendar WHERE calendar.listingID = '"+listingID+"'";
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		
		while (data.next()) {
			Date eStart = data.getDate("startDate");
			Date eEnd = data.getDate("endDate");
			// 4 scenarios
			if (eStart.compareTo(start) <= 0 && eEnd.compareTo(end) <= 0 && start.compareTo(eEnd) <= 0) {
				return true;
			}
			else if (eStart.compareTo(start) >= 0 && eEnd.compareTo(end) <= 0) {
				return true;
			}
			else if (eStart.compareTo(start) >= 0 && eEnd.compareTo(end) >= 0 && eStart.compareTo(end) <= 0) {
				return true;
			}
			else if (eStart.compareTo(start) <= 0 && eEnd.compareTo(end) >= 0) {
				return true;
			}
			
			
		}
		return false;
	}

	public static void createCalendar(Connection connection, String listingID, String startDate, String endDate, String price) throws SQLException {
		query = "INSERT INTO calendar(listingID, startDate, endDate, price) VALUES('" + listingID + "', '" + startDate + "', '" + endDate + "', '" + price +"')";
		DBAPI.sendQuery(connection, query);
	}
	
	public static void deleteListing(Connection connection, String listingID) throws SQLException {
		query = "DELETE FROM listing WHERE listingID='"+listingID+"'";
		DBAPI.sendQuery(connection, query);
		
		query = "DELETE FROM calendar WHERE listingID='"+listingID+"'";
		DBAPI.sendQuery(connection, query);
		
		query = "DELETE FROM reservations WHERE listingID='"+listingID+"'";
		DBAPI.sendQuery(connection, query);
	}

	public static void bookListing(Connection connection, String SIN, String listingID, Date start, Date end) throws SQLException {
		if (start.compareTo(end) > 0){
			// the start is after the end date somehow
			//give an error
			System.out.println("Start Date has to be after End Date, try again.");
			return;
		}
		query = "SELECT * FROM listing LEFT JOIN calendar ON listing.listingID = calendar.listingID WHERE listing.listingID="+listingID+";";
		ResultSet data = DBAPI.getDataByQuery(connection, query);// or something like this
		while (data.next()) {
			Date setStart = data.getDate("startDate");
			Date setEnd = data.getDate("endDate");
			String price = Double.toString(data.getDouble("price"));

			System.out.println();
			if (setStart.compareTo(start) < 0 && setEnd.compareTo(end) > 0 && !start.toString().equals(setStart.toString()) && !end.toString().equals(setEnd.toString()) ){
				// split the time into 2
				query = "INSERT INTO reservations(listingID, renterSIN, endDate, startDate, price) VALUES('"+listingID+"','"+SIN+"','"+start.toString()+"','"+end.toString()+"', '" + price+"')";
				DBAPI.sendQuery(connection, query);

				query = "UPDATE calendar SET endDate='" + start + "'  WHERE startDate='"+setStart+ "' AND listingID="+listingID+";";
				DBAPI.sendQuery(connection, query);

				query = "INSERT INTO calendar(listingID, startDate, endDate, price) VALUES('"+listingID+"','"+end+"','"+setEnd+"','"+price+"');";
				DBAPI.sendQuery(connection, query);

				query = "INSERT INTO history(hostSIN, renterSIN, listingID, startDate, endDate) VALUES ('"+Integer.toString(data.getInt("hostSIN"))+"', '"+SIN+"', '"+ listingID + "', '"+start.toString() + "', '" +end+"');";
				DBAPI.sendQuery(connection, query);

				return;
			}
			// entire duration
			else if (start.toString().equals(setStart.toString()) && end.toString().equals(setEnd.toString())){
				query = "INSERT INTO reservations(listingID, renterSIN, endDate, startDate, price) VALUES('"+listingID+"','"+SIN+"','"+start.toString()+"','"+end.toString()+"', '" + price+"')";
				
				DBAPI.sendQuery(connection, query);

				query = "DELETE FROM calendar WHERE listingID='"+listingID+"' AND startDate='"+setStart+"';";
				//System.out.println(query);
				DBAPI.sendQuery(connection, query);

				query = "INSERT INTO history(hostSIN, renterSIN, listingID, startDate, endDate) VALUES ('"+Integer.toString(data.getInt("hostSIN"))+"', '"+SIN+"', '"+ listingID + "', '"+start.toString() + "', '" +end+"')";

				DBAPI.sendQuery(connection, query);

				return;
			}
			// start on date, end in middle
			else if (start.toString().equals(setStart.toString()) && setEnd.compareTo(end) > 0){
				// session starts at the beginning
				query = "INSERT INTO reservations(listingID, renterSIN, endDate, startDate, price) VALUES('"+listingID+"','"+SIN+"','"+start.toString()+"','"+end.toString()+"', '" + price+"')";
				DBAPI.sendQuery(connection, query);

				query = "UPDATE calendar SET startDate='"+end.toString() + "' WHERE startDate='"+setStart+ "' AND listingID='"+listingID+"';";
				DBAPI.sendQuery(connection, query);

				query = "INSERT INTO history(hostSIN, renterSIN, listingID, startDate, endDate) VALUES ('"+Integer.toString(data.getInt("hostSIN"))+"', '"+SIN+"', '"+ listingID + "', '"+start.toString() + "', '" +end+"')";
				DBAPI.sendQuery(connection, query);
				break;
			}
			else if (setStart.compareTo(start) < 0  && end.toString().equals(setEnd.toString())){
				// start in middle, ends at end

				query = "INSERT INTO reservations(listingID, renterSIN, endDate, startDate, price) VALUES('"+listingID+"','"+SIN+"','"+start.toString()+"','"+end.toString()+"', '" + price+"')";
				DBAPI.sendQuery(connection, query);

				query = "UPDATE calendar SET endDate='"+start.toString() + "' WHERE startDate='"+setStart+ "' AND listingID='"+listingID+"';";
				DBAPI.sendQuery(connection, query);

				query = "INSERT INTO history(hostSIN, renterSIN, listingID, startDate, endDate) VALUES ('"+Integer.toString(data.getInt("hostSIN"))+"', '"+SIN+"', '"+ listingID + "', '"+start.toString() + "', '" +end+"')";
				DBAPI.sendQuery(connection, query);
				break;
			}
			else{
				// date is not within range of this entry
			}
		}
	}

	public static void hostRemoveListing(Connection connection, String listingID, Date start, Date end, String SIN, String hostSIN) throws SQLException {
		query = "DELETE FROM reservations WHERE listingID='"+listingID+"' AND startDate='"+start.toString()+"' AND endDate='" + end.toString() +"' AND renterSIN='"+SIN+"';";
		DBAPI.sendQuery(connection, query);
		query = "DELETE FROM history WHERE hostSIN='" + hostSIN + "' AND listingID='" + listingID +"' AND startDate='"+start.toString()+"' AND endDate='" + end.toString() + "' AND renterSIN='"+SIN+"';"; 
		DBAPI.sendQuery(connection, query);

		query = "INSERT INTO cancelled(hostSIN, listingID, startDate, endDate, renterSIN, hostCancel) VALUES ('"+hostSIN+"', '"+listingID + "', '"+start.toString() + "', '" +end+"', '" + SIN+"', '1');";
		DBAPI.sendQuery(connection, query);
	}

	public static void renterRemoveListing(Connection connection, String listingID, Date start, Date end, String SIN, String hostSIN, double price) throws SQLException {
		query = "DELETE FROM reservations WHERE listingID='"+listingID+"' AND startDate='"+start.toString()+"' AND endDate='" + end.toString() +"' AND renterSIN='"+SIN+"';";
		DBAPI.sendQuery(connection, query);
		query = "DELETE FROM history WHERE hostSIN='" + hostSIN + "' AND listingID='" + listingID +"' AND startDate='"+start.toString()+"' AND endDate='" + end.toString() + "' AND renterSIN='"+SIN+"';"; 
		DBAPI.sendQuery(connection, query);

		query = "INSERT INTO cancelled(hostSIN, listingID, startDate, endDate, renterSIN, hostCancel) VALUES ('"+hostSIN+"', '"+listingID + "', '"+start.toString() + "', '" +end+"', '" + SIN+"', '0');";
		DBAPI.sendQuery(connection, query);

		query = "SELECT * FROM calendar WHERE listingID="+listingID+";";

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start);
		calendar.add(Calendar.DATE, -1);
		Date yesterday = new Date(calendar.getTime().getTime());
		calendar.setTime(end);
		calendar.add(Calendar.DATE, 1);
		Date tomorrow = new Date(calendar.getTime().getTime());
		
		ResultSet data = DBAPI.getDataByQuery(connection, query);
		boolean set = false;
		while (data.next()){
			Date setStart = data.getDate("startDate");
			Date setEnd = data.getDate("endDate");
			double setPrice = data.getDouble("price");
			
			
			if (setEnd.toString().equals(yesterday.toString()) && price==setPrice && set){
				query = "UPDATE calendar SET startDate='"+setStart.toString()+"' WHERE listingID='"+listingID+"' AND startDate='" + setStart.toString()+"';";
				
				DBAPI.sendQuery(connection, query);
				query = "DELETE FROM calendar WHERE listingID='"+listingID+"' AND startDate='"+setStart+"' AND endDate='"+setEnd+"';";
				DBAPI.sendQuery(connection, query);
				System.out.println(query);
				set = true;
				System.out.println("1");
			}
			else if (setEnd.toString().equals(yesterday.toString())){
				query = "INSERT calendar(listingID, startDate, endDate, price) VALUES('"+listingID+"', '" +setStart.toString() + "', '" + end.toString() + "', '" + price+"');";
				
				DBAPI.sendQuery(connection, query);
				query = "DELETE FROM calendar WHERE listingID='"+listingID+"' AND startDate='"+setStart+"' AND endDate='"+setEnd+"';";
				DBAPI.sendQuery(connection, query);
			System.out.println("2");
				set = true;
				
			}
			else if (setStart.toString().equals(tomorrow.toString()) && price==setPrice && set){
				query = "UPDATE calendar SET endDate='"+setEnd.toString()+"' WHERE listingID='"+listingID+ "' AND endDate='"+end.toString()+"';";
				System.out.println("3");
				DBAPI.sendQuery(connection, query);
				query = "DELETE FROM calendar WHERE listingID='"+listingID+"' AND startDate='"+setStart+"' AND endDate='"+setEnd+"';";
				DBAPI.sendQuery(connection, query);
				set = true;
			}
			
			else if (setStart.toString().equals(tomorrow.toString()) && price==setPrice){
				query = "INSERT calendar(listingID, startDate, endDate, price) VALUES('"+listingID+"', '" +start.toString() + "', '" + setEnd.toString() + "', '" + price+"');";
				DBAPI.sendQuery(connection, query);
				System.out.println("4");
				query = "DELETE FROM calendar WHERE listingID='"+listingID+"' AND startDate='"+setStart+"' AND endDate='"+setEnd+"';";
				DBAPI.sendQuery(connection, query);
				set = true;
			}
			
		
		}

		if (!set) {
			query = "INSERT calendar(listingID, startDate, endDate, price) VALUES('"+listingID+"', '" +start.toString() + "', '" + end.toString() + "', '" + price+"');";
			DBAPI.sendQuery(connection, query);
		}
	}

	public static void hostUpdatePrice(Connection connection, String listingID, String SIN, Date start, Date end, String price) throws SQLException {
		query = "UPDATE calendar SET price='"+price+"' WHERE startDate='"+start+"'AND endDate='"+end+"';";
		System.out.println(query);
		DBAPI.sendQuery(connection, query);
	}
	
	public static void updateTime(Connection connection, String listingID, Date start, Date end, Date newStart, Date newEnd) throws SQLException {
		
		if (start.compareTo(end) > 0){
			// the start is after the end date somehow
			//give an error
			System.out.println("Start Date has to be after End Date, try again.");
			return;
		}
		
		if (checkDates(connection, listingID, start, end)) {
			System.out.println("New date cannot overlap other calendar reservations. Please try again with other dates");
			return;
		}
		
		query = "UPDATE calendar SET startDate='"+newStart+"', endDate='"+newEnd+"' WHERE startDate='"+start+"' AND endDate='"+end+"' AND listingID='"+listingID+"'";
		DBAPI.sendQuery(connection, query);
	}

	public static void addComment(Connection connection, String fromSIN, String toSIN, String message) throws SQLException {
		query = "INSERT INTO usercomments(commentID, toSIN,fromSIN, message) VALUES('2','" + fromSIN + "', '" + toSIN + "', '" + message + "');";
		System.out.println(query);
		DBAPI.sendQuery(connection, query);
	}

	public static void addRating(Connection connection, String SIN, String message, String rating, String listingID) throws SQLException {
		query = "INSERT INTO listingrating(ratingID, listingID, fromSIN, rating, message) VALUES('1', '" + listingID +"', '" + SIN +"', '"+ rating + "', '" + message + "');";
		System.out.println(query);
		DBAPI.sendQuery(connection, query);
	}
	
	public static void removeCalendar(Connection connection, String listingID, Date start, Date end) throws SQLException {
		
		query = "DELETE FROM calendar WHERE listingID='"+listingID+"' AND startDate='" + start.toString() + "' AND endDate='"+end.toString() + "';";
		DBAPI.sendQuery(connection, query);
	}

}
