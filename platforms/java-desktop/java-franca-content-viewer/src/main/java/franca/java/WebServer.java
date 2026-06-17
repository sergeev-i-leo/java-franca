package franca.java;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.*;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import franca.java.expected.BufferedString;
import franca.java.office.document.factory.DocumentFactory;

public class WebServer {

  private HttpServer server;

  public void start(int port) throws IOException {
    server = HttpServer.create(new InetSocketAddress(port), 0);

    server.createContext("/api/ping", new PingHandler());

    server.createContext("/", exchange -> {

      Path rootDir = Paths.get(System.getProperty("user.dir"), "samples");
      String path = exchange.getRequestURI().getPath();
      if (path.equals("/")) {
        // return loaded document
        BufferedString targetBufferedString = new BufferedString();
        DocumentFactory.serialize(Document.instance, targetBufferedString);
        String string = targetBufferedString.getString();
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        byte[] responseBytes = string.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
          os.write(responseBytes);
        }
        return;
      }

      Path file = rootDir.resolve(path.substring(1)).normalize();

      if (!file.startsWith(rootDir)) {
        exchange.sendResponseHeaders(403, -1);
        return;
      }

      if (Files.exists(file) && !Files.isDirectory(file)) {
        // Определяем MIME-тип
        String mime = Files.probeContentType(file);
        if (mime == null) {
          mime = "application/octet-stream";
        }

        exchange.getResponseHeaders().set("Content-Type", mime);
        exchange.sendResponseHeaders(200, Files.size(file));

        try (OutputStream os = exchange.getResponseBody()) {
          Files.copy(file, os);
        }
      } else {
        exchange.sendResponseHeaders(404, -1);
      }
    });

    server.setExecutor(null);
    server.start();
    System.out.println("Server started on port " + port);
  }

  public void stop() {
    if (server != null) {
      server.stop(0);
      System.out.println("Server stopped");
    }
  }

  private static class PingHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
      exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
      exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

      if ("OPTIONS".equals(exchange.getRequestMethod())) {
        exchange.sendResponseHeaders(204, -1);
        return;
      }

      String response = "pong";
      exchange.sendResponseHeaders(200, response.length());
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(response.getBytes(StandardCharsets.UTF_8));
      }
    }
  }
}
