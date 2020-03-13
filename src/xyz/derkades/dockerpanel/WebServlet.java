package xyz.derkades.dockerpanel;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public class WebServlet extends HttpServlet {

	private static final long serialVersionUID = -7682997363243721686L;

	private void callApiMethod(String methodName, RequestType requestType, HttpServletRequest request, HttpServletResponse response) throws IOException {
		for (ApiMethod method : ApiMethod.METHODS) {
			if (method.getType() != requestType) {
				continue;
			}
			
			if (!method.getName().equals(methodName)) {
				continue;
			}
		
			Map<String, String> parameters = new HashMap<>();
			
			request.getParameterNames().asIterator().forEachRemaining((key) -> {
				parameters.put(key, request.getParameter(key));
			});
			
			try {
				method.call(parameters, response);
				System.out.println("API: " + requestType + " " + methodName);
			} catch (Exception e) {
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
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (uri.contains("..")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		if (uri.startsWith("/api")) {
			callApiMethod(uri.substring(5), RequestType.POST, request, response);
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		String uri = request.getRequestURI();
		if (uri.contains("..")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		if (uri.startsWith("/api")) {
			response.setContentType("text/json");
			callApiMethod(uri.substring(5), RequestType.GET, request, response);
		} else {
			if (uri.endsWith("/")) {
				uri += "index.html";
			}
				
			// Try to serve static file
			try (InputStream stream = App.class.getResourceAsStream("/web/" + uri)){
				if (stream == null) {
					response.getWriter().write("404 Not found");
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
				
				IOUtils.copy(stream, response.getOutputStream());
				response.setStatus(HttpServletResponse.SC_OK);
			}
		}
	}

}
