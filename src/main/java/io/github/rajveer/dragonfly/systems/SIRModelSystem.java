package io.github.rajveer.dragonfly.systems;

import io.github.rajveer.dragonfly.utils.Vector;

/**
 * SIR (Susceptible-Infected-Recovered) model with population turnover:
 *
 * dS/dt = -k * S * I + μ * (1 - S)
 * dI/dt =  k * S * I - (γ + μ) * I
 * dR/dt =  γ * I - μ * R
 */
public class SIRModelSystem implements ODESystem {

    private final double k;      // transmission rate
    private final double gamma;  // recovery rate
    private final double mu;     // birth/death rate

    public SIRModelSystem(double k, double gamma, double mu) {
        this.k = k;
        this.gamma = gamma;
        this.mu = mu;
    }

    /**
     * Computes [dS/dt, dI/dt, dR/dt] at time t and state [S, I, R].
     */
    @Override
    public Vector computeDerivative(double t, Vector y) {
        double S = y.get(0);
        double I = y.get(1);
        double R = y.get(2);

        double dSdt = -k * S * I + mu * (1 - S);
        double dIdt = k * S * I - (gamma + mu) * I;
        double dRdt = gamma * I - mu * R;

        return new Vector(new double[]{dSdt, dIdt, dRdt});
    }
}
