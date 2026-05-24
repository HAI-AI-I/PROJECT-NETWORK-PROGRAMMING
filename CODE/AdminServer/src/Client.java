import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) {
        int port=1234;
        String address="localhost";
        try (Socket socket=new Socket(address,port)) {
            System.out.println("Kết nối đến server thành công");
        } catch (UnknownHostException ue) {
            System.out.println("không thể phân giải tên miền");
        }
        catch(ConnectException ce){
            System.out.println("Máy chủ từ chối kết nối");
        }
        catch(NoRouteToHostException nrthe){
            System.out.println("mạng của bạn bị ngắt hoặc router không tìm thấy đường đi");
        }
        catch(IOException e){
            System.out.println("các lỗi khác");
        }
    }
}
