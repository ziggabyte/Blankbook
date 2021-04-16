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
	
	public static boolean makeLoginQuery(UserBean userBean) { //Kollar ifall det finns en användare i db som stämmer med det som skrivits in
		boolean isInDatabase = false;
		try {
			String requestQuery = "SELECT name FROM user "
					+ "WHERE email = ? AND password = ? ";
			
			stmt = conn.prepareStatement(requestQuery);
			stmt.setString(1, userBean.getEmail());
			stmt.setString(2, userBean.getPassword());
			
			resultSet = stmt.executeQuery();
			while(resultSet.next()) {
				userBean.setName(resultSet.getString(1));
				isInDatabase = true;
			}
			closeConnection();			
		} catch (SQLException e) {
			handleSqlError(e);
		}
		return isInDatabase;
	}
	
	public static ArrayList<PostBean> makeQueryForAllPosts(){ //Hämtar alla poster från databasen för att lägga i feeden
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
	
	public static void addPostToDatabase(PostBean postBean) { // Lägger till en post i databasen
		if(openConnection("posts")) {
			if (!isTagInDatabase(postBean)) { //Kollar först ifall postens tagg finns i databasen
				if (openConnection("posts")) {
					addTagToDatabase(postBean); //Om inte: lägger till taggen i databasen
				} 
			}
		} 
		
		if (openConnection("posts")) {
			getTagIdFromDatabase(postBean); //Hämtar taggens id (för att kunna använda som referensnyckel i postens rad i databasen)
		}
		
		if (openConnection("posts")) {
			try {
				String requestQuery = "INSERT INTO `post` (`Text`, `Tag_ID`) VALUES (?, ?)"; //Lägger till posten i databasen
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
	
	private static boolean isTagInDatabase(PostBean postBean) { // Hjälpmetod till addPostToDatabase, kollar om en tagg finns i db
		boolean isInDatabase = false;
		try {
			String requestQuery = "SELECT COUNT(Tag_ID) FROM tag WHERE tagname = ?";
			stmt = conn.prepareStatement(requestQuery);
			stmt.setString(1, postBean.getTagName());
			
			stmt.executeQuery();
			resultSet = stmt.executeQuery();
			
			if (resultSet.next()) {
				if (resultSet.getInt(1) == 1) {
					isInDatabase = true;
				}
			}
			closeConnection();
		} catch (SQLException e) {
			System.out.println("Från catch i check if tag exists");
			handleSqlError(e);
		}
		return isInDatabase;
	}
	
	private static void getTagIdFromDatabase(PostBean postBean) { //Hjälpmetod till addPostToDatabase, hämtar ett Tag_ID från databasen och lägger till i en PostBean
		try {
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
	
	private static void addTagToDatabase(PostBean postBean) { // Hjälpmetod till addPostToDatabase, lägger till en tagg i databasen
		try {
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

	public static ArrayList<PostBean> makeSearchQuery(SearchBean searchBean) { //Gör en sökning i databasen
		ArrayList<PostBean> searchResults = new ArrayList<>();
		try {
			if (isSearchPhraseInTag(searchBean)) { //Kollar om sökfrasen finns bland taggar
				if (openConnection("posts")) {
					searchResults.addAll(getPostsWithSearchPhraseInTag(searchBean)) ;
				}
			}
			
			if (openConnection("posts")) { // Kollar om sökfrasen finns bland text
				if (isSearchPhraseInText(searchBean)) {
					if (openConnection("posts")) {
						searchResults.addAll(checkForDuplicates(searchResults, searchBean)); //Kollar så inga dubletter läggs i listan
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
	
	private static ArrayList<PostBean> checkForDuplicates(ArrayList<PostBean> searchResults, SearchBean searchBean) { //Hjälpmetod till makeSearchQuery, kollar efter dubletter i en arrayList
		ArrayList<PostBean> checkedForDuplicates = getPostsWithSearchPhraseInText(searchBean);
		if (searchResults.size() > 0) {
			for (int i = 0; i < searchResults.size(); i ++) {
				for (int j = 0; j < checkedForDuplicates.size(); j++) {
					if (searchResults.get(i).getPostId().equals(checkedForDuplicates.get(j).getPostId())) {
						checkedForDuplicates.remove(j);
					}
				}
			}	
		}
		return checkedForDuplicates;
	}
	
	private static ArrayList<PostBean> getPostsWithSearchPhraseInTag(SearchBean searchBean){ //Hjälpmetod till makeSearchQuerty, skapar en lista av PostBeans vars tagg matchar en sökning
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
	
	private static ArrayList<PostBean> getPostsWithSearchPhraseInText(SearchBean searchBean){ //Hjälpmetod till makeSearchQuery, skapar en lista av PostBeans vars text matchar en sökning
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
	
	private static boolean isSearchPhraseInText(SearchBean searchBean) { //Hjälpmetod till makeSearchQuery, kollar ifall en sökning finns i någon posts text
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
	
	private static boolean isSearchPhraseInTag(SearchBean searchBean) { //Hjälpmetod till makeSearchQuery, kollar ifall en sökning finns i någon tagg
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
	
	public static boolean openConnection(String databaseName) { //öppna en tråd till angiven databas
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
			System.out.println("Från open connection");
			handleSqlError(e);
			return false;		}
	}
	
	private static void handleSqlError(SQLException e) { //Hjälpmetod för att slippa upprepa denna kodbit i catch
		System.out.println("SQLException: " + e.getMessage());
		System.out.println("SQLState: " + e.getSQLState());
		System.out.println("VendorError: " + e.getErrorCode());
	}
	
	private static void closeConnection() throws SQLException { //Hjälpmetod för att slippa upprepa denna kodbit vid stängning
		conn.endRequest();
		conn.close();
	}

}
