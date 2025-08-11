package io.github.rajveer.dragonfly.simulations;

import io.github.rajveer.dragonfly.systems.SolarSystem;
import io.github.rajveer.dragonfly.utils.Figure;
import io.github.rajveer.dragonfly.utils.Vector;
import io.github.rajveer.dragonfly.ode.RK4Solver;
import io.github.rajveer.dragonfly.ode.ODESolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolarSystemSim {

    public static void main(String[] args) {
        // Masses of celestial bodies in kg
        List<Double> masses = Arrays.asList(
                1.99e30, 3.30e23, 4.87e24, 5.97e24, 7.35e22,
                6.42e23, 1.90e27, 5.68e26, 1.35e23, 8.68e25, 1.02e26
        );

        double[] radii = {
                696340,  // Sun
                2440,    // Mercury
                6052,    // Venus
                6371,    // Earth
                1737,    // Moon
                3390,    // Mars
                69911,   // Jupiter
                58232,   // Saturn
                2575,    // Titan
                25362,   // Uranus
                24622    // Neptune
        };


        // Initial positions (x, y, z) in km and velocities (vx, vy, vz) in km/s
        double[] initialStateKm = {
                // Sun
                0, 0, 0, 0, 0, 0,
                // Mercury
                -5.67e7, -3.23e7, 2.58e6, 13.9, -40.3, -4.57,
                // Venus
                -1.04e8, -3.19e7, 5.55e6, 9.89, -33.7, -1.03,
                // Earth
                -1.47e8, -2.97e7, 2.75e4, 5.31, -29.3, 6.69e-4,
                // Moon
                -1.47e8, -2.95e7, 5.29e4, 4.53, -28.6, 6.73e-2,
                // Mars
                -2.15e8, 1.27e8, 7.94e6, -11.5, -18.7, -0.111,
                // Jupiter
                5.54e7, 7.62e8, -4.40e6, -13.2, 12.9, 5.22e-2,
                // Saturn
                1.42e9, -1.91e8, -5.33e7, 0.748, 9.55, -0.196,
                // Titan
                1.42e9, -1.92e8, -5.28e7, 5.95, 7.68, 0.254,
                // Uranus
                1.62e9, 2.43e9, -1.19e7, -5.72, 3.45, 0.087,
                // Neptune
                4.47e9, -5.31e7, -1.02e8, 0.0287, 5.47, -0.113
        };

        // Create SolarSystem and RK4 solver
        List<Vector> trajectory = getVectors(masses, initialStateKm);

        // Figure Series-PosLists
        List<double[]> xs_fig = new ArrayList<>();
        List<double[]> ys_fig = new ArrayList<>();
        List<double[]> zs_fig = new ArrayList<>();

        // Output the trajectory
        for (int i = 0; i < trajectory.size(); i++) {
            System.out.println("Day " + i + ": " + trajectory.get(i));
            xs_fig.add(new double[]{i, trajectory.get(i).get(0 + (6*4))});
            ys_fig.add(new double[]{i, trajectory.get(i).get(1 + (6*4))});
            zs_fig.add(new double[]{i, trajectory.get(i).get(2 + (6*4))});
        }

        Figure.setTitle("Moon Position Over 4 years");
        Figure.setXLabel("Days");
        Figure.setYLabel("Position (km)");

        Figure.addSeries("X Position", xs_fig);
        Figure.addSeries("Y Position", ys_fig);
        Figure.addSeries("Z Position", zs_fig);

        Figure.show();

    }

    private static List<Vector> getVectors(List<Double> masses, double[] initialStateKm) {
        SolarSystem solarSystem = new SolarSystem(masses);
        ODESolver solver = new RK4Solver(); // or just: RK4Solver rk4 = new RK4Solver();

        // Simulation parameters
        double t0 = 0;
        double tEnd = 86400 * 365 * 4; // 1 year
        double dt = 86400;         // 1 day

        Vector y = new Vector(initialStateKm);
        double t = t0;
        List<Vector> trajectory = new ArrayList<>();

        while (t < tEnd) {
            trajectory.add(y);
            y = solver.step(solarSystem, t, y, dt);
            t += dt;
        }
        return trajectory;
    }
}
