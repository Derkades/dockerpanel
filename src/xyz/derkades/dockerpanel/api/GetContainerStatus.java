package xyz.derkades.dockerpanel.api;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse.ContainerState;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;
import xyz.derkades.dockerpanel.RequestType;

public class GetContainerStatus extends ApiMethod {
	
	public GetContainerStatus() {
		super("get_container_status", RequestType.GET);
	}

	@Override
	public void call(Map<String, String> parameters, HttpServletResponse response) throws Exception {
		if (!parameters.containsKey("id")) {
			response.getWriter().println("Mising parameter id");
			return;
		}
		
		String id = parameters.get("id");
		
		InspectContainerResponse inspect = App.docker().inspectContainerCmd(id).exec();	
		ContainerState state = inspect.getState();
		response.getWriter().println(state.getStatus());
	}

}
