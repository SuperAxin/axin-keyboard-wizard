package com.axin.axinkeyboardwizard.services.keyboard;

import com.axin.axinkeyboardwizard.services.keyboard.KeyboardService;
import com.axin.axinkeyboardwizard.services.keyboard.model.KeyboardConfig;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import org.springframework.stereotype.Component;

import java.awt.event.KeyEvent;

@Component
public class KeyboardController {

  private final KeyboardService keyboardService;

  @FXML
  private ComboBox<String> keyComboBox;

  @FXML
  private TextField intervalField;

  @FXML
  private Label statusLabel;

  @FXML
  private Button startButton;

  @FXML
  private Button stopButton;
  private Timeline statusTimer;
  public KeyboardController(KeyboardService keyboardService) {
    this.keyboardService = keyboardService;
  }

  @FXML
  public void initialize() {
    keyComboBox.getItems()
            .addAll("F", "A", "B", "C", "D", "E", "SPACE", "ENTER", "F1", "F2", "F3", "F4", "F5");

    keyComboBox.setValue("F");
    intervalField.setText("5");

    statusTimer = new Timeline(new KeyFrame(Duration.millis(200), event -> updateStatus()));

    statusTimer.setCycleCount(Timeline.INDEFINITE);
    statusTimer.play();

    updateStatus();
  }

  @FXML
  private void handleStart() {

    try {

      String selectedKey = keyComboBox.getValue();

      long intervalMillis = Long.parseLong(intervalField.getText()
              .trim());

      int keyCode = resolveKeyCode(selectedKey);

      KeyboardConfig config = new KeyboardConfig(keyCode, intervalMillis);

      keyboardService.start(config);

      updateStatus();

    } catch (NumberFormatException e) {

      showError("請輸入有效的數字");

    } catch (IllegalArgumentException | IllegalStateException e) {

      showError(e.getMessage());
    }
  }

  @FXML
  private void handleStop() {

    keyboardService.stop();

    updateStatus();
  }

  private void updateStatus() {

    boolean running = keyboardService.isRunning();

    statusLabel.setText(running ? "Running" : "Stopped");

    startButton.setDisable(running);
    stopButton.setDisable(!running);

    keyComboBox.setDisable(running);
    intervalField.setDisable(running);
  }

  private int resolveKeyCode(String key) {

    if (key == null) {
      throw new IllegalArgumentException("請選擇按鍵");
    }

    return switch (key) {

      case "A" -> KeyEvent.VK_A;
      case "B" -> KeyEvent.VK_B;
      case "C" -> KeyEvent.VK_C;
      case "D" -> KeyEvent.VK_D;
      case "E" -> KeyEvent.VK_E;
      case "F" -> KeyEvent.VK_F;

      case "SPACE" -> KeyEvent.VK_SPACE;
      case "ENTER" -> KeyEvent.VK_ENTER;

      case "F1" -> KeyEvent.VK_F1;
      case "F2" -> KeyEvent.VK_F2;
      case "F3" -> KeyEvent.VK_F3;
      case "F4" -> KeyEvent.VK_F4;
      case "F5" -> KeyEvent.VK_F5;

      default -> throw new IllegalArgumentException("不支援的按鍵：" + key);
    };
  }

  private void showError(String message) {

    Alert alert = new Alert(Alert.AlertType.ERROR);

    alert.setTitle("Keyboard Wizard");
    alert.setHeaderText("操作失敗");
    alert.setContentText(message);

    alert.showAndWait();
  }
}