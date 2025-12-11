import models.TwoSpeciesAnalysis;
import java.io.*;
import java.util.*;

/**
 * Analyzes aggregated trophic level interactions.
 * Aggregates individual species into functional groups to reduce noise.
 */
public class TrophicLevelAnalysis {

    private static final String CSV_FILE = "ecosystem_data.csv";

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("  Trophic Level Aggregation Analysis");
        System.out.println("  2×2 Jacobian on Functional Groups");
        System.out.println("============================================\n");

        try {
            // Run both aggregated analyses
            analyzePredatorPreyDynamics();
            System.out.println("\n" + "=".repeat(60) + "\n");
            analyzePlantHerbivoreDynamics();

        } catch (IOException e) {
            System.err.println("Error: Could not read CSV file: " + CSV_FILE);
            System.err.println("Make sure you have run the simulation first!");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n============================================");
        System.out.println("  Analysis Complete!");
        System.out.println("============================================");
    }

    /**
     * Analysis 1: Total Predators (Carnivores+Omnivores) vs. Total Herbivores
     */
    private static void analyzePredatorPreyDynamics() throws IOException {
        System.out.println("ANALYSIS 1: PREDATOR-PREY DYNAMICS");
        System.out.println("Aggregated: All Carnivores+Omnivores vs. All Herbivores");
        System.out.println("=".repeat(60));

        // Read CSV and aggregate
        Map<String, List<Integer>> data = readCSV();

        // Define trophic groups
        String[] herbivores = {"Bunny", "Deer", "FieldMouse", "GroundSquirrel", "Chipmunk"};
        String[] predators = {"Fox", "Coyote", "BlackBear"};

        // Aggregate populations
        List<Integer> totalHerbivores = aggregatePopulations(data, herbivores);
        List<Integer> totalPredators = aggregatePopulations(data, predators);

        // Export aggregated data
        exportAggregatedData("aggregated_predator_prey.csv", totalHerbivores, totalPredators,
                            "TotalHerbivores", "TotalPredators");

        // Run analysis on aggregated data
        TwoSpeciesAnalysis analysis = new TwoSpeciesAnalysis("TotalHerbivores", "TotalPredators");
        analysis.loadDataFromCSV("aggregated_predator_prey.csv");
        analysis.estimateParameters();
        analysis.calculateJacobianAndEigenvalues();
        analysis.compareWithSimulation();
        analysis.exportAnalysis("TotalPredators_TotalHerbivores_analysis.txt");

        System.out.println("\nInterpretation:");
        System.out.println("This shows overall predator-prey dynamics across ALL species.");
        System.out.println("Aggregation reduces noise from individual species extinctions.");
    }

    /**
     * Analysis 2: Total Consumers (Herbivores+Omnivores) vs. Total Plants
     */
    private static void analyzePlantHerbivoreDynamics() throws IOException {
        System.out.println("ANALYSIS 2: PLANT-HERBIVORE DYNAMICS");
        System.out.println("Aggregated: All Plants vs. All Herbivores+Omnivores");
        System.out.println("=".repeat(60));

        // Read CSV and aggregate
        Map<String, List<Integer>> data = readCSV();

        // Define trophic groups
        String[] plants = {"Wildflowers", "Berries", "Aspen", "Spruce"};
        String[] consumers = {"Bunny", "Deer", "FieldMouse", "GroundSquirrel", "Chipmunk", "BlackBear"};

        // Aggregate populations
        List<Integer> totalPlants = aggregatePopulations(data, plants);
        List<Integer> totalConsumers = aggregatePopulations(data, consumers);

        // Export aggregated data
        exportAggregatedData("aggregated_plant_consumer.csv", totalPlants, totalConsumers,
                            "TotalPlants", "TotalConsumers");

        // Run analysis on aggregated data
        TwoSpeciesAnalysis analysis = new TwoSpeciesAnalysis("TotalPlants", "TotalConsumers");
        analysis.loadDataFromCSV("aggregated_plant_consumer.csv");
        analysis.estimateParameters();
        analysis.calculateJacobianAndEigenvalues();
        analysis.compareWithSimulation();
        analysis.exportAnalysis("TotalPlants_TotalConsumers_analysis.txt");

        System.out.println("\nInterpretation:");
        System.out.println("This shows plant-herbivore dynamics at the ecosystem level.");
        System.out.println("Includes omnivores as partial plant consumers.");
    }

    /**
     * Reads CSV file into a map of species -> population list.
     */
    private static Map<String, List<Integer>> readCSV() throws IOException {
        Map<String, List<Integer>> data = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            // Read header
            String headerLine = br.readLine();
            String[] headers = headerLine.split(",");

            // Initialize lists for each species
            for (String header : headers) {
                if (!header.equals("TimeStep")) {
                    data.put(header, new ArrayList<>());
                }
            }

            // Read data rows
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                for (int i = 1; i < values.length && i < headers.length; i++) {
                    String species = headers[i];
                    if (data.containsKey(species)) {
                        data.get(species).add(Integer.parseInt(values[i]));
                    }
                }
            }
        }

        return data;
    }

    /**
     * Aggregates populations across multiple species.
     */
    private static List<Integer> aggregatePopulations(Map<String, List<Integer>> data,
                                                      String[] speciesNames) {
        // Get the length from first species
        int length = 0;
        for (String name : speciesNames) {
            if (data.containsKey(name)) {
                length = data.get(name).size();
                break;
            }
        }

        List<Integer> aggregated = new ArrayList<>(Collections.nCopies(length, 0));

        // Sum across all species
        for (String species : speciesNames) {
            if (data.containsKey(species)) {
                List<Integer> speciesData = data.get(species);
                for (int i = 0; i < speciesData.size(); i++) {
                    aggregated.set(i, aggregated.get(i) + speciesData.get(i));
                }
            }
        }

        return aggregated;
    }

    /**
     * Exports aggregated data to CSV for TwoSpeciesAnalysis to read.
     */
    private static void exportAggregatedData(String filename, List<Integer> species1Data,
                                             List<Integer> species2Data,
                                             String species1Name, String species2Name)
                                             throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println("TimeStep," + species1Name + "," + species2Name);

            // Write data
            for (int i = 0; i < species1Data.size(); i++) {
                writer.printf("%d,%d,%d%n", i, species1Data.get(i), species2Data.get(i));
            }
        }

        System.out.println("Aggregated data exported to: " + filename);
    }
}
