<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<jsp:include page="head.jsp"/>
<body>

<h1>Log in</h1>

<%
if (request.getParameter("login") != null) {
%>
<jsp:include page="loginError.jsp"/>
<%
}
%>

<form action="<%=request.getContextPath()%>/LoginController" method="post">
<label for="email">Email:</label>
<input type="email" name="email" required>
<label for="password">Password:</label>
<input type="password" name="password" required>
<label for="cookies">Can we save cookies?</label>
<label for="yes">Yes</label>
<input type="radio" name="cookies" value="yes" id="yes">
<label for="no">No</label>
<input type="radio" name="cookies" value="no" id="no" >
<input type="submit" value="Log in">
</form>

</body>
</html>