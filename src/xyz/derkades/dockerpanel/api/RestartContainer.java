package xyz.derkades.dockerpanel.api;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.github.dockerjava.api.model.Container;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;

public class RestartContainer extends ApiMethod {

	public RestartContainer() {
		super("restart_container");
	}

	@Override
	public void call(final Map<String, String> parameters, final HttpServletResponse response) throws Exception {
		response.setContentType("text/plain");

		if (!parameters.containsKey("id")) {
			response.getWriter().println("Mising parameter id");
			return;
		}

		final String id = parameters.get("id");
		final Container container = App.container(id);
		if (container == null) {
			response.getWriter().print("invalid id");
			return;
		}

		try {
			App.docker().restartContainerCmd(container.getId()).withtTimeout(30).exec();
			response.getWriter().print("ok");
		} catch (final IOException e) {
			response.getWriter().print("error");
		}
	}

}
