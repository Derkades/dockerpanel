package xyz.derkades.dockerpanel;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DockerClientBuilder;
import com.google.gson.Gson;

public class App {
	
	private static WebServer server;
	private static List<String> containers;
	private static DockerClient docker;
	public static List<String> allDockerStatuses = Arrays.asList("created", "restarting", "running", "paused", "exited");
	
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		System.out.println("Starting.. ");
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Stopping server..");
				if (server != null && !server.isStopped()) {
					server.stop();
				}
				System.out.println("bye!");
			}
		});

		File containersFile = new File("containers.txt");
		if (containersFile.exists()) {
			containers = FileUtils.readLines(containersFile, "UTF-8");
		} else {
			containersFile.createNewFile();
			System.err.println("No containers configured");
			System.exit(1);
			return;
		}
		
		docker = DockerClientBuilder.getInstance().build();
		Info info = docker.infoCmd().exec();
		System.out.println("Connected to Docker version " + info.getServerVersion());
		
		server = new WebServer();
		server.start();
		
		System.out.println("\nAll containers: ");
		for (Container container : docker.listContainersCmd().withStatusFilter(allDockerStatuses).exec()) {
			if (container.getNames().length >= 1) {
				System.out.println(" - " + container.getNames()[0].substring(1));
			} else {
				System.out.println(" - ???");
			}
		}
		
		System.out.println("\nWhitelisted containers:");
		for (String containerName : containers) {
			Container container = getContainerByName(containerName);
			if (container != null) {
				System.out.println(" - " + containerName);
			} else {
				System.out.println(" - " + containerName + " (UNAVAILABLE)");
			}
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
	
	public static Container getContainerByName(String containerName) {
		for (Container container : docker().listContainersCmd().withStatusFilter(allDockerStatuses).exec()) {
			for (String name : container.getNames()) {
				if (name.substring(1).equals(containerName)) {
					return container;
				}
			}
		}
		return null;
	}
	
	public static boolean isValidContainer(String containerName) {
		return containers.contains(containerName);
	}
	
	public static boolean isValidContainer(Container container) {
		for (String s : container.getNames()) {
			if (containers.contains(s.substring(1))) {
				return true;
			}
		}
		return false;
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
	
//	public static List<Container> getContainers() {
//		return docker.listContainersCmd().exec()
//				.stream().filter(App::isValidContainer).collect(Collectors.toList());
//	}
	
}
