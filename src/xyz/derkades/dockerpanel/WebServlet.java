package xyz.derkades.dockerpanel;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public class WebServlet extends HttpServlet {

	private static final long serialVersionUID = -7682997363243721686L;

	private void callApiMethod(final String methodName, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
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
//				System.out.println("API: " + methodName);
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

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		System.out.println("GET " + request.getRequestURI());

		String uri = request.getRequestURI();
		if (uri.contains("..")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (uri.startsWith("/api")) {
			callApiMethod(uri.substring(5), request, response);
		} else if (uri.equals("/theme.css")) {
			try (InputStream stream = App.class.getResourceAsStream("/themes/" + App.getTheme() + ".css")){
				IOUtils.copy(stream, response.getOutputStream());
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("text/css");
			};
		} else {
			if (uri.endsWith("/")) {
				uri += "index.html";
			}

			// Try to serve static file
			try (InputStream stream = App.class.getResourceAsStream("/web" + uri)){
				if (stream == null) {
					response.getWriter().write("404 Not found: " + "/web" + uri);
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					return;
				}

				IOUtils.copy(stream, response.getOutputStream());
				response.setStatus(HttpServletResponse.SC_OK);
			}

			if (uri.endsWith(".js")) {
				response.setContentType("application/javascript");
			} else if (uri.endsWith(".html")){
				response.setContentType("text/html");
			} else if (uri.endsWith(".css")){
				response.setContentType("text/css");
			} else if (uri.endsWith(".svg")) {
				response.setContentType("image/svg+xml");
			}
		}
	}

}
