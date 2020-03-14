package xyz.derkades.dockerpanel.api;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.amihaiemil.docker.Container;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;

public class StartContainer extends ApiMethod {
	
	public StartContainer() {
		super("start_container");
	}

	@Override
	public void call(Map<String, String> parameters, HttpServletResponse response) throws Exception {
		response.setContentType("text/plain");
		
		if (!parameters.containsKey("id")) {
			response.getWriter().println("Mising parameter id");
			return;
		}
		
		String id = parameters.get("id");
		Container container = App.container(id);
		if (container == null) {
			response.getWriter().print("invalid id");
			return;
		}
		
		try {
			container.start();
			response.getWriter().print("ok");
		} catch (IOException e) {
			response.getWriter().print("error");
		}
		
	}


}
