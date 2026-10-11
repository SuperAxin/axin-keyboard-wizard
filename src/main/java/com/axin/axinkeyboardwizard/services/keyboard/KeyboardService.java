package com.axin.axinkeyboardwizard.services.keyboard;

import com.axin.axinkeyboardwizard.services.keyboard.model.KeyboardConfig;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.awt.*;

@Service
public class KeyboardService {

  private final Robot robot;
  private Thread workerThread;

  @Getter
  private volatile boolean running = false;

  public KeyboardService() throws AWTException {
    this.robot = new Robot();
  }

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

    running = true;

    workerThread = new Thread(() -> {

      try {
        while (running && !Thread.currentThread().isInterrupted()) {

          robot.keyPress(config.getKeyCode());

          try {
            Thread.sleep(100);
          } finally {
            robot.keyRelease(config.getKeyCode());
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

  public synchronized void stop() {

    running = false;

    if (workerThread != null) {
      workerThread.interrupt();
    }
  }

  @PreDestroy
  public void shutdown() {
    stop();
  }
}
