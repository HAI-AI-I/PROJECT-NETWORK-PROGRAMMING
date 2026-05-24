import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        int port=1234;//tạo port 
        try(ServerSocket serverSocket=new ServerSocket(port)) {
            // tạo Server Socket và chờ client kết nối
            System.out.println("Chờ Client kết nối");

            try(Socket socket=serverSocket.accept()) {
                System.out.println("Client đã kết nối thành công đến server");           
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
}
