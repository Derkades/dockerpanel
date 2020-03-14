package xyz.derkades.dockerpanel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.amihaiemil.docker.Container;
import com.amihaiemil.docker.Docker;
import com.amihaiemil.docker.LocalDocker;
import com.google.common.collect.Streams;
import com.google.gson.Gson;

public class App {
	
	private static WebServer server;
	private static Docker docker;
	private static String theme;
	
	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		System.out.println("Starting.. ");
		
		System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
		System.setProperty("org.eclipse.jetty.LEVEL", "OFF");
		System.setProperty("org.eclipse.jetty.util.log.announce", "false");
		
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
		
		File docksock = new File("/var/run/docker.sock");
		if (!docksock.exists()) {
			System.err.println("Docker socket not found");
			System.exit(1);
			return;
		}
		
		docker = new LocalDocker(docksock);
		System.out.println("Connected to docker version " + docker.version().version() + " on " + docker.version().osName());
		
		server = new WebServer();
		server.start();
		
		System.out.println("\nContainers: ");
		for (Container container : App.getContainers()) {
			System.out.println(" - " + containerName(container));
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
	
	public static List<Container> getContainers() {
		String whitelist = System.getenv("CONTAINER_WHITELIST");
		if (whitelist == null) {
			return Streams.stream(docker().containers().all()).collect(Collectors.toList());
		} else {
			List<String> split = Arrays.asList(whitelist.split(" "));
			return Streams.stream(docker().containers().all())
					.filter((c) -> {
						try {
							return split.contains(containerName(c));
						} catch (IOException e) {
							e.printStackTrace();
							return false;
						}
					})
					.collect(Collectors.toList());
		}
	}
	
	public static String containerName(Container container) throws IOException {
		return container.inspect().asJsonObject().getString("Name").substring(1);
	}
	
	public static Container container(String id) {
		for (Container container : getContainers()) {
			if (container.containerId().equals(id)) {
				return container;
			}
		}
		return null;
	}
	
	public static Docker docker() {
		return docker;
	}
	
	public static String toJson(Object object) {
		return new Gson().toJson(object);
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
