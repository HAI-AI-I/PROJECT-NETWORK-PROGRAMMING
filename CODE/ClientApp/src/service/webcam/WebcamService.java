package service.webcam;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class WebcamService {

    private boolean isStreaming = false; // Cờ kiểm soát vòng lặp gửi ảnh
    private DataOutputStream out; // Kênh để gửi ảnh về Server

    // Khởi tạo Service và truyền kênh gửi dữ liệu vào
    public WebcamService(DataOutputStream out) {
        this.out = out;
    }

    public void openWebcam() {
        System.out.println("[WEBCAM] Camera opened (Chế độ giả lập streaming)");
        
        // Bật cờ streaming và tạo một Thread riêng để chụp/gửi ảnh
        isStreaming = true;
        new Thread(() -> streamVideo()).start();
    }

    // Hàm này chạy ngầm liên tục để gửi ảnh qua mạng
    private void streamVideo() {
        try {
            int frameCount = 0; // Bộ đếm để tạo hiệu ứng hình ảnh đang chuyển động
            
            while (isStreaming) {
                // 1. Tạo một bức ảnh giả lập kích thước 640x480
                BufferedImage image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();

                // Tô nền đen
                g2d.setColor(new Color(15, 17, 20));
                g2d.fillRect(0, 0, 640, 480);

                // Viết chữ lên ảnh
                g2d.setColor(new Color(239, 35, 49)); // Màu đỏ cho ngầu
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 40));
                g2d.drawString("WEBCAM GIẢ LẬP", 140, 200);
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                g2d.drawString("Đang truyền luồng dữ liệu qua Socket...", 150, 250);
                g2d.drawString("Frame: " + (frameCount++), 270, 300);
                g2d.dispose();

                // 2. Nén ảnh thành định dạng JPG
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "JPG", baos);
                byte[] imageBytes = baos.toByteArray();

                // 3. Gửi kích thước bức ảnh, sau đó gửi dữ liệu bức ảnh về Server
                out.writeInt(imageBytes.length);
                out.write(imageBytes);
                out.flush();
                
                // 4. Nghỉ 100ms (~10 khung hình/giây) để tránh treo máy
                Thread.sleep(100); 
            }
        } catch (Exception e) {
            System.out.println("[WEBCAM] Kết nối bị ngắt hoặc stream dừng.");
            closeWebcam();
        }
    }

    public void closeWebcam() {
        isStreaming = false; // Tắt cờ để vòng lặp streamVideo tự dừng
        System.out.println("[WEBCAM] Camera closed (Giả lập)");
    }
}