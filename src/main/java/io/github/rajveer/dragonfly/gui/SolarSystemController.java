package io.github.rajveer.dragonfly.gui;

import io.github.rajveer.dragonfly.ode.ODESolver;
import io.github.rajveer.dragonfly.ode.RK4Solver;
import io.github.rajveer.dragonfly.systems.SolarSystem;
import io.github.rajveer.dragonfly.utils.Vector;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;

public class SolarSystemController {

    @FXML
    private StackPane subSceneContainer;

    @FXML
    private Slider daySlider;

    private Group planetGroup;
    private List<Node> planetNodes = new ArrayList<>();
    private Rotate rotateX, rotateY;
    private double anchorX, anchorY, anchorAngleX, anchorAngleY;

    @FXML
    public void initialize() {
        setup3DScene();
        setupMouseControl();

        // Create SolarSystem and RK4 solver
        List<Vector> trajectory = getVectors(
                SolarSystemData.MASS,
                SolarSystemData.INITIAL_STATE
        );

        setupSlider(trajectory);
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

        // Store references to each planet sphere
        planetNodes.addAll(planetGroup.getChildren());

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

    private void setupSlider(List<Vector> trajectory) {
        daySlider.setMin(0);
        daySlider.setMax(trajectory.size() - 1);
        daySlider.setValue(0);

        daySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int dayIndex = newVal.intValue();
            Vector state = trajectory.get(dayIndex);

            for (int i = 0; i < planetNodes.size(); i++) {
                int offset = i * 6;
                double px = state.get(offset);
                double py = state.get(offset + 1);
                double pz = state.get(offset + 2);

                double scale = 1e-6; // adjust for scene size
                planetNodes.get(i).setTranslateX(px * SolarSystemData.DISTANCE_SCALE);
                planetNodes.get(i).setTranslateY(py * SolarSystemData.DISTANCE_SCALE);
                planetNodes.get(i).setTranslateZ(pz * SolarSystemData.DISTANCE_SCALE);
            }
        });
    }


    private static List<Vector> getVectors(List<Double> masses, double[] initialStateKm) {
        SolarSystem solarSystem = new SolarSystem(masses);
        ODESolver solver = new RK4Solver(); // or just: RK4Solver rk4 = new RK4Solver();

        // Simulation parameters
        double t0 = 0;
        double tEnd = SolarSystemData.SECONDS_PER_DAY * 365; // 1 year
        double dt = SolarSystemData.SECONDS_PER_DAY;         // 1 day

        Vector y = new Vector(initialStateKm);
        double t = t0;
        List<Vector> trajectory = new ArrayList<>();

        while (t < tEnd) {
            trajectory.add(y);
            y = solver.step(solarSystem, t, y, dt);
            t += dt;
        }
        return trajectory;
    }
}
