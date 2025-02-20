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
    private static final String ImageName = Paths.get("homework").toAbsolutePath().toString();

    public static void main(String[] args) throws IOException {

        int port = 9889;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.printf("Сервер запущен на http://localhost:%d/%n", port);
        server.createContext("/", new StaticFileHandler());
        server.createContext("/apps", new AppHandler());
        server.createContext("/images", new ImageHandler());
        server.createContext("/apps/profile", new ProfileHandler());
        server.start();
        System.out.println("путь: " + FileName);
    }

    static class StaticFileHandler implements HttpHandler {
        private static final Map<String, String> MIME_Types = new HashMap<>();

        static {
            MIME_Types.put("html", "text/html");
            MIME_Types.put("css", "text/css");
            MIME_Types.put("jpg", "image/jpeg");
        }

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
            String mimeType = MIME_Types.getOrDefault(extension, "application/octet-stream");

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
            try {
                exchange.getResponseHeaders().add("Content-Type", "text/plan; charset=utf-8");

                int responseCode = 200;
                int length = 0;
                exchange.sendResponseHeaders(responseCode, length);

                try (PrintWriter writer = getWriterFrom(exchange)) {
                    String method = exchange.getRequestMethod();
                    URI uri = exchange.getRequestURI();
                    String path = exchange.getHttpContext().getPath();

                    write(writer, "HTTP method", method);
                    write(writer, "Request", uri.toString());
                    write(writer, "Handler", path);
                    writeHeaders(writer, "Request headers", exchange.getRequestHeaders());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static PrintWriter getWriterFrom(HttpExchange exchange) {

            OutputStream output = exchange.getResponseBody();
            Charset charset = StandardCharsets.UTF_8;

            return new PrintWriter(output, false, charset);
        }

        private static void write(Writer writer, String msg, String method) {
            String data = String.format("%s: %s%n%n", msg, method);

            try {

                writer.write(data);
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        private static void writeHeaders(Writer writer, String type, Headers headers) {
            write(writer, type, "");
            headers.forEach((k, v) -> write(writer, "\t" + k, v.toString()));

        }
    }

    static  class ProfileHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                exchange.getResponseHeaders().add("Content-Type", "text/plan; charset=utf-8");

                int responseCode = 200;
                int length = 0;
                exchange.sendResponseHeaders(responseCode, length);

                try (PrintWriter writer = getWriterFrom(exchange)) {
                    String method = exchange.getRequestMethod();
                    URI uri = exchange.getRequestURI();
                    String path = exchange.getHttpContext().getPath();

                    write(writer, "HTTP method", method);
                    write(writer, "Request", uri.toString());
                    write(writer, "Handler", path);
                    writeHeaders(writer, "Request headers", exchange.getRequestHeaders());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
}


        private static PrintWriter getWriterFrom(HttpExchange exchange) {

            OutputStream output = exchange.getResponseBody();
            Charset charset = StandardCharsets.UTF_8;

            return new PrintWriter(output, false, charset);
        }

        private static void write(Writer writer, String msg, String method) {
            String data = String.format("%s: %s%n%n", msg, method);

            try {

                writer.write(data);
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        private static void writeHeaders(Writer writer, String type, Headers headers) {
            write(writer, type, "");
            headers.forEach((k, v) -> write(writer, "\t" + k, v.toString()));

        }
    }

    static  class ImageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath().replace("/images/", "");
            Path filePath = Paths.get(ImageName, requestPath);
            File file = filePath.toFile();

            if (!file.exists() || file.isDirectory()) {
                sendNotFound(exchange);
                return;
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "image/jpeg";
            }

            sendFileResponse(exchange, file, contentType);
        }
    }


    private static void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "Ошибка 404: файл не найден";
        exchange.sendResponseHeaders(404, response.getBytes(StandardCharsets.UTF_8).length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void sendFileResponse(HttpExchange exchange, File file, String contentType) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, file.length());

    }




}