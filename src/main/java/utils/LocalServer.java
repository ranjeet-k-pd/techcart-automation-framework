package utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * A tiny embedded web server that serves the demo app bundled in
 * src/test/resources/webapp. This means the whole test suite is
 * self-contained: no dependency on any third-party website that
 * could change, block automated browsers, or go down mid-test-run.
 *
 * Uses only the JDK's built-in HttpServer, so no extra dependency
 * is needed for this.
 */
public class LocalServer {

    private static HttpServer server;
    private static final int PORT = 8089;

    public static String start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new StaticFileHandler());
        server.setExecutor(null);
        server.start();
        return "http://localhost:" + PORT + "/";
    }

    public static void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }

            String resourcePath = "webapp" + path;
            InputStream resourceStream = LocalServer.class.getClassLoader()
                    .getResourceAsStream(resourcePath);

            if (resourceStream == null) {
                byte[] body = "404 Not Found".getBytes();
                exchange.sendResponseHeaders(404, body.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(body);
                }
                return;
            }

            byte[] bytes;
            try (InputStream is = resourceStream) {
                bytes = is.readAllBytes();
            }

            String contentType = path.endsWith(".html") ? "text/html" : "text/plain";
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
