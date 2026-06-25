package service.webcam;

import java.io.DataOutputStream;
import java.net.Socket;

public class WebcamCommandHandler {

    private Socket socket;
    private WebcamService webcamService;

    public WebcamCommandHandler(Socket socket) {
        this.socket = socket;
        try {
            // Tạo luồng Output để gửi dữ liệu ảnh đi
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            
            // Truyền 'out' vào Service
            this.webcamService = new WebcamService(out);
        } catch (Exception e) {
            System.out.println("[WEBCAM HANDLER] Lỗi khởi tạo luồng gửi ảnh.");
        }
    }

    // Đây là hàm processCommand để thằng UIClient gọi tới nè
    public void processCommand(String command) {
        if (command.equals("OPEN_WEBCAM")) {
            System.out.println("[WEBCAM HANDLER] Đang gọi dịch vụ mở camera...");
            if (webcamService != null) {
                webcamService.openWebcam();
            }
        } else if (command.equals("CLOSE_WEBCAM")) {
            System.out.println("[WEBCAM HANDLER] Đang tắt camera...");
            if (webcamService != null) {
                webcamService.closeWebcam();
            }
        }
    }
}