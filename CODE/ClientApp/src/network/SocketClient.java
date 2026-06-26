package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class SocketClient {
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private String serverIp;
    private String clientName;


    // Hàm thực hiện đâm kết nối tới Server
    public boolean connectServer(String ip, int port) {
        if (isConnected()) {
            System.out.println("[NETWORK] Đã kết nối tới Server. Vui lòng ngắt kết nối trước khi kết nối mới.");
            return true;
        }
        
        try {
            socket = new Socket(ip, port);
            this.serverIp = ip;
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            
            // Gửi thông tin client (có thể là tên máy, IP, v.v.) cho Server
            String hostname = InetAddress.getLocalHost().getHostName();
            String LocalIP = InetAddress.getLocalHost().getHostAddress();
            String osName = System.getProperty("os.name");
            String username = System.getProperty("user.name");

            dos.writeUTF("HELLO|" + hostname + "|" + LocalIP + "|" + osName + "|" + username);
            dos.flush();

            String response = dis.readUTF();
        if (response.startsWith("CLIENT_NAME|")) {
            this.clientName = response.substring("CLIENT_NAME|".length());
            System.out.println("[NETWORK] Server assigned: " + clientName);
        }

            System.out.println("[NETWORK] Kết nối thành công tới Server: " + ip);
            return true;
            
        } catch (IOException e) {
            System.out.println("[NETWORK LỖI] Không thể kết nối. Server chưa bật hoặc sai IP.");
            return false;
        }
    }
    // Hàm kiểm tra trạng thái kết nối
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    // Các hàm Getters để các Service khác (như ScreenStream, Process) lấy phễu gửi/nhận dữ liệu
    public Socket getSocket() { return socket; }
    public DataOutputStream getDos() { return dos; }
    public DataInputStream getDis() { return dis; }

    public String getServerIp() {
    return serverIp;
    }

    public String getClientName() {
        return clientName;
    }


    // Hàm ngắt kết nối
    public void disconnect() {
        try {
            if (dis != null) dis.close();
            if (dos != null) dos.close();
            if (socket != null) socket.close();
            System.out.println("[NETWORK] Đã ngắt kết nối.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}