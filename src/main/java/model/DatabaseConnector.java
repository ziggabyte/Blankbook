package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
			handleSqlError(e);
			return false;		}
	}
	
	private static String checkIfTagExists(String tag) {
		String tagId = null;
		try {
			String requestQuery = "SELECT Tag_ID FROM tag WHERE tagname = ?";
			stmt = conn.prepareStatement(requestQuery);
			stmt.setString(1, tag);
			stmt.executeQuery();
			resultSet = stmt.executeQuery();

			while(resultSet.next()) {
				tagId = String.valueOf(resultSet.getInt(1));
			}

			closeConnection();
		} catch (SQLException e) {
			System.out.println("Från check if tag exists");
			handleSqlError(e);
		}
		return tagId;
	}
	
	private static void addTagToDatabase(PostBean postBean) { /
		String tagId = checkIfTagExists(postBean.getTag());

		if (tagId == null) {
			try {
				String requestQuery = "INSERT INTO `tag` (`Tagname`) VALUES (?)";
				stmt = conn.prepareStatement(requestQuery);
				stmt.setString(1, postBean.getTag());
				stmt.executeUpdate();
				
				requestQuery =  "SELECT DISTINCT `Tag_ID` FROM `tag` WHERE `Tagname` = ?";
				stmt = conn.prepareStatement(requestQuery);
				stmt.setString(1, postBean.getTag());

				resultSet = stmt.executeQuery();
				
				while (resultSet.next()) {
					System.out.println("från while i add tag to database: " + String.valueOf(resultSet.getInt(1)));
					tagId = String.valueOf(resultSet.getInt(1)); /// HUR SKA JAG SPARA DETTA VÄRDET NÅNSTANS?
				}
				closeConnection(); // KAN INTE RETURNERA NÅGOT EFTER DENNA???? 
			} catch (SQLException e) {
				System.out.println("Från catch i add tag to database");
				handleSqlError(e);
			}
			
		} 
	}
	
	public static void addPostToDatabase(PostBean postBean) {
		try {
			String requestQuery = "INSERT INTO `post` (`Text`, `Tag_ID`) VALUES (?, ?)";
			stmt = conn.prepareStatement(requestQuery);
			stmt.setString(1, postBean.getText());
			stmt.setString(2, tagId); // HUR FAN SKA JAG FÅ TAG I TAG ID HÄR????

			stmt.executeUpdate();
			
			closeConnection();
			
		} catch (SQLException e) {
			System.out.println("Från catch i add post to database");
			handleSqlError(e);
		}
	}
	
	public static List<PostBean> makePostQuery(){
		List<PostBean> postBeanList = new ArrayList<>();
		
		try {
			String requestQuery = 
					"SELECT p.text, t.tagname "
					+ "FROM post p "
					+ "INNER JOIN tag t "
					+ "ON p.Tag_ID = t.Tag_ID";
			stmt = conn.prepareStatement(requestQuery);
			resultSet = stmt.executeQuery();
			
			while(resultSet.next()) {
				postBeanList.add(new PostBean(resultSet.getString(1), resultSet.getString(2)));
			}
			closeConnection();
		} catch (SQLException e) {
			handleSqlError(e);
		}
		
		return postBeanList;
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
			closeConnection();
			return result;
			
		} catch (SQLException e) {
			handleSqlError(e);
		}
		return result;
	}
	
	private static void handleSqlError(SQLException e) {
		System.out.println("SQLException: " + e.getMessage());
		System.out.println("SQLState: " + e.getSQLState());
		System.out.println("VendorError: " + e.getErrorCode());
	}
	
	private static void closeConnection() throws SQLException {
		conn.endRequest();
		conn.close();
	}
}
