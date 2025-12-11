package simulation;

import organisms.Organism;
import organisms.animals.Herbivore;
import organisms.animals.Carnivore;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main simulation container that manages all organisms and orchestrates updates.
 */
public class Ecosystem {
    private List<Organism> organisms;          // All organisms in simulation
    private Grid grid;                         // Spatial indexing
    private int timeStep;                      // Current simulation step
    private double sunlightIntensity;          // Sunlight level (default 1.0)
    private int gridWidth;                     // Grid width
    private int gridHeight;                    // Grid height
    private Random random;                     // Random number generator
    private EcosystemStats stats;              // Population statistics tracker

    /**
     * Creates a new ecosystem with specified grid dimensions.
     * @param gridWidth The width of the grid
     * @param gridHeight The height of the grid
     * @param cellSize The size of grid cells for spatial indexing
     */
    public Ecosystem(int gridWidth, int gridHeight, double cellSize) {
        this.organisms = new ArrayList<>();
        this.grid = new Grid(gridWidth, gridHeight, cellSize);
        this.timeStep = 0;
        this.sunlightIntensity = 1.0;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.random = new Random(42); // Fixed seed for reproducibility
        this.stats = new EcosystemStats();
    }

    /**
     * Runs the simulation for a specified number of steps.
     * @param steps The number of time steps to simulate
     */
    public void simulate(int steps) {
        for (int i = 0; i < steps; i++) {
            // 1. Increment time step
            timeStep++;

            // 2. Update environment (could adjust sunlight based on time, seasons, etc.)
            // For now, sunlight remains constant

            // 3. Update all organisms
            updateAllOrganisms();

            // 4. Remove dead organisms
            removeDeadOrganisms();

            // 5. Record statistics
            Map<String, Integer> counts = getPopulationCounts();
            stats.recordPopulations(counts, timeStep);

            // 6. Optional: Print progress every 10 steps
            if (timeStep % 10 == 0) {
                System.out.println("Time step " + timeStep + ": " + organisms.size() + " organisms");
            }
        }
    }

    /**
     * Adds an organism to the ecosystem and spatial grid.
     * @param o The organism to add
     */
    public void addOrganism(Organism o) {
        organisms.add(o);
        grid.addOrganism(o);
    }

    /**
     * Removes an organism from the ecosystem and spatial grid.
     * @param o The organism to remove
     */
    public void removeOrganism(Organism o) {
        organisms.remove(o);
        grid.removeOrganism(o);
    }

    /**
     * Gets organisms of specified types within a radius of a position.
     * @param pos The center position
     * @param radius The search radius
     * @param types List of class types to filter (e.g., Plant.class, Herbivore.class)
     * @return List of organisms matching the criteria
     */
    public List<Organism> getOrganismsInRange(Position pos, double radius, List<Class<?>> types) {
        List<Organism> inRange = grid.getOrganismsInRadius(pos, radius);

        if (types == null || types.isEmpty()) {
            return inRange;
        }

        return inRange.stream()
                .filter(o -> types.stream().anyMatch(type -> type.isInstance(o)))
                .collect(Collectors.toList());
    }

    /**
     * Updates all organisms in the ecosystem.
     */
    private void updateAllOrganisms() {
        // Create a copy of the list to avoid concurrent modification
        List<Organism> organismsToUpdate = new ArrayList<>(organisms);

        for (Organism o : organismsToUpdate) {
            if (o.isAlive()) {
                Position oldPosition = o.getPosition().clone();
                o.update(this);

                // Update position in grid if organism moved
                if (oldPosition.getX() != o.getPosition().getX() ||
                    oldPosition.getY() != o.getPosition().getY()) {
                    grid.updateOrganismPosition(o, oldPosition);
                }
            }
        }
    }

    /**
     * Removes all dead organisms from the ecosystem.
     */
    private void removeDeadOrganisms() {
        // Use iterator to safely remove during iteration
        Iterator<Organism> iterator = organisms.iterator();
        while (iterator.hasNext()) {
            Organism o = iterator.next();
            if (!o.isAlive()) {
                iterator.remove();
                grid.removeOrganism(o);
            }
        }
    }

    /**
     * Gets population counts grouped by species.
     * @return Map of species name to count
     */
    public Map<String, Integer> getPopulationCounts() {
        Map<String, Integer> counts = new HashMap<>();

        for (Organism o : organisms) {
            String species = o.getSpeciesName();
            counts.put(species, counts.getOrDefault(species, 0) + 1);
        }

        return counts;
    }

    /**
     * Gets the count of organisms matching any of the specified types.
     * Used for calculating encounter probabilities based on diet.
     * @param types List of class types to count (e.g., Wildflowers.class)
     * @return The total count of matching organisms
     */
    public int getPopulationCount(List<Class<?>> types) {
        if (types == null || types.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Organism o : organisms) {
            if (o.isAlive()) {
                for (Class<?> type : types) {
                    if (type.isInstance(o)) {
                        count++;
                        break; // Don't double-count
                    }
                }
            }
        }
        return count;
    }

    /**
     * Gets a random living organism matching any of the specified types.
     * Used for probabilistic feeding - when encounter succeeds, pick random food.
     * @param types List of class types to match
     * @return A random matching organism, or null if none found
     */
    public Organism getRandomOrganismOfType(List<Class<?>> types) {
        if (types == null || types.isEmpty()) {
            return null;
        }

        // Collect all matching organisms
        List<Organism> matches = new ArrayList<>();
        for (Organism o : organisms) {
            if (o.isAlive()) {
                for (Class<?> type : types) {
                    if (type.isInstance(o)) {
                        matches.add(o);
                        break;
                    }
                }
            }
        }

        // Return random match
        if (matches.isEmpty()) {
            return null;
        }
        return matches.get(random.nextInt(matches.size()));
    }

    /**
     * Gets the total count of living herbivores in the ecosystem.
     * Used for calculating plant encounter rates.
     * @return The count of herbivores
     */
    public int getTotalHerbivores() {
        int count = 0;
        for (Organism o : organisms) {
            if (o.isAlive() && o instanceof Herbivore) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the total count of living carnivores in the ecosystem.
     * Used for calculating prey encounter rates.
     * @return The count of carnivores
     */
    public int getTotalCarnivores() {
        int count = 0;
        for (Organism o : organisms) {
            if (o.isAlive() && o instanceof Carnivore) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the total area of the ecosystem grid.
     * Used for calculating population density.
     * @return The grid area (width * height)
     */
    public double getGridArea() {
        return gridWidth * gridHeight;
    }

    /**
     * @return The current time step
     */
    public int getTimeStep() {
        return timeStep;
    }

    /**
     * @return The sunlight intensity (0.0 to 1.0+)
     */
    public double getSunlightIntensity() {
        return sunlightIntensity;
    }

    /**
     * @return The random number generator
     */
    public Random getRandom() {
        return random;
    }

    /**
     * @return The grid width
     */
    public int getGridWidth() {
        return gridWidth;
    }

    /**
     * @return The grid height
     */
    public int getGridHeight() {
        return gridHeight;
    }

    /**
     * @return The spatial grid
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * @return The list of all organisms
     */
    public List<Organism> getOrganisms() {
        return new ArrayList<>(organisms);
    }

    /**
     * @return The count of living organisms
     */
    public int getOrganismCount() {
        return organisms.size();
    }

    /**
     * Sets the sunlight intensity.
     * @param intensity The new sunlight intensity
     */
    public void setSunlightIntensity(double intensity) {
        this.sunlightIntensity = intensity;
    }

    /**
     * @return The ecosystem statistics tracker
     */
    public EcosystemStats getStats() {
        return stats;
    }
}
