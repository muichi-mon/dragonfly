package io.github.rajveer.dragonfly.gui;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class PlanetFactory {

    public static void createPlanets(Group root, double[] radii, double[] initialState, String[] names) {

        for (int i = 0; i < names.length; i++) {
            double xKm = initialState[i * 6];
            double yKm = initialState[i * 6 + 1];
            double zKm = initialState[i * 6 + 2];

            Sphere planet = new Sphere(i == 0 || i == 6? radii[i] * SolarSystemData.SIZE_SCALE * (1.0/30.0) : radii[i] * SolarSystemData.SIZE_SCALE);
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

            RotateTransition rotation = new RotateTransition(Duration.seconds(getRotationPeriod(i)), planet);
            rotation.setAxis(Rotate.Z_AXIS);
            rotation.setByAngle(360);
            rotation.setCycleCount(Animation.INDEFINITE);
            rotation.setInterpolator(Interpolator.LINEAR);
            rotation.play();

            root.getChildren().add(planet);
        }
    }

    private static double getRotationPeriod(int i) {
        switch (i) {
            case 0:  return 25;  // Sun
            case 1:  return 58;  // Mercury
            case 2:  return 243; // Venus
            case 3:  return 1;   // Earth
            case 4:  return 27;  // Moon
            case 5:  return 1.03;// Mars
            case 6:  return 0.41;// Jupiter
            case 7:  return 0.45;// Saturn
            case 8:  return 16;  // Titan
            case 9:  return 0.72;// Uranus
            case 10: return 0.67;// Neptune
            default: return 1;
        }
    }
}
