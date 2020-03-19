package xyz.derkades.dockerpanel;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;

import xyz.derkades.dockerpanel.servlet.Api;
import xyz.derkades.dockerpanel.servlet.Index;
import xyz.derkades.dockerpanel.servlet.Login;
import xyz.derkades.dockerpanel.servlet.Theme;

public class WebServer {

	private Server server;

	public void start() {
		this.server = new Server();

		final ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		handler.addServlet(Login.class, "/login");
		handler.addServlet(Theme.class, "/theme");
		handler.addServlet(Index.class, "/");
		handler.addServlet(Api.class, "/api/*");

		this.server.setHandler(handler);

		final ServerConnector connector = new ServerConnector(this.server);
		connector.setPort(8080);
		this.server.addConnector(connector);

		new Thread() {

			@Override
			public void run() {
				try {
					WebServer.this.server.start();
					System.out.println("Listening on port 8080");
					WebServer.this.server.join(); // Join with main thread
				} catch (final Exception e) {
					System.out.println("An error occured while starting webserver: " + e.getMessage());
				}
			}

		}.start();
	}

	public void stop() {
		try {
			this.server.setStopAtShutdown(true);
			this.server.stop();
			System.out.println("Embedded webserver has been stopped.");
		} catch (final Exception e) {
			System.out.println("An error occured while stopping webserver: " + e.getMessage());
		}
	}

	boolean isStopped() {
		return this.server.isStopped();
	}

	boolean isStarted() {
		return this.server.isStarted();
	}

}
