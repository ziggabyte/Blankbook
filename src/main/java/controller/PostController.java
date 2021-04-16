package controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.DatabaseConnector;
import model.PostBean;

/**
 * Servlet implementation class PostController
 */
@WebServlet("/PostController")
public class PostController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PostController() {
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
		String text = request.getParameter("text");
		String tag = request.getParameter("tag");
		PostBean postBean = new PostBean(text, tag);		//Packar upp värden från requesten och skapar PostBean
		
		if (DatabaseConnector.openConnection("posts")) {	//Öppnar connection till databasen
			DatabaseConnector.addPostToDatabase(postBean);	//Lägger till posten i databasen
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("feed.jsp"); 
		rd.forward(request, response);						// Skickar användaren till feeden
	}

}
