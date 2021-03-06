<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<jsp:include page="head.jsp"/>

<body class="text-center">
    
<main class="form-signin">
  <form action="<%=request.getContextPath()%>/LoginController" method="post">
    <h1 class="h3 mb-3 fw-normal">blankbook</h1>
    
    <%
if (request.getParameter("login") != null) { // Kollar ifall det finns parameter i url:en, isåfall lägger till felmeddelande
%>
<jsp:include page="loginError.jsp"/>
<%
}
%>
    <div class="form-floating">
      <input type="email" class="form-control" id="floatingInput" placeholder="name@example.com" name="email" required>
      <label for="floatingInput">Email address</label>
    </div>
    <div class="form-floating">
      <input type="password" class="form-control" id="floatingPassword" placeholder="Password" name="password" required>
      <label for="floatingPassword">Password</label>
    </div>  
<%
if (request.getCookies().length <= 1) { // Kollar ifall det redan finns cookies, om inte så får man cookie-frågan
%>
<jsp:include page="cookieQuestion.jsp"/>
<%
}
%>
<button class="w-100 btn btn-lg btn-primary" type="submit">Sign in</button>
</form>
<p class="mt-5 mb-3 text-muted">&copy; 2021</p>
</main>
</body>
</html>