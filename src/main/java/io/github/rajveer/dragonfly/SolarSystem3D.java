package io.github.rajveer.dragonfly;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.IOException;

public class SolarSystem3D extends Application {

    @FXML
    private StackPane subSceneContainer;

    @FXML
    private Slider daySlider;

    private Group planetGroup;
    private PerspectiveCamera camera;

    // Rotation tracking
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private Rotate rotateX;
    private Rotate rotateY;

    @FXML
    public void initialize(){
        setup3DScene();
        setupMouseControl();
        setupSlider();
    }

    private void setup3DScene() {
        planetGroup = new Group();

        // TEMP: Add one sphere as a placeholder (Sun)
        Sphere sun = new Sphere(20);
        sun.setTranslateX(0);
        sun.setTranslateY(0);
        sun.setTranslateZ(0);
        planetGroup.getChildren().add(sun);

        // Camera
        camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-200); // Move back to see objects
        camera.setNearClip(0.1);
        camera.setFarClip(10000);

        // Rotation transforms
        rotateX = new Rotate(0, Rotate.X_AXIS);
        rotateY = new Rotate(0, Rotate.Y_AXIS);
        planetGroup.getTransforms().addAll(rotateX, rotateY);

        // SubScene
        SubScene subScene = new SubScene(
                planetGroup,
                subSceneContainer.getPrefWidth(),
                subSceneContainer.getPrefHeight(),
                true,
                SceneAntialiasing.BALANCED
        );
        subScene.setFill(Color.BLACK);
        subScene.setCamera(camera);

        // Fit to StackPane
        subScene.widthProperty().bind(subSceneContainer.widthProperty());
        subScene.heightProperty().bind(subSceneContainer.heightProperty());

        subSceneContainer.getChildren().add(subScene);
    }

    private void setupMouseControl() {
        subSceneContainer.setOnMousePressed((MouseEvent event) -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });

        subSceneContainer.setOnMouseDragged((MouseEvent event) -> {
            rotateY.setAngle(anchorAngleY + (event.getSceneX() - anchorX) * 0.5);
            rotateX.setAngle(anchorAngleX - (event.getSceneY() - anchorY) * 0.5);
        });
    }

    private void setupSlider() {
        daySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            // For now: print the day index to console
            System.out.println("Day selected: " + newVal.intValue());
        });
    }

    @Override
    public void start(Stage stage) throws IOException {
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
