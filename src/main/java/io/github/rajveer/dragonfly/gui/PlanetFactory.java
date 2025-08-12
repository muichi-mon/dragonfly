package io.github.rajveer.dragonfly.gui;

import javafx.scene.Group;
import javafx.scene.image.Image;
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
            switch (i) {
                case 0:
                    material.setDiffuseMap(new Image(PlanetFactory.class.getResource("/io/github/rajveer/dragonfly/2k_sun.jpg").toExternalForm()));
                    break;
                case 1:
                    material.setDiffuseMap(new Image(PlanetFactory.class.getResource("/io/github/rajveer/dragonfly/2k_mercury.jpg").toExternalForm()));
                    break;
                case 2:
                    material.setDiffuseMap(new Image(PlanetFactory.class.getResource("/io/github/rajveer/dragonfly/2k_venus_atmosphere.jpg").toExternalForm()));
                    break;
                case 3:
                    material.setDiffuseMap(new Image(PlanetFactory.class.getResource("/io/github/rajveer/dragonfly/2k_earth.jpg").toExternalForm()));
                    break;
                case 4:
                    material.setDiffuseMap(new Image(PlanetFactory.class.getResource("/io/github/rajveer/dragonfly/2k_moon.jpg").toExternalForm()));
                    break;
                case 5:
                    material.setDiffuseMap(new Image(PlanetFactory.class.getResource("/io/github/rajveer/dragonfly/2k_mars.jpg").toExternalForm()));
                    break;
                case 6:
                    material.setDiffuseMap(new Image(PlanetFactory.class.getResource("/io/github/rajveer/dragonfly/2k_jupiter.jpg").toExternalForm()));
                    break;
                case 7:
                    material.setDiffuseMap(new Image(PlanetFactory.class.getResource("/io/github/rajveer/dragonfly/2k_saturn.jpg").toExternalForm()));
                    break;
                case 8:
                    material.setDiffuseMap(new Image(PlanetFactory.class.getResource("/io/github/rajveer/dragonfly/2k_titan.jpg").toExternalForm()));
                    break;
                case 9:
                    material.setDiffuseMap(new Image(PlanetFactory.class.getResource("/io/github/rajveer/dragonfly/2k_uranus.jpg").toExternalForm()));
                    break;
                case 10:
                    material.setDiffuseMap(new Image(PlanetFactory.class.getResource("/io/github/rajveer/dragonfly/2k_neptune.jpg").toExternalForm()));
                    break;
                default:
                    material.setDiffuseColor(Color.LIGHTGRAY); // fallback
            }
            planet.setMaterial(material);

            root.getChildren().add(planet);
        }
    }
}
