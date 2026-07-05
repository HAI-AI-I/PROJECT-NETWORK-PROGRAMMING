package features;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.*;

public class WebcamServerDemo {
    private JLabel webcamPreviewLabel;
    private volatile boolean isRunning = false;

    public WebcamServerDemo(JLabel label) {
        this.webcamPreviewLabel = label;
    }

    public void receiveWebcamStream(Socket clientSocket) {
        isRunning = true;
        try {
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());

            while (isRunning && !clientSocket.isClosed()) {
                int imageSize = dis.readInt();
                if (imageSize > 0) {
                    byte[] imageBytes = new byte[imageSize];
                    dis.readFully(imageBytes);

                    BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

                    if (originalImage != null) {
                        SwingUtilities.invokeLater(() -> {
                            // Lấy kích thước label hiện tại
                            int labelWidth = webcamPreviewLabel.getWidth();
                            int labelHeight = webcamPreviewLabel.getHeight();

                            // Nếu label chưa được resize, dùng kích thước mặc định
                            if (labelWidth <= 0) labelWidth = 420;
                            if (labelHeight <= 0) labelHeight = 340;

                            // Scale ảnh theo kích thước label
                            Image scaledImage = originalImage.getScaledInstance(
                                    labelWidth,
                                    labelHeight,
                                    Image.SCALE_SMOOTH
                            );

                            webcamPreviewLabel.setIcon(new ImageIcon(scaledImage));
                            webcamPreviewLabel.setText("");
                            webcamPreviewLabel.repaint();
                        });
                    }
                }
            }
        } catch (java.io.EOFException e) {
            System.out.println("[WEBCAM] Client disconnected");
            stopWebcamStream();
        }catch (Exception e) {
            System.out.println("[WEBCAM] Stream closed: " + e.getMessage());
            stopWebcamStream();
        }
    }

    public void stopWebcamStream() {
        isRunning = false;
        System.out.println("[WEBCAM-SERVER] Webcam stream stopped");
    }
}
