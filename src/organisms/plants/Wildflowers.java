package organisms.plants;

import organisms.Plant;
import simulation.Position;

/**
 * Fast-growing groundcover, primary food source for small herbivores.
 */
public class Wildflowers extends Plant {

    /**
     * Creates a new Wildflowers plant at the specified position.
     * @param position The starting position
     * @param initialSize The starting size
     */
    public Wildflowers(Position position, double initialSize) {
        super(position, initialSize);

        // Set species-specific parameters
        this.maxSize = 20.0;
        this.growthRate = 0.15;  // Further reduced from 0.2
        this.reproductionAge = 8;  // Increased from 5 to slow reproduction
        this.reproductionEnergyThreshold = 15.0;
        this.seedDispersalRadius = 5;
        this.regenerationRate = 0.1;  // Reduced from 0.3
    }

    /**
     * Converts sunlight to energy through photosynthesis.
     * Wildflowers are efficient at photosynthesis.
     * @param sunlight The sunlight intensity
     */
    @Override
    public void photosynthesize(double sunlight) {
        double energyGain = sunlight * 0.3;  // Further reduced from 0.5
        currentSize += energyGain;
        currentSize = Math.min(currentSize, maxSize);
        energy = currentSize;
    }

    /**
     * Regrows after being partially eaten.
     * Only regenerates when damaged (below 70% of max size).
     * Boost regrowth when heavily grazed.
     */
    @Override
    protected void regenerate() {
        // Only regenerate if damaged (below 70% health)
        if (currentSize < maxSize * 0.7) {
            if (currentSize < maxSize * 0.5) {
                // Boost regrowth when heavily grazed
                currentSize += regenerationRate * 1.2;
            } else {
                currentSize += regenerationRate;
            }
            currentSize = Math.min(currentSize, maxSize);
            energy = currentSize;
        }
    }

    /**
     * Creates an offspring wildflower.
     * @param position The position for the offspring
     * @return A new Wildflowers plant
     */
    @Override
    protected Plant createOffspring(Position position) {
        return new Wildflowers(position, 5.0);  // Seeds start small
    }

    /**
     * Gets the species name.
     * @return "Wildflowers"
     */
    @Override
    public String getSpeciesName() {
        return "Wildflowers";
    }
}
