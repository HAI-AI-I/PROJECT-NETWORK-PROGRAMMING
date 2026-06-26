// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package service.taskmanager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessService {
   public ProcessService() {
   }

   public List<String[]> getProcessList() {
      ArrayList var1 = new ArrayList();

      try {
         Process var2 = Runtime.getRuntime().exec("tasklist");
         BufferedReader var3 = new BufferedReader(new InputStreamReader(var2.getInputStream()));

         for(int var5 = 0; var5 < 3; ++var5) {
            var3.readLine();
         }

         String var4;
         while((var4 = var3.readLine()) != null) {
            String[] var10 = var4.trim().split("\\s+");
            if (var10.length >= 5) {
               String var6 = var10[0];
               String var7 = var10[1];
               String var8 = var10[var10.length - 2] + " " + var10[var10.length - 1];
               var1.add(new String[]{var6, var7, var8});
            }
         }
      } catch (Exception var9) {
         var9.printStackTrace();
      }

      return var1;
   }

   public void killProcess(String var1) {
      try {
         Runtime.getRuntime().exec("taskkill /PID " + var1 + " /F");
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }
}
