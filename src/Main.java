import simulation.Ecosystem;
import simulation.Position;
import organisms.plants.*;
import organisms.animals.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Main entry point for the ecosystem simulation.
 * Phase 5: Statistics & Data Export with Preset System.
 */
public class Main {
    // ========== CHANGE THIS TO SWITCH PRESETS ==========
    private static final EcosystemPreset PRESET = EcosystemPreset.BOOM_BUST;
    // ===================================================

    /**
     * Available ecosystem presets with different dynamics.
     */
    public enum EcosystemPreset {
        BOOM_BUST,           // Current: Dramatic oscillations, 3 species final
        HIGH_BIODIVERSITY,   // Recommended: Multiple species coexist
        BEAR_DOMINANCE,      // Stable apex predator control
        PLANT_PARADISE       // No predators, herbivore-plant balance
    }

    public static void main(String[] args) {
        // Create ecosystem
        Ecosystem ecosystem = new Ecosystem(100, 100, 10.0);
        Random rand = new Random(System.currentTimeMillis());  // Random seed for different results each time

        System.out.println("=================================================");
        System.out.println("  Ecosystem Simulation - Preset: " + PRESET);
        System.out.println("=================================================\n");

        // Initialize ecosystem with selected preset
        initializeEcosystem(ecosystem, rand, PRESET);

        System.out.println("Total organisms: " + ecosystem.getOrganismCount());

        // Run simulation
        System.out.println("Starting simulation...\n");
        int totalSteps = 500;
        int printInterval = 50;

        for (int step = 0; step < totalSteps; step++) {
            ecosystem.simulate(1);

            // Print summary with progress bar
            if (step % printInterval == 0 || step == totalSteps - 1) {
                printProgressBar(step + 1, totalSteps);
                ecosystem.getStats().printSummary();
            }
        }

        System.out.println("\n\nSimulation complete.");

        // Export data
        try {
            String filename = "ecosystem_data.csv";
            ecosystem.getStats().exportToCSV(filename);
            System.out.println("\nData exported to: " + filename);
        } catch (IOException e) {
            System.err.println("Error exporting data: " + e.getMessage());
        }

        // Print final analysis
        printFinalAnalysis(ecosystem);
    }

    /**
     * Initializes the ecosystem with species based on selected preset.
     */
    private static void initializeEcosystem(Ecosystem ecosystem, Random rand, EcosystemPreset preset) {
        switch (preset) {
            case BOOM_BUST:
                initializeBoomBust(ecosystem, rand);
                break;
            case HIGH_BIODIVERSITY:
                initializeHighBiodiversity(ecosystem, rand);
                break;
            case BEAR_DOMINANCE:
                initializeBearDominance(ecosystem, rand);
                break;
            case PLANT_PARADISE:
                initializePlantParadise(ecosystem, rand);
                break;
        }
    }

    /**
     * BOOM_BUST: Enhanced reproduction creates dramatic oscillations.
     * Results: Deer explodes to 373, then crashes. Bears dominate endgame.
     */
    private static void initializeBoomBust(Ecosystem ecosystem, Random rand) {
        // Plants: High diversity for boom phase
        addRandomOrganisms(ecosystem, rand, 60, () -> new Wildflowers(randomPosition(rand), 10.0));
        addRandomOrganisms(ecosystem, rand, 50, () -> new Berries(randomPosition(rand), 15.0));
        addRandomOrganisms(ecosystem, rand, 30, () -> new Aspen(randomPosition(rand), 50.0));
        addRandomOrganisms(ecosystem, rand, 25, () -> new Spruce(randomPosition(rand), 60.0));

        // Herbivores: Standard populations (enhanced reproduction in species files)
        addRandomOrganisms(ecosystem, rand, 15, () -> new Deer(randomPosition(rand), 50.0));
        addRandomOrganisms(ecosystem, rand, 30, () -> new Bunny(randomPosition(rand), 25.0));
        addRandomOrganisms(ecosystem, rand, 15, () -> new FieldMouse(randomPosition(rand), 10.0));
        addRandomOrganisms(ecosystem, rand, 12, () -> new GroundSquirrel(randomPosition(rand), 12.0));
        addRandomOrganisms(ecosystem, rand, 12, () -> new Chipmunk(randomPosition(rand), 10.0));

        // Carnivores: Standard
        addRandomOrganisms(ecosystem, rand, 3, () -> new Fox(randomPosition(rand), 50.0));
        addRandomOrganisms(ecosystem, rand, 2, () -> new Coyote(randomPosition(rand), 70.0));

        // Omnivore: Apex predator
        addRandomOrganisms(ecosystem, rand, 1, () -> new BlackBear(randomPosition(rand), 100.0));
    }

    /**
     * HIGH_BIODIVERSITY: Reduced predation allows multiple species coexistence.
     * Carnivore encounter rates reduced 50% in code (see Carnivore.java and Omnivore.java).
     */
    private static void initializeHighBiodiversity(Ecosystem ecosystem, Random rand) {
        // Plants: High diversity
        addRandomOrganisms(ecosystem, rand, 60, () -> new Wildflowers(randomPosition(rand), 10.0));
        addRandomOrganisms(ecosystem, rand, 50, () -> new Berries(randomPosition(rand), 15.0));
        addRandomOrganisms(ecosystem, rand, 30, () -> new Aspen(randomPosition(rand), 50.0));
        addRandomOrganisms(ecosystem, rand, 25, () -> new Spruce(randomPosition(rand), 60.0));

        // Herbivores: INCREASED populations (+40%)
        addRandomOrganisms(ecosystem, rand, 25, () -> new Deer(randomPosition(rand), 50.0));     // +10
        addRandomOrganisms(ecosystem, rand, 40, () -> new Bunny(randomPosition(rand), 25.0));    // +10
        addRandomOrganisms(ecosystem, rand, 20, () -> new FieldMouse(randomPosition(rand), 10.0)); // +5
        addRandomOrganisms(ecosystem, rand, 18, () -> new GroundSquirrel(randomPosition(rand), 12.0)); // +6
        addRandomOrganisms(ecosystem, rand, 17, () -> new Chipmunk(randomPosition(rand), 10.0)); // +5

        // Carnivores: REDUCED to prevent overhunting
        addRandomOrganisms(ecosystem, rand, 2, () -> new Fox(randomPosition(rand), 50.0));       // -1
        addRandomOrganisms(ecosystem, rand, 1, () -> new Coyote(randomPosition(rand), 70.0));    // -1

        // Omnivore: Single bear
        addRandomOrganisms(ecosystem, rand, 1, () -> new BlackBear(randomPosition(rand), 100.0));
    }

    /**
     * BEAR_DOMINANCE: Stable equilibrium with apex predator control.
     * Original configuration before reproduction enhancements.
     */
    private static void initializeBearDominance(Ecosystem ecosystem, Random rand) {
        // Plants: Moderate diversity
        addRandomOrganisms(ecosystem, rand, 50, () -> new Wildflowers(randomPosition(rand), 10.0));
        addRandomOrganisms(ecosystem, rand, 30, () -> new Berries(randomPosition(rand), 15.0));
        addRandomOrganisms(ecosystem, rand, 20, () -> new Aspen(randomPosition(rand), 50.0));
        addRandomOrganisms(ecosystem, rand, 15, () -> new Spruce(randomPosition(rand), 60.0));

        // Herbivores: Standard populations
        addRandomOrganisms(ecosystem, rand, 15, () -> new Deer(randomPosition(rand), 50.0));
        addRandomOrganisms(ecosystem, rand, 30, () -> new Bunny(randomPosition(rand), 25.0));
        addRandomOrganisms(ecosystem, rand, 15, () -> new FieldMouse(randomPosition(rand), 10.0));
        addRandomOrganisms(ecosystem, rand, 12, () -> new GroundSquirrel(randomPosition(rand), 12.0));
        addRandomOrganisms(ecosystem, rand, 12, () -> new Chipmunk(randomPosition(rand), 10.0));

        // Carnivores: Standard
        addRandomOrganisms(ecosystem, rand, 3, () -> new Fox(randomPosition(rand), 50.0));
        addRandomOrganisms(ecosystem, rand, 2, () -> new Coyote(randomPosition(rand), 70.0));

        // Omnivore: Apex predator
        addRandomOrganisms(ecosystem, rand, 1, () -> new BlackBear(randomPosition(rand), 100.0));
    }

    /**
     * PLANT_PARADISE: No predators - tests herbivore-plant dynamics.
     * Shows plant diversity can be maintained by herbivores alone.
     */
    private static void initializePlantParadise(Ecosystem ecosystem, Random rand) {
        // Plants: High diversity
        addRandomOrganisms(ecosystem, rand, 60, () -> new Wildflowers(randomPosition(rand), 10.0));
        addRandomOrganisms(ecosystem, rand, 50, () -> new Berries(randomPosition(rand), 15.0));
        addRandomOrganisms(ecosystem, rand, 30, () -> new Aspen(randomPosition(rand), 50.0));
        addRandomOrganisms(ecosystem, rand, 25, () -> new Spruce(randomPosition(rand), 60.0));

        // Herbivores: Standard populations
        addRandomOrganisms(ecosystem, rand, 15, () -> new Deer(randomPosition(rand), 50.0));
        addRandomOrganisms(ecosystem, rand, 30, () -> new Bunny(randomPosition(rand), 25.0));
        addRandomOrganisms(ecosystem, rand, 15, () -> new FieldMouse(randomPosition(rand), 10.0));
        addRandomOrganisms(ecosystem, rand, 12, () -> new GroundSquirrel(randomPosition(rand), 12.0));
        addRandomOrganisms(ecosystem, rand, 12, () -> new Chipmunk(randomPosition(rand), 10.0));

        // NO CARNIVORES OR OMNIVORES - Pure herbivore-plant system!
    }

    /**
     * Helper method to add multiple organisms of the same type.
     */
    private static void addRandomOrganisms(Ecosystem eco, Random rand, int count,
                                          java.util.function.Supplier<organisms.Organism> creator) {
        for (int i = 0; i < count; i++) {
            eco.addOrganism(creator.get());
        }
    }

    /**
     * Helper method to generate random position.
     */
    private static Position randomPosition(Random rand) {
        return new Position(rand.nextDouble() * 100, rand.nextDouble() * 100);
    }

    /**
     * Prints a progress bar showing simulation completion.
     */
    private static void printProgressBar(int current, int total) {
        int barLength = 50;
        int progress = (int) ((current / (double) total) * barLength);

        System.out.print("\rProgress: [");
        for (int i = 0; i < barLength; i++) {
            System.out.print(i < progress ? "=" : " ");
        }
        System.out.printf("] %d/%d (%.1f%%)     ",
            current, total, (current / (double) total) * 100);
    }

    /**
     * Prints final analysis with statistics for each species.
     */
    private static void printFinalAnalysis(Ecosystem ecosystem) {
        System.out.println("\n=== Final Analysis ===");

        Map<String, List<Integer>> history = ecosystem.getStats().getPopulationHistory();

        for (Map.Entry<String, List<Integer>> entry : history.entrySet()) {
            String species = entry.getKey();
            List<Integer> data = entry.getValue();

            int initial = data.isEmpty() ? 0 : data.get(0);
            int finalPop = data.isEmpty() ? 0 : data.get(data.size() - 1);
            double avg = data.stream().mapToInt(Integer::intValue).average().orElse(0);
            int max = data.stream().mapToInt(Integer::intValue).max().orElse(0);
            int min = data.stream().mapToInt(Integer::intValue).min().orElse(0);

            System.out.printf("%-20s: Initial=%3d Final=%3d Avg=%.1f Min=%3d Max=%3d%n",
                species, initial, finalPop, avg, min, max);
        }
    }
}
