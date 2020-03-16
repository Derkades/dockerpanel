package xyz.derkades.dockerpanel.api;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;

public class SendCommand extends ApiMethod {

	public SendCommand() {
		super("send_command");
	}

	@Override
	public void call(final Map<String, String> parameters, final HttpServletResponse response) throws Exception {
		response.setContentType("text/plain");

		if (!parameters.containsKey("id")) {
			response.getWriter().println("Missing parameter id");
			return;
		}

		if (!parameters.containsKey("command")) {
			response.getWriter().println("Missing parameter command");
			return;
		}

		final String id = parameters.get("id");
		final String command = parameters.get("command");

		final Container container = App.container(id);
		if (container == null) {
			response.getWriter().print("invalid id");
			return;
		}

		// No need to do anything with stdout
		final ResultCallback.Adapter<Frame> callback = new ResultCallback.Adapter<Frame>() {
			@Override public void onError(final Throwable throwable) {
				throwable.printStackTrace();
			}
		};

		final PipedOutputStream out = new PipedOutputStream();
		final PipedInputStream in = new PipedInputStream(out);

		App.docker().attachContainerCmd(container.getId()).withFollowStream(true)
				.withStdIn(in).exec(callback);

		out.write((command + "\n").getBytes());
		out.flush();
		out.close();
		callback.close();

		if (!callback.awaitCompletion(10, TimeUnit.SECONDS)) {
			response.getWriter().println("timeout");
		}

		response.getWriter().print("ok");
	}

}
