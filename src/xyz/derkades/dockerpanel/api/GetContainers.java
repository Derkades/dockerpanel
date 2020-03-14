package xyz.derkades.dockerpanel.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;

public class GetContainers extends ApiMethod {

	public GetContainers() {
		super("get_containers");
	}

	@Override
	public void call(final Map<String, String> parameters, final HttpServletResponse response) throws Exception {
		final Map<String, String> output = new HashMap<>();

//		App.docker().listContainersCmd().withStatusFilter(App.allDockerStatuses).exec().stream()
//				.filter(App::isValidContainer)
//				.forEach((container) -> output.put(container.getId(), container.getNames()[0].substring(1)));

		App.getContainers().forEach((c) -> output.put(c.getId(), c.getNames()[0].substring(1)));
//		App.getContainers().forEach((c) -> {
//			try {
//				output.put(c.containerId(), App.containerName(c));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		});

		App.writeJson(response, output);
	}

}
