<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="model.UserBean" %>
<% UserBean userBean = (UserBean) session.getAttribute("userBean"); %>
<%
if (session.getAttribute("userBean") == null) {
	RequestDispatcher rd = request.getRequestDispatcher("LoginController");
	rd.forward(request, response);
}
%>
<!DOCTYPE html>
<html>
<%@ include file="head.jsp" %>
<body>
<%@ include file="newPostHeader.jsp" %>
<%@ include file="newPostMain.jsp" %>
<%@ include file="newPostFooter.jsp" %>
</body>
</html>