
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.PostBean" %>

<%
ArrayList<PostBean> postBeanList = (ArrayList<PostBean>) request.getAttribute("searchResults");
out.print("<div class='searchResultsContainer' id='searchResultsContainer'>");
out.print("<button type='button' id='closeSearchResultsButton'>Close</button>");
 if (postBeanList.size() > 0) {	 
	 out.print("<h1>Search results</h1>");

	 for (PostBean pb : postBeanList) {
		 out.print("<div class='card' style='width: 18rem;'>" 
					+ "<div class='card-body'>" 
					+ "<p class='card-text'>" 
					+ pb.getText() 
					+ "</p>"
					+ "<form action='SearchController' method='post'>" 
					+ "<button type='submit' name='search' value='" + pb.getTagName() + "'>"
					+ "#" + pb.getTagName()
					+ "</button>" 
					+ "</form>" 
					+ "</div>" 
					+ "</div>");
		}
	 

 } else {
	 out.print("<h2>No search results found</h2>");
 }
 out.print("</div>");
 
%>
