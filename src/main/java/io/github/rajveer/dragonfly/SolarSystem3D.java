package io.github.rajveer.dragonfly;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
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

        String[] orderedNames = {
                "Sun", "Mercury", "Venus", "Earth", "Moon",
                "Mars", "Jupiter", "Saturn", "Titan", "Uranus", "Neptune"
        };

        // Planet radii in kms
        double[] radii = {
                696340,  // Sun
                2440,    // Mercury
                6052,    // Venus
                6371,    // Earth
                1737,    // Moon
                3390,    // Mars
                69911,   // Jupiter
                58232,   // Saturn
                2575,    // Titan
                25362,   // Uranus
                24622    // Neptune
        };

        // Initial positions (x, y, z) in km and velocities (vx, vy, vz) in km/s
        double[] initialStateKm = {
                // Sun
                0, 0, 0, 0, 0, 0,
                // Mercury
                -5.67e7, -3.23e7, 2.58e6, 13.9, -40.3, -4.57,
                // Venus
                -1.04e8, -3.19e7, 5.55e6, 9.89, -33.7, -1.03,
                // Earth
                -1.47e8, -2.97e7, 2.75e4, 5.31, -29.3, 6.69e-4,
                // Moon
                -1.47e8, -2.95e7, 5.29e4, 4.53, -28.6, 6.73e-2,
                // Mars
                -2.15e8, 1.27e8, 7.94e6, -11.5, -18.7, -0.111,
                // Jupiter
                5.54e7, 7.62e8, -4.40e6, -13.2, 12.9, 5.22e-2,
                // Saturn
                1.42e9, -1.91e8, -5.33e7, 0.748, 9.55, -0.196,
                // Titan
                1.42e9, -1.92e8, -5.28e7, 5.95, 7.68, 0.254,
                // Uranus
                1.62e9, 2.43e9, -1.19e7, -5.72, 3.45, 0.087,
                // Neptune
                4.47e9, -5.31e7, -1.02e8, 0.0287, 5.47, -0.113
        };

        setup3DScene(radii, initialStateKm, orderedNames);
        setupMouseControl();
        setupSlider();

    }

    private void setup3DScene(double[] radii, double[] initialPos, String[] names) {
        planetGroup = new Group();

        setupPlanets(planetGroup, radii, initialPos,names);

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

    private void setupPlanets(Group root, double[] radii, double[] initialState, String[] planetNames) {

        // Scale factors (tweak as needed)
        double distanceScale = 1e-7; // Converts km → scene units
        double sizeScale = 0.00005;  // Converts radius km → scene units

        for (int i = 0; i < planetNames.length; i++) {
            // Extract position (skip velocities)
            double xKm = initialState[i * 6];
            double yKm = initialState[i * 6 + 1];
            double zKm = initialState[i * 6 + 2];

            // Create sphere
            Sphere planet = new Sphere(radii[i] * sizeScale);
            planet.setTranslateX(xKm * distanceScale);
            planet.setTranslateY(yKm * distanceScale);
            planet.setTranslateZ(zKm * distanceScale);

            // Give it a basic material
            PhongMaterial material = new PhongMaterial();
            if (i == 0) {
                material.setDiffuseColor(Color.YELLOW);
            } else {
                material.setDiffuseColor(Color.LIGHTGRAY);
            }
            planet.setMaterial(material);

            // Add to scene
            root.getChildren().add(planet);
        }
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
