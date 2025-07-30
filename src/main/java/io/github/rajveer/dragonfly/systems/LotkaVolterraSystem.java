package io.github.rajveer.dragonfly.systems;

import io.github.rajveer.dragonfly.utils.Vector;

/**
 * Lotka-Volterra (predator-prey) system of ODEs:
 *
 * dx/dt = αx - βxy      (prey)
 * dy/dt = δxy - γy      (predator)
 */
public class LotkaVolterraSystem implements ODESystem {

    private final double alpha;  // prey birth rate
    private final double beta;   // predation rate
    private final double delta;  // predator reproduction rate
    private final double gamma;  // predator death rate

    /**
     * Constructs a Lotka-Volterra system with given parameters.
     */
    public LotkaVolterraSystem(double alpha, double beta, double delta, double gamma) {
        this.alpha = alpha;
        this.beta = beta;
        this.delta = delta;
        this.gamma = gamma;
    }

    /**
     * Computes the derivatives [dx/dt, dy/dt] for prey and predator populations.
     *
     * @param t time (not used in this autonomous system)
     * @param y state vector [x, y] where x is prey, y is predator
     * @return derivative vector [dx/dt, dy/dt]
     */
    @Override
    public Vector computeDerivative(double t, Vector y) {
        double x = y.get(0); // prey
        double yPred = y.get(1); // predator

        double dxdt = alpha * x - beta * x * yPred;
        double dydt = delta * x * yPred - gamma * yPred;

        return new Vector(new double[]{dxdt, dydt});
    }
}
