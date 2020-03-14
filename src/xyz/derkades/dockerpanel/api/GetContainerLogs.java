package xyz.derkades.dockerpanel.api;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
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
		
		String id = parameters.get("id");
		
		int tail;
		
		if (parameters.containsKey("tail")) {
			tail = Integer.parseInt(parameters.get("tail"));
		} else {
			tail = 100;
		}
		
//		Process process = new ProcessBuilder()
//				.command("docker", "exec", id, "tail", "-n", tail + "", "logs/latest.log"))
//				.start();
		;
		
//		response.getWriter().print(IOUtils.toString(process.getInputStream(), "UTF-8"));
		
		ExecCreateCmdResponse cmd = App.docker().execCreateCmd(id)
				.withAttachStdout(true).withCmd("tail", "-n", tail + "", "logs/latest.log").exec();
		
		ThreadBlocker blocker = new ThreadBlocker();
		
		ResultCallback<Frame> callback = new ResultCallback<Frame>() {

			@Override
			public void close() throws IOException {}

			@Override
			public void onStart(Closeable closeable) {}

			@Override
			public void onNext(Frame object) {
				try {
					response.getWriter().println(new String(object.getPayload()).trim());
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
		
		App.docker().execStartCmd(cmd.getId()).exec(callback);
		
		blocker.block();
	}

}
