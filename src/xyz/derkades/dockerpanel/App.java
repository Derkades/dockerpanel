package xyz.derkades.dockerpanel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.okhttp.OkHttpDockerCmdExecFactory;
import com.google.gson.Gson;

public class App {

	private static WebServer server;
	private static DockerClient docker;
	private static String theme;
	public static int tailLines;

	public static void main(final String[] args) throws Exception {
		final long startTime = System.currentTimeMillis();
		System.out.println("Starting.. ");

		System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
		System.setProperty("org.eclipse.jetty.LEVEL", "OFF");
		System.setProperty("org.eclipse.jetty.util.log.announce", "false");

		loadTheme();

		tailLines = System.getenv("TAIL_LINES") == null ? 100 : Integer.parseInt(System.getenv("TAIL_LINES"));

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Stopping server..");
				if (server != null && !server.isStopped()) {
					server.stop();
				}
				System.out.println("bye!");
			}
		});

		final File docksock = new File("/var/run/docker.sock");
		if (!docksock.exists()) {
			System.err.println("Docker socket not found");
			System.exit(1);
			return;
		}

		docker = DockerClientImpl.getInstance()
			    .withDockerCmdExecFactory(new OkHttpDockerCmdExecFactory());

		System.out.println("Connected to docker version " + docker.versionCmd().exec().getVersion() + " on " + docker.versionCmd().exec().getOperatingSystem());

		server = new WebServer();
		server.start();

		System.out.println("\nContainers: ");
		for (final Container container : App.getContainers()) {
			System.out.println(" - " + containerName(container));
		}

		System.out.println();

		// Wait for webserver to start

		while (!server.isStarted()) {
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Started (" + (System.currentTimeMillis() - startTime) + " ms)");
	}

	public static List<Container> getContainers() {
		final String whitelist = System.getenv("CONTAINER_WHITELIST");
		final List<String> allStatuses = Arrays.asList("created", "restarting", "running", "paused", "exited");
		if (whitelist == null) {
			return docker().listContainersCmd().withStatusFilter(allStatuses).exec();
		} else {
			return docker().listContainersCmd().withStatusFilter(allStatuses)
					.withNameFilter(Arrays.stream(whitelist.split(" ")).map((s) -> "/" + s).collect(Collectors.toList()))
					.exec();
		}
	}

	public static String containerName(final Container container) throws IOException {
//		return container.inspect().asJsonObject().getString("Name").substring(1);
		return container.getNames()[0].substring(1);
	}

	public static Container container(final String id) {
		for (final Container container : getContainers()) {
			if (container.getId().equals(id)) {
				return container;
			}
		}
		return null;
	}

	public static DockerClient docker() {
		return docker;
	}

	public static String toJson(final Object object) {
		return new Gson().toJson(object);
	}

	public static void writeJson(final HttpServletResponse response, final Object object) throws IOException {
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
		} catch (final IOException e) {
			e.printStackTrace();
		}

		App.theme = theme;
	}

	public static String getTheme() {
		return theme;
	}

}
