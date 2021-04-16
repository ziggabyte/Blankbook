<div class="feedContainer">

<%
ArrayList<PostBean> postBeanList = null;

if (DatabaseConnector.openConnection("posts")) {
	postBeanList = DatabaseConnector.makeQueryForAllPosts();
}

if (postBeanList != null){
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
