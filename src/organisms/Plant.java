package organisms;

import simulation.Ecosystem;
import simulation.Position;

/**
 * Abstract base class for all photosynthesizing organisms.
 */
public abstract class Plant extends Organism {
    protected double growthRate;              // Size increase per time step with sunlight
    protected double maxSize;                 // Maximum size limit
    protected double currentSize;             // Current size (synchronized with energy)
    protected int seedDispersalRadius;        // How far seeds travel
    protected double reproductionEnergyThreshold;  // Minimum size to reproduce
    protected int reproductionAge;            // Minimum age to reproduce
    protected double regenerationRate;        // Regrowth rate after being eaten
    protected int reproductionCooldown;       // Time steps between reproductions
    protected int timeSinceLastReproduction;  // Time since last reproduction

    /**
     * Creates a new plant at the specified position with initial size.
     * @param position The starting position
     * @param initialSize The starting size (also sets initial energy)
     */
    public Plant(Position position, double initialSize) {
        super(position, initialSize);
        this.currentSize = initialSize;
        this.reproductionCooldown = 30;  // Default cooldown
        this.timeSinceLastReproduction = 30;  // Start ready to reproduce
    }

    /**
     * Photosynthesis - converts sunlight to energy/size.
     * Must be implemented by each plant species.
     * @param sunlight The sunlight intensity
     */
    public abstract void photosynthesize(double sunlight);

    /**
     * Creates an offspring of the same species.
     * @param position The position for the offspring
     * @return A new Plant offspring
     */
    protected abstract Plant createOffspring(Position position);

    /**
     * Main update loop for plants.
     * @param ecosystem The ecosystem containing this plant
     */
    @Override
    public void update(Ecosystem ecosystem) {
        if (!isAlive) return;

        incrementAge();
        timeSinceLastReproduction++;
        photosynthesize(ecosystem.getSunlightIntensity());
        grow();
        regenerate();

        if (shouldReproduce()) {
            reproduce(ecosystem);
        }
    }

    /**
     * Increases size based on growth rate.
     */
    protected void grow() {
        if (currentSize < maxSize) {
            currentSize += growthRate;
            currentSize = Math.min(currentSize, maxSize);
            energy = currentSize;  // Sync energy with size
        }
    }

    /**
     * Regrows after being partially eaten.
     * Only regenerates when damaged (below 70% of max size).
     */
    protected void regenerate() {
        // Only regenerate if damaged (below 70% health)
        if (currentSize < maxSize * 0.7) {
            currentSize += regenerationRate;
            currentSize = Math.min(currentSize, maxSize);
            energy = currentSize;
        }
    }

    /**
     * Checks if plant is ready to reproduce.
     * @return true if plant meets reproduction conditions
     */
    protected boolean shouldReproduce() {
        return age >= reproductionAge &&
               currentSize >= reproductionEnergyThreshold &&
               timeSinceLastReproduction >= reproductionCooldown;
    }

    /**
     * Creates offspring at a nearby location.
     * @param ecosystem The ecosystem to add offspring to
     * @return true if reproduction was successful
     */
    public boolean reproduce(Ecosystem ecosystem) {
        if (!shouldReproduce()) return false;

        // Only 2% chance of reproduction per time step when conditions are met
        if (ecosystem.getRandom().nextDouble() > 0.02) return false;

        // Create seed at random nearby location
        double angle = ecosystem.getRandom().nextDouble() * 2 * Math.PI;
        double distance = ecosystem.getRandom().nextDouble() * seedDispersalRadius;
        double newX = position.getX() + distance * Math.cos(angle);
        double newY = position.getY() + distance * Math.sin(angle);

        // Check bounds
        newX = Math.max(0, Math.min(newX, ecosystem.getGridWidth()));
        newY = Math.max(0, Math.min(newY, ecosystem.getGridHeight()));

        Position seedPosition = new Position(newX, newY);

        // Create offspring (subclass-specific)
        Plant offspring = createOffspring(seedPosition);
        ecosystem.addOrganism(offspring);

        // Cost of reproduction
        currentSize *= 0.9;  // 10% energy cost
        energy = currentSize;

        // Reset reproduction timer
        timeSinceLastReproduction = 0;

        return true;
    }

    /**
     * Wrapper for abstract reproduce method compatibility.
     * @return true if reproduction was successful
     */
    @Override
    public boolean reproduce() {
        // This should not be called directly for plants
        // Use reproduce(Ecosystem ecosystem) instead
        return false;
    }

    /**
     * Handles death of the plant.
     */
    @Override
    public void die() {
        isAlive = false;
    }

    /**
     * Returns energy value when eaten.
     * Herbivores can only eat a portion of the plant.
     * @return The energy value available for consumption
     */
    @Override
    public double getEnergyValue() {
        // Herbivores can only eat portion of plant
        return Math.min(currentSize * 0.3, currentSize);
    }

    /**
     * Reduces plant size when consumed by a herbivore.
     * @param amount The amount consumed
     */
    public void getConsumed(double amount) {
        currentSize -= amount;
        energy = currentSize;

        if (currentSize <= 0) {
            die();
        }
    }

    /**
     * Gets the current size of the plant.
     * @return The current size
     */
    public double getCurrentSize() {
        return currentSize;
    }
}
