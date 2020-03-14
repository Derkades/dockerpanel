package xyz.derkades.dockerpanel.api;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
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

//		String id = parameters.get("id");
		final String command = parameters.get("command");
//
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

		final String id = parameters.get("id");
		final Container container = App.container(id);
		if (container == null) {
			response.getWriter().print("invalid id");
			return;
		}


		final ResultCallback<Frame> callback = new ResultCallback<Frame>() {

			@Override
			public void close() throws IOException {
				System.out.println("close");
			}

			@Override
			public void onStart(final Closeable closeable) {
				System.out.println("start");
			}

			@Override
			public void onNext(final Frame object) {
				System.out.println("next");
			}

			@Override
			public void onError(final Throwable throwable) {
				throwable.printStackTrace();
			}

			@Override
			public void onComplete() {
				System.out.println("complete");
			}

		};

		final InputStream stdin = new ByteArrayInputStream(command.getBytes());
		App.docker().attachContainerCmd(container.getId()).withStdIn(stdin).exec(callback);
		response.getWriter().print("ok");
	}

}
