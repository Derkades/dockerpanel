package xyz.derkades.dockerpanel.api;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.amihaiemil.docker.Container;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;

public class GetContainerStatus extends ApiMethod {
	
	public GetContainerStatus() {
		super("get_container_status");
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
			response.getWriter().print(container.inspect().get("State").asJsonObject().get("Status").toString().replace("\"", ""));
		} catch (IOException e) {
			response.getWriter().print("error");
		}
	}

}
