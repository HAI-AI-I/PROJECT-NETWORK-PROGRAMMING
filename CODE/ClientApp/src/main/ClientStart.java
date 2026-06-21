package main;

import java.net.Socket;
import service.taskmanager.TaskCommandHandler;
import UI.UIClient;

public class ClientStart {

    public static void main(String[] args) {

        // chạy UI trước
        new UIClient().run();
        // network chạy riêng
        new Thread(() -> {
            try {

                Socket screenSocket = new Socket("localhost", 1412);
                System.out.println("Screen connected");

                new Thread(new ScreenHandler(screenSocket)).start();

                try {
                    Socket taskSocket = new Socket("localhost", 1413);
                    System.out.println("Task connected");

                    new Thread(new TaskCommandHandler(taskSocket)).start();
                } catch (Exception e) {
                    System.out.println("⚠ Task server chưa chạy");
                }

                System.out.println("Client đã kết nối server");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
