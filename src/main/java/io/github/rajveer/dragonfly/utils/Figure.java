package io.github.rajveer.dragonfly.utils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Figure} class provides a simple utility for creating and displaying
 * 2D line plots using JavaFX. It allows setting chart title and axis labels,
 * and adding multiple series from 2D point data (x, y).
 *
 * <p>This class extends {@link Application} and launches a single JavaFX window.
 * Currently, it supports one chart window per JVM launch.
 *
 * <p><b>Usage Example:</b>
 * <pre>
 * Figure.setTitle("Example Plot");
 * Figure.setXLabel("Time");
 * Figure.setYLabel("Value");
 *
 * List&lt;double[]&gt; data = new ArrayList&lt;&gt;();
 * data.add(new double[]{0, 1});
 * data.add(new double[]{1, 3});
 * data.add(new double[]{2, 2});
 *
 * Figure.addSeries("Sample Series", data);
 * Figure.show();
 * </pre>
 *
 * @author Rajveer
 */
public class Figure extends Application {

    /** The title of the chart window. */
    private static String chartTitle = "";

    /** The label for the X-axis. */
    private static String xLabel = "X";

    /** The label for the Y-axis. */
    private static String yLabel = "Y";

    /** A list to hold all the series to be plotted on the chart. */
    private static final List<XYChart.Series<Number, Number>> seriesList = new ArrayList<>();

    /**
     * Sets the title of the chart window.
     *
     * @param title the chart title to be displayed
     */
    public static void setTitle(String title) {
        chartTitle = title;
    }

    /**
     * Sets the label for the X-axis.
     *
     * @param label the X-axis label
     */
    public static void setXLabel(String label) {
        xLabel = label;
    }

    /**
     * Sets the label for the Y-axis.
     *
     * @param label the Y-axis label
     */
    public static void setYLabel(String label) {
        yLabel = label;
    }

    /**
     * Adds a new data series to the chart using a list of 2D points.
     *
     * @param label     the label/name of the series
     * @param pnts_xy   a list of double arrays representing points,
     *                  where each array is of form {x, y}
     * @throws IllegalArgumentException if the input list is null, empty,
     *                                  or any point does not have exactly 2 values
     */
    public static void addSeries(String label, List<double[]> pnts_xy) {
        if (pnts_xy == null || pnts_xy.isEmpty()) {
            throw new IllegalArgumentException("Point list cannot be null or empty");
        }

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(label);

        for (double[] point : pnts_xy) {
            if (point.length != 2) {
                throw new IllegalArgumentException("Each point must be a double array of length 2 (x, y)");
            }
            series.getData().add(new XYChart.Data<>(point[0], point[1]));
        }

        seriesList.add(series);
    }

    /**
     * Launches the JavaFX application in a new thread and displays the chart.
     * <p>This should be called only once per JVM due to JavaFX constraints.
     */
    public static void show() {
        new Thread(() -> Application.launch(Figure.class)).start();
    }

    /**
     * JavaFX entry point. Automatically called when {@link #show()} is used.
     *
     * @param stage the primary stage/window of the JavaFX application
     */
    @Override
    public void start(Stage stage) {
        stage.setTitle(chartTitle);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(xLabel);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yLabel);

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(chartTitle);
        lineChart.setCreateSymbols(false); // disables hollow symbols, just lines

        for (XYChart.Series<Number, Number> series : seriesList) {
            lineChart.getData().add(series);
        }

        VBox vbox = new VBox(lineChart);
        Scene scene = new Scene(vbox, 600, 400);
        stage.setScene(scene);
        stage.show();
    }
}
