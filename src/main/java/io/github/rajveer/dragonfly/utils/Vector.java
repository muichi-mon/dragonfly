package io.github.rajveer.dragonfly.utils;

import java.util.Arrays;

/**
 * A simple immutable vector class that supports basic linear algebra operations
 * such as addition, subtraction, scaling, slicing, dot product, and normalization.
 */
public class Vector {

    private final double[] data;

    /**
     * Constructs a new Vector with the provided array.
     *
     * @param data the array of doubles representing the vector
     */
    public Vector(double[] data) {
        this.data = Arrays.copyOf(data, data.length);
    }

    /**
     * Returns the length (number of elements) of this vector.
     *
     * @return the number of elements
     */
    public int length() {
        return data.length;
    }

    /**
     * Returns the element at the specified index.
     *
     * @param i the index
     * @return the element at the given index
     */
    public double get(int i) {
        return data[i];
    }

    /**
     * Sets the element at the specified index to the given value.
     *
     * @param i     the index
     * @param value the new value to set
     */
    public void set(int i, double value) {
        data[i] = value;
    }

    /**
     * Returns a new vector where each element is scaled by the given scalar.
     *
     * @param scalar the scalar multiplier
     * @return a new scaled vector
     */
    public Vector scale(double scalar) {
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i] * scalar;
        }
        return new Vector(result);
    }

    /**
     * Returns a sliced subvector from start (inclusive) to end (exclusive).
     *
     * @param start the starting index (inclusive)
     * @param end   the ending index (exclusive)
     * @return the sliced subvector
     * @throws IllegalArgumentException if indices are invalid
     */
    public Vector slice(int start, int end) {
        if (start < 0 || end > data.length || start >= end) {
            throw new IllegalArgumentException("Invalid slice indices: [" + start + ", " + end + ")");
        }
        return new Vector(Arrays.copyOfRange(data, start, end));
    }

    /**
     * Returns a new vector resulting from element-wise addition.
     *
     * @param other the vector to add
     * @return the sum vector
     * @throws IllegalArgumentException if vectors are not the same length
     */
    public Vector add(Vector other) {
        checkLength(other);
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = this.data[i] + other.data[i];
        }
        return new Vector(result);
    }

    /**
     * Returns a new vector resulting from element-wise subtraction.
     *
     * @param other the vector to subtract
     * @return the difference vector
     * @throws IllegalArgumentException if vectors are not the same length
     */
    public Vector subtract(Vector other) {
        checkLength(other);
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = this.data[i] - other.data[i];
        }
        return new Vector(result);
    }

    /**
     * Computes the dot product of this vector with another.
     *
     * @param other the other vector
     * @return the dot product
     * @throws IllegalArgumentException if vectors are not the same length
     */
    public double dot(Vector other) {
        checkLength(other);
        double sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += this.data[i] * other.data[i];
        }
        return sum;
    }

    /**
     * Returns the Euclidean norm (magnitude) of this vector.
     *
     * @return the magnitude
     */
    public double magnitude() {
        double sum = 0;
        for (double v : data) {
            sum += v * v;
        }
        return Math.sqrt(sum);
    }

    /**
     * Returns a unit vector (normalized vector).
     *
     * @return the normalized vector
     * @throws ArithmeticException if the vector is zero
     */
    public Vector normalize() {
        double mag = magnitude();
        if (mag == 0) {
            throw new ArithmeticException("Cannot normalize a zero vector");
        }
        return scale(1.0 / mag);
    }

    /**
     * Checks if the length of the other vector matches this one.
     *
     * @param other the vector to check
     * @throws IllegalArgumentException if the lengths do not match
     */
    private void checkLength(Vector other) {
        if (this.data.length != other.data.length) {
            throw new IllegalArgumentException("Vector length mismatch: " +
                    this.data.length + " vs " + other.data.length);
        }
    }

    /**
     * Returns a string representation of this vector.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return Arrays.toString(data);
    }
}
