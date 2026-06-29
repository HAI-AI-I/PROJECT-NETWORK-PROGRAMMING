package features;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TaskManagerService {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public TaskManagerService(Socket socket) {
        try {
            this.socket = socket;

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================
    // GET PROCESS LIST
    // ==========================
    public synchronized List<String[]> getProcessList() {

        List<String[]> list = new ArrayList<>();

        try {

            System.out.println("[SERVER] Send GET_PROCESS_LIST");

            out.writeUTF("GET_PROCESS_LIST");
            out.flush();

            int size = in.readInt();

            System.out.println("[SERVER] Receive " + size + " processes");

            for (int i = 0; i < size; i++) {

                String name = in.readUTF();
                String pid = in.readUTF();
                String ram = in.readUTF();

                list.add(new String[]{name, pid, ram});
            }

            System.out.println("[SERVER] Finish receive");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ==========================
    // KILL PROCESS
    // ==========================
    public synchronized void killProcess(String pid) {

        try {

            out.writeUTF("KILL_PROCESS");
            out.writeUTF(pid);
            out.flush();

            System.out.println("[SERVER] Kill PID = " + pid);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}