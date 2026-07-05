package features;

import com.sun.management.OperatingSystemMXBean;
import java.io.DataOutputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class StressTestService {
    private volatile boolean running = false;
    private DataOutputStream dos;

    public StressTestService(DataOutputStream dos) {
        this.dos = dos;
    }
    private double currentCpuUsage = 0;
    private long currentMemoryUsage = 0;

    // CPU Stress Test
    public void startCpuStress(int percentage) {
        if (running) {
            System.out.println("[STRESS] Already running");
            return;
        }

        running = true;
        System.out.println("[STRESS] CPU test started - " + percentage + "%");
        
        long testDuration = 60000; // 60 giây
        long startTime = System.currentTimeMillis();
        
        int threadCount = Math.max(1, (percentage / 25));
        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(() -> {
                double value = 0;
                while (running && (System.currentTimeMillis() - startTime) < testDuration) {
                    for (int j = 0; j < 100000; j++) {
                        value += Math.sqrt(j);
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        }

        // Monitor thread
        Thread monitor = new Thread(() -> {
            try {
                OperatingSystemMXBean osBean =
                    (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

                while (running && (System.currentTimeMillis() - startTime) < testDuration) {
                    double cpu = osBean.getSystemCpuLoad() * 100;
                    if (cpu < 0) cpu = 0;

                    currentCpuUsage = cpu;
                    System.out.println("[STRESS-CPU] Usage: " + String.format("%.2f", cpu) + "%");

                    Thread.sleep(1000);
                }
                
                running = false;
                System.out.println("[STRESS] CPU test stopped");
            } catch (Exception e) {
                System.out.println("[STRESS] Monitor error: " + e.getMessage());
            }
        });
        monitor.setDaemon(true);
        monitor.start();
    }

    // Memory Stress Test
    public void startMemoryStress(int percentageOfMax) {
    if (running) {
        System.out.println("[STRESS] Already running");
        return;
    }

    running = true;
    System.out.println("[STRESS] Memory test started - " + percentageOfMax + "%");
    
    Runtime runtime = Runtime.getRuntime();
    long maxMemory = runtime.maxMemory();
    long targetMemory = (maxMemory * percentageOfMax) / 100;
    
    List<byte[]> memoryBlocks = new ArrayList<>();

    Thread memThread = new Thread(() -> {  // Tạo Thread object
        try {
            long testDuration = 60000;
            long startTime = System.currentTimeMillis();
            
            while (running && (System.currentTimeMillis() - startTime) < testDuration) {
                long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                
                if (usedMemory < targetMemory) {
                    byte[] block = new byte[10 * 1024 * 1024];
                    memoryBlocks.add(block);
                    
                    currentMemoryUsage = (usedMemory * 100) / maxMemory;
                    System.out.println("[STRESS-MEM] Usage: " + String.format("%.2f", currentMemoryUsage) + "%");
                } else {
                    break;
                }
                
                Thread.sleep(500);
            }
            
            running = false;
            memoryBlocks.clear();
            System.gc();
            System.out.println("[STRESS] Memory test stopped");
        } catch (Exception e) {
            System.out.println("[STRESS-MEM] Error: " + e.getMessage());
        }
    });
    memThread.setDaemon(true);  // Gọi setDaemon trên Thread object
    memThread.start();  // Gọi start trên Thread object
}

    // Disk I/O Stress Test
    public void startDiskStress(int durationSeconds) {
    if (running) {
        System.out.println("[STRESS] Already running");
        return;
    }

    running = true;
    System.out.println("[STRESS] Disk test started - " + durationSeconds + "s");
    
    Thread diskThread = new Thread(() -> {  // Tạo Thread object
        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            long startTime = System.currentTimeMillis();
            long testDuration = durationSeconds * 1000L;
            int fileCount = 0;
            
            while (running && (System.currentTimeMillis() - startTime) < testDuration) {
                String filename = tempDir + "stress_test_" + fileCount + ".tmp";
                java.nio.file.Files.write(
                    java.nio.file.Paths.get(filename),
                    new byte[1024 * 1024]
                );
                
                java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(filename));
                
                fileCount++;
                System.out.println("[STRESS-DISK] Files I/O: " + fileCount);
                
                Thread.sleep(100);
            }
            
            running = false;
            System.out.println("[STRESS] Disk test stopped");
        } catch (Exception e) {
            System.out.println("[STRESS-DISK] Error: " + e.getMessage());
        }
    });
    diskThread.setDaemon(true);  // Gọi setDaemon trên Thread object
    diskThread.start();  // Gọi start trên Thread object
}

    public void stop() {
        running = false;
        System.out.println("[STRESS] Stopped");
    }

    public boolean isRunning() {
        return running;
    }

    public double getCpuUsage() {
        return currentCpuUsage;
    }

    public long getMemoryUsage() {
        return currentMemoryUsage;
    }
}