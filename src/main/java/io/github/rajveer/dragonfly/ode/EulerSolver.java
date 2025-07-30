package io.github.rajveer.dragonfly.ode;

import io.github.rajveer.dragonfly.systems.ODESystem;
import io.github.rajveer.dragonfly.utils.Vector;

/**
 * A simple Euler method ODE solver.
 * Approximates y(t + dt) ≈ y(t) + dt * f(t, y)
 */
public class EulerSolver implements ODESolver {

    /**
     * Perform one Euler integration step.
     *
     * @param system the ODE system
     * @param t current time
     * @param y current state vector
     * @param dt time step
     * @return new state vector y(t + dt)
     */
    @Override
    public Vector step(ODESystem system, double t, Vector y, double dt) {
        Vector dy = system.computeDerivative(t, y);
        return y.add(dy.scale(dt));
    }
}
