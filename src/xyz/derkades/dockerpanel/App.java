package xyz.derkades.dockerpanel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DockerClientBuilder;

public class App {
	
	static WebServer server;
	static List<String> containers;
	static DockerClient docker;
	
	public static void main(String[] args) throws IOException {
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
	}
	
	public static boolean isValidContainer(Container container) {
		for (String s : container.getNames()) {
			if (containers.contains(s)) {
				return true;
			}
		}
		return false;
	}
	
	public static List<Container> getContainers() {
		return docker.listContainersCmd().exec()
				.stream().filter(App::isValidContainer).collect(Collectors.toList());

	}
	
}
