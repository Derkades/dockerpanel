package xyz.derkades.dockerpanel.api;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.github.dockerjava.api.exception.NotModifiedException;

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
		
		try {
			App.docker().startContainerCmd(id).exec();
			response.getWriter().print("ok");
		} catch (NotModifiedException e) {
			response.getWriter().print("already started");
		}
		
	}


}
