import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {

            HttpServer server = makeServer();
            initRoutes(server);
            server.start();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private static HttpServer makeServer() throws IOException {
        String host = "localhost";
        InetSocketAddress address = new InetSocketAddress(host, 8090);

        String msg = "запускаем сервер по адресу" + " http://%s:%s/%n";
        System.out.printf(msg, address.getHostName(), address.getPort());

        HttpServer server = HttpServer.create(address, 50);
        System.out.println("  удачно!");
        return server;

    }

    private static void initRoutes(HttpServer server) {
        server.createContext("/", exchange -> handleRequest(exchange));
        server.createContext("/apps/", exchange -> handleRequest(exchange));
        server.createContext("/apps/profile", exchange -> handleRequest(exchange));
    }

    private static void handleRequest(HttpExchange exchange) {
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

    private static BufferedReader getReader(HttpExchange exchange) {

        InputStream input = exchange.getRequestBody();
        Charset charset = StandardCharsets.UTF_8;
        InputStreamReader isr = new InputStreamReader(input, charset);

        return new BufferedReader(isr);

    }

    private static void writeData(Writer writer, HttpExchange exchange) {

        try (BufferedReader reader = getReader(exchange)) {
            if (!reader.ready()) {
                return;
            }

            write(writer, "Блок данных", "");
            reader.lines().forEach(v -> write(writer, "\t", v));
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

}