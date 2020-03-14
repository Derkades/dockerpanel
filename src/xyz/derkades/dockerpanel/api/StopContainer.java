package xyz.derkades.dockerpanel.api;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;

public class StopContainer extends ApiMethod {

	public StopContainer() {
		super("stop_container");
	}

	@Override
	public void call(Map<String, String> parameters, HttpServletResponse response) throws Exception {
		response.setContentType("text/plain");
		
		if (!parameters.containsKey("id")) {
			response.getWriter().print("Mising parameter id");
			return;
		}
		
		String id = parameters.get("id");		
		App.docker().stopContainer(id, 10);
		response.getWriter().print("ok");
	}

}
