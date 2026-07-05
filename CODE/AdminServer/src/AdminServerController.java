import config.ConfigManager;
import features.TaskManagerServerDemo;
import features.WebcamServerDemo;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class AdminServerController {

    private AdminServerApp ui;
    private ServerSocket serverSocket;
    private volatile boolean serverRunning = false;
    private int clientCount = 0;
    private List<ClientHandler> clients = new ArrayList<ClientHandler>();
    private ServerSocket webcamServerSocket;
    private Map<String, JLabel> webcamLabels = new ConcurrentHashMap<>();
    private List<Integer> freeClientNumbers = new ArrayList<Integer>();
    private ServerSocket taskServerSocket;
    private Map<String, JTable> taskTables = new ConcurrentHashMap<>();
    private Map<String, DefaultTableModel> taskModels = new ConcurrentHashMap<>();

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
        startWebcamServer();
        startTaskServer();
        startStressTestServer();
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

public void startWebcamServer() {
    new Thread(() -> {
        try {
            int webcamPort = ConfigManager.getInt("webcam_port", 1413);
            webcamServerSocket = new ServerSocket(webcamPort);
            System.out.println("[WEBCAM] Server listening on port " + webcamPort);

            while (true) {
                Socket clientSocket = webcamServerSocket.accept();
                new Thread(() -> {
                    try {
                        DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                        String clientId = dis.readUTF();
                        JLabel label = webcamLabels.get(clientId);

                        if (label == null) {
                            System.out.println("[WEBCAM] Không tìm label cho " + clientId);
                            clientSocket.close();
                            return;
                        }

                        new WebcamServerDemo(label).receiveWebcamStream(clientSocket);
                    } catch (Exception e) {
                        System.out.println("[WEBCAM] Client error: " + e.getMessage());
                    }
                }).start();
            }
        } catch (SocketException e) {
            System.out.println("[WEBCAM] Webcam server stopped.");
        } catch (Exception e) {
            System.out.println("[WEBCAM] Error: " + e.getMessage());
        }
    }).start();
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

    public void sendCommandToClient(String clientName, String command) {
    for (ClientHandler handler : clients) {
        if (handler.clientName.equals(clientName)) {
            handler.sendMessage(command);
            ui.addLog("[SERVER] Sent command to " + clientName + ": " + command);
            return;
        }
    }
    ui.addLog("[SERVER] Client " + clientName + " not found.");
}

public void sendPowerCommand(String clientName, String command) {
    for (ClientHandler client : clients) {
        if (client.clientName.equals(clientName)) {
            client.sendMessage("POWER|" + command);
            ui.addLog("[SERVER] Sent " + command + " command to " + clientName);
            return;
        }
    }
    ui.addLog("[SERVER] Client " + clientName + " not found to send power command.");
}


public void sendTaskCommand(String clientName, String command) {
    // Forward lệnh tới task server qua main socket
    // Client sẽ relay qua task socket
    sendCommandToClient(clientName, command);

}
    public void registerWebcamLabel(String clientName, JLabel label) {
    webcamLabels.put(clientName, label);
}

    public void registerTaskTable(String clientName, JTable table, DefaultTableModel model) {
    taskTables.put(clientName, table);
    taskModels.put(clientName, model);
}

    public void startTaskServer() {
    new Thread(() -> {
        try {
            int taskPort = ConfigManager.getInt("task_port", 1414);
            taskServerSocket = new ServerSocket(taskPort);
            System.out.println("[TASK] Server listening on port " + taskPort);

            while (true) {
                try {
                    Socket clientSocket = taskServerSocket.accept();
                    clientSocket.setSoTimeout(30000); // ← Timeout 30 giây
                    
                    System.out.println("[TASK] Client connected from " + clientSocket.getInetAddress());

                    // ← Chạy trong thread riêng (không block accept() lần tiếp)
                    new Thread(() -> {
                        try {
                            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                            String clientId = dis.readUTF();
                            System.out.println("[TASK] ClientId: " + clientId);

                            JTable table = taskTables.get(clientId);
                            DefaultTableModel model = taskModels.get(clientId);

                            if (table == null || model == null) {
                                System.out.println("[TASK] Table not found for " + clientId);
                                clientSocket.close();
                                return;
                            }

                            System.out.println("[TASK] Starting handler for " + clientId);
                            new TaskManagerServerDemo(table, model).handleTaskManager(clientSocket);

                        } catch (java.net.SocketTimeoutException e) {
                            System.out.println("[TASK] Timeout for client");
                        } catch (Exception e) {
                            System.out.println("[TASK] Error: " + e.getMessage());
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (Exception e) {
                            }
                        }
                    }).start();

                } catch (Exception e) {
                    System.out.println("[TASK] Accept error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("[TASK] Server error: " + e.getMessage());
        }
    }).start();
}
    public void startStressTestServer() {
    new Thread(() -> {
        try {
            int stressPort = ConfigManager.getInt("stress_port", 1417);
            ServerSocket stressServerSocket = new ServerSocket(stressPort);
            System.out.println("[STRESS-SERVER] Listening on port " + stressPort);
            ui.addLog("[STRESS-SERVER] Listening on port " + stressPort);

            while (true) {
                Socket clientSocket = stressServerSocket.accept();
                System.out.println("[STRESS-SERVER] Client connected");
                ui.addLog("[STRESS-SERVER] Client connected");
                
                new Thread(() -> {
                    DataInputStream dis = null;
                    try {
                        dis = new DataInputStream(clientSocket.getInputStream());
                        String clientId = dis.readUTF();
                        System.out.println("[STRESS-SERVER] Client: " + clientId);
                        ui.addLog("[STRESS-SERVER] Client ID: " + clientId);

                        while (!clientSocket.isClosed()) {
                            try {
                                String message = dis.readUTF();
                                if (message != null && !message.isEmpty()) {  // CHECK NULL
                                    System.out.println("[STRESS-SERVER] " + clientId + " - " + message);
                                    ui.addLog("[STRESS] " + clientId + " - " + message);
                                    ui.updateStressTestData(clientId, message);
                                }
                            } catch (java.io.EOFException e) {
                                System.out.println("[STRESS-SERVER] Client closed");
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("[STRESS-SERVER] Exception: " + e.getClass().getName());  // ← LOG CLASS
                        System.out.println("[STRESS-SERVER] Message: " + e.getMessage());  // LOG MESSAGE
                        e.printStackTrace();  // ← PRINT STACK TRACE
                        ui.addLog("[STRESS-SERVER] Error: " + e.getClass().getName() + " - " + e.getMessage());
                    } finally {
                        try {
                            if (clientSocket != null) clientSocket.close();
                        } catch (Exception ignored) {}
                    }
                }).start();
            }
        } catch (Exception e) {
            System.out.println("[STRESS-SERVER] Server Error: " + e.getMessage());
            e.printStackTrace();
            ui.addLog("[STRESS-SERVER] Server Error: " + e.getMessage());
        }
    }).start();
}
    public void removeTaskData(String clientName) {
        if (taskTables != null) {
            taskTables.remove(clientName);
        }
        if (taskModels != null) {
            taskModels.remove(clientName);
        }
        System.out.println("[SERVER] Removed task data for " + clientName);
    }

    public void removeWebcamData(String clientName) {
        if (webcamLabels != null) {
            webcamLabels.remove(clientName);
        }
        System.out.println("[SERVER] Removed webcam data for " + clientName);
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

                dos.writeUTF("CLIENT_NAME|" + clientName);
                dos.flush();

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

            } catch (java.io.EOFException e) {
                // ← Client disconnect bình thường
                System.out.println("[CLIENT] " + clientName + " EOFException");
                handleClientDisconnect();
            } catch (Exception e) {
                // ← Lỗi khác (socket closed, v.v)
                System.out.println("[CLIENT] " + clientName + " error: " + e.getMessage());
                handleClientDisconnect();
            } finally {
                close();
                clients.remove(this);
                ui.updateOnlineCount(clients.size());
                
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

    private void handleClientDisconnect() {
        ui.addLog("[CLIENT] " + clientName + " disconnected. Cleaning up...");
        
        // Xoá card
        ui.removeClientCard(clientName);
        
        // Xoá webcam label
        if (webcamLabels != null) {
            webcamLabels.remove(clientName);
        }
        
        // Xoá task manager table
        if (taskTables != null) {
            taskTables.remove(clientName);
        }
        if (taskModels != null) {
            taskModels.remove(clientName);
        }
        
        System.out.println("[SERVER] Cleaned up all data for " + clientName);
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

