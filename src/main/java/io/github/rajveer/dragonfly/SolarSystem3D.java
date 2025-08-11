package io.github.rajveer.dragonfly;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SolarSystem3D extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                SolarSystem3D.class.getResource("solar-system-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        stage.setTitle("Solar System 3D Viewer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
