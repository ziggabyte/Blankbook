package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnector {
	
	private static Connection conn = null;
	private static PreparedStatement stmt = null;
	private static ResultSet resultSet = null;
	
	public static boolean openConnection(String databaseName) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception e) {
			System.out.println("Driver: ");
			e.printStackTrace();
		}
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" 
		+ databaseName 
		+ "?serverTimezone=UTC", 
		DatabaseLogin.getUsername(), 
		DatabaseLogin.getPassword());
			return true;
			
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			return false;		}
	}
	
	public static boolean makeLoginQuery(UserBean userBean) {
		boolean result = false;
		
		try {
			String requestQuery = "SELECT name FROM user "
					+ "WHERE email = ? AND password = ? ";

			stmt = conn.prepareStatement(requestQuery);
			stmt.setString(1, userBean.getEmail());
			stmt.setString(2, userBean.getPassword());
			
			resultSet = stmt.executeQuery();
			
			while(resultSet.next()) {
				userBean.setName(resultSet.getString(1));
				result = true;
			}
			
			conn.endRequest();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return result;
		
	}
}
