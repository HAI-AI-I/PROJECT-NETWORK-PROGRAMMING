import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AdminServerController {

    private AdminServerApp ui;
    private ServerSocket serverSocket;
    private boolean serverRunning = false;
    private int clientCount = 0;
    private List<ClientHandler> clients = new ArrayList<ClientHandler>();

    public AdminServerController(AdminServerApp ui) {
        this.ui = ui;
    }

    public void startServer() {
        if (serverRunning) {
            ui.addLog("[SERVER] Server is already running.");
            return;
        }
        serverRunning = true;

        Thread serverThread = new Thread(() -> {
            try {
                int port = 1412;
                serverSocket = new ServerSocket(port);

                String localIp = InetAddress.getLocalHost().getHostAddress();

                ui.addLog("[SERVER] Server started on port " + port);
                ui.addLog("[SERVER] Local endpoint: 127.0.0.1:" + port);
                ui.addLog("[SERVER] LAN endpoint: " + localIp + ":" + port);
                ui.addLog("[SERVER] Waiting for client connections...");
                while (serverRunning) {
                    Socket socket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(socket);
                    clients.add(handler);
                    Thread clientThread = new Thread(handler);
                    clientThread.start();
                }
            } catch (Exception e) {
                if (serverRunning) {
                    ui.addLog("[SERVER] Server error: " + e.getMessage());
                }
            }
        });

        serverThread.start();
    }

    public void stopServer() {
        try {
            serverRunning = false;
            for (int i = 0; i < clients.size(); i++) {
                clients.get(i).close();
            }
            clients.clear();
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            ui.addLog("[SERVER] Server stopped.");
        } catch (Exception e) {
            ui.addLog("[SERVER] Error stopping server: " + e.getMessage());
        }
    }

    public void broadcastMessage(String message) {
        if (clients.size() == 0) {
            ui.addLog("[SERVER] No clients connected to broadcast message.");
            return;
        }
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).sendMessage("BROADCAST|" + message);
        }
        ui.addLog("[SERVER] Sent message to " + clients.size() + " client(s): " + message);
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private String clientName = "Unknown Client";

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);

                String message = reader.readLine();
                if (message != null && message.startsWith("HELLO|")) {
                    String[] parts = message.split("\\|");
                    String hostname = parts.length > 1 ? parts[1] : "Unknown";
                    String ip = parts.length > 2 ? parts[2] : socket.getInetAddress().getHostAddress();
                    String os = parts.length > 3 ? parts[3] : "Unknown OS";
                    String username = parts.length > 4 ? parts[4] : "Unknown User";

                    clientCount++;
                    clientName = "CLIENT-" + String.format("%02d", clientCount);
                    ui.addLog("[CLIENT] " + clientName + " connected from " + ip);
                    ui.addClientCard(clientName, hostname, ip, os, username);
                }

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("BYE")) {
                        break;
                    }
                    ui.addLog("[" + clientName + "] " + line);
                }
            } catch (Exception e) {
                ui.addLog("[CLIENT] " + clientName + " disconnected.");
            } finally {
                close();
                clients.remove(this);
            }
        }

        public void sendMessage(String message) {
            if (writer != null) {
                writer.println(message);
            }
        }

        public void close() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (Exception ignored) {
            }
        }
    }
}
