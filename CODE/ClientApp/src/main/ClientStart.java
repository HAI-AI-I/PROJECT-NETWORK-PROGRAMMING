package main;

import java.net.Socket;
import service.taskmanager.TaskCommandHandler;
import UI.UIClient;

public class ClientStart {

    public static void main(String[] args) {

        try {

            Socket screenSocket = new Socket("localhost", 1412);
            System.out.println("Screen connected");

            Socket taskSocket = new Socket("localhost", 1413);
            System.out.println("Task connected");

            new Thread(new ScreenHandler(screenSocket)).start();
            new Thread(new TaskCommandHandler(taskSocket)).start();

            System.out.println("Client đã kết nối server");

            new UIClient().run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}