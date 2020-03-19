package xyz.derkades.dockerpanel.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import xyz.derkades.dockerpanel.App;

public class Login extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		App.writeResource(response, "/web/login.html");
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final String password = request.getParameter("password");

		if (password != null && password.equals(System.getenv("PASSWORD"))) {
			final HttpSession session = request.getSession();
			session.setAttribute("password", password);
			response.setHeader("Location", "/");
			response.setStatus(HttpServletResponse.SC_SEE_OTHER);
			response.getWriter().println("Successful login from " + request.getRemoteAddr());
		} else {
			response.getWriter().println("Login with invalid password denied from " + request.getRemoteAddr());
			response.getWriter().println("Invalid password");
		}
	}

}
