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
	
	private static void checkIfTagExists(PostBean postBean) {		
		try {
			String requestQuery = "SELECT COUNT(Tag_ID) FROM tag WHERE tagname = ?";
			stmt = conn.prepareStatement(requestQuery);
			stmt.setString(1, postBean.getTagName());
			
			stmt.executeQuery();
			resultSet = stmt.executeQuery();
			
			if (resultSet.next()) {
				if (resultSet.getInt(1) == 1) {
					postBean.setIsInDatabase(true);
				}
			}

			closeConnection();
		} catch (SQLException e) {
			System.out.println("Från catch i check if tag exists");
			handleSqlError(e);
		}
	}
	
	private static void getTagIdFromDatabase(PostBean postBean) {
		try {
			//HÄMTAR ID FRÅN DEN TAG SOM FINNS I POSTBEAN
			String requestQuery =  "SELECT DISTINCT `Tag_ID` FROM `tag` WHERE `Tagname` = ?";
			stmt = conn.prepareStatement(requestQuery);
			stmt.setString(1, postBean.getTagName());
			resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				postBean.setTagId(String.valueOf(resultSet.getInt(1))); 
			}
			closeConnection();
		} catch (SQLException e) {
			System.out.println("Från catch i add tag to database");
			handleSqlError(e);
		}
	}
	
	private static void addTagToDatabase(PostBean postBean) {
		try {
			// LÄGG TILL EN RAD I TAG MED DET TAGNAME SOM FINNS I POSTBEAN
			String requestQuery = "INSERT INTO `tag` (`Tagname`) VALUES (?)";
			stmt = conn.prepareStatement(requestQuery);
			stmt.setString(1, postBean.getTagName());
			stmt.executeUpdate();
			
			closeConnection();
		} catch (SQLException e) {
			System.out.println("Från catch i add tag to database");
			handleSqlError(e);
		}
	}
	
	public static void addPostToDatabase(PostBean postBean) {
		checkIfTagExists(postBean); 
		
		if(openConnection("posts")) {
			if (!postBean.isInDatabase()) {
				if (openConnection("posts")) {
					addTagToDatabase(postBean);
				} 
			}
		} 
		
		if (openConnection("posts")) {
			getTagIdFromDatabase(postBean);
		}
		
		if (openConnection("posts")) {
			try {
				String requestQuery = "INSERT INTO `post` (`Text`, `Tag_ID`) VALUES (?, ?)";
				stmt = conn.prepareStatement(requestQuery);
				stmt.setString(1, postBean.getText());
				stmt.setString(2, postBean.getTagId());

				stmt.executeUpdate();
				closeConnection();
				
			} catch (SQLException e) {
				System.out.println("Från catch i add post to database");
				handleSqlError(e);
			}
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

	public static ArrayList<PostBean> makeSearchQuery(SearchBean searchBean) {
		ArrayList<PostBean> searchResults = new ArrayList<>();
		try {
			//Finns sökfrasen bland text
			if (openConnection("posts")) {
				if (isSearchPhraseInDatabase(searchBean, "text")) {
					if (openConnection("posts")) {
						searchResults.addAll(getPostsWithSearchPhrase(searchBean, "text"));
					}
				}
			}
			
			//Finns sökfrasen bland taggar
			if (openConnection("posts")) {
				if (isSearchPhraseInDatabase(searchBean, "tagname")) {
					if (openConnection("posts")) {
						searchResults.addAll(getPostsWithSearchPhrase(searchBean, "tagname")) ;
					}
				}
			}

			closeConnection();
		} catch (SQLException e) {
			System.out.println("Från catch i Make search query");
			handleSqlError(e);
		}
		
		return searchResults;
	}
	
	private static ArrayList<PostBean> getPostsWithSearchPhrase(SearchBean searchBean, String column ){
		ArrayList<PostBean> searchResults = new ArrayList<>();
		try {
			String requestQuery = 
					"SELECT p.text, t.tagname FROM post p INNER JOIN tag t ON p.Tag_ID = t.Tag_ID WHERE ? LIKE ?";
			stmt = conn.prepareStatement(requestQuery);
			stmt.setString(1, column);
			stmt.setString(2, "%" + searchBean.getSearch() + "%");			
			
			resultSet = stmt.executeQuery();
			
			while(resultSet.next()) {
				searchResults.add(new PostBean(resultSet.getString(1), resultSet.getString(2)));
			}

			closeConnection();
		} catch (SQLException e) {
			System.out.println("Från catch i get posts with search phrase");
			handleSqlError(e);
		}
		
		return searchResults;
	}
	
	private static boolean isSearchPhraseInDatabase(SearchBean searchBean, String column) {
		boolean isInDatabase = false;
		try {
			String requestQuery = 
					"SELECT COUNT(*) FROM post p INNER JOIN tag t ON p.Tag_ID = t.Tag_ID WHERE ? LIKE ?";
			stmt = conn.prepareStatement(requestQuery);
			stmt.setString(1, column);
			stmt.setString(2, "%" + searchBean.getSearch() + "%");			
			
			resultSet = stmt.executeQuery();
			
			while(resultSet.next()) {
				if (Integer.valueOf(resultSet.getString(1)) >= 1) {
					isInDatabase = true;
					}
				}
			closeConnection();
			} catch (SQLException e) {
				System.out.println("från is search phrase in database");
				handleSqlError(e);
			}
		
		return isInDatabase;
	}
}
