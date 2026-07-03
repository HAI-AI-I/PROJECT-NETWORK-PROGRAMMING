package features;

import java.io.DataInputStream;
import javax.swing.JTextArea;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

public class StressTestServerDemo extends Thread {

    private DataInputStream dis;
    private JTextArea resultArea;

    // ====== SYSTEM MONITOR NHÚT VÀO ĐÂY LUÔN ======
    private static final OperatingSystemMXBean osBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private static double getCpuUsage() {
        double cpu = osBean.getSystemCpuLoad() * 100;
        return (cpu < 0) ? 0 : cpu;
    }

    private static double getRamUsage() {
        long total = osBean.getTotalPhysicalMemorySize();
        long free = osBean.getFreePhysicalMemorySize();
        return ((double) (total - free) / total) * 100;
    }
    // ==============================================

    public StressTestServerDemo(DataInputStream dis, JTextArea resultArea) {
        this.dis = dis;
        this.resultArea = resultArea;
    }

    @Override
    public void run() {

        try {
            while (true) {

                String message = dis.readUTF();

                String[] parts = message.split("\\|");

                String cpu = parts.length > 0 ? parts[0] : "0";
                String ram = parts.length > 1 ? parts[1] : "0";

                resultArea.setText(
                        "Stress Test Console\n"
                                + "-------------------\n"
                                + "Client: LIVE\n"
                                + "Status: Running\n"
                                + "CPU Usage : " + cpu + "%\n"
                                + "RAM Usage : " + ram + " MB\n"
                );
            }

        } catch (Exception e) {
            System.out.println("[STRESS SERVER] " + e.getMessage());
        }
    }
}