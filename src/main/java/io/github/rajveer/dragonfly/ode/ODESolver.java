package io.github.rajveer.dragonfly.ode;

import io.github.rajveer.dragonfly.systems.ODESystem;
import io.github.rajveer.dragonfly.utils.Vector;

public interface ODESolver {
    /**
     * Perform one step of the ODE solver.
     *
     * @param system the ODE system
     * @param t current time
     * @param y current state vector
     * @param dt time step
     * @return estimated state vector after time step
     */
    Vector step(ODESystem system, double t, Vector y, double dt);
}
