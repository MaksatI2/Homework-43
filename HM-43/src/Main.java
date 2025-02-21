import Handlers.AppHandler;
import Handlers.ProfileHandler;
import Handlers.RequestHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

public class Main {
    private static final String HTML_FILE_PATH = Paths.get("HTMLFiles").toAbsolutePath().toString();

    public static void main(String[] args) throws IOException {
        int port = 9889;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.printf("Сервер запущен на http://localhost:%d/%n", port);

        server.createContext("/", new RequestHandler());
        server.createContext("/apps", new AppHandler(HTML_FILE_PATH));
        server.createContext("/apps/profile", new ProfileHandler(HTML_FILE_PATH));

        server.start();
    }
}