import com.github.sarxos.webcam.Webcam;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class WebcamClientDemo {
    public void startWebcamStream(Socket socket) {
        try {
            // Lấy webcam mặc định của máy
            Webcam webcam = Webcam.getDefault();
            if (webcam != null) {
                webcam.open();
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                // Vòng lặp stream (nên đặt trong 1 Thread riêng hoặc SwingWorker)
                while (true) {
                    BufferedImage image = webcam.getImage();
                    if (image != null) {
                        // Nén ảnh sang JPEG vào bộ đệm
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "JPG", baos);
                        byte[] imageBytes = baos.toByteArray();

                        // Giao thức đóng gói: Gửi kích thước trước, gửi dữ liệu sau
                        dos.writeInt(imageBytes.length);
                        dos.write(imageBytes);
                        dos.flush();
                    }
                    // Nghỉ 100ms để đạt ~10 FPS, tránh quá tải mạng
                    Thread.sleep(100); 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}