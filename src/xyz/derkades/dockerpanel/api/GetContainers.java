package xyz.derkades.dockerpanel.api;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.github.dockerjava.api.model.Container;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;

public class GetContainers extends ApiMethod {

	public GetContainers() {
		super("get_containers");
	}

	@Override
	public void call(final Map<String, String> parameters, final HttpServletResponse response) throws Exception {
		response.setContentType("text/json");
		final JsonWriter writer = new Gson().newJsonWriter(response.getWriter());
		writer.beginObject();
		for (final Container container : App.getContainers()) {
			writer.name(container.getId());
			writer.beginObject();
			writer.name("name");
			writer.value(App.containerName(container));
			writer.name("state");
			writer.value(container.getState());
			writer.endObject();
		}
		writer.endObject();
	}

}
