<div class="feedContainer">

<%
ArrayList<PostBean> postBeanList = null;

if (DatabaseConnector.openConnection("posts")) { // �ppnar koppling till databasen och h�mtar alla poster som finns
	postBeanList = DatabaseConnector.makeQueryForAllPosts();
}

if (postBeanList != null){ // Ger varje post en div, och om det finns tag: en knapp som vid klick g�r en s�kning p� v�rdet i taggen
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
