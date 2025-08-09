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
 * Probe optimizer that searches for an initial probe state (rx,ry,rz,vx,vy,vz)
 * placed on Earth's surface that minimises final distance to Titan after 1 year.
 *
 * - Uses finite-difference gradient descent with random restarts.
 * - Probe mass = 50,000 kg appended as last body.
 * - Keeps SolarSystem class unchanged (Sun remains fixed in your current implementation).
 */
public class ExploratoryMission {

    private static final double[] INITIAL_STATE_KM = {
            // Sun
            0, 0, 0, 0, 0, 0,
            // Mercury
            -5.67e7, -3.23e7, 2.58e6, 13.9, -40.3, -4.57,
            // Venus
            -1.04e8, -3.19e7, 5.55e6, 9.89, -33.7, -1.03,
            // Earth (index 3)
            -1.47e8, -2.97e7, 2.75e4, 5.31, -29.3, 6.69e-4,
            // Moon
            -1.47e8, -2.95e7, 5.29e4, 4.53, -28.6, 6.73e-2,
            // Mars
            -2.15e8, 1.27e8, 7.94e6, -11.5, -18.7, -0.111,
            // Jupiter
            5.54e7, 7.62e8, -4.40e6, -13.2, 12.9, 5.22e-2,
            // Saturn
            1.42e9, -1.91e8, -5.33e7, 0.748, 9.55, -0.196,
            // Titan (index 8)
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

    // Planet radii in km, same order as INITIAL_STATE_KM bodies
    private static final double[] PLANET_RADII_KM = {
            696340, 2440, 6052, 6371, 1737, 3390, 69911, 58232, 2575, 25362, 24622
    };

    private static final int EARTH_BODY_INDEX = 3;
    private static final int TITAN_BODY_INDEX = 8;
    private static final int BASE_NUM_BODIES = 11; // Sun..Neptune (no probe)

    // appended probe will be index BASE_NUM_BODIES (i.e. 11)
    private static final int PROBE_INDEX = BASE_NUM_BODIES;

    // time units
    private static final double SECONDS_PER_DAY = 86400.0;
    private static final double ONE_YEAR_SECONDS = SECONDS_PER_DAY * 365.0;
    private static final double DT = SECONDS_PER_DAY; // 1 day step

    // velocity constraint: 60 km/s
    private static final double MAX_V_KM_PER_S = 60.0; //

    // probe mass (affects its own motion)
    private static final double PROBE_MASS_KG = 50_000.0;

    // solver + RNG
    private final ODESolver solver = new RK4Solver();
    private final Random rng = new Random();

    /**
     * Find a good initial probe state using random-restart finite-difference gradient descent.
     *
     * @param epochs    number of random restarts
     * @param iters     gradient-descent iterations per restart
     * @return best found initial state vector [rx,ry,rz,vx,vy,vz] (km, km/s)
     */
    public double[] optimize(int epochs, int iters) {
        // base guess (start from Earth's COM + Earth's velocity)
        double[] base = new double[6];
        int earthPosIdx = EARTH_BODY_INDEX * 6;
        base[0] = INITIAL_STATE_KM[earthPosIdx];
        base[1] = INITIAL_STATE_KM[earthPosIdx + 1];
        base[2] = INITIAL_STATE_KM[earthPosIdx + 2];
        base[3] = INITIAL_STATE_KM[earthPosIdx + 3];
        base[4] = INITIAL_STATE_KM[earthPosIdx + 4];
        base[5] = INITIAL_STATE_KM[earthPosIdx + 5];

        double[] best = null;
        double bestCost = Double.POSITIVE_INFINITY;

        for (int e = 0; e < epochs; e++) {
            // Start probe on Earth's surface (random point); initial velocity = Earth's vel
            double[] x = placeProbeOnEarthSurface();

            // small random perturb to velocity to explore different launch directions
            for (int k = 3; k < 6; k++) {
                x[k] += (rng.nextDouble() * 2 - 1) * 0.005; // ±0.005 km/s
            }
            enforceVelocityConstraintRelativeToEarth(x);

            for (int iter = 0; iter < iters; iter++) {
                double cost = evaluateCost(x);
                if (Double.isFinite(cost) && cost < bestCost) {
                    bestCost = cost;
                    best = x.clone();
                    System.out.printf("[epoch %d iter %d] new best: %.3f km%n", e, iter, bestCost);
                }

                // finite difference gradient
                double epsPos = 1e-2;   // km (small step for pos)
                double epsVel = 1e-5;   // km/s (small step for vel)
                double[] grad = new double[6];

                // baseline cost may be needed; compute once
                double baseline = cost;

                for (int k = 0; k < 6; k++) {
                    double old = x[k];
                    if (k < 3) x[k] = old + epsPos;
                    else x[k] = old + epsVel;

                    double cplus = evaluateCost(x);

                    // reset
                    x[k] = old;

                    // central difference using symmetric step if possible:
                    if (k < 3) x[k] = old - epsPos;
                    else x[k] = old - epsVel;

                    double cminus = evaluateCost(x);
                    x[k] = old;

                    if (!Double.isFinite(cplus) || !Double.isFinite(cminus)) {
                        // if either side crashes, push away from crash direction
                        grad[k] = Double.isFinite(cplus) ? (cplus - baseline) / (k < 3 ? epsPos : epsVel) : -1.0;
                    } else {
                        grad[k] = (cplus - cminus) / ( (k < 3 ? 2*epsPos : 2*epsVel) );
                    }
                }

                // gradient step: different learning rates for position and velocity
                double lrPos = 1e2;    // km per gradient unit (tunable)
                double lrVel = 1e-2;   // km/s per gradient unit (tunable)

                for (int k = 0; k < 3; k++) x[k] -= lrPos * grad[k];
                for (int k = 3; k < 6; k++) x[k] -= lrVel * grad[k];

                // keep the probe initially on Earth's surface (enforce starting radius)
                enforceProbeOnEarthSurface(x);

                // enforce velocity constraint relative to Earth's vel
                enforceVelocityConstraintRelativeToEarth(x);

                // small noise to escape shallow local minima
                for (int k = 0; k < 6; k++) {
                    x[k] += (rng.nextDouble() * 2 - 1) * 1e-4;
                }
            }
        }

        System.out.println("Optimization finished. Best cost = " + bestCost);
        if (best == null) {
            // fallback: return place on Earth surface with no delta-V
            return placeProbeOnEarthSurface();
        }
        return best;
    }

    /**
     * Evaluate cost (final distance to Titan in km) for a probe starting at x = [rx..vz].
     * Returns large penalty if probe collides with any planet during the year.
     */
    private double evaluateCost(double[] x) {
        try {
            // Compose masses (append probe mass)
            List<Double> masses = new ArrayList<>(BASE_MASSES);
            masses.add(PROBE_MASS_KG);

            // Compose full state (base bodies + probe appended)
            double[] full = Arrays.copyOf(INITIAL_STATE_KM, INITIAL_STATE_KM.length + 6);
            int probeOffset = INITIAL_STATE_KM.length;
            System.arraycopy(x, 0, full, probeOffset, 6);

            SolarSystem system = new SolarSystem(masses); // unchanged SolarSystem
            Vector y = new Vector(full);
            double t = 0.0;

            while (t < ONE_YEAR_SECONDS) {
                y = solver.step(system, t, y, DT);
                t += DT;

                // if probe collides with any planet, heavy penalty
                if (probeCollided(y)) return 1e12;
            }

            // compute probe final distance to Titan
            double px = y.get(PROBE_INDEX * 6);
            double py = y.get(PROBE_INDEX * 6 + 1);
            double pz = y.get(PROBE_INDEX * 6 + 2);

            double tx = y.get(TITAN_BODY_INDEX * 6);
            double ty = y.get(TITAN_BODY_INDEX * 6 + 1);
            double tz = y.get(TITAN_BODY_INDEX * 6 + 2);

            double dx = px - tx;
            double dy = py - ty;
            double dz = pz - tz;
            return Math.sqrt(dx*dx + dy*dy + dz*dz);

        } catch (Exception ex) {
            ex.printStackTrace();
            return 1e12;
        }
    }

    /** Returns true if probe (last body) is inside any planet radius. */
    private boolean probeCollided(Vector y) {
        double px = y.get(PROBE_INDEX * 6);
        double py = y.get(PROBE_INDEX * 6 + 1);
        double pz = y.get(PROBE_INDEX * 6 + 2);

        for (int i = 0; i < PLANET_RADII_KM.length; i++) {
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

    /** Place probe on Earth's surface (random direction) and set initial velocity = Earth's vel. */
    private double[] placeProbeOnEarthSurface() {
        int earthPosIndex = EARTH_BODY_INDEX * 6;
        double ex = INITIAL_STATE_KM[earthPosIndex];
        double ey = INITIAL_STATE_KM[earthPosIndex + 1];
        double ez = INITIAL_STATE_KM[earthPosIndex + 2];

        double evx = INITIAL_STATE_KM[earthPosIndex + 3];
        double evy = INITIAL_STATE_KM[earthPosIndex + 4];
        double evz = INITIAL_STATE_KM[earthPosIndex + 5];

        double earthRadius = PLANET_RADII_KM[EARTH_BODY_INDEX];

        // pick random point on sphere (uniform)
        double u = rng.nextDouble();
        double v = rng.nextDouble();
        double theta = 2.0 * Math.PI * u;
        double phi = Math.acos(2.0 * v - 1.0);
        double dx = Math.sin(phi) * Math.cos(theta);
        double dy = Math.sin(phi) * Math.sin(theta);
        double dz = Math.cos(phi);

        double rx = ex + earthRadius * dx;
        double ry = ey + earthRadius * dy;
        double rz = ez + earthRadius * dz;

        return new double[] { rx, ry, rz, evx, evy, evz };
    }

    /** Ensure the probe initial position remains on Earth's surface (distance = earth radius). */
    private void enforceProbeOnEarthSurface(double[] x) {
        int earthPosIndex = EARTH_BODY_INDEX * 6;
        double ex = INITIAL_STATE_KM[earthPosIndex];
        double ey = INITIAL_STATE_KM[earthPosIndex + 1];
        double ez = INITIAL_STATE_KM[earthPosIndex + 2];
        double earthRadius = PLANET_RADII_KM[EARTH_BODY_INDEX];

        double rx = x[0] - ex;
        double ry = x[1] - ey;
        double rz = x[2] - ez;
        double r = Math.sqrt(rx*rx + ry*ry + rz*rz);
        if (r == 0) {
            // place at north pole if degenerate
            x[0] = ex;
            x[1] = ey;
            x[2] = ez + earthRadius;
            return;
        }
        double scale = earthRadius / r;
        x[0] = ex + rx * scale;
        x[1] = ey + ry * scale;
        x[2] = ez + rz * scale;
    }

    /**
     * Enforce the maximum allowed initial velocity relative to Earth's velocity.
     * If relative speed > MAX_V_KM_PER_S, scale the delta-v so the relative speed equals the max.
     */
    private void enforceVelocityConstraintRelativeToEarth(double[] x) {
        int earthPosIdx = EARTH_BODY_INDEX * 6;
        double evx = INITIAL_STATE_KM[earthPosIdx + 3];
        double evy = INITIAL_STATE_KM[earthPosIdx + 4];
        double evz = INITIAL_STATE_KM[earthPosIdx + 5];

        double rvx = x[3] - evx;
        double rvy = x[4] - evy;
        double rvz = x[5] - evz;
        double speed = Math.sqrt(rvx*rvx + rvy*rvy + rvz*rvz);
        if (speed > MAX_V_KM_PER_S) {
            double scale = MAX_V_KM_PER_S / speed;
            x[3] = evx + rvx * scale;
            x[4] = evy + rvy * scale;
            x[5] = evz + rvz * scale;
        }
    }

    /** small helper: randomize around base (not used for position since we enforce Earth surface). */
    private double[] randomizeAround(double[] base, double posSpread, double velSpread) {
        double[] out = base.clone();
        for (int i = 0; i < 3; i++) out[i] = base[i] + (rng.nextDouble() * 2 - 1) * posSpread;
        for (int i = 3; i < 6; i++) out[i] = base[i] + (rng.nextDouble() * 2 - 1) * velSpread;
        return out;
    }

    /** Example main to run optimization. */
    public static void main(String[] args) {
        ExploratoryMission opt = new ExploratoryMission();
        int epochs = 6;
        int iters = 40;

        double[] best = opt.optimize(epochs, iters);
        System.out.println("Best initial probe state (rx,ry,rz,vx,vy,vz): " + Arrays.toString(best));
    }
}
