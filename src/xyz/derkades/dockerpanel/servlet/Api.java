package xyz.derkades.dockerpanel.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import xyz.derkades.dockerpanel.ApiMethod;

public class Api extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		if (System.getenv("PASSWORD") != null) {
			final HttpSession session = request.getSession();

			if (session.getAttribute("password") == null ||
					!session.getAttribute("password").equals(System.getenv("PASSWORD"))) {
				response.getWriter().print("Unauthorized");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}

		final String methodName = request.getRequestURI().substring(5);

		for (final ApiMethod method : ApiMethod.METHODS) {
			if (!method.getName().equals(methodName)) {
				continue;
			}

			final Map<String, String> parameters = new HashMap<>();

			request.getParameterNames().asIterator().forEachRemaining((key) -> {
				parameters.put(key, request.getParameter(key));
			});

			try {
				method.call(parameters, response);
			} catch (final Exception e) {
				System.err.println("Error occured in API request");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				e.printStackTrace();
			}
			return;
		}

		System.err.println("API method not found: " + methodName);
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.getWriter().write("Invalid API method");
	}

}
