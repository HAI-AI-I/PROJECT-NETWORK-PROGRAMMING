package ui.taskmanager;

import features.TaskManagerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TaskManagerPanel extends JPanel {

    private TaskManagerService service;
    private JTable table;
    private DefaultTableModel model;

    public TaskManagerPanel(TaskManagerService service) {
        this.service = service;
        setLayout(new BorderLayout());

        // TABLE
        model = new DefaultTableModel(new String[]{"Name", "PID", "RAM"}, 0);
        table = new JTable(model);

        JScrollPane scroll = new JScrollPane(table);

        // BUTTON
        JButton btnRefresh = new JButton("Refresh");
        JButton btnKill = new JButton("Kill");

        JPanel top = new JPanel();
        top.add(btnRefresh);
        top.add(btnKill);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // =========================
        // REFRESH (KHÔNG ĐƯỢC BLOCK UI)
        // =========================
        btnRefresh.addActionListener(e -> {
    model.setRowCount(0);

    List<String[]> list = service.getProcessList();

    for (String[] p : list) {
        model.addRow(p);
    }
});

        // =========================
        // KILL PROCESS
        // =========================
        btnKill.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn process trước!");
                return;
            }

            String selected = model.getValueAt(row, 0).toString();

            // TÁCH PID
            String[] parts = selected.trim().split("\\s+");

            String pid = model.getValueAt(row, 1).toString();

            for (String part : parts) {
                if (part.matches("\\d+")) {
                    pid = part;
                    break;
                }
            }

            if (pid != null) {
                String finalPid = pid;

                System.out.println("UI gửi kill PID: " + finalPid);

                // KHÔNG BLOCK UI
                new Thread(() -> {
                    service.killProcess(finalPid);
                }).start();

            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy PID!");
            }
        });
    }

}