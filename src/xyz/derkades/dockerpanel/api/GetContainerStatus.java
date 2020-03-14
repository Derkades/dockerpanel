package xyz.derkades.dockerpanel.api;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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
		
//		InspectContainerResponse inspect = App.docker().inspectContainerCmd(id).exec();	
//		ContainerState state = inspect.getState();
//		response.getWriter().print(state.getStatus());
		
		response.getWriter().print(App.docker().inspectContainer(id).state().status());
	}

}
