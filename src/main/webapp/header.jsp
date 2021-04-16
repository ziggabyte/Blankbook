<header> <!-- Här har jag använt mig av en header från Bootstrap -->
  <nav class="navbar navbar-expand-md navbar-dark fixed-top">
    <div class="container-fluid">
      
      <a class="navbar-brand" href="#">blankbook</a>
      
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      
      <div class="collapse navbar-collapse" id="navbarCollapse">
        
        <ul class="navbar-nav me-auto mb-2 mb-md-0">
        <li class="nav-item">
            <p class="nav-link">Logged in as <% if (userBean != null) out.print(userBean.getName()); %></p>
        </li>
          <li class="nav-item">
          	<a href="<%=request.getContextPath()%>/LogoutController" id="logOutButton" class="nav-link" tabindex="-1">Log out</a>
          </li>
        </ul>
        
      <button class="toggle" id="toggle">Dark mode</button>
        
         <form class="d-flex" action="SearchController" method="post">
          <input class="form-control me-2" type="search" placeholder="Search" aria-label="Search" name="search" required>
          <button class="btn btn-outline-success" type="submit" id="searchButton">Search</button>
        </form>
         
      </div>
    </div>
  </nav>
</header>