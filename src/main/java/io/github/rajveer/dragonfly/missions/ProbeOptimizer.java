package io.github.rajveer.dragonfly.missions;

import io.github.rajveer.dragonfly.ode.ODESolver;
import io.github.rajveer.dragonfly.ode.RK4Solver;
import io.github.rajveer.dragonfly.systems.SolarSystem;
import io.github.rajveer.dragonfly.utils.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Optimizer that searches for probe initial conditions (rx,ry,rz,vx,vy,vz)
 * that minimise final distance to Titan after 1 year.
 *
 * - Uses finite-difference gradients + small learning rate
 * - Enforces initial speed constraint (<= maxSpeedKmPerS relative to Earth)
 * - Penalises collisions and failure to get near Titan within 1 year
 */
public class ProbeOptimizer {

    private static final double[] INITIAL_STATE_KM = {
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
            // Titan (we treat Titan as 9th index in masses list below)
            1.42e9, -1.92e8, -5.28e7, 5.95, 7.68, 0.254,
            // Uranus
            1.62e9, 2.43e9, -1.19e7, -5.72, 3.45, 0.087,
            // Neptune
            4.47e9, -5.31e7, -1.02e8, 0.0287, 5.47, -0.113
    };

    private static final List<Double> BASE_MASSES = Arrays.asList(
            1.99e30, 3.30e23, 4.87e24, 5.97e24, 7.35e22,
            6.42e23, 1.90e27, 5.68e26, 1.35e23, 8.68e25, 1.02e26
    );

    private static final double[] PLANET_RADII_KM = {
            696340, 2440, 6052, 6371, 1737, 3390, 69911, 58232, 2575, 25362, 24622
    };

    // probe index will be 11 (appended)
    private static final int PROBE_INDEX = 11;
    private static final int TITAN_INDEX = 8; // Titan as in your list

    // simulation params
    private static final double SECONDS_PER_DAY = 86400.0;
    private static final double ONE_YEAR_SECONDS = SECONDS_PER_DAY * 365.0;
    private static final double DT = SECONDS_PER_DAY; // 1 day

    // velocity constraint: 60 km/hr = 60/3600 km/s
    private static final double MAX_V_KM_PER_S = 60.0; //

    // optimizer defaults
    private final ODESolver solver = new RK4Solver();
    private final Random rng = new Random();

    /**
     * Run gradient-descent search.
     *
     * @param epochs number of random restarts
     * @param iters number of gradient iterations per restart
     * @param baseGuess 6-element array [rx,ry,rz,vx,vy,vz] to centre random search
     * @return best 6-element initial probe state found
     */
    public double[] findBestProbeInitial(int epochs, int iters, double[] baseGuess) {
        double[] best = baseGuess.clone();
        double bestCost = Double.POSITIVE_INFINITY;

        for (int e = 0; e < epochs; e++) {
            // randomize initial guess around base
            double[] x = randomizeAround(baseGuess, 1e6, 0.005); // pos +/-1e6 km, vel +/-0.005 km/s
            enforceVelocityConstraint(x, baseGuess); // keep velocity reasonable relative to Earth's vel
            for (int it = 0; it < iters; it++) {
                // evaluate cost and gradient via finite differences
                double cost = evaluateCost(x);
                if (Double.isFinite(cost) && cost < bestCost) {
                    bestCost = cost;
                    best = x.clone();
                    System.out.printf("Epoch %d iter %d: new best cost = %.3f km%n", e, it, bestCost);
                }

                // finite-difference gradient
                double eps = 1e-3; // small delta (km for pos, km/s for vel)
                double lr = 5e3;   // learning rate for positions/vels — tuned empirically
                double[] grad = new double[6];
                for (int k = 0; k < 6; k++) {
                    double old = x[k];
                    x[k] = old + eps;
                    double costPlus = evaluateCost(x);
                    x[k] = old - eps;
                    double costMinus = evaluateCost(x);
                    x[k] = old;
                    double g;
                    if (!Double.isFinite(costPlus) || !Double.isFinite(costMinus)) {
                        // if either sim crashes, push away from that direction
                        g = Double.isFinite(costPlus) ? (costPlus - cost) / eps : -1.0;
                    } else {
                        g = (costPlus - costMinus) / (2 * eps);
                    }
                    grad[k] = g;
                }

                // gradient step (note: scale differently for pos/vel)
                double posScale = lr;           // apply to position components (km)
                double velScale = lr * 1e-3;    // smaller step for velocities (km/s)
                for (int k = 0; k < 3; k++) x[k] -= grad[k] * posScale;
                for (int k = 3; k < 6; k++) x[k] -= grad[k] * velScale;

                // enforce velocity constraint relative to Earth's initial velocity
                enforceVelocityConstraint(x, baseGuess);

                // optionally small random perturb to avoid local minima
                for (int k = 0; k < 6; k++) {
                    x[k] += (rng.nextDouble() * 2 - 1) * 1e-3; // tiny noise
                }
            }
        }

        System.out.println("Best cost final: " + bestCost);
        return best;
    }

    /**
     * Evaluate cost for a probe initial vector x = [rx,ry,rz,vx,vy,vz].
     *
     * - Runs simulation up to 1 year with dt = 1 day
     * - Returns final distance to Titan (km)
     * - If probe collides with any planet -> return LARGE COST
     * - If anything goes wrong -> return LARGE COST
     */
    private double evaluateCost(double[] x) {
        try {
            // Build masses list with appended probe mass (use 50,000 kg)
            List<Double> masses = new ArrayList<>(BASE_MASSES);
            masses.add(5.0e4);

            // Build full initial state (copy base + probe)
            double[] full = Arrays.copyOf(INITIAL_STATE_KM, INITIAL_STATE_KM.length + 6);
            int probeOffset = INITIAL_STATE_KM.length;
            System.arraycopy(x, 0, full, probeOffset, 6);

            // run one-year simulation (stepwise with RK4)
            SolarSystem system = new SolarSystem(masses); // unchanged SolarSystem
            Vector y = new Vector(full);
            double t = 0.0;
            boolean crashed = false;

            while (t < ONE_YEAR_SECONDS) {
                y = solver.step(system, t, y, DT);
                t += DT;

                // check collision of probe with planets (probe is last)
                if (probeCollided(y)) {
                    crashed = true;
                    break;
                }
            }

            if (crashed) {
                return 1e12; // huge penalty for collision
            }

            // compute final distance to Titan
            double[] probePos = { y.get(PROBE_INDEX * 6), y.get(PROBE_INDEX * 6 + 1), y.get(PROBE_INDEX * 6 + 2) };
            double[] titanPos = { y.get(TITAN_INDEX * 6), y.get(TITAN_INDEX * 6 + 1), y.get(TITAN_INDEX * 6 + 2) };
            double dx = probePos[0] - titanPos[0];
            double dy = probePos[1] - titanPos[1];
            double dz = probePos[2] - titanPos[2];
            double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);

            return dist; // cost = distance in km

        } catch (Exception ex) {
            ex.printStackTrace();
            return 1e12;
        }
    }

    /** Check if probe (last body) collided with any planet (within planet radius). */
    private boolean probeCollided(Vector y) {
        int nPlanets = PLANET_RADII_KM.length;
        double px = y.get(PROBE_INDEX * 6);
        double py = y.get(PROBE_INDEX * 6 + 1);
        double pz = y.get(PROBE_INDEX * 6 + 2);

        for (int i = 0; i < nPlanets; i++) {
            double bx = y.get(i * 6);
            double by = y.get(i * 6 + 1);
            double bz = y.get(i * 6 + 2);
            double dx = px - bx;
            double dy = py - by;
            double dz = pz - bz;
            double d2 = dx*dx + dy*dy + dz*dz;
            double r = PLANET_RADII_KM[i];
            if (d2 < r*r) return true;
        }
        return false;
    }

    /** small helper: randomize near base guess */
    private double[] randomizeAround(double[] base, double posSpread, double velSpread) {
        double[] out = new double[6];
        for (int i = 0; i < 3; i++) out[i] = base[i] + (rng.nextDouble() * 2 - 1) * posSpread;
        for (int i = 3; i < 6; i++) out[i] = base[i] + (rng.nextDouble() * 2 - 1) * velSpread;
        return out;
    }

    /** enforce max relative speed to Earth (limit in km/s). baseGuess must have Earth's vel for reference. */
    private void enforceVelocityConstraint(double[] x, double[] baseGuess) {
        // compute Earth's velocity from initial state (earth is body index 3)
        double earthVx = INITIAL_STATE_KM[3*6 + 3];
        double earthVy = INITIAL_STATE_KM[3*6 + 4];
        double earthVz = INITIAL_STATE_KM[3*6 + 5];

        double rvx = x[3] - earthVx;
        double rvy = x[4] - earthVy;
        double rvz = x[5] - earthVz;
        double speed = Math.sqrt(rvx*rvx + rvy*rvy + rvz*rvz);
        if (speed > MAX_V_KM_PER_S) {
            double scale = MAX_V_KM_PER_S / speed;
            double nvx = earthVx + rvx * scale;
            double nvy = earthVy + rvy * scale;
            double nvz = earthVz + rvz * scale;
            x[3] = nvx; x[4] = nvy; x[5] = nvz;
        }
    }

    // ---------- Example main to run
    public static void main(String[] args) {
        ProbeOptimizer optim = new ProbeOptimizer();

        // base guess: near Earth position and similar orbital velocity
        double[] base = new double[] {
                INITIAL_STATE_KM[3*6 + 0], INITIAL_STATE_KM[3*6 + 1], INITIAL_STATE_KM[3*6 + 2], // rx,ry,rz ~ Earth
                INITIAL_STATE_KM[3*6 + 3], INITIAL_STATE_KM[3*6 + 4], INITIAL_STATE_KM[3*6 + 5]  // vx,vy,vz ~ Earth's vel
        };

        // run search
        double[] best = optim.findBestProbeInitial(8, 50, base); // 8 restarts, 50 iters each
        System.out.println("Best initial probe state (rx,ry,rz,vx,vy,vz): " + Arrays.toString(best));
    }
}
