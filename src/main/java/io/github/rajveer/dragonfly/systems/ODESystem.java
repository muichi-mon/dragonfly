package io.github.rajveer.dragonfly.systems;

import io.github.rajveer.dragonfly.utils.Vector;

/**
 * Represents a system of ordinary differential equations (ODEs) of the form:
 * dy/dt = f(t, y)
 */
public interface ODESystem {

    /**
     * Computes the derivative of the system at a given time and state.
     *
     * @param t current time
     * @param y current state vector y(t)
     * @return vector representing dy/dt
     */
    Vector computeDerivative(double t, Vector y);

}
