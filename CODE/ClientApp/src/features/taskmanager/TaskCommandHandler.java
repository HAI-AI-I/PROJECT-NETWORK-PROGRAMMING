package features.taskmanager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;

public class TaskCommandHandler implements Runnable {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ProcessService ps;

    public TaskCommandHandler(Socket socket) {
        this.socket = socket;
        this.ps = new ProcessService();
    }

    @Override
    public void run() {
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            System.out.println("[TASK-HANDLER] Ready");

            // Gửi process list ngay khi kết nối
            sendProcessList();

            while (true) {
                try {
                    String command = dis.readUTF();
                    System.out.println("[TASK-HANDLER] Received: " + command);

                    if (command.equals("GET_PROCESS_LIST")) {
                        sendProcessList();
                    }
                    else if (command.equals("KILL_PROCESS")) {
                        String pid = dis.readUTF();
                        System.out.println("[TASK-HANDLER] Kill: " + pid);
                        ps.killProcess(pid);
                        
                        // ← QUAN TRỌNG: Gửi process list lại sau khi kill
                        Thread.sleep(500); // Chờ process thực sự bị kill
                        sendProcessList();
                    }
                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("[TASK-HANDLER] Closed");
        }
    }

    private void sendProcessList() {
        try {
            List<String[]> list = ps.getProcessList();
            System.out.println("[TASK-HANDLER] Sending " + list.size() + " processes");
            
            dos.writeInt(list.size());
            for (String[] p : list) {
                dos.writeUTF(p[0]);
                dos.writeUTF(p[1]);
                dos.writeUTF(p[2]);
            }
            dos.flush();
        } catch (Exception ex) {
            System.out.println("[TASK-HANDLER] Error: " + ex.getMessage());
        }
    }
}