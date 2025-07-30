package io.github.rajveer.dragonfly.ode;

import io.github.rajveer.dragonfly.systems.ODESystem;
import io.github.rajveer.dragonfly.utils.Vector;

/**
 * A classic 4th-order Runge-Kutta ODE solver.
 * Approximates y(t + dt) using intermediate slopes:
 *
 * y(t + dt) ≈ y + (dt/6) * (k1 + 2k2 + 2k3 + k4)
 */
public class RK4Solver implements ODESolver {

    /**
     * Perform one RK4 step.
     *
     * @param system the ODE system
     * @param t current time
     * @param y current state vector
     * @param dt time step
     * @return estimated state vector at t + dt
     */
    @Override
    public Vector step(ODESystem system, double t, Vector y, double dt) {
        Vector k1 = system.computeDerivative(t, y);
        Vector k2 = system.computeDerivative(t + dt / 2, y.add(k1.scale(dt / 2)));
        Vector k3 = system.computeDerivative(t + dt / 2, y.add(k2.scale(dt / 2)));
        Vector k4 = system.computeDerivative(t + dt, y.add(k3.scale(dt)));

        Vector sum = k1
                .add(k2.scale(2))
                .add(k3.scale(2))
                .add(k4);
        return y.add(sum.scale(dt / 6.0));
    }
}
