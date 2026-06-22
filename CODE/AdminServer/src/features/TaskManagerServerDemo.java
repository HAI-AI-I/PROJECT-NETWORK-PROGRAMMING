package features;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TaskManagerServerDemo {
    private JTable table;
    private DefaultTableModel model;
    private DataInputStream dis;
    private DataOutputStream dos;

    public TaskManagerServerDemo(JTable table, DefaultTableModel model) {
        this.table = table;
        this.model = model;
    }

    public void handleTaskManager(Socket clientSocket) {
    try {
        clientSocket.setSoTimeout(30000); // ← Timeout 30 giây
        dis = new DataInputStream(clientSocket.getInputStream());
        dos = new DataOutputStream(clientSocket.getOutputStream());

        System.out.println("[TASK-SERVER] Connected, waiting for data...");

        while (true) {
            try {
                int size = dis.readInt();
                System.out.println("[TASK-SERVER] Received " + size + " processes");

                String[][] data = new String[size][3];
                for (int i = 0; i < size; i++) {
                    data[i][0] = dis.readUTF();
                    data[i][1] = dis.readUTF();
                    data[i][2] = dis.readUTF();
                }

                System.out.println("[TASK-SERVER] Read completed, updating table...");

                SwingUtilities.invokeLater(() -> {
                    model.setRowCount(0);
                    for (String[] row : data) {
                        model.addRow(row);
                    }
                    System.out.println("[TASK-SERVER] Table updated with " + size + " rows");
                });

            } catch (java.net.SocketTimeoutException e) {
                System.out.println("[TASK-SERVER] Timeout, closing connection");
                break;
            } catch (Exception e) {
                System.out.println("[TASK-SERVER] Error: " + e.getMessage());
                break;
            }
        }
    } catch (Exception e) {
        System.out.println("[TASK-SERVER] Failed: " + e.getMessage());
    }
}
}