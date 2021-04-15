package controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.DatabaseConnector;
import model.PostBean;
import model.SearchBean;

/**
 * Servlet implementation class SearchController
 */
@WebServlet("/SearchController")
public class SearchController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("index.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SearchBean searchBean = null;
		
		if (!request.getParameter("search").isBlank() && !request.getParameter("search").isEmpty()) {			
			searchBean = new SearchBean(request.getParameter("search"));	
		}
		
		ArrayList<PostBean> searchResults = getSearchResults(searchBean);
		request.setAttribute("searchResults", searchResults);
		
		RequestDispatcher rd = request.getRequestDispatcher("feed.jsp");
		rd.forward(request, response);
	}
	
	private ArrayList<PostBean> getSearchResults(SearchBean searchBean) {
		if (DatabaseConnector.openConnection("posts")) {
			return DatabaseConnector.makeSearchQuery(searchBean);
		}
		return null;
	}

}
