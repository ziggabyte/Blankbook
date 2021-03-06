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
		UserBean userBean = new UserBean(request.getParameter("email"), request.getParameter("password") );
		
		if (hasCorrectLogin(userBean)) {				// kollar inloggningsuppgifter		
			setBeanAsAttribute(request, userBean);		// sätter UserBean som attribut till requesten
			if (hasAsweredCookieQuestion(request)) { 	// Kollar ifall cookie-frågan besvarats vid inloggning
				if (isConsentingToCookies(request)) { 	// Om sagt ja till cookies
					setConsentCookie(response, "yes"); 	// Spara svaret och sätt cookie för tema
					setThemeCookie(response);
				}
				else {
					setConsentCookie(response, "no"); 	// spara svaret
				}
			}
			forwardToFeed(request,response); 			// skicka vidare till feed
		} else {
			response.sendRedirect("index.jsp?login=fail"); // skicka tillbaka till inlogg med attribut som används för att printa felmeddelande
		}			
	}
	
	//Här nedan är hjälpmetoder för att öka läsbarheten i koden

	private void forwardToFeed(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		RequestDispatcher rd = request.getRequestDispatcher("feed.jsp");
		rd.forward(request, response);
	}
	
	private void setBeanAsAttribute(HttpServletRequest request, UserBean userBean) {
		HttpSession session = request.getSession();
		session.setMaxInactiveInterval(600);
		session.setAttribute("userBean", userBean);
		request.setAttribute("userBean", userBean);
	}
	
	private void setConsentCookie(HttpServletResponse response, String value) {
		Cookie cookie = new Cookie("wantsCookies", value);
		cookie.setMaxAge(1000);
		response.addCookie(cookie);
	}
	
	private void setThemeCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie("theme", "light");
		cookie.setMaxAge(1000);
		response.addCookie(cookie);
	}
	
	private boolean isConsentingToCookies(HttpServletRequest request) {
		return (request.getParameter("cookies")).equals("yes") ? true : false;
	}
		
	private boolean hasAsweredCookieQuestion(HttpServletRequest request) {
		return request.getParameter("cookies") != null ? true : false;
	}
	
	private boolean hasCorrectLogin(UserBean userBean) {
		if (DatabaseConnector.openConnection("users")) {
			return DatabaseConnector.makeLoginQuery(userBean);
		}
		return false;
	}
	
}
