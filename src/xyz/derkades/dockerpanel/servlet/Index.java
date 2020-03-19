package xyz.derkades.dockerpanel.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import xyz.derkades.dockerpanel.App;

public class Index extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		if (System.getenv("PASSWORD") != null) {
			final HttpSession session = request.getSession();

			if (session.getAttribute("password") == null ||
					!session.getAttribute("password").equals(System.getenv("PASSWORD"))) {
				response.setHeader("Location", "/login");
				response.setStatus(HttpServletResponse.SC_SEE_OTHER);
				return;
			}
		}

		final String uri = request.getRequestURI();

		if (uri.equals("/")) {
			App.writeResource(response, "/web/index.html");
		} else if (uri.equals("/script.js") ||
				uri.equals("/styles.css") ||
				uri.equals("/icons/send-white.svg")) {
			App.writeResource(response, "/web" + uri);
		} else {
			response.getWriter().println("not found");
		}
	}

}
