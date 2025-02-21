package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static Util.HttpServerUtil.getFileExtension;
import static Util.HttpServerUtil.sendNotFound;

public class RequestHandler implements HttpHandler {

    private static final String FILE_PATH = Paths.get("homework").toAbsolutePath().toString();
    private static final String HTML_FILE_PATH = Paths.get("HTMLFiles").toAbsolutePath().toString();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();

        if (requestPath.equals("/")) {
            RootHandler(exchange);
            return;
        }

        Path filePath = Path.of(FILE_PATH, requestPath);
        if (Files.exists(filePath)) {
            serveFile(exchange, filePath);
            return;
        }

        filePath = Path.of(HTML_FILE_PATH, requestPath);
        if (Files.exists(filePath)) {
            serveFile(exchange, filePath);
            return;
        }

        sendNotFound(exchange);
    }

    private void RootHandler(HttpExchange exchange) throws IOException {
        Path filePath = Path.of(HTML_FILE_PATH, "RootTemplate.html");

        if (!Files.exists(filePath)) {
            sendNotFound(exchange);
            return;
        }

        serveFile(exchange, filePath);
    }

    private void serveFile(HttpExchange exchange, Path filePath) throws IOException {
        String extension = getFileExtension(filePath.getFileName().toString());
        String mimeType = MIME_Types.getMimeType(extension);

        exchange.getResponseHeaders().set("Content-Type", mimeType);
        exchange.sendResponseHeaders(200, Files.size(filePath));

        try (OutputStream os = exchange.getResponseBody()) {
            Files.copy(filePath, os);
        }
    }
}
