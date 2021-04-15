
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.PostBean" %>

<%
ArrayList<PostBean> postBeanList = null;
 if (request.getAttribute("searchResults") != null) {
	 postBeanList = (ArrayList<PostBean>) request.getAttribute("searchResults");
	 
	 out.print("<div class='searchResultsContainer' id='searchResultsContainer'>");
	 out.print("<h1>Search results</h1>");
	 out.print("<button type='button' id='closeSearchResultsButton'>Close search results</button>");

	 for (PostBean pb : postBeanList) {
		 out.print("<div class='card' style='width: 18rem;'><div class='card-body'><p class='card-text'>" 
		+ pb.getText() 
		+ "</p><h6 class='card-subtitle mb-2 text-muted'>#" 
		+ pb.getTagName() 
		+ "</h6></div></div>");
		}
	 out.print("</div>");

 }
%>
