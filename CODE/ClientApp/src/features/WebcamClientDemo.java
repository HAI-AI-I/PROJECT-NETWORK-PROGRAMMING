package features;

import com.github.sarxos.webcam.Webcam;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class WebcamClientDemo {
    public void startWebcamStream(Socket socket) {
        try {
            Webcam webcam = Webcam.getDefault();
            System.out.println("[WEBCAM-CLIENT] Webcam: " + webcam);
            
            if (webcam == null) {
                System.out.println("[WEBCAM-CLIENT] Không có webcam!");
                return;
            }

            webcam.open();
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            System.out.println("[WEBCAM-CLIENT] Started streaming...");

            while (true) {
                BufferedImage image = webcam.getImage();
                if (image != null) {
                    // Nén ảnh thành JPEG
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "JPG", baos);
                    byte[] imageBytes = baos.toByteArray();

                    // Gửi kích thước + dữ liệu
                    dos.writeInt(imageBytes.length);
                    dos.write(imageBytes);
                    dos.flush();
                }

                Thread.sleep(100);  // 10 FPS
            }
        } catch (Exception e) {
            System.out.println("[WEBCAM-CLIENT] Stream stopped: " + e.getMessage());
        }
    }
}