package xyz.derkades.dockerpanel.api;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
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

//		response.getWriter().print(container.logs().fetch());

//		String[] command = { "tail", "-n", tail + "", "logs/latest.log" };
//		ExecCreation execCreation = App.docker().execCreate(id, command);
//		LogStream stream = App.docker().execStart(execCreation.id());
//		response.getWriter().print(stream.readFully());
//		stream.forEachRemaining((message) -> {
//			try {
//				response.getWriter().println(new String(message.content().array()).trim());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		});

		final ExecCreateCmdResponse cmd = App.docker().execCreateCmd(id)
				.withAttachStdout(true).withCmd("tail", "-n", tail + "", "logs/latest.log").exec();

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

		App.docker().execStartCmd(cmd.getId()).exec(callback);

		blocker.block();
	}

}
