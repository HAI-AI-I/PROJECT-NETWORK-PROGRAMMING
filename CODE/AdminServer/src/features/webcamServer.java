import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.Socket;

public class WebcamServerDemo {
    private JLabel webcamPreviewLabel; // Label trên giao diện UI của bạn

    public WebcamServerDemo(JLabel label) {
        this.webcamPreviewLabel = label;
    }

    public void receiveWebcamStream(Socket clientSocket) {
        try {
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());

            // Vòng lặp lắng nghe liên tục (Phải nằm trong Thread riêng biệt với giao diện)
            while (true) {
                // Bước 1: Đọc 4 byte đầu tiên để biết kích thước bức ảnh
                int imageSize = dis.readInt();
                
                if (imageSize > 0) {
                    // Bước 2: Cấp phát bộ đệm và đọc chính xác số byte của ảnh
                    byte[] imageBytes = new byte[imageSize];
                    dis.readFully(imageBytes); // Đảm bảo đọc đủ data mới đi tiếp

                    // Bước 3: Giải mã mảng byte thành hình ảnh
                    ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                    BufferedImage image = ImageIO.read(bais);

                    // Bước 4: Đẩy lên giao diện Admin an toàn
                    if (image != null) {
                        SwingUtilities.invokeLater(() -> {
                            webcamPreviewLabel.setIcon(new ImageIcon(image));
                            webcamPreviewLabel.repaint();
                        });
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Mất kết nối hoặc lỗi stream: " + e.getMessage());
        }
    }
}