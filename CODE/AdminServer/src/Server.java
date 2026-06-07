import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public class Server {
    private static Robot robot;
    private static final int port=1234;//tạo port 
    public static void main(String[] args) {
        try(ServerSocket serverSocket=new ServerSocket(port)) {
            robot=new Robot();
            // tạo Server Socket và chờ client kết nối
            System.out.println("Chờ Client kết nối");

            try(Socket socket=serverSocket.accept()) {
                System.out.println("Client đã kết nối thành công đến server");  
                
                new Thread(()->sendScreen(socket)).start(); 
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (BindException be) {
            System.out.println("Port đã được dùng, dùng port khác");
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("các lỗi khác");
        }
    }
    private static void sendScreen(Socket socket){
        try {
            // cấu hình lại bộ nén jpeg để tối ưu băng thông      
            Iterator<ImageWriter> writers=ImageIO.getImageWritersByFormatName("jpg");
            ImageWriter writer=writers.next();
            ImageWriteParam param=writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.6f);//giảm chất lượng ảnh xuống 60%
            
            Rectangle screenRect=new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
            while(!socket.isClosed()){
                // chụp ảnh
                BufferedImage image=robot.createScreenCapture(screenRect);
                IIOImage iioimage=new IIOImage(image, null, null);
    
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                MemoryCacheImageOutputStream mcios=new MemoryCacheImageOutputStream(baos);
                writer.setOutput(mcios);
                writer.write(null, iioimage, param);
                mcios.close();
    
                byte[] bytes=baos.toByteArray();
    
                dos.writeInt(bytes.length);
                dos.write(bytes);
                dos.flush();
    
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
