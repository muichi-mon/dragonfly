package io.github.rajveer.dragonfly.gui;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class PlanetFactory {

    public static void createPlanets(Group root, double[] radii, double[] initialState, String[] names) {
        double distanceScale = 1e-7;
        double sizeScale = 0.00005;

        for (int i = 0; i < names.length; i++) {
            double xKm = initialState[i * 6];
            double yKm = initialState[i * 6 + 1];
            double zKm = initialState[i * 6 + 2];

            Sphere planet = new Sphere(radii[i] * sizeScale);
            planet.setTranslateX(xKm * distanceScale);
            planet.setTranslateY(yKm * distanceScale);
            planet.setTranslateZ(zKm * distanceScale);

            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(i == 0 ? Color.YELLOW : Color.LIGHTGRAY);
            planet.setMaterial(material);

            root.getChildren().add(planet);
        }
    }
}
