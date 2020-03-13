package xyz.derkades.dockerpanel;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

public class WebServer {

	private Server server;

	public void start() {
		this.server = new Server();

        final ServletHandler handler = new ServletHandler();

        handler.addServletWithMapping(WebServlet.class, "/*");

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
					WebServer.this.server.join(); //Join with main thread
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

}
