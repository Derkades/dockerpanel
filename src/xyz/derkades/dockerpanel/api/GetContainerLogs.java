package xyz.derkades.dockerpanel.api;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;

public class GetContainerLogs extends ApiMethod {

	public GetContainerLogs() {
		super("get_container_logs");
	}

	@Override
	public void call(final Map<String, String> parameters, final HttpServletResponse response) throws Exception {
		response.setContentType("text/plain");

		if (!parameters.containsKey("id")) {
			response.getWriter().println("Missing parameter id");
			return;
		}

		final String id = parameters.get("id");
		final Container container = App.container(id);
		if (container == null) {
			response.getWriter().print("invalid id");
			return;
		}

		final ResultCallback.Adapter<Frame> callback = new ResultCallback.Adapter<Frame>() {

			@Override
			public void onNext(final Frame object) {
				try {
					response.getWriter().println(new String(object.getPayload()).trim());
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(final Throwable throwable) {
				throwable.printStackTrace();
			}

		};

		App.docker().logContainerCmd(container.getId()).withTail(App.tailLines)
				.withStdOut(true)
				.withStdErr(true)
				.exec(callback);

		if (!callback.awaitCompletion(10, TimeUnit.SECONDS)) {
			response.getWriter().println("timeout");
		}
	}

}
