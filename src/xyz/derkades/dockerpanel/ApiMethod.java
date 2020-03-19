package xyz.derkades.dockerpanel;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import xyz.derkades.dockerpanel.api.GetContainerLogs;
import xyz.derkades.dockerpanel.api.GetContainerStatus;
import xyz.derkades.dockerpanel.api.GetContainers;
import xyz.derkades.dockerpanel.api.GetThemes;
import xyz.derkades.dockerpanel.api.RestartContainer;
import xyz.derkades.dockerpanel.api.SendCommand;
import xyz.derkades.dockerpanel.api.StartContainer;
import xyz.derkades.dockerpanel.api.StopContainer;

public abstract class ApiMethod {

	public static final ApiMethod[] METHODS = {
			new GetContainers(),
			new GetContainerLogs(),
			new GetContainerStatus(),
			new GetThemes(),
			new RestartContainer(),
			new SendCommand(),
			new StartContainer(),
			new StopContainer(),
	};

	private final String name;

	public ApiMethod(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public abstract void call(Map<String, String> parameters, HttpServletResponse response) throws Exception;

}
