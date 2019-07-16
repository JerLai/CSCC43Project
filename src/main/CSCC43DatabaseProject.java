package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CSCC43DatabaseProject {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		CSCC43DatabaseProject pro = new CSCC43DatabaseProject();
		pro.createConnection();
	}

	void createConnection() throws SQLException{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con  = DriverManager.getConnection("jdbc:mysql://localhost/test", "Kamito", "TarElendil1");
			System.out.println("Database Connection Success");
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(CSCC43DatabaseProject.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SQLException ex) {
			Logger.getLogger(CSCC43DatabaseProject.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
