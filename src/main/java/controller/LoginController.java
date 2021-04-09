package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.DatabaseConnector;
import model.UserBean;

/**
 * Servlet implementation class LoginController
 */
@WebServlet("/LoginController")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserBean userBean = new UserBean(
				request.getParameter("email"), 
				request.getParameter("password") );

		if (validateLogin(userBean)) {			
			HttpSession session = request.getSession();
			session.setMaxInactiveInterval(600);
			session.setAttribute("userBean", userBean);
			request.setAttribute("userBean", userBean);
			
			//nåt med cookies här som jag inte kommer på nu:
			// kolla om det redan finns en cookie
			// om det inte finns  - kolla om svaret för consent är ja
			// isåfall sätt en ny cookie
			
			RequestDispatcher rd = request.getRequestDispatcher("feed.jsp");
			rd.forward(request, response);
		} else {
			response.sendRedirect("index.jsp?login=fail");
		}			
	}
		
	private boolean validateLogin(UserBean userBean) {
		if (DatabaseConnector.openConnection("users")) {
			return DatabaseConnector.makeLoginQuery(userBean);
		}
		return false;
	}
	
}
