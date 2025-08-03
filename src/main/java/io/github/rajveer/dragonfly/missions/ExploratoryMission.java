package io.github.rajveer.dragonfly.missions;

import io.github.rajveer.dragonfly.ode.ODESolver;
import io.github.rajveer.dragonfly.systems.SolarSystem;
import io.github.rajveer.dragonfly.systems.ODESystem;
import io.github.rajveer.dragonfly.utils.Vector;

import java.util.List;
import java.util.Random;

public class ExploratoryMission {

    private final ODESystem solarSystem;
    private final List<Double> masses;
    private final ODESolver solver;
    private final double dt;
    private final double missionDuration;
    private final int titanIndex;
    private final Random random;

    public ExploratoryMission(List<Double> masses, ODESolver solver, double dt, double missionDuration, int titanIndex) {
        this.masses = masses;
        this.solarSystem = new SolarSystem(masses);
        this.solver = solver;
        this.dt = dt;
        this.missionDuration = missionDuration;
        this.titanIndex = titanIndex;
        this.random = new Random();
    }

    public Vector findBestInitialProbeState(Vector initialState, int probeIndex, int epochs, int innerLoops) {
        double bestCost = Double.MAX_VALUE;
        Vector bestProbeState = null;

        for (int e = 0; e < epochs; e++) {
            // Start with a new random probe state
            Vector probeInit = generateRandomProbeState();

            for (int loop = 0; loop < innerLoops; loop++) {
                Vector state = insertProbeState(initialState, probeInit, probeIndex);
                Vector finalState = simulate(state);

                double cost = distanceToTitan(finalState, probeIndex);
                if (cost < bestCost) {
                    bestCost = cost;
                    bestProbeState = probeInit;
                    System.out.printf("Epoch %d Loop %d => New best distance: %.2f km%n", e, loop, cost);
                }

                // (optional) do gradient descent here — for now we stick to random search
            }
        }

        return bestProbeState;
    }

    private Vector simulate(Vector state) {
        double t = 0.0;
        while (t < missionDuration) {
            state = solver.step(solarSystem, t, state, dt);
            t += dt;
        }
        return state;
    }

    private double distanceToTitan(Vector state, int probeIndex) {
        Vector probePos = state.slice(probeIndex * 6, probeIndex * 6 + 3);
        Vector titanPos = state.slice(titanIndex * 6, titanIndex * 6 + 3);
        return probePos.subtract(titanPos).magnitude();
    }

    private Vector generateRandomProbeState() {
        double rx = random.nextDouble(-5e5, 5e5);
        double ry = random.nextDouble(-5e5, 5e5);
        double rz = random.nextDouble(-5e5, 5e5);

        double vx = random.nextDouble(-10, 10);
        double vy = random.nextDouble(-10, 10);
        double vz = random.nextDouble(-10, 10);

        return new Vector(new double[]{rx, ry, rz, vx, vy, vz});
    }

    private Vector insertProbeState(Vector base, Vector probe, int probeIndex) {
        double[] newState = base.toArray().clone();
        for (int i = 0; i < 6; i++) {
            newState[probeIndex * 6 + i] = probe.get(i);
        }
        return new Vector(newState);
    }
}
