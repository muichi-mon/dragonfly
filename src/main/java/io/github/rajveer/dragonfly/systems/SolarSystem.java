package io.github.rajveer.dragonfly.systems;

import io.github.rajveer.dragonfly.utils.Vector;
import java.util.List;

/**
 * Represents an N-body solar system simulation under Newtonian gravity.
 * <p>
 * Each body is defined by its position, velocity, and mass. This class implements
 * the {@link ODESystem} interface, providing the method to compute the time derivative
 * of the system state vector (i.e., positions and velocities of all bodies).
 * </p>
 *
 * <p>
 * The state vector y is structured as a concatenation of position and velocity triples:
 * <pre>
 *     y = [r1x, r1y, r1z, v1x, v1y, v1z, r2x, ..., vNz]
 * </pre>
 * where ri and vi are the position and velocity vectors of the i-th body.
 * The output dy/dt contains:
 * <pre>
 *     dy/dt = [v1x, v1y, v1z, a1x, a1y, a1z, v2x, ..., aNz]
 * </pre>
 * where ai is the acceleration of the i-th body due to gravitational forces.
 * </p>
 */
public class SolarSystem implements ODESystem {

    /** Gravitational constant (in m^3⋅kg^−1⋅s^−2) */
    private static final double G = 6.67430e-11;

    /** Masses of all celestial bodies in the system, in kilograms */
    private final List<Double> masses;

    /**
     * Constructs a new SolarSystem with the given list of body masses.
     *
     * @param masses A list of doubles representing the mass of each body.
     *               The list size must match the number of bodies in the state vector.
     */
    public SolarSystem(List<Double> masses) {
        this.masses = masses;
    }

    /**
     * Computes the derivative of the state vector y at time t.
     * <p>
     * For each body, this includes:
     * <ul>
     *     <li>Derivative of position = current velocity</li>
     *     <li>Derivative of velocity = acceleration due to gravity from other bodies</li>
     * </ul>
     * </p>
     *
     * @param t Current simulation time (not used in this system as it is autonomous)
     * @param y The current state vector of all bodies: positions and velocities
     * @return The time derivative of the state vector (dy/dt)
     */
    @Override
    public Vector computeDerivative(double t, Vector y) {
        int numBodies = masses.size();
        double[] dydt = new double[6 * numBodies];

        for (int i = 0; i < numBodies; i++) {
            int posIndex = i * 6;
            int velIndex = posIndex + 3;

            // Extract position and velocity of body i
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

            // Derivative of position is current velocity
            dydt[posIndex] = vi.get(0);
            dydt[posIndex + 1] = vi.get(1);
            dydt[posIndex + 2] = vi.get(2);

            // Initialize acceleration vector to zero
            Vector ai = new Vector(new double[]{0, 0, 0});

            // Calculate gravitational acceleration from all other bodies
            for (int j = 0; j < numBodies; j++) {
                if (i == j) continue; // Skip self-interaction

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

            // Derivative of velocity is acceleration
            dydt[velIndex] = ai.get(0);
            dydt[velIndex + 1] = ai.get(1);
            dydt[velIndex + 2] = ai.get(2);
        }

        return new Vector(dydt);
    }
}
