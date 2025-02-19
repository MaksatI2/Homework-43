import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            HttpServer server = makeServer();
            initRoutes(server);
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HttpServer makeServer() throws IOException {
        String host = "localhost";
        HttpServer server = HttpServer.create(new InetSocketAddress(host, 8080), 0);
        System.out.printf("запускаем сервер по адресу http://%s:%d%n", host, 8080);
        return server;
    }

    private static void initRoutes(HttpServer server) {
        server.createContext("/", new RootHandler());
        server.createContext("/apps/", new AppsHandler());
        server.createContext("/apps/profile", new ProfileHandler());
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = """
                <html>
                <head><title>Добро пожаловать</title></head>
                <body>
                    <h1>🚀 Добро пожаловать на наш сайт!</h1>
                    <p>Вы находитесь на главной странице.</p>
                    <ul>
                        <li><a href="/apps/">📱 Перейти в список приложений</a></li>
                        <li><a href="/apps/profile">👤 Посмотреть профиль</a></li>
                    </ul>
                </body>
                </html>
                """;
            sendHtmlResponse(exchange, response);
        }
    }


    static class AppsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = """
                <html>
                <head><title>Приложения</title></head>
                <body>
                    <h1>📱 Наши приложения</h1>
                    <ul>
                        <li>ᯤ Spotify</li>
                        <li>➣ Telegram</li>
                        <li>📅 Планировщик задач</li>
                    </ul>
                    <a href="/">🔙 Вернуться на главную</a>
                </body>
                </html>
                """;
            sendHtmlResponse(exchange, response);
        }
    }


    static class ProfileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = """
                <html>
                <head><title>Профиль</title></head>
                <body>
                    <h1>👤 Профиль пользователя</h1>
                    <p>Имя: <strong>Максат</strong></p>
                    <p>Статус: <em>Online</em></p>
                    <a href="/">🔙 Вернуться на главную</a>
                </body>
                </html>
                """;
            sendHtmlResponse(exchange, response);
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