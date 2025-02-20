import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final String FileName = Paths.get("homework").toAbsolutePath().toString();

    public static void main(String[] args) throws IOException {

        int port = 9889;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.printf("Сервер запущен на http://localhost:%d/%n", port);
        server.createContext("/", new StaticFileHandler());
        server.createContext("/apps", new AppHandler());
        server.createContext("/apps/profile", new ProfileHandler());
        server.start();
        System.out.println("путь: " + FileName);
    }

    static class StaticFileHandler implements HttpHandler {
//        private static final Map<String, String> MIME_Types = new HashMap<>();
//
//        static {
//            MIME_Types.put("html", "text/html");
//            MIME_Types.put("css", "text/css");
//            MIME_Types.put("jpg", "image/jpg");
//            MIME_Types.put("png", "image/png");
//        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            if (requestPath.equals("/")) requestPath = "/index.html";

            Path filePath = Path.of(FileName , requestPath);
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

        private String getFileExtension(String filename) {
            int dotIndex = filename.lastIndexOf('.');
            return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1).toLowerCase();
        }

    }

    static class AppHandler implements  HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

        }
    }

    static  class ProfileHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

        }
    }

    public class MIME_Types {
        private static final Map<String, String> MIME_Types = initMimeTypes();

        private static Map<String, String> initMimeTypes() {
            Map<String, String> mimeTypes = new HashMap<>();
            mimeTypes.put("html", "text/html");
            mimeTypes.put("css", "text/css");
            mimeTypes.put("jpg", "image/jpg");
            mimeTypes.put("png", "image/png");
            return mimeTypes;
        }

        public static String getMimeType(String extension) {
            return MIME_Types.getOrDefault(extension, "application/octet-stream");
        }
    }


    private static void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "Error 404";
        exchange.sendResponseHeaders(404, response.getBytes(StandardCharsets.UTF_8).length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void sendHtmlResponse(HttpExchange exchange, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}