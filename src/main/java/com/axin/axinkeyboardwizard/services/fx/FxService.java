package com.axin.axinkeyboardwizard.services.fx;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class FxService {

  private final ApplicationContext context;

  public FxService(ApplicationContext context) {
    this.context = context;
  }

  public void start() {
    Platform.startup(() -> {
      try {
        MainWindow();
      } catch (IOException e) {
        e.printStackTrace();
        Platform.exit();
      }
    });
  }

  public void MainWindow() throws IOException {
    FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/view.fxml")
    );

    loader.setControllerFactory(context::getBean);

    Scene scene = new Scene(loader.load(), 450, 400);

    Stage stage = new Stage();
    stage.setTitle("Axin Keyboard Wizard");
    stage.setScene(scene);

    stage.show();
  }

}