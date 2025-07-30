package io.github.rajveer.dragonfly.simulations;

import io.github.rajveer.dragonfly.ode.RK4Solver;
import io.github.rajveer.dragonfly.systems.FitzHughNagumoSystem;
import io.github.rajveer.dragonfly.systems.ODESystem;
import io.github.rajveer.dragonfly.utils.Vector;

public class FitzHughNagumoSim {

    public static void main(String[] args){

        ODESystem neuron = new FitzHughNagumoSystem(
                0.08,  // epsilon
                0.7,   // a
                0.8,   // b
                0.5    // I_ext
        );

        RK4Solver solver = new RK4Solver();
        Vector state = new Vector(new double[]{0.0, 0.0});
        double t = 0, dt = 0.1;

        for (int i = 0; i < 200; i++) {
            System.out.printf("t=%.2f, V=%.4f, W=%.4f%n", t, state.get(0), state.get(1));
            state = solver.step(neuron, t, state, dt);
            t += dt;
        }


    }

}
