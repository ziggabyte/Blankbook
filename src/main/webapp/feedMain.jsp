<div class="feedContainer">

<%
ArrayList<PostBean> postBeanList = null;

if (DatabaseConnector.openConnection("posts")) { // Öppnar koppling till databasen och hämtar alla poster som finns
	postBeanList = DatabaseConnector.makeQueryForAllPosts();
}

if (postBeanList != null){ // Ger varje post en div, och om det finns tag: en knapp som vid klick gör en sökning på värdet i taggen
	for (PostBean pb : postBeanList) {
		out.print("<div class='my-card'><div class='card-body'><p class='card-text'>" + pb.getText() + "</p>");
	 if (!pb.getTagName().equals("")){ 
		 out.print("<form action='SearchController' method='post'><button type='submit' name='search' value='" 
	 	+ pb.getTagName() + "'>#" + pb.getTagName() + "</button></form>");
	 }
	out.print("</div></div>");
	}
}
%>

</div>
