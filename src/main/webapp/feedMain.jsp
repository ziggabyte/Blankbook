<div class="feedContainer">
<%
ArrayList<PostBean> postBeanList = null;

if (DatabaseConnector.openConnection("posts")) {
	postBeanList = DatabaseConnector.makePostQuery();
}

if (postBeanList != null){
	for (PostBean pb : postBeanList) {
		out.print("<div class='card' style='width: 18rem;'><div class='card-body'><p class='card-text'>" 
	+ pb.getText() 
	+ "</p><h6 class='card-subtitle mb-2 text-muted'>#" 
	+ pb.getTagName() 
	+ "</h6></div></div>");
	}
}
%>

</div>
