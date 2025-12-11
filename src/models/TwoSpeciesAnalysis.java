package models;

import java.io.*;
import java.util.*;

/**
 * Analyzes two-species interactions using 2x2 Jacobian matrix and eigenvalue analysis.
 *
 * Mathematical Model:
 * dx/dt = x(r_x + a_xx*x + a_xy*y)
 * dy/dt = y(r_y + a_yx*x + a_yy*y)
 *
 * Where:
 * - r_i = intrinsic growth rate (per capita)
 * - a_ij = effect of species j on species i
 */
public class TwoSpeciesAnalysis {

    // Species data
    private String species1Name;
    private String species2Name;
    private List<Double> species1Data;
    private List<Double> species2Data;
    private List<Integer> timeSteps;

    // Model parameters
    private double r1;  // Intrinsic growth rate species 1
    private double r2;  // Intrinsic growth rate species 2
    private double a11; // Intraspecific competition species 1
    private double a12; // Effect of species 2 on species 1
    private double a21; // Effect of species 1 on species 2
    private double a22; // Intraspecific competition species 2

    // Equilibrium point
    private double x_star;
    private double y_star;

    // Eigenvalues (may be complex)
    private Complex lambda1;
    private Complex lambda2;

    // Stability classification
    private String stabilityType;

    /**
     * Creates a new two-species analysis.
     */
    public TwoSpeciesAnalysis(String species1, String species2) {
        this.species1Name = species1;
        this.species2Name = species2;
        this.species1Data = new ArrayList<>();
        this.species2Data = new ArrayList<>();
        this.timeSteps = new ArrayList<>();
    }

    /**
     * Loads data from CSV file for the two species.
     */
    public void loadDataFromCSV(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String headerLine = br.readLine();
            String[] headers = headerLine.split(",");

            // Find column indices for our species
            int species1Index = -1;
            int species2Index = -1;
            int timeStepIndex = 0;

            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equals(species1Name)) species1Index = i;
                if (headers[i].equals(species2Name)) species2Index = i;
                if (headers[i].equals("TimeStep")) timeStepIndex = i;
            }

            if (species1Index == -1 || species2Index == -1) {
                throw new IllegalArgumentException("Species not found in CSV: " +
                    species1Name + " or " + species2Name);
            }

            // Read data
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                timeSteps.add(Integer.parseInt(values[timeStepIndex]));
                species1Data.add(Double.parseDouble(values[species1Index]));
                species2Data.add(Double.parseDouble(values[species2Index]));
            }
        }

        System.out.println("Loaded " + timeSteps.size() + " timesteps for " +
            species1Name + " and " + species2Name);
    }

    /**
     * Estimates model parameters from data using regression.
     * Uses early time steps (exponential growth phase) and equilibrium phase.
     */
    public void estimateParameters() {
        System.out.println("\n=== Estimating Parameters ===");

        // Estimate intrinsic growth rates from early exponential phase (steps 5-30)
        r1 = estimateGrowthRate(species1Data, 5, 30);
        r2 = estimateGrowthRate(species2Data, 5, 30);

        System.out.printf("r_%s = %.4f per step%n", species1Name, r1);
        System.out.printf("r_%s = %.4f per step%n", species2Name, r2);

        // Find equilibrium (average of last 50 steps)
        int n = species1Data.size();
        x_star = average(species1Data.subList(Math.max(0, n - 50), n));
        y_star = average(species2Data.subList(Math.max(0, n - 50), n));

        System.out.printf("Equilibrium: %s* = %.1f, %s* = %.1f%n",
            species1Name, x_star, species2Name, y_star);

        // Estimate interaction coefficients from equilibrium conditions
        // At equilibrium: r_i + a_ii*N_i* + a_ij*N_j* = 0
        estimateInteractionCoefficients();
    }

    /**
     * Estimates per capita growth rate from exponential phase.
     */
    private double estimateGrowthRate(List<Double> data, int startStep, int endStep) {
        if (endStep > data.size()) endStep = data.size() - 1;

        double N0 = data.get(startStep);
        double Nt = data.get(endStep);
        int deltaT = endStep - startStep;

        if (N0 <= 0 || Nt <= 0) {
            // Species extinct, use small negative rate
            return -0.01;
        }

        // r = ln(Nt/N0) / deltaT
        return Math.log(Nt / N0) / deltaT;
    }

    /**
     * Estimates interaction coefficients from equilibrium and dynamics.
     */
    private void estimateInteractionCoefficients() {
        // For now, use simplified estimation
        // In full implementation, would use regression on dN/dt data

        // Intraspecific competition (negative)
        a11 = -0.001;  // Self-limitation
        a22 = -0.001;

        // Interspecific interactions - depends on relationship
        // Estimate from equilibrium: r_i + a_ii*N_i* + a_ij*N_j* ≈ 0

        if (x_star > 0 && y_star > 0) {
            // Both species present - calculate from equilibrium
            a12 = -(r1 + a11 * x_star) / y_star;
            a21 = -(r2 + a22 * y_star) / x_star;
        } else {
            // One or both extinct - use defaults
            a12 = -0.001;
            a21 = 0.001;
        }

        System.out.printf("a_%s%s = %.6f (intraspecific)%n",
            species1Name, species1Name, a11);
        System.out.printf("a_%s%s = %.6f (effect of %s on %s)%n",
            species1Name, species2Name, a12, species2Name, species1Name);
        System.out.printf("a_%s%s = %.6f (effect of %s on %s)%n",
            species2Name, species1Name, a21, species1Name, species2Name);
        System.out.printf("a_%s%s = %.6f (intraspecific)%n",
            species2Name, species2Name, a22);
    }

    /**
     * Calculates the Jacobian matrix at equilibrium and finds eigenvalues.
     */
    public void calculateJacobianAndEigenvalues() {
        System.out.println("\n=== Jacobian Analysis ===");

        // Jacobian matrix elements at equilibrium (x*, y*)
        // J = [ df/dx  df/dy ]  = [ x*(a11 + r1/x* + a11*x* + a12*y*)   x*a12 ]
        //     [ dg/dx  dg/dy ]    [ y*a21                                y*(a22 + r2/y* + a21*x* + a22*y*) ]

        // Simplified for Lotka-Volterra form:
        // J = [ r1 + 2*a11*x* + a12*y*    a12*x* ]
        //     [ a21*y*                     r2 + 2*a22*y* + a21*x* ]

        double j11 = r1 + 2 * a11 * x_star + a12 * y_star;
        double j12 = a12 * x_star;
        double j21 = a21 * y_star;
        double j22 = r2 + 2 * a22 * y_star + a21 * x_star;

        System.out.println("Jacobian matrix at equilibrium:");
        System.out.printf("J = [ %8.4f  %8.4f ]%n", j11, j12);
        System.out.printf("    [ %8.4f  %8.4f ]%n", j21, j22);

        // Calculate eigenvalues analytically for 2x2 matrix
        // λ^2 - Trace(J)*λ + Det(J) = 0

        double trace = j11 + j22;
        double det = j11 * j22 - j12 * j21;

        System.out.printf("Trace(J) = %.4f%n", trace);
        System.out.printf("Det(J) = %.4f%n", det);

        // Discriminant
        double discriminant = trace * trace - 4 * det;

        System.out.printf("Discriminant = %.4f%n", discriminant);

        if (discriminant >= 0) {
            // Real eigenvalues
            double sqrtDisc = Math.sqrt(discriminant);
            lambda1 = new Complex((trace + sqrtDisc) / 2, 0);
            lambda2 = new Complex((trace - sqrtDisc) / 2, 0);

            System.out.printf("λ₁ = %.4f (real)%n", lambda1.real);
            System.out.printf("λ₂ = %.4f (real)%n", lambda2.real);
        } else {
            // Complex eigenvalues
            double realPart = trace / 2;
            double imagPart = Math.sqrt(-discriminant) / 2;
            lambda1 = new Complex(realPart, imagPart);
            lambda2 = new Complex(realPart, -imagPart);

            System.out.printf("λ₁ = %.4f + %.4fi (complex)%n", lambda1.real, lambda1.imag);
            System.out.printf("λ₂ = %.4f - %.4fi (complex)%n", lambda2.real, lambda2.imag);
        }

        classifyStability();
    }

    /**
     * Classifies the stability of the equilibrium based on eigenvalues.
     */
    private void classifyStability() {
        System.out.println("\n=== Stability Classification ===");

        double realPart = lambda1.real;
        boolean hasImaginary = Math.abs(lambda1.imag) > 1e-10;

        if (realPart < -1e-10) {
            // Negative real part - stable
            if (hasImaginary) {
                stabilityType = "Stable Spiral (Damped Oscillations)";
                double period = 2 * Math.PI / Math.abs(lambda1.imag);
                System.out.println("Type: " + stabilityType);
                System.out.printf("Oscillation period: %.1f time steps%n", period);
            } else {
                stabilityType = "Stable Node (Monotonic Convergence)";
                System.out.println("Type: " + stabilityType);
            }
        } else if (realPart > 1e-10) {
            // Positive real part - unstable
            if (hasImaginary) {
                stabilityType = "Unstable Spiral (Growing Oscillations)";
                double period = 2 * Math.PI / Math.abs(lambda1.imag);
                System.out.println("Type: " + stabilityType);
                System.out.printf("Oscillation period: %.1f time steps%n", period);
            } else {
                stabilityType = "Unstable Node (Exponential Divergence)";
                System.out.println("Type: " + stabilityType);
            }
        } else {
            // Real part near zero
            if (hasImaginary) {
                stabilityType = "Center (Neutral Oscillations)";
                double period = 2 * Math.PI / Math.abs(lambda1.imag);
                System.out.println("Type: " + stabilityType);
                System.out.printf("Oscillation period: %.1f time steps%n", period);
            } else {
                stabilityType = "Marginally Stable";
                System.out.println("Type: " + stabilityType);
            }
        }
    }

    /**
     * Compares model predictions with actual simulation data.
     */
    public void compareWithSimulation() {
        System.out.println("\n=== Model vs Simulation Comparison ===");

        // Calculate correlation between predicted oscillations and actual
        // For now, just report observed dynamics

        double species1Mean = average(species1Data);
        double species2Mean = average(species2Data);
        double species1Std = standardDeviation(species1Data, species1Mean);
        double species2Std = standardDeviation(species2Data, species2Mean);

        System.out.printf("%s: Mean=%.1f, StdDev=%.1f, CV=%.2f%%%n",
            species1Name, species1Mean, species1Std, 100*species1Std/species1Mean);
        System.out.printf("%s: Mean=%.1f, StdDev=%.1f, CV=%.2f%%%n",
            species2Name, species2Mean, species2Std, 100*species2Std/species2Mean);

        // Check if observed dynamics match prediction
        double cv1 = species1Std / species1Mean;
        double cv2 = species2Std / species2Mean;
        boolean hasOscillations = cv1 > 0.3 || cv2 > 0.3;

        if (stabilityType.contains("Spiral") && hasOscillations) {
            System.out.println("✓ Prediction matches observation: Oscillations detected");
        } else if (stabilityType.contains("Node") && !hasOscillations) {
            System.out.println("✓ Prediction matches observation: Monotonic dynamics");
        } else {
            System.out.println("⚠ Prediction differs from observation");
        }
    }

    /**
     * Exports analysis results to a text file.
     */
    public void exportAnalysis(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Two-Species Jacobian Analysis");
            writer.println("Species: " + species1Name + " and " + species2Name);
            writer.println("======================================\n");

            writer.println("Model Parameters:");
            writer.printf("r_%s = %.6f%n", species1Name, r1);
            writer.printf("r_%s = %.6f%n", species2Name, r2);
            writer.printf("a_%s%s = %.6f%n", species1Name, species1Name, a11);
            writer.printf("a_%s%s = %.6f%n", species1Name, species2Name, a12);
            writer.printf("a_%s%s = %.6f%n", species2Name, species1Name, a21);
            writer.printf("a_%s%s = %.6f%n", species2Name, species2Name, a22);

            writer.println("\nEquilibrium:");
            writer.printf("%s* = %.2f%n", species1Name, x_star);
            writer.printf("%s* = %.2f%n", species2Name, y_star);

            writer.println("\nEigenvalues:");
            writer.printf("λ₁ = %.4f + %.4fi%n", lambda1.real, lambda1.imag);
            writer.printf("λ₂ = %.4f + %.4fi%n", lambda2.real, lambda2.imag);

            writer.println("\nStability: " + stabilityType);
        }

        System.out.println("\nAnalysis exported to: " + filename);
    }

    // Helper methods

    private double average(List<Double> data) {
        return data.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private double standardDeviation(List<Double> data, double mean) {
        double variance = data.stream()
            .mapToDouble(x -> Math.pow(x - mean, 2))
            .average()
            .orElse(0.0);
        return Math.sqrt(variance);
    }

    /**
     * Simple complex number class for eigenvalue calculations.
     */
    private static class Complex {
        double real;
        double imag;

        Complex(double real, double imag) {
            this.real = real;
            this.imag = imag;
        }
    }

    // Getters
    public String getStabilityType() { return stabilityType; }
    public double getEquilibriumX() { return x_star; }
    public double getEquilibriumY() { return y_star; }
}
