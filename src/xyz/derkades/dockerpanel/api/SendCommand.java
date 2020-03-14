package xyz.derkades.dockerpanel.api;

import java.io.OutputStreamWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import xyz.derkades.dockerpanel.ApiMethod;

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
		
		String id = parameters.get("id");
		String command = parameters.get("command");

		Process process = 
				new ProcessBuilder()
				.command("socat", "EXEC:\"docker attach " + id + "\",pty", "STDIN")
				.start();
		OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream());
		writer.write(command + "\n");
		writer.flush();
		writer.close();

		if (process.waitFor() == 0) {
			response.getWriter().write("ok");
		} else {
			response.getWriter().write("error");
		}
	}

}
