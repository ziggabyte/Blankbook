package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
	
	public static ArrayList<PostBean> makePostQuery(){
		ArrayList<PostBean> postBeanList = new ArrayList<>();
		
		try {
			String requestQuery = 
					"SELECT post.text, tag.tagname FROM post INNER JOIN tag ON post.Tag_ID = tag.Tag_ID ORDER BY post.Post_ID DESC";
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
			
			//Finns sökfrasen bland taggar
			if (openConnection("posts")) {
				if (isSearchPhraseInTag(searchBean)) {
					if (openConnection("posts")) {
						searchResults.addAll(getPostsWithSearchPhraseInTag(searchBean)) ;
					}
				}
			}
			
			//Finns sökfrasen bland text och kolla så inga dubletter läggs till
			if (openConnection("posts")) {
				if (isSearchPhraseInPost(searchBean)) {
					if (openConnection("posts")) {
						ArrayList<PostBean> checkForDuplicates = getPostsWithSearchPhraseInText(searchBean);
						if (searchResults.size() > 0) {
							for (int i = 0; i < searchResults.size(); i ++) {
								for (int j = 0; j < checkForDuplicates.size(); j++) {
									if (searchResults.get(i).getPostId().equals(checkForDuplicates.get(j).getPostId())) {
										checkForDuplicates.remove(j);
									}
								}
							}	
						}
						searchResults.addAll(checkForDuplicates);
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
	
	private static ArrayList<PostBean> getPostsWithSearchPhraseInTag(SearchBean searchBean){
		ArrayList<PostBean> searchResults = new ArrayList<>();
		try {
			String requestQuery = 
					"SELECT post.text, tag.tagname, post.Post_ID FROM post INNER JOIN tag ON post.Tag_ID = tag.Tag_ID WHERE tag.Tagname LIKE ? ORDER BY post.Post_ID DESC";
			stmt = conn.prepareStatement(requestQuery);
			stmt.setString(1, "%" + searchBean.getSearch() + "%");			
			resultSet = stmt.executeQuery();
			
			while(resultSet.next()) {
				searchResults.add(new PostBean(resultSet.getString(1), resultSet.getString(2), String.valueOf(resultSet.getInt(3))));
			}

			closeConnection();
		} catch (SQLException e) {
			System.out.println("Från catch i get posts with search phrase");
			handleSqlError(e);
		}
		
		return searchResults;
	}
	
	private static ArrayList<PostBean> getPostsWithSearchPhraseInText(SearchBean searchBean){
		ArrayList<PostBean> searchResults = new ArrayList<>();
		try {
			String requestQuery = 
					"SELECT post.text, tag.tagname, post.Post_ID FROM post INNER JOIN tag ON post.Tag_ID = tag.Tag_ID WHERE post.Text LIKE ? ORDER BY post.Post_ID DESC";
			stmt = conn.prepareStatement(requestQuery);
			stmt.setString(1, "%" + searchBean.getSearch() + "%");			
			resultSet = stmt.executeQuery();
			
			while(resultSet.next()) {
				searchResults.add(new PostBean(resultSet.getString(1), resultSet.getString(2), String.valueOf(resultSet.getInt(3))));
			}

			closeConnection();
		} catch (SQLException e) {
			System.out.println("Från catch i get posts with search phrase");
			handleSqlError(e);
		}
		
		return searchResults;
	}
	
	private static boolean isSearchPhraseInPost(SearchBean searchBean) {
		boolean isInDatabase = false;
		try {
			String requestQuery = 
					"SELECT COUNT(*) FROM post INNER JOIN tag ON post.Tag_ID = tag.Tag_ID WHERE post.Text LIKE ?";
			stmt = conn.prepareStatement(requestQuery);
			stmt.setString(1, "%" + searchBean.getSearch() + "%");						
			resultSet = stmt.executeQuery();
			
			while(resultSet.next()) {
				if (Integer.valueOf(resultSet.getString(1)) >= 1) {
					isInDatabase = true;
					}
				}
			closeConnection();
			} catch (SQLException e) {
				System.out.println("från catch i  is search phrase in database");
				handleSqlError(e);
			}
		return isInDatabase;
	}
	
	private static boolean isSearchPhraseInTag(SearchBean searchBean) {
		boolean isInDatabase = false;
		try {
			String requestQuery = 
					"SELECT COUNT(*) FROM post INNER JOIN tag ON post.Tag_ID = tag.Tag_ID WHERE tag.Tagname LIKE ?";
			stmt = conn.prepareStatement(requestQuery);
			stmt.setString(1, "%" + searchBean.getSearch() + "%");			
			resultSet = stmt.executeQuery();
			
			while(resultSet.next()) {
				if (Integer.valueOf(resultSet.getString(1)) >= 1) {
					isInDatabase = true;
					}
				}
			closeConnection();
			} catch (SQLException e) {
				System.out.println("från catch i  is search phrase in database");
				handleSqlError(e);
			}
		return isInDatabase;
	}
}
