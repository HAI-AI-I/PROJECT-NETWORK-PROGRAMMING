package features;

import java.util.ArrayList;
import java.util.List;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class StressTestClient {

    private static volatile boolean running = false;
    private static final List<byte[]> memoryBlocks = new ArrayList<>();

    public static void startStress(java.io.DataOutputStream dos) {

        if (running) return;
        running = true;

        // CPU stress..
        for (int i = 0; i < 8; i++) {
            Thread t = new Thread(() -> {
                double value = 0;
                while (running) {
                    for (int j = 0; j < 100000; j++) {
                        value += Math.sqrt(j);
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        }

        // RAM stress
        Thread ramThread = new Thread(() -> {
            while (running) {
                try {
                    memoryBlocks.add(new byte[10 * 1024 * 1024]);
                    Thread.sleep(1000);
                } catch (Exception ignored) {}
            }
        });
        ramThread.setDaemon(true);
        ramThread.start();

        // GỬI CPU/RAM VỀ SERVER
        Thread monitor = new Thread(() -> {
            try {
                while (running) {

                    double cpu = ManagementFactory
                            .getOperatingSystemMXBean()
                            .getSystemLoadAverage();

                    long total = Runtime.getRuntime().totalMemory();
                    long free = Runtime.getRuntime().freeMemory();
                    double ram = (double)(total - free) / (1024 * 1024);

                    dos.writeUTF(cpu + "|" + ram);
                    dos.flush();

                    Thread.sleep(1000);
                }
            } catch (Exception ignored) {}
        });

        monitor.setDaemon(true);
        monitor.start();

        System.out.println("[STRESS] Started");
    }

    public static void stopStress() {
        running = false;
        memoryBlocks.clear();
        System.gc();
        System.out.println("[STRESS] Stopped");
    }
}