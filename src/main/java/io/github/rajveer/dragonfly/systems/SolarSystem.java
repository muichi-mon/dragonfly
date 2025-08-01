package io.github.rajveer.dragonfly.systems;

import io.github.rajveer.dragonfly.utils.Vector;
import java.util.List;

/**
 * Represents an N-body solar system simulation under Newtonian gravity.
 * <p>
 * Each celestial body has 3 position components and 3 velocity components,
 * forming a state vector of size 6N (for N bodies). The gravitational interaction
 * between each pair of bodies is computed, and the resulting accelerations are used
 * to compute derivatives of the state.
 * <p>
 * The system supports any number of bodies, with masses provided at construction.
 * Note: In the current implementation, the Sun (index 0) is kept static and does not move.
 */
public class SolarSystem implements ODESystem {

    /**
     * Gravitational constant in km³·kg⁻¹·s⁻².
     */
    private static final double G = 6.67430e-20;

    /**
     * List of masses for each celestial body in kilograms.
     */
    private final List<Double> masses;

    /**
     * Constructs a new solar system with the specified masses.
     *
     * @param masses list of body masses in kilograms, in the same order as the state vector
     */
    public SolarSystem(List<Double> masses) {
        this.masses = masses;
    }

    /**
     * Computes the time derivative of the state vector at time {@code t}.
     * <p>
     * Each body is represented by 6 values in the state vector:
     * the first 3 are position (x, y, z), and the next 3 are velocity (vx, vy, vz).
     *
     * @param t the current simulation time
     * @param y the current state vector of length 6N (N = number of bodies)
     * @return the time derivative dy/dt, also a vector of length 6N
     */
    @Override
    public Vector computeDerivative(double t, Vector y) {
        int numBodies = masses.size();
        double[] dydt = new double[6 * numBodies];

        for (int i = 0; i < numBodies; i++) {
            int posIndex = i * 6;
            int velIndex = posIndex + 3;

            // For this implementation, the Sun (index 0) is fixed and does not move
            if (i == 0) {
                for (int k = 0; k < 6; k++) {
                    dydt[posIndex + k] = 0;
                }
                continue;
            }

            // Extract position and velocity for body i
            Vector ri = new Vector(new double[]{
                    y.get(posIndex),
                    y.get(posIndex + 1),
                    y.get(posIndex + 2)
            });
            Vector vi = new Vector(new double[]{
                    y.get(velIndex),
                    y.get(velIndex + 1),
                    y.get(velIndex + 2)
            });

            // Derivative of position is the velocity
            dydt[posIndex] = vi.get(0);
            dydt[posIndex + 1] = vi.get(1);
            dydt[posIndex + 2] = vi.get(2);

            // Initialize acceleration
            Vector ai = new Vector(new double[]{0, 0, 0});

            // Compute gravitational acceleration from other bodies
            for (int j = 0; j < numBodies; j++) {
                if (i == j) continue;

                int rjIndex = j * 6;
                Vector rj = new Vector(new double[]{
                        y.get(rjIndex),
                        y.get(rjIndex + 1),
                        y.get(rjIndex + 2)
                });

                Vector rij = rj.subtract(ri);
                double dist = rij.magnitude();
                if (dist == 0) continue; // Avoid division by zero

                double factor = G * masses.get(j) / (dist * dist * dist);
                ai = ai.add(rij.scale(factor));
            }

            // Derivative of velocity is the acceleration
            dydt[velIndex] = ai.get(0);
            dydt[velIndex + 1] = ai.get(1);
            dydt[velIndex + 2] = ai.get(2);
        }

        return new Vector(dydt);
    }
}
