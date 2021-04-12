<header>
<p>Logged in as <% if (userBean != null) out.print(userBean.getName()); %></p>
<img src="" alt="logo">
<form action="searchController" method="post">
<input type="text" name="search" >
<button type="submit"><img src="" alt="search"></button>
</form>
<button class="toggle" id="toggle">Dark mode</button>
</header>