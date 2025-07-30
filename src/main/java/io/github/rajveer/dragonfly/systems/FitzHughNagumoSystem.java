package io.github.rajveer.dragonfly.systems;

import io.github.rajveer.dragonfly.utils.Vector;

/**
 * FitzHugh–Nagumo neuron model:
 *
 * dV/dt = V - V^3 / 3 - W + I_ext
 * dW/dt = ε(V + a - bW)
 */
public class FitzHughNagumoSystem implements ODESystem {

    private final double epsilon;  // time scale for W
    private final double a;        // threshold parameter
    private final double b;        // recovery coupling
    private final double Iext;     // external current

    /**
     * Constructs the FitzHugh-Nagumo system.
     *
     * @param epsilon time scale separation
     * @param a parameter 'a'
     * @param b parameter 'b'
     * @param Iext external current input
     */
    public FitzHughNagumoSystem(double epsilon, double a, double b, double Iext) {
        this.epsilon = epsilon;
        this.a = a;
        this.b = b;
        this.Iext = Iext;
    }

    /**
     * Computes derivatives [dV/dt, dW/dt] at time t and state [V, W].
     */
    @Override
    public Vector computeDerivative(double t, Vector y) {
        double V = y.get(0); // membrane voltage
        double W = y.get(1); // recovery variable

        double dVdt = V - (V * V * V) / 3.0 - W + Iext;
        double dWdt = epsilon * (V + a - b * W);

        return new Vector(new double[]{dVdt, dWdt});
    }
}
