package xyz.derkades.dockerpanel.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;
import xyz.derkades.dockerpanel.RequestType;

public class GetAvailableContainers extends ApiMethod {

	public GetAvailableContainers() {
		super("get_available_containers", RequestType.GET);
	}
	
	@Override
	public void call(Map<String, String> parameters, HttpServletResponse response) throws Exception {
		Map<String, String> output = new HashMap<>();
		
		App.docker().listContainersCmd().withStatusFilter(App.allDockerStatuses).exec().stream()
				.filter(App::isValidContainer)
				.forEach((container) -> output.put(container.getId(), container.getNames()[0].substring(1)));
		
		App.writeJson(response, output);
	}

}
