package xyz.derkades.dockerpanel.api;

import java.io.Closeable;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Map;

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

//		Process process =
//				new ProcessBuilder()
//				.command("socat", "EXEC:\"docker attach " + id + "\",pty", "STDIN")
//				.start();
//		OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream());
//		writer.write(command + "\n");
//		writer.flush();
//		writer.close();
//
//		if (process.waitFor() == 0) {
//			response.getWriter().write("ok");
//		} else {
//			response.getWriter().write("error");
//		}

		final Container container = App.container(id);
		if (container == null) {
			response.getWriter().print("invalid id");
			return;
		}

		final ResultCallback.Adapter<Frame> callback = new ResultCallback.Adapter<Frame>() {

			@Override
			public void close() throws IOException {}

			@Override
			public void onStart(final Closeable closeable) {}

			@Override
			public void onNext(final Frame object) {}

			@Override
			public void onError(final Throwable throwable) {
				throwable.printStackTrace();
			}

			@Override
			public void onComplete() {}

		};

		final PipedOutputStream out = new PipedOutputStream();
		final PipedInputStream in = new PipedInputStream(out);

		App.docker().attachContainerCmd(container.getId()).withFollowStream(true)
				.withStdIn(in).exec(callback);

		out.write((command + "\n").getBytes());
		out.flush();
		out.close();
		callback.close();
		response.getWriter().print("ok");
	}

}
