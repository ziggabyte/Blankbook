<main class="main">
<% 
List<PostBean> postBeanList = null;

if (DatabaseConnector.openConnection("posts")) {
	postBeanList = DatabaseConnector.makePostQuery();
}

if (postBeanList != null){
	for (PostBean pb : postBeanList) {
		out.print("<div class='post'><p class='postText'>" 
	+ pb.getText() 
	+ "</p><p class='postTag'>#" 
	+ pb.getTag() 
	+ "</p></div>");
	}
}

%>

</main>