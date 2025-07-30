package io.github.rajveer.dragonfly.simulations;

import io.github.rajveer.dragonfly.ode.RK4Solver;
import io.github.rajveer.dragonfly.systems.ODESystem;
import io.github.rajveer.dragonfly.systems.SIRModelSystem;
import io.github.rajveer.dragonfly.utils.Figure;
import io.github.rajveer.dragonfly.utils.Vector;

import java.util.ArrayList;
import java.util.List;

public class SIRSim {

    public static void main(String[] args){

        // SIR Model parameters: transmission rate k, recovery rate gamma, turnover rate mu
        ODESystem sir = new SIRModelSystem(
                0.5,   // transmission rate k
                0.1,   // recovery rate gamma
                0.01   // turnover rate mu
        );

        RK4Solver solver = new RK4Solver();
        Vector state = new Vector(new double[]{0.99, 0.01, 0.0}); // initial S, I, R
        double t = 0, dt = 0.1;

        List<double[]> sSeries = new ArrayList<>();
        List<double[]> iSeries = new ArrayList<>();
        List<double[]> rSeries = new ArrayList<>();

        for (int i = 0; i < 300; i++) {
            double S = state.get(0);
            double I = state.get(1);
            double R = state.get(2);

            // System.out.printf("t=%.1f, S=%.4f, I=%.4f, R=%.4f%n", t, S, I, R);

            sSeries.add(new double[]{t, S});
            iSeries.add(new double[]{t, I});
            rSeries.add(new double[]{t, R});

            state = solver.step(sir, t, state, dt);
            t += dt;
        }

        // Configure and show the figure
        Figure.setTitle("SIR Epidemiological Model");
        Figure.setXLabel("Time");
        Figure.setYLabel("Population Fraction");
        Figure.addSeries("Susceptible (S)", sSeries);
        Figure.addSeries("Infected (I)", iSeries);
        Figure.addSeries("Recovered (R)", rSeries);
        Figure.show();
    }
}
