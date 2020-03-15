package xyz.derkades.dockerpanel.api;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;
import xyz.derkades.dockerpanel.ThreadBlocker;

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

		int tail;

		if (parameters.containsKey("tail")) {
			tail = Integer.parseInt(parameters.get("tail"));
		} else {
			tail = 100;
		}

		final String id = parameters.get("id");
		final Container container = App.container(id);
		if (container == null) {
			response.getWriter().print("invalid id");
			return;
		}

		final ThreadBlocker blocker = new ThreadBlocker();

		final ResultCallback<Frame> callback = new ResultCallback<Frame>() {

			@Override
			public void close() throws IOException {}

			@Override
			public void onStart(final Closeable closeable) {}

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

			@Override
			public void onComplete() {
				blocker.done();
			}

		};

		App.docker().logContainerCmd(container.getId()).withTail(tail)
				.withStdOut(true)
				.withStdErr(true)
				.exec(callback);

		blocker.block();
	}

}
