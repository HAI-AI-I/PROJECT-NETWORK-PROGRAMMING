package features.taskmanager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class ProcessService {

    public List<String[]> getProcessList() {
        ArrayList<String[]> list = new ArrayList<>();
        Map<String, ProcessInfo> processMap = new TreeMap<>(); // ← Dùng TreeMap để sort
        
        try {
            Process process = Runtime.getRuntime().exec("tasklist");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Skip header lines
            for (int i = 0; i < 3; i++) {
                reader.readLine();
            }

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                if (parts.length < 3) continue;

                String name = parts[0].toLowerCase(); // ← Lowercase để so sánh
                String pid = parts[1];
                String ramStr = parts[parts.length - 2];
                
                // Parse memory
                long ramKB = 0;
                try {
                    ramKB = Long.parseLong(ramStr.replace(",", ""));
                } catch (Exception e) {
                    continue;
                }

                // ← Filter: Bỏ process memory < 5MB
                if (ramKB < 5000) {
                    continue;
                }

                // ← Ghép những process cùng tên
                if (processMap.containsKey(name)) {
                    ProcessInfo info = processMap.get(name);
                    info.addPid(pid);
                    info.addMemory(ramKB);
                    System.out.println("[PROCESS] Merged: " + name + " PID=" + pid + " RAM=" + ramKB + "KB");
                } else {
                    ProcessInfo info = new ProcessInfo(name, pid, ramKB);
                    processMap.put(name, info);
                    System.out.println("[PROCESS] New: " + name + " PID=" + pid + " RAM=" + ramKB + "KB");
                }
            }

            // Convert map thành list
            for (ProcessInfo info : processMap.values()) {
                list.add(new String[]{
                    info.displayName,
                    info.getDisplayPid(),
                    info.getTotalMemory()
                });
            }

            // Sort theo memory giảm dần
            list.sort((a, b) -> {
                long memA = parseMemory(a[2]);
                long memB = parseMemory(b[2]);
                return Long.compare(memB, memA);
            });

            System.out.println("[PROCESS] Total unique processes: " + list.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static long parseMemory(String memStr) {
        try {
            return Long.parseLong(memStr.replace(",", "").replace("K", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public void killProcess(String pid) {
        try {
            Runtime.getRuntime().exec("taskkill /PID " + pid + " /F");
            System.out.println("[PROCESS] Killed PID: " + pid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Class để lưu thông tin process
    private static class ProcessInfo {
        String displayName; // Tên gốc (không lowercase)
        List<String> pids = new ArrayList<>();
        long totalMemory = 0;

        ProcessInfo(String name, String pid, long memory) {
            this.displayName = name; // Giữ tên gốc
            this.pids.add(pid);
            this.totalMemory = memory;
        }

        void addPid(String pid) {
            if (!pids.contains(pid)) { // Tránh duplicate PID
                this.pids.add(pid);
            }
        }

        void addMemory(long memory) {
            this.totalMemory += memory;
        }

        String getDisplayPid() {
            if (pids.size() == 1) {
                return pids.get(0);
            }
            // Hiển thị PID đầu tiên + số lượng instances
            return pids.get(0) + " (+" + (pids.size() - 1) + ")";
        }

        String getTotalMemory() {
            return String.format("%,d K", totalMemory); // Format: 100,512 K
        }
    }
}