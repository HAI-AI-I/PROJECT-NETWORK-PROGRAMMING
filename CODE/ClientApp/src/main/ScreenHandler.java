package main;

import java.net.Socket;

public class ScreenHandler implements Runnable {

    private Socket socket;

    public ScreenHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("ScreenHandler started: " + socket);
    }
}