<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>    
<%@ page import="model.UserBean"  %>
<%@ page import="model.PostBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.DatabaseConnector" %>
<% UserBean userBean = (UserBean) session.getAttribute("userBean"); %>
<%
if (session.getAttribute("userBean") == null) {
	RequestDispatcher rd = request.getRequestDispatcher("LoginController");
	rd.forward(request, response);
}
%>
<!DOCTYPE html>
<html>
<%@include file="head.jsp"%>
<body>
<%@include file="header.jsp"%>
<main>
<%@ include file="newPost.jsp" %>

<% if (request.getAttribute("searchResults") != null) {%> <!-- Kollar ifall det finns sökresultat och inkluderar i så fall -->
<jsp:include page="searchResults.jsp"/>
<% } %>

<%@include file="feedMain.jsp"%>
</main>
<%@include file="footer.jsp"%>
</body>
</html>