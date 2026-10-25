package com.axin.axinkeyboardwizard.services.keyboard;

import com.axin.axinkeyboardwizard.services.keyboard.model.KeyboardConfig;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import org.springframework.stereotype.Service;
import com.sun.jna.Platform;

@Service
public class KeyboardService {

  private Thread workerThread;

  @Getter
  private volatile boolean running = false;

  public synchronized void start(KeyboardConfig config) {
    if (running) {
      throw new IllegalStateException(
              "已經正在執行"
      );
    }

    if (workerThread != null && workerThread.isAlive()) {
      throw new IllegalStateException(
              "上一個執行緒還在停止"
      );
    }

    if (Platform.isWindows()) {
      WindowsKeyboardInput.createInput(config.getKeyCode(), false);
    } else {
      ensureNativeHookRegistered();
    }

    running = true;

    workerThread = new Thread(() -> {

      try {
        while (running && !Thread.currentThread().isInterrupted()) {

          postKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, config.getKeyCode());

          try {
            Thread.sleep(100);
          } finally {
            postKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, config.getKeyCode());
          }

          Thread.sleep(config.getIntervalMillis() * 1000 );
        }

      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();

      } catch (Exception e) {
        e.printStackTrace();

      } finally {
        running = false;
      }

    }, "keyboard-worker");

    workerThread.setDaemon(true);
    workerThread.start();
  }

  private void ensureNativeHookRegistered() {
    if (GlobalScreen.isNativeHookRegistered()) {
      return;
    }

    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException e) {
      throw new IllegalStateException(
              "無法啟用鍵盤控制，請在 macOS 系統設定中允許本程式使用輔助使用權限",
              e
      );
    }
  }

  private void postKeyEvent(int eventType, int keyCode) {
    if (Platform.isWindows()) {
      WindowsKeyboardInput.send(keyCode, eventType == NativeKeyEvent.NATIVE_KEY_RELEASED);
      return;
    }

    NativeKeyEvent event = new NativeKeyEvent(
            eventType,
            0,
            0,
            keyCode,
            NativeKeyEvent.CHAR_UNDEFINED,
            NativeKeyEvent.KEY_LOCATION_STANDARD
    );

    GlobalScreen.postNativeEvent(event);
  }

  public synchronized void stop() {

    running = false;

    if (workerThread != null) {
      workerThread.interrupt();
    }
  }

  @PreDestroy
  public void shutdown() {
    stop();

    if (GlobalScreen.isNativeHookRegistered()) {
      try {
        GlobalScreen.unregisterNativeHook();
      } catch (NativeHookException e) {
        System.err.println("無法關閉 JNativeHook：" + e.getMessage());
      }
    }
  }
}
