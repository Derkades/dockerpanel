package xyz.derkades.dockerpanel;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import xyz.derkades.dockerpanel.api.GetAvailableContainers;
import xyz.derkades.dockerpanel.api.GetContainerLogs;
import xyz.derkades.dockerpanel.api.GetContainerStatus;
import xyz.derkades.dockerpanel.api.GetUnavailableContainers;
import xyz.derkades.dockerpanel.api.StartContainer;
import xyz.derkades.dockerpanel.api.StopContainer;

public abstract class ApiMethod {
	
	public static final ApiMethod[] METHODS = {
			new GetAvailableContainers(),
			new GetContainerLogs(),
			new GetContainerStatus(),
			new GetUnavailableContainers(),
			new StartContainer(),
			new StopContainer(),
	};
	
	private String name;
	private RequestType type;
	
	public ApiMethod(String name, RequestType type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public RequestType getType() {
		return this.type;
	}
	
	public abstract void call(Map<String, String> parameters, HttpServletResponse response) throws Exception;
	
	

}
