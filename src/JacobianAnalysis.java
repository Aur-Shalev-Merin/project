import models.TwoSpeciesAnalysis;
import java.io.IOException;
import java.util.Scanner;

/**
 * Interactive program for analyzing two-species interactions using Jacobian analysis.
 * Users can select any pair of species from the simulation to analyze.
 */
public class JacobianAnalysis {

    private static final String CSV_FILE = "ecosystem_data.csv";

    // Available species from the simulation
    private static final String[] AVAILABLE_SPECIES = {
        "Wildflowers", "Berries", "Aspen", "Spruce",           // Plants
        "Bunny", "Deer", "FieldMouse", "GroundSquirrel", "Chipmunk",  // Herbivores
        "Fox", "Coyote", "BlackBear"                           // Carnivores/Omnivores
    };

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("  2×2 Jacobian Analysis for Ecosystems");
        System.out.println("  Linear Stability & Eigenvalue Analysis");
        System.out.println("============================================\n");

        Scanner scanner = new Scanner(System.in);

        // Interactive mode - user selects species
        if (args.length == 0) {
            runInteractiveMode(scanner);
        }
        // Command-line mode - species specified as arguments
        else if (args.length == 2) {
            String species1 = args[0];
            String species2 = args[1];
            analyzeSpeciesPair(species1, species2);
        }
        // Batch mode - analyze common pairs
        else if (args.length == 1 && args[0].equals("--all")) {
            analyzeCommonPairs();
        }
        else {
            System.out.println("Usage:");
            System.out.println("  java JacobianAnalysis                  (interactive mode)");
            System.out.println("  java JacobianAnalysis Species1 Species2 (analyze specific pair)");
            System.out.println("  java JacobianAnalysis --all            (analyze all common pairs)");
        }

        scanner.close();
    }

    /**
     * Interactive mode - user selects species from menu.
     */
    private static void runInteractiveMode(Scanner scanner) {
        while (true) {
            System.out.println("\nAvailable species:");
            System.out.println("Plants:     1=Wildflowers  2=Berries  3=Aspen  4=Spruce");
            System.out.println("Herbivores: 5=Bunny  6=Deer  7=FieldMouse  8=GroundSquirrel  9=Chipmunk");
            System.out.println("Predators:  10=Fox  11=Coyote  12=BlackBear");
            System.out.println("(Enter 0 to quit)");

            System.out.print("\nSelect first species (1-12): ");
            int choice1 = scanner.nextInt();
            if (choice1 == 0) break;

            System.out.print("Select second species (1-12): ");
            int choice2 = scanner.nextInt();
            if (choice2 == 0) break;

            if (choice1 < 1 || choice1 > 12 || choice2 < 1 || choice2 > 12) {
                System.out.println("Invalid choice. Please select 1-12.");
                continue;
            }

            if (choice1 == choice2) {
                System.out.println("Please select two different species.");
                continue;
            }

            String species1 = AVAILABLE_SPECIES[choice1 - 1];
            String species2 = AVAILABLE_SPECIES[choice2 - 1];

            analyzeSpeciesPair(species1, species2);

            System.out.print("\nAnalyze another pair? (y/n): ");
            String again = scanner.next();
            if (!again.equalsIgnoreCase("y")) break;
        }

        System.out.println("\nAnalysis complete!");
    }

    /**
     * Analyzes a specific pair of species.
     */
    private static void analyzeSpeciesPair(String species1, String species2) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ANALYZING: " + species1 + " ↔ " + species2);
        System.out.println("=".repeat(60));

        try {
            // Create analysis object
            TwoSpeciesAnalysis analysis = new TwoSpeciesAnalysis(species1, species2);

            // Load data from CSV
            analysis.loadDataFromCSV(CSV_FILE);

            // Estimate parameters
            analysis.estimateParameters();

            // Calculate Jacobian and eigenvalues
            analysis.calculateJacobianAndEigenvalues();

            // Compare with simulation
            analysis.compareWithSimulation();

            // Export results
            String outputFile = species1 + "_" + species2 + "_analysis.txt";
            analysis.exportAnalysis(outputFile);

            System.out.println("\n" + "=".repeat(60));

        } catch (IOException e) {
            System.err.println("Error: Could not read CSV file: " + CSV_FILE);
            System.err.println("Make sure you have run the simulation first!");
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Species may not be present in the CSV file.");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Analyzes all common/interesting species pairs.
     */
    private static void analyzeCommonPairs() {
        System.out.println("Analyzing common ecological interactions...\n");

        String[][] commonPairs = {
            // Classic predator-prey
            {"Wildflowers", "Bunny"},      // Plant-Herbivore
            {"Bunny", "Fox"},              // Prey-Predator
            {"Deer", "Coyote"},            // Large herbivore-predator

            // Omnivore interactions
            {"Deer", "BlackBear"},         // Prey-Omnivore
            {"Fox", "BlackBear"},          // Carnivore-Omnivore

            // Competition
            {"Bunny", "Deer"},             // Herbivore competition
            {"Fox", "Coyote"},             // Carnivore competition
            {"Wildflowers", "Berries"},    // Plant competition

            // Small herbivore dynamics
            {"Wildflowers", "FieldMouse"},
            {"Berries", "Chipmunk"}
        };

        for (String[] pair : commonPairs) {
            analyzeSpeciesPair(pair[0], pair[1]);
            System.out.println("\n" + "-".repeat(60) + "\n");

            // Pause between analyses
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\nAll analyses complete!");
        System.out.println("Check individual *_analysis.txt files for detailed results.");
    }

    /**
     * Suggests interesting species pairs based on ecological relationships.
     */
    private static void suggestInterestingPairs() {
        System.out.println("\nSuggested pairs to analyze:");
        System.out.println("\nClassic Predator-Prey:");
        System.out.println("  • Wildflowers ↔ Bunny  (plant-herbivore)");
        System.out.println("  • Bunny ↔ Fox          (prey-predator)");
        System.out.println("  • Deer ↔ Coyote        (large prey-predator)");

        System.out.println("\nOmnivore Dynamics:");
        System.out.println("  • Deer ↔ BlackBear     (prey-omnivore)");
        System.out.println("  • Fox ↔ BlackBear      (competition?)");

        System.out.println("\nCompetition:");
        System.out.println("  • Bunny ↔ Deer         (herbivore competition)");
        System.out.println("  • Fox ↔ Coyote         (carnivore competition)");
        System.out.println("  • Wildflowers ↔ Berries (plant competition)");
    }
}
