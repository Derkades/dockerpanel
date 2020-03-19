package xyz.derkades.dockerpanel.api;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import xyz.derkades.dockerpanel.ApiMethod;
import xyz.derkades.dockerpanel.App;

public class GetThemes extends ApiMethod {

	public GetThemes() {
		super("get_themes");
	}

	@Override
	public void call(final Map<String, String> parameters, final HttpServletResponse response) throws Exception {
		final URI uri = this.getClass().getResource("/themes").toURI();

		final Path path;
		if (uri.getScheme().equals("jar")) {
			try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())){
				path = fs.getPath("/themes");
			}
		} else {
			path = Paths.get(uri);
		}

		App.writeJson(response,
				Files.walk(path)
				.filter(Files::isRegularFile)
				.map((s) -> s.getName(s.getNameCount() - 1).toString()) // Filename only
				.map((s) -> s.substring(0, s.length() - 4)) // Remove extension
				.collect(Collectors.toList()));
	}

}
