package io.github.rajveer.dragonfly.simulations;

import io.github.rajveer.dragonfly.ode.RK4Solver;
import io.github.rajveer.dragonfly.systems.FitzHughNagumoSystem;
import io.github.rajveer.dragonfly.systems.ODESystem;
import io.github.rajveer.dragonfly.utils.Figure;
import io.github.rajveer.dragonfly.utils.Vector;

import java.util.ArrayList;
import java.util.List;

public class FitzHughNagumoSim {

    public static void main(String[] args) {

        // Model setup
        ODESystem neuron = new FitzHughNagumoSystem(
                0.08,  // epsilon
                0.7,   // a
                0.8,   // b
                0.5    // I_ext
        );

        RK4Solver solver = new RK4Solver();
        Vector state = new Vector(new double[]{0.0, 0.0}); // [V, W]
        double t = 0, dt = 0.1;

        List<double[]> voltageSeries = new ArrayList<>();
        List<double[]> recoverySeries = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            double V = state.get(0);
            double W = state.get(1);

            // System.out.printf("t=%.2f, V=%.4f, W=%.4f%n", t, V, W);

            voltageSeries.add(new double[]{t, V});
            recoverySeries.add(new double[]{t, W});

            state = solver.step(neuron, t, state, dt);
            t += dt;
        }

        // Configure and show the figure
        Figure.setTitle("FitzHugh-Nagumo Neuron Simulation");
        Figure.setXLabel("Time");
        Figure.setYLabel("Values");
        Figure.addSeries("Membrane Voltage (V)", voltageSeries);
        Figure.addSeries("Recovery Variable (W)", recoverySeries);
        Figure.show();
    }
}
