package xyz.derkades.dockerpanel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.okhttp.OkHttpDockerCmdExecFactory;
import com.google.gson.Gson;

public class App {

	private static WebServer server;
	private static DockerClient docker;
	private static String theme;
	private static Gson gson;
	public static int tailLines;
	public static int timeout;
	public static boolean disableInput;
	public static boolean disableButtons;

	public static void main(final String[] args) throws Exception {
		final long startTime = System.currentTimeMillis();
		System.out.println("Starting.. ");

		// Disable annoying jetty messages
		System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
		System.setProperty("org.eclipse.jetty.LEVEL", "OFF");
		System.setProperty("org.eclipse.jetty.util.log.announce", "false");

		loadTheme();

		try {
			tailLines = System.getenv("TAIL_LINES") == null ? 100 : Integer.parseInt(System.getenv("TAIL_LINES"));
		} catch (NumberFormatException e) {
			System.out.println("TAIL_LINES is not a valid number");
			System.exit(1);
			return;
		}
	
		try {
			timeout = System.getenv("TIMEOUT") == null ? 10 : Integer.parseInt(System.getenv("TIMEOUT"));
		} catch (NumberFormatException e) {
			System.out.println("TIMEOUT is not a valid number");
			System.exit(1);
			return;
		}
		
		disableInput = "true".equals(System.getenv("DISABLE_INPUT"));
		disableButtons = "true".equals(System.getenv("DISABLE_BUTTONS"));
		
		final int port;
		try {
			port = System.getenv("PORT") == null ? 80 : Integer.parseInt(System.getenv("PORT"));
			if (port < 1 || port > 65535) {
				System.out.println("PORT is set to a number outside of the valid port range");
				System.exit(1);
				return;
			}
		} catch (NumberFormatException e) {
			System.out.println("PORT is not a valid number");
			System.exit(1);
			return;
		}

		gson = new Gson();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (server != null) {
					server.stop();
				}
				System.out.println("bye!");
			}
		});

		final File docksock = new File("/var/run/docker.sock");
		if (!docksock.exists()) {
			System.out.println("Docker socket not found.");
			System.out.println("Make sure docker is installed and listening on unix:///var/run/docker.sock");
			System.out.println("If you are running dockerpanel in a docker container, make sure to mount the file in the container.");
			System.exit(1);
			return;
		}

		try {
			docker = DockerClientImpl.getInstance()
				    .withDockerCmdExecFactory(new OkHttpDockerCmdExecFactory());

			System.out.println("Connected to docker version " + docker.versionCmd().exec().getVersion() + " on " + docker.versionCmd().exec().getOperatingSystem());
		} catch (final Exception e) {
			System.out.println("Failed to connect to docker: " + e.getMessage());
			System.exit(1);
			return;
		}
		
		// Docker is initialized, web server can now be started async		
		server = new WebServer(port);
		server.start();

		System.out.println("\nContainers: ");
		for (final Container container : App.getContainers()) {
			System.out.println(" - " + containerName(container));
		}
		System.out.println("To change which docker containers are displayed, set the CONTAINER_WHITELIST environment variable.");

		if (System.getenv("PASSWORD") == null) {
			System.out.println("\nWARNING: Password authentication is disabled. Set a password using the PASSWORD environment variable.");
		}

		System.out.println();

		// Wait for webserver to start if not done yet
		server.waitForStart();

		System.out.println("Started (" + (System.currentTimeMillis() - startTime) + " ms)");
		System.out.println("Listening for requests on port " + port);
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
		// TODO safety checks: does array index 0 exist and is length > 1?
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

	public static void writeJson(final HttpServletResponse response, final Object object) throws IOException {
		response.getWriter().println(gson.toJson(object));
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

	public static void writeResource(final HttpServletResponse response, final String path) throws IOException {
		try (InputStream stream = App.class.getResourceAsStream(path)){
			if (stream == null) {
				response.getWriter().write("Resource not found: " + path);
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			IOUtils.copy(stream, response.getOutputStream());
			response.setStatus(HttpServletResponse.SC_OK);
		}

		if (path.endsWith(".js")) {
			response.setContentType("application/javascript");
		} else if (path.endsWith(".html")){
			response.setContentType("text/html");
		} else if (path.endsWith(".css")){
			response.setContentType("text/css");
		} else if (path.endsWith(".svg")) {
			response.setContentType("image/svg+xml");
		}

		response.setCharacterEncoding("UTF-8");
	}

}
