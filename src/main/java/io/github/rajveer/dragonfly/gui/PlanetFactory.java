package io.github.rajveer.dragonfly.gui;

import io.github.rajveer.dragonfly.utils.ImageUtils;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.Group;
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
                    material.setDiffuseMap(ImageUtils.rotateLeft("/io/github/rajveer/dragonfly/2k_sun.jpg"));
                    break;
                case 1:
                    material.setDiffuseMap(ImageUtils.rotateLeft("/io/github/rajveer/dragonfly/2k_mercury.jpg"));
                    break;
                case 2:
                    material.setDiffuseMap(ImageUtils.rotateLeft("/io/github/rajveer/dragonfly/2k_venus_atmosphere.jpg"));
                    break;
                case 3:
                    material.setDiffuseMap(ImageUtils.rotateLeft("/io/github/rajveer/dragonfly/2k_earth.jpg"));
                    break;
                case 4:
                    material.setDiffuseMap(ImageUtils.rotateLeft("/io/github/rajveer/dragonfly/2k_moon.jpg"));
                    break;
                case 5:
                    material.setDiffuseMap(ImageUtils.rotateLeft("/io/github/rajveer/dragonfly/2k_mars.jpg"));
                    break;
                case 6:
                    material.setDiffuseMap(ImageUtils.rotateLeft("/io/github/rajveer/dragonfly/2k_jupiter.jpg"));
                    break;
                case 7:
                    // Rotate Saturn texture right for proper alignment
                    material.setDiffuseMap(ImageUtils.rotateRight("/io/github/rajveer/dragonfly/2k_saturn.jpg"));
                    break;
                case 8:
                    material.setDiffuseMap(ImageUtils.rotateLeft("/io/github/rajveer/dragonfly/2k_titan.jpg"));
                    break;
                case 9:
                    // Rotate Uranus texture right
                    material.setDiffuseMap(ImageUtils.rotateRight("/io/github/rajveer/dragonfly/2k_uranus.jpg"));
                    break;
                case 10:
                    material.setDiffuseMap(ImageUtils.rotateLeft("/io/github/rajveer/dragonfly/2k_neptune.jpg"));
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
