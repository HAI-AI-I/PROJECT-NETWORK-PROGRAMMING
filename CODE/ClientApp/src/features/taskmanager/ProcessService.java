package features.taskmanager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessService {
    public List<String[]> getProcessList() {
        ArrayList<String[]> list = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("tasklist");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Skip header lines
            for (int i = 0; i < 3; i++) {
                reader.readLine();
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 5) {
                    String name = parts[0];
                    String pid = parts[1];
                    String ram = parts[parts.length - 2] + " " + parts[parts.length - 1];
                    list.add(new String[]{name, pid, ram});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void killProcess(String pid) {
        try {
            Runtime.getRuntime().exec("taskkill /PID " + pid + " /F");
            System.out.println("[PROCESS] Killed PID: " + pid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}