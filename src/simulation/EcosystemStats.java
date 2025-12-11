package simulation;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Tracks and exports population data over time for analysis.
 * Provides CSV export functionality and statistical summaries.
 */
public class EcosystemStats {
    private Map<String, List<Integer>> populationHistory;  // Species -> [counts per timestep]
    private int currentTimeStep;
    private List<String> speciesNames;  // Ordered list for consistent CSV columns

    /**
     * Creates a new statistics tracker.
     */
    public EcosystemStats() {
        populationHistory = new HashMap<>();
        currentTimeStep = 0;
        speciesNames = new ArrayList<>();
    }

    /**
     * Records population counts for the current timestep.
     * @param counts Map of species name to population count
     * @param timeStep The current time step
     */
    public void recordPopulations(Map<String, Integer> counts, int timeStep) {
        currentTimeStep = timeStep;

        // Initialize new species if encountered
        for (String species : counts.keySet()) {
            if (!populationHistory.containsKey(species)) {
                populationHistory.put(species, new ArrayList<>());
                speciesNames.add(species);
            }
        }

        // Record counts (0 for absent species)
        for (String species : speciesNames) {
            int count = counts.getOrDefault(species, 0);
            populationHistory.get(species).add(count);
        }
    }

    /**
     * Exports population data to a CSV file.
     * @param filename The output file path
     * @throws IOException If file writing fails
     */
    public void exportToCSV(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Header
            writer.print("TimeStep");
            for (String species : speciesNames) {
                writer.print("," + species);
            }
            writer.println();

            // Data rows
            int maxSteps = populationHistory.values().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

            for (int t = 0; t < maxSteps; t++) {
                writer.print(t);
                for (String species : speciesNames) {
                    List<Integer> history = populationHistory.get(species);
                    int count = (t < history.size()) ? history.get(t) : 0;
                    writer.print("," + count);
                }
                writer.println();
            }
        }
    }

    /**
     * Prints a summary of current populations to console.
     */
    public void printSummary() {
        System.out.println("\n=== Population Summary (Step " + currentTimeStep + ") ===");
        for (String species : speciesNames) {
            List<Integer> history = populationHistory.get(species);
            int current = history.isEmpty() ? 0 : history.get(history.size() - 1);
            System.out.printf("%-20s: %4d%n", species, current);
        }
    }

    /**
     * Gets the complete population history.
     * @return Map of species name to list of population counts over time
     */
    public Map<String, List<Integer>> getPopulationHistory() {
        return new HashMap<>(populationHistory);
    }

    /**
     * Gets the population history for a specific species as doubles.
     * @param speciesName The species to get history for
     * @return List of population counts as doubles
     */
    public List<Double> getSpeciesHistory(String speciesName) {
        return populationHistory.getOrDefault(speciesName, new ArrayList<>())
            .stream()
            .map(Integer::doubleValue)
            .collect(Collectors.toList());
    }

    /**
     * Gets the current timestep being tracked.
     * @return The current timestep
     */
    public int getCurrentTimeStep() {
        return currentTimeStep;
    }

    /**
     * Gets the list of all species being tracked.
     * @return List of species names
     */
    public List<String> getSpeciesNames() {
        return new ArrayList<>(speciesNames);
    }
}
