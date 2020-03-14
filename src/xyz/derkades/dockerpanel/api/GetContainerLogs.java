package xyz.derkades.dockerpanel.api;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;
import xyz.derkades.dockerpanel.ThreadBlocker;

public class GetContainerLogs extends ApiMethod {

	public GetContainerLogs() {
		super("get_container_logs");
	}

	@Override
	public void call(Map<String, String> parameters, HttpServletResponse response) throws Exception {
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
		
		ThreadBlocker blocker = new ThreadBlocker();
		
		ResultCallback<Frame> loggingCallback = new ResultCallback<Frame>() {			
			@Override
			public void close() throws IOException {}

			@Override
			public void onStart(Closeable closeable) {}

			@Override
			public void onNext(Frame object) {
				try {
					// substring to remove STDOUT: STDERR:				
					response.getWriter().println(object.toString().substring(8));
				} catch (IOException e) {
					e.printStackTrace();
				}
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
				
		App.docker().logContainerCmd(parameters.get("id")).withTail(tail)
				.withTimestamps(false).withStdErr(true).withStdOut(true)
				.exec(loggingCallback);
		
		blocker.block();
	}

}
