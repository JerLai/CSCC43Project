package main;

import java.sql.SQLException;

public class CSCC43DatabaseProject {

	public static void main(String[] args) throws SQLException {
		CommandLineHandler cmd = new CommandLineHandler();
		if (cmd.startSession()) {
			if (cmd.execute()) {
				cmd.endSession();
			}
		}
	}
}
