<div class="feedContainer">
<h1>Feed</h1>

<%
ArrayList<PostBean> postBeanList = null;

if (DatabaseConnector.openConnection("posts")) {
	postBeanList = DatabaseConnector.makePostQuery();
}

if (postBeanList != null){
	for (PostBean pb : postBeanList) {
		out.print(
	"<div class='card' style='width: 18rem;'>" 
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
}
%>

</div>
