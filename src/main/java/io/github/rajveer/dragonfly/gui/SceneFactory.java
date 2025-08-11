package io.github.rajveer.dragonfly.gui;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;

public class SceneFactory {

    public static SubScene createSubScene(Group root) {
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-200);
        camera.setNearClip(0.1);
        camera.setFarClip(10000);

        SubScene subScene = new SubScene(
                root,
                800,
                600,
                true,
                SceneAntialiasing.BALANCED
        );
        subScene.setFill(Color.BLACK);
        subScene.setCamera(camera);

        return subScene;
    }
}
