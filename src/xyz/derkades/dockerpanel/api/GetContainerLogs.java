package xyz.derkades.dockerpanel.api;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

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
		
		String id = parameters.get("id");
		
		int tail;
		
		if (parameters.containsKey("tail")) {
			tail = Integer.parseInt(parameters.get("tail"));
		} else {
			tail = 100;
		}
		
//		ThreadBlocker blocker = new ThreadBlocker();
//		
//		ResultCallback<Frame> loggingCallback = new ResultCallback<Frame>() {			
//			@Override
//			public void close() throws IOException {}
//
//			@Override
//			public void onStart(Closeable closeable) {}
//
//			@Override
//			public void onNext(Frame object) {
//				try {
//					String string = new String(object.getPayload()).strip().trim();
//					
//					System.out.println("'" + string + "'");
//
////					if (string.startsWith(">....") && string.length() >= 10) {
//////						System.out.println("'" + string + "'");
////						string = string.substring(9);
////					}
////					
////					if (string.startsWith("?")) {
////						System.out.println("'" + string + "'");
////					}
//					
//					response.getWriter().println(string);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//
//			@Override
//			public void onError(Throwable throwable) {
//				throwable.printStackTrace();
//			}
//
//			@Override
//			public void onComplete() {
//				blocker.done();
//			}
//			
//		};
//				
//		App.docker().logContainerCmd(parameters.get("id")).withTail(tail)
//				.withTimestamps(false).withStdErr(true).withStdOut(true)
//				.exec(loggingCallback);
//		
//		blocker.block();
		
//		File file = new File("D:\\file_name.xml");
//		int n_lines = 10;
//		int counter = 0; 
//		ReversedLinesFileReader object = new ReversedLinesFileReader(file);
//		while(counter < n_lines) {
//		    System.out.println(object.readLine());
//		    counter++;
//		}
		
		Process process = new ProcessBuilder()
				.command("docker", "exec", id, "tail", "-n", tail + "", "logs/latest.log")
				.start();
		;
		
		response.getWriter().print(IOUtils.toString(process.getInputStream(), "UTF-8"));
	}

}
