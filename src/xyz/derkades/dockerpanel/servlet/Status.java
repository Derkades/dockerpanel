package xyz.derkades.dockerpanel.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xyz.derkades.dockerpanel.App;

public class Status extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		try {
			App.docker().infoCmd().exec();
			response.getWriter().println("up");
		} catch (Exception e) {
			response.getWriter().println("can't connect to docker");
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
	}

}
