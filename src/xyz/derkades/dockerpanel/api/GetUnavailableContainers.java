package xyz.derkades.dockerpanel.api;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletResponse;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;
import xyz.derkades.dockerpanel.RequestType;

public class GetUnavailableContainers extends ApiMethod {
	
	public GetUnavailableContainers() {
		super("get_unavailable_containers", RequestType.GET);
	}

	@Override
	public void call(Map<String, String> parameters, HttpServletResponse response) throws Exception {
		Map<String, String> output = new HashMap<>();
		
		App.docker().listContainersCmd().exec().stream()
				.filter(Predicate.not(App::isValidContainer))
				.forEach((container) -> output.put(container.getId(), container.getNames()[0].substring(1)));
		
		App.writeJson(response, output);
	}

}