<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.PostBean" %>
<!DOCTYPE html>
<html>
<%@include file="head.jsp"%>
<body>
<h1>Search results</h1>

<%
ArrayList<PostBean> postBeanList = null;

System.out.println(request.getAttribute("searchResults"));

 if (request.getAttribute("searchResults") != null) {
	 
	 System.out.println("testing 1");
	 
	 postBeanList = (ArrayList<PostBean>) request.getAttribute("searchResults");
	 System.out.println(postBeanList.size());
	 
	 System.out.println("testing 2");

	 for (PostBean pb : postBeanList) {
		 System.out.println(pb.getText() + " " + pb.getTagName());
			// out.print("<div class='card' style='width: 18rem;'><div class='card-body'><p class='card-text'>" 
		//+ pb.getText() 
		//+ "</p><h6 class='card-subtitle mb-2 text-muted'>#" 
		//+ pb.getTagName() 
		//+ "</h6></div></div>");
		}
	 
	 System.out.println("testing 3");

 }
%>


</body>
</html>