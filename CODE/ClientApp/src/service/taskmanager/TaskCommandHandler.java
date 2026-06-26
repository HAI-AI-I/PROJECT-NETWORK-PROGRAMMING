package service.taskmanager;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class TaskCommandHandler implements Runnable {

    private Socket socket;

    public TaskCommandHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream in =
                new DataInputStream(socket.getInputStream());

            DataOutputStream out =
                new DataOutputStream(socket.getOutputStream());
            ProcessService ps = new ProcessService();

            while (true) {
                String command = in.readUTF();

                switch (command) {

                    case "GET_PROCESS_LIST":
                        List<String[]> list = ps.getProcessList();

                        out.writeInt(list.size());

                        for (String[] p : list) {
                        out.writeUTF(p[0]); // name
                        out.writeUTF(p[1]); // pid
                        out.writeUTF(p[2]); // ram
                    }
                        out.flush();
                        break;

                    case "KILL_PROCESS":
                        String pid = in.readUTF();
                        System.out.println("Nhận lệnh kill PID: " + pid);

                        ps.killProcess(pid);
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}