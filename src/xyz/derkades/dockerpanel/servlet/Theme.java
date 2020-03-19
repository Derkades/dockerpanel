package xyz.derkades.dockerpanel.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xyz.derkades.dockerpanel.App;

public class Theme extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		String theme = null;

		if (request.getCookies() != null) {
			for (final Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals("theme")) {
					theme = cookie.getValue();
					break;
				}
			}
		}

		if (theme == null) {
			// Theme cookie is not set, fall back to environment variable setting
			theme = App.getTheme();
		} else {
			// User theme is set, check if it's valid
			try (InputStream stream = App.class.getResourceAsStream("/themes/" + theme + ".css")) {
				if (stream == null) {
					// User set theme is invalid. Clear user cookie
					final Cookie cookie = new Cookie("theme", null);
					cookie.setMaxAge(0);
					response.addCookie(cookie);

					// Use global theme setting
					theme = App.getTheme();
				}
			}
		}

		App.writeResource(response, "/themes/" + theme + ".css");
	}

}
