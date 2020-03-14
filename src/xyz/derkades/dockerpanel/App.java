package xyz.derkades.dockerpanel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;

public class App {
	
	private static WebServer server;
	private static DockerClient docker;
	private static String theme;
	
	public static void main(String[] args) throws IOException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, DockerException, InterruptedException {
		long startTime = System.currentTimeMillis();
		System.out.println("Starting.. ");
		
		loadTheme();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Stopping server..");
				if (server != null && !server.isStopped()) {
					server.stop();
				}
				System.out.println("bye!");
			}
		});
		
		docker = new DefaultDockerClient(URI.create("unix:///var/run/docker.sock"));
		System.out.println("Connected to docker version " + docker.version().version());
		
		server = new WebServer();
		server.start();
		
		System.out.println("\nContainers: ");
		for (Container container : App.getContainers()) {
			System.out.println(container.names().get(0).substring(1));
		}
		
		System.out.println();
		
		// Wait for webserver to start
		
		while (!server.isStarted()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Started (" + (System.currentTimeMillis() - startTime) + " ms)");
	}
	
	public static List<Container> getContainers() throws DockerException, InterruptedException {
		String whitelist = System.getenv("CONTAINER_WHITELIST");
		if (whitelist == null) {
			return docker().listContainers();
		} else {
			return docker().listContainers()
					.stream()
					.filter((c) -> Arrays.binarySearch(whitelist.split(""), c.names().get(0).substring(1)) >= 0)
					.collect(Collectors.toList());
		}
	}
	
	public static Container getContainerByName(String containerName) throws DockerException, InterruptedException {
		for (Container container : getContainers()) {
			for (String name : container.names()) {
				if (name.substring(1).equals(containerName)) {
					return container;
				}
			}
		}
		return null;
	}
	
	public static DockerClient docker() {
		return docker;
	}
	
	public static Gson gson() {
		return new Gson(); // TODO persistent gson object
	}
	
	public static String toJson(Object object) {
		return gson().toJson(object);
	}
	
	public static void writeJson(HttpServletResponse response, Object object) throws IOException {
		response.getWriter().println(toJson(object));
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/json");
	}
	
	private static void loadTheme() {
		String theme = System.getenv("THEME");
		if (theme == null) {
			System.out.println("No theme set, using default.");
			theme = "default";
		}
		
		try (InputStream stream = App.class.getResourceAsStream("/themes/" + theme + ".css")){
			if (stream == null) {
				System.err.println("Theme not found");
				System.exit(1);
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		App.theme = theme;
	}
	
	public static String getTheme() {
		return theme;
	}
	
}
