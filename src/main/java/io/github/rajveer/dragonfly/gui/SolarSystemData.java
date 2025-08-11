package io.github.rajveer.dragonfly.gui;

public class SolarSystemData {

    public static final String[] PLANET_NAMES = {
            "Sun", "Mercury", "Venus", "Earth", "Moon",
            "Mars", "Jupiter", "Saturn", "Titan", "Uranus", "Neptune"
    };

    public static final double[] RADII = {
            696340, 2440, 6052, 6371, 1737,
            3390, 69911, 58232, 2575, 25362, 24622
    };

    public static final double[] INITIAL_STATE = {
            // Sun
            0, 0, 0, 0, 0, 0,
            // Mercury
            -5.67e7, -3.23e7, 2.58e6, 13.9, -40.3, -4.57,
            // Venus
            -1.04e8, -3.19e7, 5.55e6, 9.89, -33.7, -1.03,
            // Earth
            -1.47e8, -2.97e7, 2.75e4, 5.31, -29.3, 6.69e-4,
            // Moon
            -1.47e8, -2.95e7, 5.29e4, 4.53, -28.6, 6.73e-2,
            // Mars
            -2.15e8, 1.27e8, 7.94e6, -11.5, -18.7, -0.111,
            // Jupiter
            5.54e7, 7.62e8, -4.40e6, -13.2, 12.9, 5.22e-2,
            // Saturn
            1.42e9, -1.91e8, -5.33e7, 0.748, 9.55, -0.196,
            // Titan
            1.42e9, -1.92e8, -5.28e7, 5.95, 7.68, 0.254,
            // Uranus
            1.62e9, 2.43e9, -1.19e7, -5.72, 3.45, 0.087,
            // Neptune
            4.47e9, -5.31e7, -1.02e8, 0.0287, 5.47, -0.113
    };
}
