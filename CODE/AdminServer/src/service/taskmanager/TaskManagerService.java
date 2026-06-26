package service.taskmanager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TaskManagerService {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    public TaskManagerService(Socket socket) {
        try {
            this.socket = socket;
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy danh sách process
    public List<String[]> getProcessList() {
    List<String[]> list = new ArrayList<>();

    try {
        out.writeUTF("GET_PROCESS_LIST");
        out.flush();

        int size = in.readInt();

        for (int i = 0; i < size; i++) {
            String name = in.readUTF();
            String pid = in.readUTF();
            String ram = in.readUTF();

            list.add(new String[]{name, pid, ram});
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

    // Kill process
    public void killProcess(String pid) {
        try {
            out.writeUTF("KILL_PROCESS");
            out.writeUTF(pid);
            out.flush();

            System.out.println("Server gửi kill PID: " + pid);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}