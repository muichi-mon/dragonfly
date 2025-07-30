package io.github.rajveer.dragonfly.simulations;

import io.github.rajveer.dragonfly.ode.RK4Solver;
import io.github.rajveer.dragonfly.systems.ODESystem;
import io.github.rajveer.dragonfly.systems.SIRModelSystem;
import io.github.rajveer.dragonfly.utils.Vector;

public class SIRSim {

    public static void main(String[] args){

        ODESystem sir = new SIRModelSystem(
                0.5,   // transmission rate k
                0.1,   // recovery rate gamma
                0.01   // turnover rate mu
        );

        RK4Solver solver = new RK4Solver();
        Vector state = new Vector(new double[]{0.99, 0.01, 0.0}); // initial S, I, R
        double t = 0, dt = 0.1;

        for (int i = 0; i < 300; i++) {
            System.out.printf("t=%.1f, S=%.4f, I=%.4f, R=%.4f%n", t, state.get(0), state.get(1), state.get(2));
            state = solver.step(sir, t, state, dt);
            t += dt;
        }

    }
}
