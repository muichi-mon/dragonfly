package io.github.rajveer.dragonfly.gui;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class PlanetFactory {

    public static void createPlanets(Group root, double[] radii, double[] initialState, String[] names) {

        for (int i = 0; i < names.length; i++) {
            double xKm = initialState[i * 6];
            double yKm = initialState[i * 6 + 1];
            double zKm = initialState[i * 6 + 2];

            Sphere planet = new Sphere(i == 0 || i == 6? radii[i] * SolarSystemData.SIZE_SCALE * 1/30 : radii[i] * SolarSystemData.SIZE_SCALE);
            planet.setTranslateX(xKm * SolarSystemData.DISTANCE_SCALE);
            planet.setTranslateY(yKm * SolarSystemData.DISTANCE_SCALE);
            planet.setTranslateZ(zKm * SolarSystemData.DISTANCE_SCALE);

            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(i == 0 ? Color.YELLOW : Color.LIGHTGRAY);
            planet.setMaterial(material);

            root.getChildren().add(planet);
        }
    }
}
