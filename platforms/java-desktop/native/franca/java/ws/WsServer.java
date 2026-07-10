package franca.java.ws;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class WsServer extends WebSocketServer {

  private WsRouter wsRouter;
  private final Set<WebSocket> clients = new HashSet<>();

  public WsServer(int port, WsRouter wsRouter) {
    super(new InetSocketAddress(port));
    this.wsRouter = wsRouter;
  }

  @Override
  public void onOpen(WebSocket webSocket, ClientHandshake handshake) {
    clients.add(webSocket);
    System.out.println("onOpen: " + webSocket.getRemoteSocketAddress());
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    clients.remove(conn);
    System.out.println("onClose: " + conn.getRemoteSocketAddress());
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    System.out.println("Message from " + conn.getRemoteSocketAddress() + ": " + message);
    conn.send("Echo: " + message);
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    ex.printStackTrace();
  }

  @Override
  public void onStart() {
    System.out.println("WebSocket-server started on port " + getPort());
  }
}
