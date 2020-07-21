package xyz.derkades.dockerpanel;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;

import xyz.derkades.dockerpanel.servlet.Api;
import xyz.derkades.dockerpanel.servlet.Index;
import xyz.derkades.dockerpanel.servlet.Login;
import xyz.derkades.dockerpanel.servlet.Status;
import xyz.derkades.dockerpanel.servlet.Theme;

public class WebServer {

	private Server server;

	private final int port;

	WebServer(int port) {
		this.port = port;
	}

	public void start() {
		this.server = new Server();

		final ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		handler.addServlet(Login.class, "/login");
		handler.addServlet(Theme.class, "/theme");
		handler.addServlet(Index.class, "/");
		handler.addServlet(Api.class, "/api/*");
		handler.addServlet(Status.class, "/status");

		this.server.setHandler(handler);

		final ServerConnector connector = new ServerConnector(this.server);
		connector.setPort(this.port);
		this.server.addConnector(connector);

		new Thread() {

			@Override
			public void run() {
				try {
					WebServer.this.server.start();
//					System.out.println("Listening on port " + WebServer.this.port);
					WebServer.this.server.join();
				} catch (final Exception e) {
					System.out.println("An error occured while starting webserver: " + e.getMessage());
					if (e.getMessage().equals("Permission denied") && port < 1024) {
						System.out.println("Try using a port number >1024");
					}
					System.exit(1); // TODO don't do this
				}
			}

		}.start();
	}

	public void stop() {
		if (this.server.isStopped()) {
			return;
		}

		try {
			this.server.setStopAtShutdown(true);
			this.server.stop();
			System.out.println("Embedded webserver has been stopped.");
		} catch (final Exception e) {
			System.out.println("An error occured while stopping webserver: " + e.getMessage());
		}
	}

	public void waitForStart() {
		while (!this.server.isStarted()) {
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
