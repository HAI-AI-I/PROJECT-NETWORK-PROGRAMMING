package network;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketClient {
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;

    // Hàm thực hiện đâm kết nối tới Server
    public boolean connectServer(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            
            System.out.println("[NETWORK] Kết nối thành công tới Server: " + ip);
            return true;
            
        } catch (IOException e) {
            System.out.println("[NETWORK LỖI] Không thể kết nối. Server chưa bật hoặc sai IP.");
            return false;
        }
    }

    // Các hàm Getters để các Service khác (như ScreenStream, Process) lấy phễu gửi/nhận dữ liệu
    public Socket getSocket() { return socket; }
    public DataOutputStream getDos() { return dos; }
    public DataInputStream getDis() { return dis; }

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