package xyz.derkades.dockerpanel.api;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;
import xyz.derkades.dockerpanel.ThreadBlocker;

public class SendCommand extends ApiMethod {

	public SendCommand() {
		super("send_command");
	}

	@Override
	public void call(Map<String, String> parameters, HttpServletResponse response) throws Exception {
		response.setContentType("text/plain");
		
		if (!parameters.containsKey("id")) {
			response.getWriter().println("Missing parameter id");
			return;
		}
		
		if (!parameters.containsKey("command")) {
			response.getWriter().println("Missing parameter command");
			return;
		}
		
		ThreadBlocker blocker = new ThreadBlocker();
		
		ResultCallback<Frame> callback = new ResultCallback<Frame>() {

			@Override
			public void close() throws IOException {
				System.out.println("CLOSE");
			}

			@Override
			public void onStart(Closeable closeable) {
				System.out.println("START");
			}

			@Override
			public void onNext(Frame object) {
				System.out.println(object.toString());
			}

			@Override
			public void onError(Throwable throwable) {
				throwable.printStackTrace();
			}

			@Override
			public void onComplete() {
				blocker.done();
			}

		};
		
		InputStream stream = new ByteArrayInputStream(parameters.get("command").getBytes());
		
		App.docker().attachContainerCmd(parameters.get("id")).withStdIn(stream).exec(callback);
		
		blocker.block();
	}

}
