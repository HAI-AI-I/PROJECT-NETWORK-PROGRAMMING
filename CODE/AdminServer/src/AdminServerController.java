import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.net.SocketException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import config.ConfigManager;


public class AdminServerController {

    private AdminServerApp ui;
    private ServerSocket serverSocket;
    private volatile boolean serverRunning = false;
    private int clientCount = 0;
    private List<ClientHandler> clients = new ArrayList<ClientHandler>();
    private List<Integer> freeClientNumbers = new ArrayList<Integer>();

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
                int port = ConfigManager.getInt("server_port",1412);
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
                    ui.updateOnlineCount(clients.size());
                    Thread clientThread = new Thread(handler);
                    clientThread.start();
                }
            } catch (SocketException e) {
                if (serverRunning) {
                    ui.addLog("[SERVER] Server error: " + e.getClass().getSimpleName());
                    ui.addLog("[SERVER] Message: " + e.getMessage());
                } else {
                    ui.addLog("[SERVER] Server socket closed normally.");
                }
            } catch (Exception e) {
                if (serverRunning) {
                    ui.addLog("[SERVER] Server error: " + e.getClass().getSimpleName());
                    ui.addLog("[SERVER] Message: " + e.getMessage());
                    e.printStackTrace();
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
        ui.updateOnlineCount(0);
        clientCount = 0;
        freeClientNumbers.clear();

        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }

        ui.clearClientCards();

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
    
public void sendPowerCommand(String clientName, String command) {
    for (ClientHandler client : clients) {
        if (client.getClientName().equals(clientName)) {
            client.sendMessage("POWER|" + command);
            ui.addLog(
                    "[POWER] Đã gửi lệnh "
                            + command
                            + " tới "
                            + clientName
            );

            break;
        }
    }
}

    private class ClientHandler implements Runnable {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String clientName = "Unknown Client";
    private int clientNumber = 0;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            // Nhận thông tin đầu tiên từ client
            String message = dis.readUTF();

            if (message.startsWith("HELLO|")) {
                String[] parts = message.split("\\|");

                String hostname = parts.length > 1 ? parts[1] : "Unknown";
                String ip = parts.length > 2 ? parts[2] : socket.getInetAddress().getHostAddress();
                String os = parts.length > 3 ? parts[3] : "Unknown OS";
                String username = parts.length > 4 ? parts[4] : "Unknown User";

                clientNumber = getClientNumber();
                clientName = "CLIENT-" + String.format("%02d", clientNumber);

                ui.addLog("[CLIENT] " + clientName + " connected from " + ip);
                ui.addClientCard(clientName, hostname, ip, os, username);
            }

            // Nhận màn hình liên tục
            while (true) {
                int size = dis.readInt();

                byte[] data = new byte[size];
                dis.readFully(data);

                BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));

                if (image != null) {
                    ui.updateClientScreen(clientName, image);
                }
            }

        } catch (Exception e) {
            ui.addLog("[CLIENT] " + clientName + " disconnected.");
        } finally {
            close();
            clients.remove(this);

            ui.updateOnlineCount(clients.size());

            if (!clientName.equals("Unknown Client")) {
                ui.removeClientCard(clientName);
            }
            if (clientNumber > 0) {
                freeClientNumbers.add(clientNumber);
            }
        }
    }
    // Client cũ bay màu thì client mới thừa kế số client cũ.
    private int getClientNumber() {
    if (freeClientNumbers.size() > 0) {
        Collections.sort(freeClientNumbers);
        int number = freeClientNumbers.get(0);
        freeClientNumbers.remove(0);
        return number;
    }

    clientCount++;
    return clientCount;
    }

    public void sendMessage(String message) {
        try {
            if (dos != null) {
                dos.writeUTF(message);
                dos.flush();
            }
        } catch (Exception e) {
            ui.addLog("[SERVER] Cannot send message to " + clientName);
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

