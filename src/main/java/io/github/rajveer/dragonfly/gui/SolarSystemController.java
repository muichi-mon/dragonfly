package io.github.rajveer.dragonfly.gui;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

public class SolarSystemController {

    @FXML
    private StackPane subSceneContainer;

    @FXML
    private Slider daySlider;

    private Group planetGroup;
    private Rotate rotateX, rotateY;
    private double anchorX, anchorY, anchorAngleX, anchorAngleY;

    @FXML
    public void initialize() {
        setup3DScene();
        setupMouseControl();
        setupSlider();
    }

    private void setup3DScene() {
        planetGroup = new Group();

        // Add planets
        PlanetFactory.createPlanets(
                planetGroup,
                SolarSystemData.RADII,
                SolarSystemData.INITIAL_STATE,
                SolarSystemData.PLANET_NAMES
        );

        // Camera + transforms
        rotateX = new Rotate(0, Rotate.X_AXIS);
        rotateY = new Rotate(0, Rotate.Y_AXIS);
        planetGroup.getTransforms().addAll(rotateX, rotateY);

        SubScene subScene = SceneFactory.createSubScene(planetGroup);
        subScene.widthProperty().bind(subSceneContainer.widthProperty());
        subScene.heightProperty().bind(subSceneContainer.heightProperty());

        subSceneContainer.getChildren().add(subScene);
    }

    private void setupMouseControl() {
        subSceneContainer.setOnMousePressed(this::onMousePressed);
        subSceneContainer.setOnMouseDragged(this::onMouseDragged);
    }

    private void onMousePressed(MouseEvent event) {
        anchorX = event.getSceneX();
        anchorY = event.getSceneY();
        anchorAngleX = rotateX.getAngle();
        anchorAngleY = rotateY.getAngle();
    }

    private void onMouseDragged(MouseEvent event) {
        rotateY.setAngle(anchorAngleY + (event.getSceneX() - anchorX) * 0.5);
        rotateX.setAngle(anchorAngleX - (event.getSceneY() - anchorY) * 0.5);
    }

    private void setupSlider() {
        daySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Day selected: " + newVal.intValue());
        });
    }
}
