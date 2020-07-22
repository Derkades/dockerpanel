package xyz.derkades.dockerpanel.api;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.github.dockerjava.api.model.Container;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;

public class GetContainerStatus extends ApiMethod {

	public GetContainerStatus() {
		super("get_container_status");
	}

	@Override
	public void call(final Map<String, String> parameters, final HttpServletResponse response) throws Exception {
		if (parameters.containsKey("id")) {
			// Show status of one container in plaintext
			response.setContentType("text/plain");

			final String id = parameters.get("id");
			final Container container = App.container(id);
			if (container == null) {
				response.getWriter().print("invalid id");
				return;
			}

			response.getWriter().print(container.getState());
		} else {
			// Show status of all contains in json
			response.setContentType("text/json");

			final JsonWriter writer = new Gson().newJsonWriter(response.getWriter());
			writer.beginObject();
			for (final Container container : App.getContainers()) {
				writer.name(container.getId());
				writer.value(container.getState());
			}
			writer.endObject();
		}
	}

}
