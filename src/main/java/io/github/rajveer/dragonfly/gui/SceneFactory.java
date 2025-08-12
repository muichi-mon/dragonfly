package io.github.rajveer.dragonfly.gui;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

public class SceneFactory {

    public static SubScene createSubScene(Group root) {
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1000);
        camera.setNearClip(0.1);
        camera.setFarClip(10000);

        SubScene subScene = new SubScene(
                root,
                800,
                600,
                true,
                SceneAntialiasing.BALANCED
        );
        subScene.setFill(Color.TRANSPARENT);
        subScene.setCamera(camera);

        // Zoom handling
        subScene.addEventHandler(ScrollEvent.SCROLL, event -> {
            double zoomSpeed = 20; // Smaller = slower zoom
            double newZ = camera.getTranslateZ() + event.getDeltaY() * zoomSpeed * -0.05;

            if (newZ < -300 && newZ > -3000) {
                camera.setTranslateZ(newZ);
            }

        });

        return subScene;
    }
}
