package com.axin.axinkeyboardwizard.services.fx;

import com.axin.axinkeyboardwizard.services.keyboard.KeyboardService;
import com.axin.axinkeyboardwizard.services.keyboard.model.KeyboardConfig;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import org.springframework.stereotype.Component;

@Component
public class FxController {

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

  public FxController(KeyboardService keyboardService) {
    this.keyboardService = keyboardService;
  }

  @FXML
  public void initialize() {
    keyComboBox.getItems()
            .addAll("F", "A", "B", "C", "D", "E", "SPACE", "ENTER", "F1", "F2", "F3", "F4", "F5");

    keyComboBox.setValue("F");
    intervalField.setText("5");

    Timeline statusTimer = new Timeline(new KeyFrame(Duration.millis(200), event -> updateStatus()));

    statusTimer.setCycleCount(Timeline.INDEFINITE);
    statusTimer.play();

    updateStatus();
  }

  @FXML
  private void handleStart() {

    try {

      String selectedKey = keyComboBox.getValue();
      int keyCode = resolveKeyCode(selectedKey);

      long intervalMillis = Long.parseLong(intervalField.getText()
              .trim());

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

    statusLabel.setText(running ? "執行中" : "停止");

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

      case "A" -> NativeKeyEvent.VC_A;
      case "B" -> NativeKeyEvent.VC_B;
      case "C" -> NativeKeyEvent.VC_C;
      case "D" -> NativeKeyEvent.VC_D;
      case "E" -> NativeKeyEvent.VC_E;
      case "F" -> NativeKeyEvent.VC_F;

      case "SPACE" -> NativeKeyEvent.VC_SPACE;
      case "ENTER" -> NativeKeyEvent.VC_ENTER;

      case "F1" -> NativeKeyEvent.VC_F1;
      case "F2" -> NativeKeyEvent.VC_F2;
      case "F3" -> NativeKeyEvent.VC_F3;
      case "F4" -> NativeKeyEvent.VC_F4;
      case "F5" -> NativeKeyEvent.VC_F5;

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
