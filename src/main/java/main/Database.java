package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Database {
	
	private static final String hostName	= "localhost";
	private static final String database	= "smart_parking";
	private static final int	port 		= 3306;
	private static final String user		= "smart_parking";
	private static final String password	= "smart_parking";

	private static Database instance = null;
	
	private static Connection conn = null;
	
	private static boolean connect2DB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://" + hostName + "/" + database +"?useSSL=false", user, password);
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("[ERROR]" + e.getMessage());
			return false;
			
		}
		System.out.println("Connected to " + hostName + "/" + database);
		return true;
	}
	
	public static Database getInstance() {
		if (instance == null) {
			instance = new Database();
			if (instance.connect2DB() == false) {
				System.exit(101);
			}
		}
		return instance;
	}
	
}
