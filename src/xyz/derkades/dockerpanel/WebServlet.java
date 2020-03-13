package xyz.derkades.dockerpanel;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;

public class WebServlet extends HttpServlet {

	private static final long serialVersionUID = -7682997363243721686L;

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		
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
			response.getWriter().println("{\"message\":\"Hello!\"}");
		} else {
			if (uri.endsWith("/")) {
				uri += "index.html";
			}
				
			// Try to serve static file
			try (InputStream stream = App.class.getResourceAsStream("/web/" + uri)){
				if (stream == null) {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
				
				IOUtils.copy(stream, response.getOutputStream());
				response.setStatus(HttpServletResponse.SC_OK);
			}
			
		}
		
	}

}
