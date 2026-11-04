package franca.java.ws

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress

class WsServer(port: Int, private val wsRouter: WsDevice) : WebSocketServer(InetSocketAddress(port)) {
  private val clients: MutableSet<WebSocket> = HashSet()
  override fun onOpen(webSocket: WebSocket, handshake: ClientHandshake) {
    clients.add(webSocket)
    println("onOpen: " + webSocket.remoteSocketAddress)
  }

  override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
    clients.remove(conn)
    println("onClose: " + conn.remoteSocketAddress)
  }

  override fun onMessage(conn: WebSocket, message: String) {
    println("Message from " + conn.remoteSocketAddress + ": " + message)
    conn.send("Echo: $message")
  }

  override fun onError(conn: WebSocket, ex: Exception) {
    ex.printStackTrace()
  }

  override fun onStart() {
    println("WebSocket-server started on port $port")
  }
}
