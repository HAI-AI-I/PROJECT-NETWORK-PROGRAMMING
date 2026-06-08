package network;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;


public class Screen {
    private DataOutputStream dos;
    private boolean isRunning = true;
    
    public Screen(DataOutputStream dos) {
        this.dos = dos;
    }

    public void start(){
        new Thread(() -> {
            try {
                Robot robot = new Robot();
                Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

                while (isRunning){
                    BufferedImage image = robot.createScreenCapture(screenRect);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "jpg", baos);

                    byte[] data = baos.toByteArray();
                    dos.writeInt(data.length);
                    dos.write(data);
                    dos.flush();

                    Thread.sleep(150); // Giới hạn tốc độ gửi hình ảnh
                }
            } catch (Exception e) {
                System.out.println("Dung gui hinh anh: man hinh.");
            }
        }).start();
    }
    public void stop(){
        isRunning = false;
    }
}
