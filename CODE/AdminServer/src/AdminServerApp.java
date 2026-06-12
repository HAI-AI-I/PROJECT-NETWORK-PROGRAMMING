import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.image.BufferedImage;

public class AdminServerApp extends JFrame {

    private JTextArea eventLog;
    private JPanel clientGrid;
    private AdminServerController controller;
    private JLabel onlineInfoLabel;
    private Map<String, JLabel> screenLabels = new HashMap<String, JLabel>();
    private Map<String, JPanel> clientCards = new HashMap<String, JPanel>();
    private Map<String, JLabel> bigScreenLabels = new HashMap<String, JLabel>();
    private Map<String, BufferedImage> latestScreens = new HashMap<String, BufferedImage>();

    private final Color BG = new Color(7, 7, 8);
    private final Color PANEL = new Color(15, 17, 20);
    private final Color PANEL_2 = new Color(20, 22, 26);
    private final Color RED = new Color(239, 35, 49);
    private final Color RED_DARK = new Color(120, 10, 18);
    private final Color TEXT = new Color(240, 240, 240);
    private final Color MUTED = new Color(165, 165, 165);
    private final Color BORDER = new Color(80, 28, 32);

    public AdminServerApp() {
        controller = new AdminServerController(this);

        setTitle("Admin Server - Network Monitoring System");
        setSize(1450, 850);
        setMinimumSize(new Dimension(1180, 720));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        add(createSidebar(), BorderLayout.WEST);
        add(createDashboard(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setPreferredSize(new Dimension(0, 86));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, RED),
                BorderFactory.createEmptyBorder(14, 28, 14, 28)
        ));

        JLabel title = new JLabel("ADMIN SERVER - NETWORK MONITORING SYSTEM");
        title.setIcon(loadIcon("t1.png", 67, 67));
        title.setIconTextGap(12);
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 4));
        buttonPanel.setOpaque(false);

        JButton startButton = createTopButton("Start Server", true);
        startButton.setIcon(loadIcon("playbutton.png", 16, 16));
        JButton stopButton = createTopButton("Stop Server", false);
        stopButton.setIcon(loadIcon("stop.png", 16, 16));
        JButton broadcastButton = createTopButton("Broadcast", false);
        broadcastButton.setIcon(loadIcon("broadcast.png", 24, 24));

        startButton.addActionListener(e -> controller.startServer());
        stopButton.addActionListener(e -> controller.stopServer());
        broadcastButton.addActionListener(e -> showBroadcastDialog());

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(broadcastButton);

        header.add(title, BorderLayout.WEST);
        header.add(buttonPanel, BorderLayout.EAST);

        return header;
    }

    private JButton createTopButton(String text, boolean primary) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(165, 42));
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (primary) {
            button.setBackground(RED_DARK);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(RED, 1),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)
            ));
        } else {
            button.setBackground(PANEL);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(130, 35, 40), 1),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)
            ));
        }

        return button;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(9, 10, 12));
        sidebar.setPreferredSize(new Dimension(265, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 1, BORDER),
                BorderFactory.createEmptyBorder(25, 0, 25, 0)
        ));

        JLabel menuTitle = new JLabel("   MONITORING MENU");
        menuTitle.setForeground(RED);
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        menuTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(menuTitle);
        sidebar.add(Box.createVerticalStrut(22));

        sidebar.add(createMenuButton("Dashboard", "Dashboard.png", true));
        //sidebar.add(createMenuButton("Webcam", "webcam.png", false));
        //sidebar.add(createMenuButton("Keylogger", "keyboard.png", false));
        //sidebar.add(createMenuButton("Task Manager", "task.png", false));
        //sidebar.add(createMenuButton("File Explorer", "file.png", false));
        //sidebar.add(createMenuButton("System Power", "power.png", false));
        sidebar.add(createMenuButton("Stress Test", "stress.png", false));

        sidebar.add(Box.createVerticalGlue());

        onlineInfoLabel = new JLabel(
                "<html><div style='padding-left:26px;color:white;'>"
                        + "<span style='color:#ef2331;'>●</span> Online: 0<br>"
                        + "Port: <span style='color:#ef2331;'>1412</span>"
                        + "</div></html>"
        );
        onlineInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        onlineInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(onlineInfoLabel);

        return sidebar;
    }

    private JButton createMenuButton(String text, String iconName, boolean selected) {
    JButton button = new JButton(text);

    ImageIcon icon = loadIcon(iconName, 22, 22);
    if (icon != null) {
        button.setIcon(icon);
    }

    button.setIconTextGap(14);

    button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
    button.setMinimumSize(new Dimension(265, 52));
    button.setPreferredSize(new Dimension(265, 52));
    button.setFocusPainted(false);
    button.setHorizontalAlignment(SwingConstants.LEFT);
    button.setFont(new Font("Segoe UI", Font.BOLD, 15));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

    if (selected) {
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(90, 10, 17));
        button.setBorder(BorderFactory.createMatteBorder(0, 6, 0, 4, RED));
    } else {
        button.setForeground(TEXT);
        button.setBackground(new Color(9, 10, 12));
        button.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
    }

    button.addActionListener(e -> addLog("[MENU] Open " + text));

    return button;
}
    private JPanel createDashboard() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.setBorder(BorderFactory.createEmptyBorder(22, 30, 22, 30));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("CONNECTED CLIENTS DASHBOARD");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JLabel slash = new JLabel("////");
        slash.setForeground(RED);
        slash.setFont(new Font("Segoe UI", Font.BOLD, 30));

        titlePanel.add(title, BorderLayout.WEST);
        titlePanel.add(slash, BorderLayout.EAST);
        titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, RED));

        clientGrid = new JPanel(new GridLayout(0, 3, 18, 18));
        clientGrid.setBackground(BG);
        clientGrid.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG);
        center.add(clientGrid, BorderLayout.CENTER);
        center.add(createEventLog(), BorderLayout.SOUTH);

        wrapper.add(titlePanel, BorderLayout.NORTH);
        wrapper.add(center, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createClientCard(int clientNumber) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel clientName = new JLabel("CLIENT-" + String.format("%02d", clientNumber));
        clientName.setIcon(loadIcon("computer.png", 16, 16));
        clientName.setForeground(TEXT);
        clientName.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel status = new JLabel("● ONLINE");
        status.setForeground(RED);
        status.setFont(new Font("Segoe UI", Font.BOLD, 12));

        top.add(clientName, BorderLayout.WEST);
        top.add(status, BorderLayout.EAST);

        JLabel screenPreview = new JLabel(
                "<html><div style='text-align:center;color:#b0b0b0;'>"
                        + "<div style='font-size:28px;color:#333333;'>▭</div>"
                        + "LIVE SCREEN PREVIEW"
                        + "</div></html>",
                SwingConstants.CENTER
        );
        screenPreview.setOpaque(true);
        screenPreview.setBackground(new Color(10, 12, 15));
        screenPreview.setForeground(MUTED);
        screenPreview.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        screenPreview.setBorder(BorderFactory.createLineBorder(new Color(65, 65, 70), 1));

        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 6));
        infoPanel.setBackground(PANEL);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        infoPanel.add(createInfoLabel("◎ IP: 192.168.1." + (100 + clientNumber), TEXT));
        infoPanel.add(createInfoLabel("◈ Status: ONLINE", RED));
        infoPanel.add(createInfoLabel("▦ OS: Windows " + (clientNumber % 2 == 0 ? "11" : "10") + " Pro", TEXT));
        infoPanel.add(createInfoLabel("◴ FPS: " + (16 + clientNumber), TEXT));

        card.add(top, BorderLayout.NORTH);
        card.add(screenPreview, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addLog("[CLIENT] Open detail CLIENT-" + String.format("%02d", clientNumber));
                    openClientDetailDialog(clientNumber);
                }
            }

            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(RED, 1),
                        BorderFactory.createEmptyBorder(12, 14, 12, 14)
                ));
            }

            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        BorderFactory.createEmptyBorder(12, 14, 12, 14)
                ));
            }
        });

        return card;
    }

    private JLabel createInfoLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }

    private JPanel createEventLog() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 155));
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(RED, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(12, 12, 14));
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 65)));

        JLabel title = new JLabel("  EVENT LOG");
        title.setForeground(RED);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel deco = new JLabel("////   🗑  ");
        deco.setForeground(RED);
        deco.setFont(new Font("Segoe UI", Font.BOLD, 16));

        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(deco, BorderLayout.EAST);

        eventLog = new JTextArea();
        eventLog.setEditable(false);
        eventLog.setBackground(new Color(7, 8, 10));
        eventLog.setForeground(TEXT);
        eventLog.setCaretColor(RED);
        eventLog.setFont(new Font("Consolas", Font.PLAIN, 13));
        eventLog.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        eventLog.setText(
                "[INFO] Admin Server initialized.\n"
                        + "[INFO] Waiting for client connections...\n"
                        + "[INFO] Dashboard loaded.\n"
        );

        JScrollPane scrollPane = new JScrollPane(eventLog);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(7, 8, 10));
        scrollPane.getVerticalScrollBar().setBackground(BG);

        panel.add(titleBar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public void addLog(String message) {
    if (eventLog != null) {
        SwingUtilities.invokeLater(() -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String currentTime = LocalDateTime.now().format(formatter);
            
            eventLog.append("[" + currentTime + "] " + message + "\n");
            eventLog.setCaretPosition(eventLog.getDocument().getLength());
        });
    }
}

    private void openClientDetailDialog(int clientNumber) {
        JDialog dialog = new JDialog(this, "Client Detail - CLIENT-" + String.format("%02d", clientNumber), true);
        dialog.setSize(1120, 720);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG);

        JLabel title = new JLabel("  CLIENT-" + String.format("%02d", clientNumber) + " DETAIL MONITORING");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, RED),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        title.setOpaque(true);
        title.setBackground(BG);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(PANEL);
        tabs.setForeground(TEXT);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabs.addTab("Screen", createScreenTab());
        tabs.addTab("Webcam", createWebcamTab());
        tabs.addTab("Keylogger", createKeyloggerTab());
        tabs.addTab("Task Manager", createTaskManagerTab());
        tabs.addTab("File Explorer", createFileExplorerTab());
        tabs.addTab("System Power", createPowerTab());
        tabs.addTab("Broadcast", createBroadcastTab());
        tabs.addTab("Stress Test", createStressTestTab());

        dialog.add(title, BorderLayout.NORTH);
        dialog.add(tabs, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    public void addClientCard(String clientName, String hostname, String ip, String os, String username) {
    SwingUtilities.invokeLater(() -> {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel nameLabel = new JLabel("▣  " + clientName);
        nameLabel.setForeground(TEXT);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel statusLabel = new JLabel("● ONLINE");
        statusLabel.setForeground(RED);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        top.add(nameLabel, BorderLayout.WEST);
        top.add(statusLabel, BorderLayout.EAST);

        JLabel preview = new JLabel(
        "<html><div style='text-align:center;color:#b0b0b0;'>"
                + "<div style='font-size:28px;color:#333333;'>▭</div>"
                + "CONNECTED CLIENT"
                + "</div></html>",
        SwingConstants.CENTER
);

        preview.setOpaque(true);
        preview.setBackground(new Color(10, 12, 15));
        preview.setForeground(MUTED);
        preview.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        preview.setBorder(BorderFactory.createLineBorder(new Color(65, 65, 70), 1));

        screenLabels.put(clientName, preview);

        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 6));
        infoPanel.setBackground(PANEL);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        infoPanel.add(createInfoLabel(" IP: " + ip, TEXT));
        infoPanel.add(createInfoLabel(" User: " + username, RED));
        infoPanel.add(createInfoLabel(" Host: " + hostname, TEXT));
        infoPanel.add(createInfoLabel(" OS: " + os, TEXT));

        card.add(top, BorderLayout.NORTH);
        card.add(preview, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.SOUTH);
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addLog("[SCREEN] Open big action panel for " + clientName);
                    openClientActionDialog(clientName);
                }
            }

            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(RED, 1),
                        BorderFactory.createEmptyBorder(12, 14, 12, 14)
                ));
            }

            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        BorderFactory.createEmptyBorder(12, 14, 12, 14)
                ));
            }
        });

        clientCards.put(clientName, card);
        clientGrid.add(card);
        clientGrid.revalidate();
        clientGrid.repaint();
    });
}
// Hàm này sẽ được gọi từ ClientHandler khi nhận được hình ảnh mới từ client
    public void updateClientScreen(String clientName, BufferedImage image) {
    SwingUtilities.invokeLater(() -> {
        latestScreens.put(clientName, image);

        JLabel smallLabel = screenLabels.get(clientName);

        if (smallLabel != null) {
            Image smallImg = image.getScaledInstance(
                    smallLabel.getWidth(),
                    smallLabel.getHeight(),
                    Image.SCALE_FAST
            );

            smallLabel.setText("");
            smallLabel.setIcon(new ImageIcon(smallImg));
        }

        JLabel bigLabel = bigScreenLabels.get(clientName);

        if (bigLabel != null) {
            Image bigImg = image.getScaledInstance(
                    bigLabel.getWidth(),
                    bigLabel.getHeight(),
                    Image.SCALE_SMOOTH
            );

            bigLabel.setText("");
            bigLabel.setIcon(new ImageIcon(bigImg));
        }
    });
}
// Hàm này sẽ được gọi khi client ngắt kết nối hoặc bị ngắt kết nối
public void removeClientCard(String clientName) {
    SwingUtilities.invokeLater(() -> {
        JPanel card = clientCards.get(clientName);

        if (card != null) {
            clientGrid.remove(card);
            clientCards.remove(clientName);
            screenLabels.remove(clientName);
            latestScreens.remove(clientName);

            clientGrid.revalidate();
            clientGrid.repaint();
        }
    });
}
    public void clearClientCards() {
    SwingUtilities.invokeLater(() -> {
        clientGrid.removeAll();
        screenLabels.clear();
        clientCards.clear();
        latestScreens.clear();
        bigScreenLabels.clear();

        clientGrid.revalidate();
        clientGrid.repaint();
    });
}

    private JPanel createScreenTab() {
        JPanel panel = createBaseTabPanel();

        JLabel screen = createPreviewLabel("HIGH RESOLUTION SCREEN STREAM");

        JPanel controlPanel = createControlPanel();
        JButton start = createRedButton("Start Screen");
        JButton stop = createDarkButton("Stop Screen");
        JButton screenshot = createDarkButton("Take Screenshot");
        JButton fullscreen = createDarkButton("Full Screen");

        start.addActionListener(e -> addLog("[SCREEN] Start screen monitoring."));
        stop.addActionListener(e -> addLog("[SCREEN] Stop screen monitoring."));
        screenshot.addActionListener(e -> addLog("[SCREEN] Screenshot captured."));
        fullscreen.addActionListener(e -> addLog("[SCREEN] Open fullscreen mode."));

        controlPanel.add(start);
        controlPanel.add(stop);
        controlPanel.add(screenshot);
        controlPanel.add(fullscreen);

        panel.add(screen, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createWebcamTab() {
        JPanel panel = createBaseTabPanel();

        JLabel webcam = createPreviewLabel("WEBCAM STREAM");

        JPanel controlPanel = createControlPanel();
        JButton start = createRedButton("Start Webcam");
        JButton stop = createDarkButton("Stop Webcam");
        JButton capture = createDarkButton("Capture");

        start.addActionListener(e -> addLog("[WEBCAM] Start webcam stream."));
        stop.addActionListener(e -> addLog("[WEBCAM] Stop webcam stream."));
        capture.addActionListener(e -> addLog("[WEBCAM] Capture webcam image."));

        controlPanel.add(start);
        controlPanel.add(stop);
        controlPanel.add(capture);

        panel.add(webcam, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createKeyloggerTab() {
        JPanel panel = createBaseTabPanel();

        JTextArea keyLogArea = createDarkTextArea();
        keyLogArea.setText(
                "[10:01:22] User typed: hello\n"
                        + "[10:01:25] Key pressed: ENTER\n"
                        + "[10:02:10] User typed: chrome.exe\n"
        );

        JPanel controlPanel = createControlPanel();
        JButton start = createRedButton("Start Keylogger");
        JButton stop = createDarkButton("Stop Keylogger");
        JButton export = createDarkButton("Export Log");
        JButton clear = createDarkButton("Clear");

        start.addActionListener(e -> addLog("[KEYLOGGER] Start keylogger."));
        stop.addActionListener(e -> addLog("[KEYLOGGER] Stop keylogger."));
        export.addActionListener(e -> addLog("[KEYLOGGER] Export key log."));
        clear.addActionListener(e -> keyLogArea.setText(""));

        controlPanel.add(start);
        controlPanel.add(stop);
        controlPanel.add(export);
        controlPanel.add(clear);

        panel.add(createScroll(keyLogArea), BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTaskManagerTab() {
        JPanel panel = createBaseTabPanel();

        String[] columns = {"PID", "Process Name", "CPU", "RAM", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        model.addRow(new Object[]{"1024", "chrome.exe", "12%", "450 MB", "Running"});
        model.addRow(new Object[]{"2048", "explorer.exe", "3%", "120 MB", "Running"});
        model.addRow(new Object[]{"3050", "java.exe", "8%", "300 MB", "Running"});
        model.addRow(new Object[]{"4088", "notepad.exe", "1%", "30 MB", "Running"});

        JTable table = new JTable(model);
        styleTable(table);

        JPanel controlPanel = createControlPanel();
        JButton refresh = createRedButton("Refresh");
        JButton kill = createDarkButton("Kill Process");
        JButton search = createDarkButton("Search");

        refresh.addActionListener(e -> addLog("[TASK] Refresh process list."));
        kill.addActionListener(e -> addLog("[TASK] Kill selected process."));
        search.addActionListener(e -> addLog("[TASK] Search process."));

        controlPanel.add(refresh);
        controlPanel.add(kill);
        controlPanel.add(search);

        panel.add(createScroll(table), BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFileExplorerTab() {
        JPanel panel = createBaseTabPanel();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Client Disk");
        DefaultMutableTreeNode cDrive = new DefaultMutableTreeNode("C:\\");
        DefaultMutableTreeNode users = new DefaultMutableTreeNode("Users");
        DefaultMutableTreeNode admin = new DefaultMutableTreeNode("Admin");
        DefaultMutableTreeNode documents = new DefaultMutableTreeNode("Documents");
        DefaultMutableTreeNode downloads = new DefaultMutableTreeNode("Downloads");

        root.add(cDrive);
        cDrive.add(users);
        users.add(admin);
        admin.add(documents);
        admin.add(downloads);

        JTree tree = new JTree(root);
        tree.setBackground(new Color(8, 9, 11));
        tree.setForeground(TEXT);
        tree.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tree.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea fileInfo = createDarkTextArea();
        fileInfo.setText("Select a file or folder to view information.");

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createScroll(tree),
                createScroll(fileInfo)
        );
        splitPane.setDividerLocation(300);
        splitPane.setBackground(BG);
        splitPane.setBorder(BorderFactory.createLineBorder(BORDER));

        JPanel controlPanel = createControlPanel();
        JButton refresh = createRedButton("Refresh");
        JButton download = createDarkButton("Download");
        JButton upload = createDarkButton("Upload");
        JButton delete = createDarkButton("Delete");

        refresh.addActionListener(e -> addLog("[FILE] Refresh file tree."));
        download.addActionListener(e -> addLog("[FILE] Download file from client."));
        upload.addActionListener(e -> addLog("[FILE] Upload file to client."));
        delete.addActionListener(e -> addLog("[FILE] Delete selected file."));

        controlPanel.add(refresh);
        controlPanel.add(download);
        controlPanel.add(upload);
        controlPanel.add(delete);

        panel.add(splitPane, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPowerTab() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        panel.add(createPowerCard("Lock Screen", "Lock client computer"));
        panel.add(createPowerCard("Restart", "Restart client computer"));
        panel.add(createPowerCard("Shutdown", "Shutdown client computer"));
        panel.add(createPowerCard("Sleep", "Put client into sleep mode"));

        return panel;
    }

    private JPanel createPowerCard(String title, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setForeground(MUTED);
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton button = createRedButton(title);
        button.addActionListener(e -> addLog("[POWER] " + title + " command sent."));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descriptionLabel, BorderLayout.CENTER);
        card.add(button, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createBroadcastTab() {
        JPanel panel = createBaseTabPanel();

        JTextArea messageArea = createDarkTextArea();
        messageArea.setText("Enter message to send to client...");

        JPanel controlPanel = createControlPanel();
        JButton sendClient = createRedButton("Send To Client");
        JButton sendAll = createDarkButton("Send To All Clients");
        JButton clear = createDarkButton("Clear");

        sendClient.addActionListener(e -> addLog("[BROADCAST] Message sent to selected client."));
        sendAll.addActionListener(e -> addLog("[BROADCAST] Message sent to all clients."));
        clear.addActionListener(e -> messageArea.setText(""));

        controlPanel.add(sendClient);
        controlPanel.add(sendAll);
        controlPanel.add(clear);

        panel.add(createScroll(messageArea), BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStressTestTab() {
        JPanel panel = createBaseTabPanel();

        JTextArea resultArea = createDarkTextArea();
        resultArea.setText(
                "Stress Test Console\n"
                        + "-------------------\n"
                        + "Virtual Clients: 0\n"
                        + "CPU Usage: 0%\n"
                        + "RAM Usage: 0 MB\n"
                        + "Status: Idle\n"
        );

        JPanel controlPanel = createControlPanel();

        JComboBox<String> clientCountBox = new JComboBox<>(new String[]{"50", "75", "100"});
        clientCountBox.setBackground(PANEL);
        clientCountBox.setForeground(TEXT);
        clientCountBox.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JButton start = createRedButton("Start Test");
        JButton stop = createDarkButton("Stop Test");
        JButton export = createDarkButton("Export Report");

        start.addActionListener(e -> {
            String count = clientCountBox.getSelectedItem().toString();
            resultArea.setText(
                    "Stress Test Console\n"
                            + "-------------------\n"
                            + "Virtual Clients: " + count + "\n"
                            + "CPU Usage: 35%\n"
                            + "RAM Usage: 512 MB\n"
                            + "Status: Running\n"
            );
            addLog("[STRESS TEST] Started with " + count + " virtual clients.");
        });

        stop.addActionListener(e -> {
            resultArea.append("\nStatus: Stopped\n");
            addLog("[STRESS TEST] Stopped.");
        });

        export.addActionListener(e -> addLog("[STRESS TEST] Export report."));

        JLabel countLabel = new JLabel("Virtual Clients:");
        countLabel.setForeground(TEXT);

        controlPanel.add(countLabel);
        controlPanel.add(clientCountBox);
        controlPanel.add(start);
        controlPanel.add(stop);
        controlPanel.add(export);

        panel.add(createScroll(resultArea), BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBaseTabPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        return panel;
    }

    private JLabel createPreviewLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(new Color(5, 6, 8));
        label.setForeground(MUTED);
        label.setFont(new Font("Consolas", Font.BOLD, 26));
        label.setBorder(BorderFactory.createLineBorder(RED, 1));
        return label;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        return panel;
    }

    private JTextArea createDarkTextArea() {
        JTextArea area = new JTextArea();
        area.setBackground(new Color(8, 9, 11));
        area.setForeground(TEXT);
        area.setCaretColor(RED);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return area;
    }

    private JScrollPane createScroll(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(new Color(8, 9, 11));
        return scrollPane;
    }

    private JButton createRedButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(RED_DARK);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(RED),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createDarkButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PANEL_2);
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void styleTable(JTable table) {
        table.setBackground(new Color(8, 9, 11));
        table.setForeground(TEXT);
        table.setGridColor(BORDER);
        table.setRowHeight(30);
        table.setSelectionBackground(RED_DARK);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setBackground(PANEL);
        table.getTableHeader().setForeground(RED);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(new Color(8, 9, 11));
        renderer.setForeground(TEXT);
        table.setDefaultRenderer(Object.class, renderer);
    }

    private void showBroadcastDialog() {
        String message = JOptionPane.showInputDialog(
                this,
                "Enter broadcast message:",
                "Broadcast",
                JOptionPane.PLAIN_MESSAGE
        );

        if (message != null && message.trim().length() > 0) {
            controller.broadcastMessage(message.trim());
        }
    }

    private ImageIcon loadIcon(String fileName, int width, int height) {
        String path = "assets/icons/" + fileName;
        ImageIcon icon = new ImageIcon(path);

        if (icon.getIconWidth() == -1) {
            System.out.println("Khong tim thay icon: " + path);
            System.out.println("Working Directory = " + System.getProperty("user.dir"));
            return null;
        }

        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return new ImageIcon(scaledImage);
    }

    // Hàm này sẽ được gọi khi người dùng double-click vào client
    private void openBigScreen(String clientName) {
    JDialog dialog = new JDialog(this, "Screen Monitor - " + clientName, false);
    dialog.setSize(1000, 650);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());
    dialog.getContentPane().setBackground(BG);

    JLabel title = new JLabel("  LIVE SCREEN - " + clientName);
    title.setForeground(TEXT);
    title.setFont(new Font("Segoe UI", Font.BOLD, 22));
    title.setOpaque(true);
    title.setBackground(BG);
    title.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, RED),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)
    ));

    JLabel screen = new JLabel("WAITING SCREEN...", SwingConstants.CENTER);
    screen.setOpaque(true);
    screen.setBackground(Color.BLACK);
    screen.setForeground(MUTED);
    screen.setFont(new Font("Consolas", Font.BOLD, 24));
    screen.setBorder(BorderFactory.createLineBorder(RED, 1));

    bigScreenLabels.put(clientName, screen);

    BufferedImage lastImage = latestScreens.get(clientName);

    if (lastImage != null) {
        Image bigImg = lastImage.getScaledInstance(
                1000,
                560,
                Image.SCALE_SMOOTH
        );

        screen.setText("");
        screen.setIcon(new ImageIcon(bigImg));
    }

    dialog.add(title, BorderLayout.NORTH);
    dialog.add(screen, BorderLayout.CENTER);

    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
        public void windowClosing(java.awt.event.WindowEvent e) {
            bigScreenLabels.remove(clientName);
        }
    });

    dialog.setVisible(true);
}

    public void updateOnlineCount(int count) {
    SwingUtilities.invokeLater(() -> {
        if (onlineInfoLabel != null) {
            onlineInfoLabel.setText(
                    "<html><div style='padding-left:26px;color:white;'>"
                            + "<span style='color:#ef2331;'>●</span> Online: " + count + "<br>"
                            + "Port: <span style='color:#ef2331;'>1412</span>"
                            + "</div></html>"
            );
        }
    });
}
    private void openClientActionDialog(String clientName) {
    JDialog dialog = new JDialog(this, "Client Actions - " + clientName, false);
    dialog.setSize(420, 420);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());
    dialog.getContentPane().setBackground(BG);

    JLabel title = new JLabel("  ACTIONS FOR " + clientName);
    title.setForeground(TEXT);
    title.setFont(new Font("Segoe UI", Font.BOLD, 20));
    title.setOpaque(true);
    title.setBackground(BG);
    title.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, RED),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)
    ));

    JPanel actionPanel = new JPanel(new GridLayout(4, 2, 12, 12));
    actionPanel.setBackground(BG);
    actionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JButton screenBtn = createRedButton("Screen Monitor");
    JButton webcamBtn = createDarkButton("Webcam");
    JButton keyloggerBtn = createDarkButton("Keylogger");
    JButton taskBtn = createDarkButton("Task Manager");
    JButton powerBtn = createDarkButton("System Power");
    JButton closeBtn = createDarkButton("Close");

    screenBtn.addActionListener(e -> {
        addLog("[SCREEN] Open screen for " + clientName);
        openBigScreen(clientName);
    });

    webcamBtn.addActionListener(e -> {
        addLog("[WEBCAM] Open webcam for " + clientName);
    });

    keyloggerBtn.addActionListener(e -> {
        addLog("[KEYLOGGER] Open keylogger for " + clientName);
    });

    taskBtn.addActionListener(e -> {
        addLog("[TASK] Open task manager for " + clientName);
    });


    powerBtn.addActionListener(e -> {
        addLog("[POWER] Open power control for " + clientName);
    });


    closeBtn.addActionListener(e -> dialog.dispose());

    actionPanel.add(screenBtn);
    actionPanel.add(webcamBtn);
    actionPanel.add(keyloggerBtn);
    actionPanel.add(taskBtn);
    actionPanel.add(powerBtn);
    actionPanel.add(closeBtn);

    dialog.add(title, BorderLayout.NORTH);
    dialog.add(actionPanel, BorderLayout.CENTER);

    dialog.setVisible(true);
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            new AdminServerApp().setVisible(true);
        });
    }
}
