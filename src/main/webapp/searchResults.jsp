
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.PostBean" %>

<%
ArrayList<PostBean> postBeanList = (ArrayList<PostBean>) request.getAttribute("searchResults"); //H�mtar s�kresultat som sitter som ett attribut i requesten
out.print("<div class='searchResultsContainer' id='searchResultsContainer'>");
 if (postBeanList.size() > 0) {	 
	 out.print("<h1>Search results</h1>");

	 for (PostBean pb : postBeanList) { //Ger varje s�kresultat en div, och en knapp med tagg ifall det finns tagg - vid klick s�ker den p� v�rdet i taggen. 
			out.print("<div class='my-card'><div class='card-body'><p class='card-text'>" + pb.getText() + "</p>");
			 if (!pb.getTagName().equals("")){
				 out.print("<form action='SearchController' method='post'><button type='submit' name='search' value='" 
			 	+ pb.getTagName() + "'>#" + pb.getTagName() + "</button></form>");
			 }
			out.print("</div></div>");
		}

 } else {
	 out.print("<h2>No search results found</h2>"); // Om det inte fanns s�kresultat ges detta felmeddelande
 }
 out.print("<button type='button' id='closeSearchResultsButton'>Close</button>"); //En knapp som st�nger s�kresultaten med javascript
 out.print("</div>");
 
%>
