package xyz.derkades.dockerpanel.api;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Container;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;

public class StopContainer extends ApiMethod {

	public StopContainer() {
		super("stop_container");
	}

	@Override
	public void call(final Map<String, String> parameters, final HttpServletResponse response) throws Exception {
		response.setContentType("text/plain");
		
		if (App.disableButtons) {
			response.getWriter().print("disabled");
			return;
		}

		if (!parameters.containsKey("id")) {
			response.getWriter().print("Mising parameter id");
			return;
		}

		final String id = parameters.get("id");
		final Container container = App.container(id);
		if (container == null) {
			response.getWriter().print("invalid id");
			return;
		}

		try {
			App.docker().stopContainerCmd(container.getId()).withTimeout(App.timeout).exec();
			response.getWriter().print("ok");
		} catch (final NotModifiedException e) {
			response.getWriter().print("already stopped");
		} catch (final IOException e) {
			response.getWriter().print("error");
		}
	}

}
