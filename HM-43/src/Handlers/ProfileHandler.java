package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static Util.HttpServerUtil.getFileExtension;
import static Util.HttpServerUtil.sendNotFound;

public class ProfileHandler implements HttpHandler {
    private final String filePath;

    public ProfileHandler(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();

        if (requestPath.equals("/apps/profile")) {
            requestPath = "/ProfileTemplate.html";
        }

        Path filePath = Path.of(this.filePath, requestPath);
        if (!Files.exists(filePath)) {
            sendNotFound(exchange);
            return;
        }

        String extension = getFileExtension(filePath.getFileName().toString());
        String mimeType = MIME_Types.getMimeType(extension);

        exchange.getResponseHeaders().set("Content-Type", mimeType);
        exchange.sendResponseHeaders(200, Files.size(filePath));

        try (OutputStream os = exchange.getResponseBody()) {
            Files.copy(filePath, os);
        }
    }

}