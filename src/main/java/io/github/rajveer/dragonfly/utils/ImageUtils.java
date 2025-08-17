package io.github.rajveer.dragonfly.utils;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class ImageUtils {

    /**
     * Rotates an image 90 degrees counterclockwise (to the left).
     *
     * @param resourcePath Path to the image resource in the classpath.
     * @return The rotated Image.
     */
    public static Image rotateLeft(String resourcePath) {
        Image original = new Image(ImageUtils.class.getResource(resourcePath).toExternalForm());
        return rotateImage(original, -90);
    }

    /**
     * Rotates an image 90 degrees clockwise (to the right).
     *
     * @param resourcePath Path to the image resource in the classpath.
     * @return The rotated Image.
     */
    public static Image rotateRight(String resourcePath) {
        Image original = new Image(ImageUtils.class.getResource(resourcePath).toExternalForm());
        return rotateImage(original, 90);
    }

    /**
     * Helper method to rotate an image by a given angle.
     * Positive angle = clockwise, Negative = counterclockwise.
     */
    private static Image rotateImage(Image original, double angle) {
        Canvas canvas = new Canvas(original.getHeight(), original.getWidth());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        if (angle == 90) {
            gc.translate(original.getHeight(), 0);
        } else if (angle == -90) {
            gc.translate(0, original.getWidth());
        }
        gc.rotate(angle);
        gc.drawImage(original, 0, 0);

        WritableImage rotated = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, rotated);

        return rotated;
    }
}
