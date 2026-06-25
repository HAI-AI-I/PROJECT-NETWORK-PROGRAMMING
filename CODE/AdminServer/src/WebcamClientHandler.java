import java.io.DataOutputStream;
import java.net.Socket;

public class WebcamClientHandler {

    private Socket socket;
    private DataOutputStream out;

    public WebcamClientHandler(Socket socket) {
        try {
            this.socket = socket;
            // Khởi tạo luồng ghi dữ liệu ra Socket
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("[WEBCAM HANDLER] Lỗi khởi tạo: " + e.getMessage());
        }
    }

    public void openWebcam() {
        try {
            if (out != null) {
                out.writeUTF("OPEN_WEBCAM");
                out.flush();
            }
        } catch (Exception e) {
            System.out.println("[WEBCAM HANDLER] Lỗi gửi lệnh OPEN: " + e.getMessage());
        }
    }

    public void closeWebcam() {
        try {
            if (out != null) {
                out.writeUTF("CLOSE_WEBCAM");
                out.flush();
            }
        } catch (Exception e) {
            System.out.println("[WEBCAM HANDLER] Lỗi gửi lệnh CLOSE: " + e.getMessage());
        }
    }
}