package xyz.derkades.dockerpanel.api;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;
import xyz.derkades.dockerpanel.RequestType;

public class StartContainer extends ApiMethod {
	
	public StartContainer() {
		super("start_container", RequestType.POST);
	}

	@Override
	public void call(Map<String, String> parameters, HttpServletResponse response) throws Exception {
		if (!parameters.containsKey("id")) {
			response.getWriter().println("Mising parameter id");
			return;
		}
		
		String id = parameters.get("id");
		
		App.docker().startContainerCmd(id).exec();
	}


}
